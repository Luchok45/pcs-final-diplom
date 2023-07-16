import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
public class Client {
    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 8989);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                Scanner scanner = new Scanner(System.in);
        ) {
            while (true) {
                System.out.print("Введите слово для поиска (или 'exit' для выхода): ");
                String word = scanner.nextLine();

                if (word.equalsIgnoreCase("exit")) {
                    break;
                }

                out.println(word);
                out.flush();

                String jsonResponse = in.readLine();
                Gson gson = new Gson();
                List<PageEntry> results = gson.fromJson(jsonResponse, new TypeToken<List<PageEntry>>() {}.getType());
                for (PageEntry  result : results) {
                    System.out.println(result);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при подключении к серверу");
            e.printStackTrace();
        }
    }
}
