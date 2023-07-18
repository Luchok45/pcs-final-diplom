import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class BooleanSearchEngine implements SearchEngine {
    private List<PageEntry> indexedFiles;

    public BooleanSearchEngine() {
        indexedFiles = new ArrayList<>();
    }

    @Override
    public void indexFiles(File folder) {
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".pdf")) {
                    try {
                        PdfDocument doc = new PdfDocument(new PdfReader(file));
                        int pageCount = doc.getNumberOfPages();

                        for (int pageNumber = 1; pageNumber <= pageCount; pageNumber++) {
                            PdfPage page = doc.getPage(pageNumber);
                            String text = PdfTextExtractor.getTextFromPage(page);


                            PageEntry entry = new PageEntry(file.getName(), pageNumber, text);
                            indexedFiles.add(entry);
                        }

                        doc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String keyword) {
        List<PageEntry> results = new ArrayList<>();

        for (PageEntry entry : indexedFiles) {
            int wordCount = countWordOccurrences(entry.getText(), keyword);
            if (wordCount > 0) {
                PageEntry resultEntry = new PageEntry(entry.getFileName(), entry.getPage(), wordCount);
                results.add( resultEntry);
            }
        }
        Collections.reverse(results);
        return results;
    }

    private int countWordOccurrences(String text, String keyword) {
        int count = 0;
        String[] words = text.split("\\P{IsAlphabetic}+");
        for (String word : words) {
            if (word.equalsIgnoreCase(keyword)) {
                count++;
            }
        }
        return count;
    }
}