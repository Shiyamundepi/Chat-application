import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    private static int clientCount = 0;
    private static List<PrintWriter> clientWriters = new ArrayList<>();
    private static List<String> clientNicknames = new ArrayList<>();

    public static void startServer(String host, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on " + host + " at port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientCount++;
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast message to all connected clients
    private static void broadcast(String message) {
        for (PrintWriter out : clientWriters) {
            out.println(message);
        }
    }

    // Handle communication with the client
    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String nickname;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Send prompt for nickname
                out.println("NICK");

                // Receive the client's nickname
                nickname = in.readLine();
                clientNicknames.add(nickname);
                clientWriters.add(out);
                System.out.println("Nickname of the client is " + nickname);

                // Broadcast that the client has joined
                broadcast(nickname + " has joined the chat");

                String message;
                while ((message = in.readLine()) != null) {
                    broadcast(nickname + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                    clientWriters.remove(out);
                    clientNicknames.remove(nickname);
                    broadcast(nickname + " has left the chat");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        // Dynamically get local IP address to host server
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Server is hosted at IP: " + host);
            startServer(host, 7676);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}