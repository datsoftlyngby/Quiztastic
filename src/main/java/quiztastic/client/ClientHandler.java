package quiztastic.client;

import quiztastic.server.JeopardyServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClientHandler implements Runnable {
    private Set<String> players = new HashSet<>();
    private String clientUserName;
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<ClientHandler> clients;


    public ClientHandler(Socket ClientSocklet, ArrayList<ClientHandler> clients, Set<String> players) throws IOException {
        this.players = players;
        this.client = ClientSocklet;
        this.clients = clients;
        in = new BufferedReader((new InputStreamReader(client.getInputStream())));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String userInput = in.readLine();
                if (userInput.startsWith("say")) {
                    int space = userInput.indexOf(" ");
                    if (space != -1) {
                        broadCast(userInput.substring(space + 1));
                    }

                } else if (userInput.contains("play")) {
                    out.println("Whats your username?");
                    String username = in.readLine();
                    try {
                        if (JeopardyServer.addPlayers(username)) {
                            broadCast(username + " Is already in-game");
                        } else {
                            broadCast(username + " added to the game");
                            out.println("Hello " + username);
                            players.add(username);
                            clientUserName = username;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IO ERROR ");
        } finally {
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadCast(String substring) {
        for (ClientHandler clientHandler : clients) {
            if (clientUserName == null) {
                clientUserName = "";
            } else {
                clientHandler.out.println(clientUserName + " Said: " + substring);
            }
        }

    }

    public Set<String> getPlayers() {
        return players;
    }
}
