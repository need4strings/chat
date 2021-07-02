package Client;

import Server.ChatServer;

import java.io.*;
import java.net.Socket;

public class UserThread implements Runnable {

    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;
    private String name;

    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            writer.println("What is your name?");
            String userName = reader.readLine();
            name = userName;
            server.addUserName(userName);

            String serverMessage = "New user connected with username: " + userName + "! Be nice!";
            server.broadcast(serverMessage, this);

            String clientMessage;

            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]" + clientMessage;

                if (clientMessage.equals("/list")) {
                    printUsers();
                } else if (clientMessage.split(" ")[0].equals("/whisper")) {
                    String[] whisperArray = clientMessage.split(" ");
                    String userToWhisperTo = whisperArray[1];
                    server.broadcast(userToWhisperTo, clientMessage, this);
                    continue;
                }

                server.broadcast(serverMessage, this);
            } while(!clientMessage.equals("/quit"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has quitted";
            server.broadcast(serverMessage, this);

        } catch (IOException e) {
            System.err.println("ERROR IN UserThread: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public void printUsers() {
        if (server.hasUsers()) {
            writer.println("USERS ONLINE: " + server.getUserNames());
        } else {
            writer.println("NO OTHER USERS ONLINE");
        }
    }

    public String getName() {
        return this.name;
    }
}
