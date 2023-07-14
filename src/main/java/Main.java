import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SearchEngine searchEngine = new BooleanSearchEngine();
        String folderPath = "pdfs";
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            searchEngine.indexFiles(folder);
            try (ServerSocket serverSocket = new ServerSocket(8999)) {
                System.out.println("Сервер запущен и слушает порт 8999");
                while (true) {
                    try (
                            Socket socket = serverSocket.accept();
                            InputStream inputStream = socket.getInputStream();
                            OutputStream outputStream = socket.getOutputStream();
                            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                            PrintWriter out = new PrintWriter(outputStream);
                            Scanner scanner = new Scanner(System.in);
                    ) {
                        String word = in.readLine();

                        if (word == null || word.isEmpty()) {
                            break;
                        }
                        List<PageEntry> results = searchEngine.search(word);


                        for (PageEntry entry : results) {
                            System.out.println(entry);
                        }

                        Gson gson = new Gson();
                        String jsonResult = gson.toJson(results);
                        out.println(jsonResult);
                        out.flush();
                        System.out.println("Результаты поиска отправлены клиенту");
                    } catch (IOException e) {
                        System.out.println("Ошибка при обработке подключения");
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.out.println("Не могу стартовать сервер");
                e.printStackTrace();
            }
        }
    }
}