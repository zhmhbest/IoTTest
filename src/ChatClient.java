import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class ChatClientThread extends Thread {
    public boolean loop = true;
    private BufferedReader reader;

    public ChatClientThread(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        while (loop) {
            try {
                // 实时输出服务端消息
                System.out.println(reader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

public class ChatClient {
    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1";
        int port = 1103;

        Socket socket = new Socket(host, port);
        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        BufferedReader reader = new BufferedReader(inputStreamReader);
        PrintWriter writer = new PrintWriter(outputStreamWriter);

        ChatClientThread thread = new ChatClientThread(reader);
        thread.start();
        Scanner scanner = new Scanner(System.in);
        while (thread.loop) {
            String line = scanner.nextLine();
            writer.println(line);
            writer.flush();
            if (line.equals("bye")) {
                thread.loop = false;
            }
        }
    }
}
