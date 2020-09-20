package newserver;

import quiztastic.core.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
    private static int PORT = 3400;
    private final ServerSocket socket;
    private final List<GameHandler> clients;


    public Server(ServerSocket socket, List<GameHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }
    public Server(ServerSocket socket){
        this(socket,new ArrayList<>());
    }

    public static void main(String[] args) {

    }

    @Override
    public void run() {
        System.out.println("[SERVER] listing to port " +PORT);
        try {
            while (true){
                GameHandler handler = new GameHandler(this,socket.accept(),String);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

