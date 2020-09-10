package quiztastic.entries;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TUIServer {
    private static int port = 4200;
    private final ServerSocket socket;

    public TUIServer(ServerSocket socket) {
        this.socket = socket;
    }

    public static TUIServer startServer() throws IOException {
        ServerSocket socket = new ServerSocket(port);
        return new TUIServer(socket);
    }

    public static void main(String[] args) throws IOException {
        TUIServer server = null;
        try {
            server = startServer();
            while (true)
                server.openPort();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null)
                server.close();
        }
    }

    private void close() throws IOException {
        this.socket.close();
        Thread thread = new Thread(new )
    }

    private void openPort() throws IOException {
        Socket openPort = this.socket.accept();

    }
}
