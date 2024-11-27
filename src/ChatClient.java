import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {

    private String nickname;
    private String host;
    private int port = 7676;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ChatClient(String nickname, String host) throws IOException {
        this.nickname = nickname;
        this.host = host;
        socket = new Socket(host, port);  // Connect to the server
        out = new PrintWriter(socket.getOutputStream(), true);  // For sending messages
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  // For receiving messages
    }

    // Receive messages from the server
    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);  // Print the received message
            }
        } catch (IOException e) {
            System.out.println("Error receiving message.");
        }
    }

    // Send messages to the server
    private void sendMessages() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.nextLine();
            out.println(message);  // Send the message to the server
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the server's IP address: ");
        String host = scanner.nextLine();
        System.out.println("Enter your nickname: ");
        String nickname = scanner.nextLine();

        try {
            // Create a ChatClient instance and send the nickname to the server
            ChatClient client = new ChatClient(nickname, host);
            client.out.println(nickname);  // Send nickname to the server

            // Start a thread to listen for incoming messages from the server
            new Thread(() -> {
                client.receiveMessages();
            }).start();

            // Start a thread to send messages to the server
            client.sendMessages();
        } catch (IOException e) {
            System.out.println("Error connecting to the server.");
            e.printStackTrace();
        }
    }
}