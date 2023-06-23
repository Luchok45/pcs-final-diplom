import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private final int count;
    private String text;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    public String getPdfName() {
        return pdfName;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }

    public PageEntry incrementCount() {
        return new PageEntry(this.pdfName, this.page, this.count + 1);
    }

    private static String getPageText(String pdfName, int pageNum) {
        try (PDDocument document = PDDocument.load(new File(pdfName))) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(pageNum);
            stripper.setEndPage(pageNum);
            return stripper.getText(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    static PageEntry getPageEntry(String pdfName, int pageNum) {
        String text = getPageText(pdfName, pageNum);
        return new PageEntry(pdfName, pageNum,  0);
    }

    public String getText() {
        return text;
    }
    @Override
    public int compareTo(PageEntry other) {
        return Integer.compare(other.count, this.count);
    }

    @Override
    public String toString() {
        return "PageEntry{" +
                "pdfName='" + pdfName + '\'' +
                ", page=" + page +
                ", count=" + count +
                '}';
    }
}

