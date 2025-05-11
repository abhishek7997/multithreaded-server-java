import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService threadPool;

    public Server(int poolSize) {
        this.threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public void handleClient(Socket clientSocket) {
        try (PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            System.out.println("Request received is " + fromClient.readLine());
            String response = Files.readString(Paths.get("data.json"));
            toClient.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final int poolSize = 10;
        final int port = 8010;

        Server server = new Server(poolSize);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(10000); // set connection timeout to 10s
            while (true) {
                System.out.println("Server listening on port : " + port);

                Socket clientSocket = serverSocket.accept();

                System.out.println("Connection accepted from client : " + clientSocket.getRemoteSocketAddress());
                server.threadPool.execute(() -> server.handleClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.threadPool.shutdown();
        }
    }
}
