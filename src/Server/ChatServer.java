package Server;

import Client.UserThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();
    private final ExecutorService pool;
    private final ServerSocket serverSocket;

    public ChatServer(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        pool = Executors.newCachedThreadPool();
    }

    public void execute() throws IOException {
        try(serverSocket) {
            System.out.println("SERVER LISTENING ON PORT: " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");

                UserThread newUser = new UserThread(socket, this);
                pool.submit(newUser);
                userThreads.add(newUser);
                //newUser.start();
            }
        }
    }

    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.out.println("Syntax: java ChatServer <port-number>");
            System.exit(0);
        }

        int port = Integer.parseInt(args[0]);

        ChatServer server = new ChatServer(port);
        server.execute();
    }

    public void broadcast(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    public void broadcast(String userToWhisper, String message, UserThread excludedUser) {
            for (UserThread aUser : userThreads) {
                if (aUser != excludedUser && aUser.getName().equals(userToWhisper)) {
                    aUser.sendMessage(message);
                }
            }
    }

    public void addUserName(String userName) {
        userNames.add(userName);
        System.out.println("USERNAME: " + userName);
    }

    public void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("USER: " + userName + " has quitted");
        }
    }

    public boolean hasUsers() {
        return !this.userNames.isEmpty();
    }

    public Set<String> getUserNames() {
        return this.userNames;
    }
}
