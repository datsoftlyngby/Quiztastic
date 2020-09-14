package quiztastic.entries;

import quiztastic.ui.Protocol;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

class ServerUI implements Runnable {
    final static int port = 3400;
    private final Socket socket;

    public ServerUI(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println(socket.getInetAddress() + " has connected.");
            Thread thread = new Thread(new ServerUI(socket));
            thread.start();
        }
    }

    @Override
    public void run() {
        try {
            Protocol p = new Protocol(new Scanner(socket.getInputStream()), new PrintWriter(socket.getOutputStream()));
            p.run();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}