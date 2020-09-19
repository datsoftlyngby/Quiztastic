package WipServer;

import quiztastic.core.Player;
import quiztastic.ui.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ClientHandler extends Thread {
    private Set<String> players; //temp player info
    private Player clientUserName; //clients - players username
    private final BufferedReader in;
    private final PrintWriter out;
    private final ArrayList<ClientHandler> clients; //todo
    private volatile int waitingFor = 0;


    public ClientHandler(Socket ClientSocklet, ArrayList<ClientHandler> clients, Set<String> players) throws IOException { //consructor
        this.players = players;
        this.clients = clients;
        in = new BufferedReader((new InputStreamReader(ClientSocklet.getInputStream())));
        out = new PrintWriter(ClientSocklet.getOutputStream(), true);
    }

    @Override
    public synchronized void run() { //thread start for client
        try {
            out.println("what is your username?:");
            out.flush();
            String username = in.readLine(); //check if username is allowed / enabled < - >
            try {
                if (JeopardyServer.addPlayers(username) || username.isEmpty() || username.toLowerCase().contains("anus")) {
                    out.println(username + " Is already in-game / not allowed");
                    out.println("Reconnect and try again.");
                    in.close();
                    out.close(); //dc client if username not allowed  - available todo fix change so it doesnt dc the person lol
                } else {
                    serverBroadCast(username + " added to the game");
                    serverBroadCast("Current users in lobby: " + getPlayersInLobby());
                    players.add(username);
                    clientUserName = new Player(username);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            while (true) {
                synchronized (this) {
                    out.println("Jeopardy MENU:");
                    out.println("'1'. Play \n'2'. Show players");
                    String userInput = in.readLine();
                    switch (userInput) {
                        case "1":
                            waitForB(this.clientUserName);
                            serverBroadCast("Game starting... First player to chose category is: " + clientUserName.toString());
                            Protocol p = new Protocol(in, out, clientUserName);
                            break;
                        case "2":
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (JeopardyServer.removePlayer(clientUserName.toString())) {
                    removePlayer(clientUserName);
                    serverBroadCast(clientUserName + " has left");
                    getPlayersInLobby();
                    clientUserName = null;
                }
                out.close();
                in.close();
                removePlayer(clientUserName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void waitForB(Player client) throws InterruptedException {
       boolean i = false;
       synchronized (this){
           if (waitingFor == 0){
               i = true;
               waitingFor = 3;
               serverBroadCast("waiting for " + waitingFor);
           }
       }
       serverBroadCast("waiting for players");
       synchronized (this){
           waitingFor --;
           serverBroadCast(" < >" +waitingFor);
           if (waitingFor == 0){
               this.notifyAll();
               serverBroadCast("Released");
           }else {
               while (waitingFor > 0){
                   this.wait();
               }
           }
       }

    }


    public void serverBroadCast(String substring) { //server broadcast
        for (ClientHandler clientHandler : clients) {
            clientHandler.out.println("[SERVER] " + substring);
        }
    }


    public Set<String> getPlayersInLobby() { //gets players on server
        players = JeopardyServer.getPlayers();
        return players;
    }

    public void removePlayer(Player player) { //remove players
        players.remove(player);
        JeopardyServer.removePlayer(player.toString());
    }

}
