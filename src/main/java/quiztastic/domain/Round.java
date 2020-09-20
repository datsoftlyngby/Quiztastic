package quiztastic.domain;

import quiztastic.core.Board;
import quiztastic.core.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Round implements Runnable {
    private final Game game;
    private final Set<Player> activePlayers;
    private final Thread thread;

    private volatile Player roundPlayer = null;
    private volatile Game.Answer answer = null;
    private volatile Game.ActiveQuestion activeQuestion = null;
    private volatile Player buzzPlayer = null;

    private volatile CyclicBarrier playerBarrier = null;

    public Round(Game game, Set<Player> activePlayers) {
        this.game = game;
        this.activePlayers = activePlayers;
        this.thread = new Thread(this);
    }

    public Round(Game game) {
        this(game, new HashSet<>());
    }

    private Set<Player> getMissingPlayers() {
        synchronized (activePlayers) {
            HashSet<Player> missingPlayers = new HashSet<>(game.getPlayers());
            missingPlayers.removeAll(activePlayers);
            return missingPlayers;
        }
    }

    private Player addActivePlayerAndWaitForRound(
            Player player,
            InteractionHandler ih
            ) throws InterruptedException {

        synchronized (activePlayers) {
            if (roundPlayer != null) throw new RuntimeException("Game already started");
            activePlayers.add(player);
            activePlayers.notifyAll();
        }

        synchronized (activePlayers) {
            Set<Player> missingPlayers;
            while((missingPlayers = getMissingPlayers()).size() > 0) {
                ih.notifyWaitingForPlayers(missingPlayers);
                activePlayers.wait();
            }
        }

        synchronized (this) {
            while (roundPlayer == null) wait();
            return roundPlayer;
        }
    }

    public void notifyNumberOfPlayersUpdated() {
        synchronized (activePlayers) {
            activePlayers.notifyAll();
        }
    }

    private void chooseRoundPlayer() throws InterruptedException {
        synchronized (activePlayers) {
            while(activePlayers.size() < game.getPlayers().size()) {
                System.out.println(" - " + activePlayers + " < " + game.getPlayers());
                activePlayers.wait();
            }
            synchronized (this) {
                int index = new Random().nextInt(activePlayers.size());
                roundPlayer = List.copyOf(activePlayers).get(index);
                playerBarrier = new CyclicBarrier(activePlayers.size());
                notifyAll();
            }
        }
    }

    public void startRound() {
        this.thread.start();
    }

    @Override
    public void run() {
        try {
            chooseRoundPlayer();
            Game.ActiveQuestion aq = waitForActiveQuestion();
            Thread.sleep(30000);
            endGame(aq.timeoutQuestion());
        } catch (InterruptedException e) { }
    }

    public void play(Player player, InteractionHandler ih) throws InterruptedException{
        Player roundPlayer = addActivePlayerAndWaitForRound(player, ih);
        ih.notifyRoundPlayerSelected(roundPlayer);

        if (player.equals(roundPlayer)) {
            Board.Index index = ih.chooseBoardIndex();
            setActiveQuestion(game.selectQuestion(index));
        }

        Game.ActiveQuestion aq = waitForActiveQuestion();
        ih.notifyQuestionSelected(aq);

        while (!done()) {

            Thread t = new Thread(() -> {
                ih.waitForBuzz();
                setBuzzer(player);
            });
            t.start();

            Player buzzPlayer = waitForBuzzer();
            t.interrupt();

            if (buzzPlayer != null) {
                ih.notifyBuzzPlayer(buzzPlayer);
            }
            if (player.equals(buzzPlayer)) {
                String playerAnswer = ih.getAnswerToQuestion();
                setAnswer(aq.answerQuestion(player, playerAnswer));
                resetBuzzer();
            }

            Game.Answer answer = waitForAnswer();
            ih.notifyAnswer(answer);

            if (player.equals(buzzPlayer) && !done()) {
                this.resetAnswer();
            }

            waitForPlayers();
        }
    }

    private synchronized void resetBuzzer() {
        buzzPlayer = null;
        playerBarrier.reset();
    }

    private synchronized void resetAnswer() {
        answer = null;
    }

    private void waitForPlayers() throws InterruptedException {
        try {
            playerBarrier.await();
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean done() {
        return answer != null && answer.isCorrect();
    }

    public synchronized void setAnswer(Game.Answer answer) {
        if (!done()) {
            this.answer = answer;
            game.addAnswer(answer);
            notifyAll();
        }
    }

    private synchronized Game.Answer waitForAnswer() throws InterruptedException {
        while (answer == null) wait();
        return this.answer;
    }

    private synchronized void endGame(Game.Answer timeout) {
        if (!done()) {
            setAnswer(timeout);
        }
    }

    private synchronized Game.ActiveQuestion waitForActiveQuestion() throws InterruptedException {
         while (activeQuestion == null) wait();
         return activeQuestion;
    }

    public synchronized void setActiveQuestion(Game.ActiveQuestion activeQuestion) {
        this.activeQuestion = activeQuestion;
        notifyAll();
    }

    private synchronized void setBuzzer(Player player) {
        if (buzzPlayer == null) {
            buzzPlayer = player;
            notifyAll();
        }
    }

    public synchronized Player waitForBuzzer() throws InterruptedException {
        while (buzzPlayer == null && !done()) {
            wait();
        }
        return buzzPlayer;
    }

    public static interface InteractionHandler {
        void notifyWaitingForPlayers(Set<Player> players);
        void notifyRoundPlayerSelected(Player roundPlayer);
        void notifyQuestionSelected(Game.ActiveQuestion question);
        void notifyBuzzPlayer(Player buzzPlayer);
        void notifyAnswer(Game.Answer answer);

        Board.Index chooseBoardIndex();
        String getAnswerToQuestion();
        void waitForBuzz();

    }
}
