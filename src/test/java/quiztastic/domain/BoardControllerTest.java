package quiztastic.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import quiztastic.core.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BoardControllerTest {
    private static int counter;
    private final HashMap<Integer, Player> playerLobby = new HashMap<>();
    Player player = new Player(1, "1", 200);
    Player player2 = new Player(2, "2", 500);
    Player player5 = new Player(4, "3", 400);
    Player player4 = new Player(2, "4", 500);
    Player player6 = new Player(2, "5", 500);

    @Test
    void setUp() {
        naMe(player);
        naMe(player2);
        naMe(player4);
        naMe(player5);
        naMe(player6);
    }

    void naMe(Player x) {
        counter = playerLobby.size();
        if (playerLobby.size() >= 4) {
            System.out.println("Lobby is full");
            printLobby();
        }else {
            if (playerLobby.containsKey(counter)) {
                System.out.println("Player " + x.getUserName() + " Already here ");
            } else {
                playerLobby.put(counter, x);
                System.out.println("Player " + x.getUserName() + " added ");
                counter++;
            }
        }

    }

    private void printLobby() {
        System.out.println(playerLobby.values());

    }
}