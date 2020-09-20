package quiztastic.entries;

import quiztastic.ui.Protocol;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TUIServer implements Closeable {

    private static final int PORT = 6666;
    private final ServerSocket serverSocket;

    public TUIServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static TUIServer startServer(int port) throws IOException{
        String serverIP = InetAddress.getLocalHost().getHostAddress();
        System.out.println("Server ip: " + serverIP);

        ServerSocket serverSocket = new ServerSocket(port);
        return new TUIServer(serverSocket);
    }

    public static void main(String[] args) throws IOException {
        TUIServer server = null;
        try {
            server = TUIServer.startServer(PORT);
            while (true) server.listen();
        } finally {
            if (server != null) server.close();
        }
    }

    private void listen() throws IOException {
        Socket openSocket = serverSocket.accept();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Server accepts requests at: " + openSocket);
                    Protocol p = new Protocol(openSocket.getInputStream(),
                            new PrintWriter(openSocket.getOutputStream()));
                    p.run();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        openSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }
}
