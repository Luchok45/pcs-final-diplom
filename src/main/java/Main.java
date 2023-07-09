import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SearchEngine searchEngine = new BooleanSearchEngine();
        String folderPath = "C:/Users/msi/IdeaProjects/pcs-final-diplom/pdfs";
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            searchEngine.indexFiles(folder);
            List<PageEntry> results = searchEngine.search("бизнес");
            for (PageEntry entry : results) {
                System.out.println(entry);
            }
        } else {
            System.out.println("Неверный путь к папке");
        }
    }
}