import java.io.*;
import java.util.*;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import static com.itextpdf.kernel.pdf.PdfDocument.*;

public class BooleanSearchEngine implements SearchEngine {
    private Map<String, List<PageEntry>> index;

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        // создаем мапу, где ключом будет слово, а значением - список объектов PageEntry
        index = new HashMap<>();

        // получаем список всех файлов pdf в директории pdfsDir
        File[] pdfFiles = pdfsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        // перебираем все pdf-файлы
        for (File pdfFile : pdfFiles) {
            // создаем объект PdfDocument для каждого pdf-файла
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfFile));

            // перебираем все страницы pdf-файла
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                // извлекаем текст со страницы
                String pageText = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i));

                // разбиваем текст на слова и подсчитываем частоту каждого слова
                Map<String, Integer> wordFreqs = new HashMap<>();
                String[] words = pageText.split("\\P{IsAlphabetic}+");
                for (String word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    wordFreqs.put(word, wordFreqs.getOrDefault(word, 0) + 1);
                }

                // создаем объект PageEntry для каждого уникального слова на странице,
                // и добавляем его в мапу index
                for (String word : wordFreqs.keySet()) {
                    PageEntry pageEntry = new PageEntry(pdfFile.getName(), i, wordFreqs.get(word));
                    List<PageEntry> pageEntries = index.getOrDefault(word, new ArrayList<>());
                    pageEntries.add(pageEntry);
                    index.put(word, pageEntries);
                }
            }

            // закрываем PdfDocument
            pdfDoc.close();
        }


        // сортируем списки PageEntry для каждого слова в мапе index
        for (List<PageEntry> pageEntries : index.values()) {
            Collections.sort(pageEntries, Collections.reverseOrder());
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        // переводим слово в нижний регистр
        word = word.toLowerCase();

        // получаем список объектов PageEntry для заданного слова
        List<PageEntry> pageEntries = index.getOrDefault(word, new ArrayList<>());

        return pageEntries;
    }
    public void indexDocuments(List<File> pdfFiles) throws IOException {
        index = new HashMap<>();
        for (File pdfFile : pdfFiles) {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfFile));
            int numPages = pdfDoc.getNumberOfPages();
            for (int i = 1; i <= numPages; i++) {
                PdfPage page = pdfDoc.getPage(i);
                String pageText = PdfTextExtractor.getTextFromPage(page);
                String[] words = pageText.split("\\P{IsAlphabetic}+");
                for (String word : words) {
                    if (index.containsKey(word)) {
                        List<PageEntry> entries = index.get(word);
                        PageEntry pageEntry = new PageEntry(pdfFile.getName(), i, 1);
                        if (entries.contains(pageEntry)) {
                            int index = entries.indexOf(pageEntry);
                            entries.set(index, entries.get(index).incrementCount());
                        } else {
                            entries.add(pageEntry);
                        }
                    } else {
                        List<PageEntry> entries = new ArrayList<>();
                        entries.add(new PageEntry(pdfFile.getName(), i, 1));
                        index.put(word, entries);
                    }
                }
            }
            pdfDoc.close();
        }
    }
    private String getPageText(PdfDocument pdfDoc, int pageNumber) {
        PdfPage pdfPage = pdfDoc.getPage(pageNumber);
        return PdfTextExtractor.getTextFromPage(pdfPage);
    }
}
