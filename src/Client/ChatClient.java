package Client;

import Server.ReadThread;
import Server.WriteThread;

import java.io.*;
import java.net.Socket;

public class ChatClient {

    private String hostname;
    private int port;
    private String userName;
    private PrintWriter writer;

    public ChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);

            System.out.println("connected to the chat server");

            new ReadThread(socket, this).start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            writer = new PrintWriter(socket.getOutputStream(), true);

            try {
                String userName = reader.readLine();
                writer.println(userName);

                while (true) {
                    writer.println(reader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.getMessage();
        }
    }

    public static void main(String[] args) {

        if (args.length < 2) {
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        ChatClient client = new ChatClient(hostname, port);
        client.execute();
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
