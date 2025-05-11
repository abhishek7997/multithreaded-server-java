import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable {
    private static final int port = 8010;

    @Override
    public void run() {
        try {
            InetAddress address = InetAddress.getByName("localhost");
            Socket socket = new Socket(address, port); // create a socket at client side which will connect to the server
            PrintWriter toSocket = new PrintWriter(socket.getOutputStream(), true); // write data into the output stream of the client side socket, crucial to set autoFlush to true, otherwise printWriter will remain stuck at println()
            BufferedReader fromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream())); // read data present in the socket

            final String request = "{\"message\":\"hello from client side.\"}";

            try (socket; fromSocket; toSocket) {
                toSocket.println(request);
                String response = fromSocket.readLine();
                System.out.println("Response from server: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        for(int i=0;i<100;i++) {
            Thread thread = new Thread(client);
            thread.start();
        }
    }
}
