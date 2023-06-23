import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.client.ProtocolHandler;

import javax.naming.directory.SearchResult;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private static Map<String, List<Integer>> index = new HashMap<>();

    private static final int PORT = 8989;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            BooleanSearchEngine searchEngine = new BooleanSearchEngine(new File("pdfs"));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                // получаем строку запроса от клиента
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String query = in.readLine();
                System.out.println("Received query: " + query);

                // выполняем поиск в базе данных
                SearchResult result = (SearchResult) searchEngine.search(query);

                // преобразуем результаты поиска в JSON и отправляем обратно клиенту
                String json = gson.toJson(result);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(json);

                in.close();
                out.close();
                clientSocket.close();
                System.out.println("Client disconnected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void ProtocolHandler(InputStream inputStream, OutputStream outputStream) throws IOException {
        while (true) {
            // читаем запрос от клиента
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            String request = new String(buffer, 0, bytesRead);

            // обрабатываем запрос и получаем ответ
            String response = processRequest(request);

            // отправляем ответ клиенту
            outputStream.write(response.getBytes());
        }
    }

    private static String processRequest(String request) {
        // обрабатываем запрос и возвращаем ответ
        return "Response to " + request;
    }

    private static List<PageEntry> search(String word) {
        List<PageEntry> result = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> entry : index.entrySet()) {
            String pdfName = entry.getKey();
            List<Integer> pages = entry.getValue();

            for (int page : pages) {
                PageEntry pageEntry = PageEntry.getPageEntry(pdfName, page);

                for (String str : pageEntry.getText().split("\\s+")) {
                    if (str.toLowerCase().equals(word)) {
                        int index = result.indexOf(pageEntry);

                        if (index == -1) {
                            result.add(pageEntry);
                        } else {
                            result.get(index).incrementCount();
                        }

                        break;
                    }
                }
            }
        }

        Collections.sort(result);
        return result;
    }
}