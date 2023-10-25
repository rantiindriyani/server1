import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPEchoServer {
    private static int numConnections;

    public static void main(String[] args) {
        final int PORT = 12345;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server berjalan dan menunggu koneksi di port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ClientHandler implements Runnable {
    private static int numConnections;
    private int connectionId = 0;
    private Socket link;

    public ClientHandler(Socket socket) {
        connectionId = numConnections++;
        System.out.println("Melayani koneksi ke-" + connectionId);
        link = socket;
    }

    public void run() {
        PrintWriter out = null;
        BufferedReader in = null;
        int numMessages = 0;

        try {
            out = new PrintWriter(link.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(link.getInputStream()));

            String message = in.readLine();

            while (message != null && !message.equals("close")) {
                System.out.println("Pesan diterima: [" + message + "] dari client " + connectionId +
                        " dalam " + message.length() + " bytes");
                numMessages++;

                out.println("Isi Pesan " + numMessages + ": " + message);
                message = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }

                if (in != null) {
                    in.close();
                }

                link.close();
                System.out.println("Menutup koneksi, #" + connectionId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}