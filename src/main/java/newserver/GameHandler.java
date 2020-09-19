package newserver;

public class
GameHandler extends Thread {
    private final Server server;
    private final Socket socket;
    private String name;
    private final ClientHandler handler;
    private final BlockingQueue<String> messageQueue;

    public GameHandler(Server server, String name) {
        this.server = server;
        this.name = name;
    }
}
