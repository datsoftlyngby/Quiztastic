package quiztastic.server;

import quiztastic.client.Client;
import quiztastic.client.ClientHandler;
import quiztastic.core.Player;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class JeopardyServer {
    private static int PORT = 3400;
    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private static Set<String> players = new HashSet<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(6);
    private static int threadCount = 1;

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);
        System.out.println("[SERVER] listeing on port: " + PORT);
        while (true) {
            Socket client = listener.accept();
            InetAddress info;
            info = client.getInetAddress();
            System.out.println("[SERVER] Client has connected " + client.getInetAddress());
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            out.println("Welcome to Team JUMBO SNEGL SERVER");
            out.println("PLEASE RUN USING JEOPARDY CLIENT FOR BETTER RESULTS");
            out.println(info.getAddress() + " // " + info.getHostName() + " // " + info.getHostAddress() + " // " + PORT + "]");
            ClientHandler clientThread = new ClientHandler(client, clientHandlers,players);
            clientHandlers.add(clientThread);
            pool.execute(clientThread);
            if (pool instanceof ThreadPoolExecutor) {
                threadCount = ((ThreadPoolExecutor) pool).getActiveCount();
                out.println("running clients: " + threadCount);
                try {
                    out.println("Current players: "+ getPlayers());
                }catch (Exception e){
                    e.printStackTrace();
                }

            }


        }
    }



    public static Set<String> getPlayers() {
        return players;
    }

    public static boolean addPlayers(String player) {
        if (players.contains(player))
        {
            System.out.println("[SERVER] User already in party");
            return true;
        }
        else {
            players.add(player);
            System.out.println("[SERVER] User ADDED to party");
            return false;
        }
    }
}
