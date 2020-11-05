import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class ChatServerThread extends Thread {
    private Set<Socket> clients;
    private Socket client;

    public ChatServerThread(Socket client, Set<Socket> clients) {
        this.client = client;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            process();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process() throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(client.getOutputStream());
        BufferedReader reader = new BufferedReader(inputStreamReader);
        PrintWriter writer = new PrintWriter(outputStreamWriter);

        // 设置用户名
        writer.println("Welcome, please enter your name: ");
        writer.flush();
        String userName = reader.readLine();
        writer.printf("Your name is [%s]\n", userName);
        writer.flush();
        ChatServer.sendAll(clients, client, String.format("System: %s joined!", userName));

        // 交流
        String input = reader.readLine();
        while (!input.equals("bye")) {
            String msg = String.format("%s: %s", userName, input);
            ChatServer.sendAll(clients, client, msg);
            writer.println("System: accepted"); writer.flush();
            input = reader.readLine();
        }
        writer.println("bye");
        writer.flush();
        ChatServer.sendAll(clients, client, String.format("System: %s bye!", userName));
        client.close();
        clients.remove(client);
    }
}

public class ChatServer {

    public static void sendAll(Set<Socket> clients, Socket except, String msg) {
        Iterator<Socket> iterator = clients.iterator();
        while (iterator.hasNext()) {
            Socket socket = iterator.next();
            if (socket != except) {
                try {
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                    writer.println(msg);
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(msg);
    }

    public static void main(String[] args) throws IOException {
        int port = 1103;
        Set<Socket> clients = new HashSet<>();

        ServerSocket server = new ServerSocket(port);
        System.out.printf("Listen %d\n", port);
        while (true) {
            Socket socket = server.accept();
            clients.add(socket);
            System.out.printf("Accept %s\n", socket.getRemoteSocketAddress().toString());
            new ChatServerThread(socket, clients).start();
        }
    }

}