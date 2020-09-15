package quiztastic.ui;

import quiztastic.entries.JeopardyServer;

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
            out.println("what is your username?:");
            String username = in.readLine();
            try {
                if (JeopardyServer.addPlayers(username) || username.isEmpty()) {
                    serverBroadCast(username + " Is already in-game / not allowed");
                    serverBroadCast("Reconnect and try again.");
                    in.close();
                    out.close();
                } else {
                    serverBroadCast(username + " added to the game");
                    out.println("Hello " + username);
                    players.add(username);
                    clientUserName = username;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            while (true) {
                String userInput = in.readLine();
                if (userInput.startsWith("say")) {
                    int space = userInput.indexOf(" ");
                    if (space != -1) {
                        playerBroadCast(userInput.substring(space + 1));
                    }else if ((userInput.startsWith("!"))){
                        space = userInput.indexOf(" ");
                        String command = userInput.substring(space +1);
                        switch (command.toLowerCase()){
                            case "players":
                                getPlayersInLobby();
                                break;
                            case "start":
                                serverBroadCast("Starting game with: " + getPlayersInLobby() + " in the game...");
                                break;
                            default:
                                break;
                            }

                        }
                    }
                }
            }catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if (JeopardyServer.removePlayer(clientUserName)) {
                    serverBroadCast(clientUserName + " has left");
                    clientUserName = null;
                }
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void serverBroadCast(String substring) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.out.println("[SERVER] " + substring);
        }
    }

    private void playerBroadCast(String substring) {
        for (ClientHandler clientHandler : clients) {
            if (clientUserName == null) {
                clientUserName = "[SERVER]";
            } else {
                clientHandler.out.println(clientUserName + " Said: " + substring);
            }
        }

    }

    public Set<String> getPlayersInLobby() {
        Set<String> playersInLobby = new HashSet<>();
        playersInLobby = JeopardyServer.getPlayers();
        serverBroadCast("Current users in lobby: " + playersInLobby);

return playersInLobby;
    }

    public Set<String> getPlayers() {
        return players;
    }
}
