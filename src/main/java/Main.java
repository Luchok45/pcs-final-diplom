import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.javalin.Javalin;
import com.google.gson.Gson;
import org.bouncycastle.asn1.ocsp.Request;
import org.eclipse.jetty.client.api.Response;

import javax.naming.directory.SearchResult;

public class Main {
    private static Map<String, List<Integer>> index = new HashMap<>();

    private static final int PORT = 8989;
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", PORT);
             InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {

            // отправляем запрос на сервер
            String query = "java programming";
            outputStream.write(query.getBytes());

            // читаем ответ от сервера
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            String response = new String(buffer, 0, bytesRead);
            System.out.println("Response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


