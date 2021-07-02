package Server;

import Client.ChatClient;

import java.io.*;
import java.net.Socket;

public class WriteThread implements Runnable{

    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;
    private ChatClient client;

    public WriteThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException e) {
            System.out.println("ERROR GETTING OUTPUT STREAM: " + e.getMessage());
            e.getStackTrace();
        }
    }

    @Override
    public void run() {



    }
}
