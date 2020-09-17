package quiztastic.domain;

import quiztastic.core.Board;
import quiztastic.core.Player;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class Round {
    private final Set<Player> activePlayers;
    private volatile Player roundPlayer;
    private volatile Board.Index activeQuestion;

    public Round(Set<Player> players, Set<Player> activePlayers) {
        this.players = Set.copyOf(players);
        this.activePlayers = activePlayers;
    }

    public void play(Player player, RoundInteractionHandler in) throws InterruptedException {
        addActivePlayer(player);

        Player roundPlayer = waitForRoundPlayer();
        in.roundPlayerSelected(roundPlayer);

        if (roundPlayer == player) {
            Board.Index index = in.chooseBoardIndex();
            setActiveQuestion(index);
        }

        Board.Index question = waitForActiveQuestion();
        in.questionSelected(question);

        in.getBuzz(5000);
    }

    private synchronized Player waitForRoundPlayer() throws InterruptedException {
        while (roundPlayer == null) wait();
        return roundPlayer;
    }

    private synchronized Board.Index waitForActiveQuestion() throws InterruptedException {
        while (activeQuestion == null) wait();
        return activeQuestion;
    }

    private void addActivePlayer(Player player) {
        synchronized (activePlayers) {
            activePlayers.add(player);
        }
    }

    public synchronized void setActiveQuestion(Board.Index activeQuestion) {
        this.activeQuestion = activeQuestion;
        notifyAll();
    }
}
