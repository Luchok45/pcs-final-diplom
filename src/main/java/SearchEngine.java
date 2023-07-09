import java.util.List;
import java.io.File;
public interface SearchEngine {
    void indexFiles(File folder);
    List<PageEntry> search(String keyword);

}
