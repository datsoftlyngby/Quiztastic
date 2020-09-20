package quiztastic.domain;

import quiztastic.core.Board;
import quiztastic.core.Category;
import quiztastic.core.Player;
import quiztastic.core.Question;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

public class Game {
    private final int id;
    private final Board board;
    private final List<Answer> answers;
    private final List<Player> players;
    private Round activeRound;

    public Game(int id, Board board, List<Answer> answers, List<Player> players) {
        this.id = id;
        this.board = board;
        this.answers = answers;
        this.players = players;
        this.activeRound = null;
    }

    public Game(int id, Board board) {
        this(id, board, new ArrayList<>(), new ArrayList<>());
    }

    public List<Category> getCategories() {
        return this.board.getGroups()
                .stream()
                .map(Board.Group::getCategory)
                .collect(Collectors.toList());
    }

    public boolean isAnswered(Board.Index index) {
        synchronized (answers) {
            return answers.stream().anyMatch(a -> a.hasIndex(index));
        }
    }

    public ActiveQuestion selectQuestion(Board.Index index) {
        if (!isAnswered(index)) {
            return new ActiveQuestion(index);
        } else {
            throw new InvalidParameterException("Questions is already answered");
        }
    }

    private Question getQuestion(Board.Index index) {
        return board.getQuestion(index);
    }

    public synchronized void removePlayer(Player player) {
        players.remove(player);
        if (activeRound != null) activeRound.notifyNumberOfPlayersUpdated();
    }

    public synchronized void addPlayer(Player player) {
        if (players.contains(player)) {
            throw new InvalidParameterException("Player already exist");
        }
        players.add(player);
        if (activeRound != null) activeRound.notifyNumberOfPlayersUpdated();
    }

    public synchronized Round getActiveRound() {
        if (activeRound == null || activeRound.done()) {
            activeRound = new Round(this);
            activeRound.startRound();
        }
        return activeRound;
    }

    public Map<Player, Integer> getScores () {
        Map<Player, Integer> scores = new HashMap<>();

        // Give all players 0 scores
        synchronized (players) {
            for (Player p : players) {
                scores.put(p, 0);
            }
        }

        // Read all the correct answers given and compute add the score
        // if the player exist.
        synchronized (answers) {
            for (Answer a : answers) {
                if (!a.correct) continue;
                scores.computeIfPresent(a.player,
                        (k, v) -> v + a.index.getScoreValue());
            }
        }

        return scores;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public synchronized void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public class ActiveQuestion {
        private final Board.Index index;

        private ActiveQuestion(Board.Index index) {
            this.index = index;
        }

        public Answer timeoutQuestion() {
            return new Answer(index, null, getQuestion().getAnswer(), true);
        }

        public Answer answerQuestion(Player player, String answer) {
            Question q = getQuestion();
            boolean isCorrect = q.getAnswer().toLowerCase().equals(answer.toLowerCase());
            return new Answer(index, player, answer, isCorrect);
        }

        public String getQuestionText() {
            return getQuestion().getQuestion();
        }

        private Question getQuestion () {
            return Game.this.getQuestion(index);
        }
    }

    public static class Answer {
        private final Board.Index index;
        private final Player player;
        private final String answer;
        private final Boolean correct;

        public Answer(Board.Index index, Player player, String answer, Boolean correct) {
            this.index = index;
            this.player = player;
            this.answer = answer;
            this.correct = correct;
        }

        public boolean hasIndex(Board.Index index) {
            return this.index.equals(index);
        }

        @Override
        public String toString() {
            return "Answer{" +
                    "index=" + index +
                    ", player=" + player +
                    ", answer='" + answer + '\'' +
                    ", correct=" + correct +
                    '}';
        }

        public boolean isCorrect() {
            return correct;
        }

        public Player getPlayer() {
            return player;
        }

        public Board.Index getIndex() {
            return index;
        }

        public String getAnswer() {
            return answer;
        }
    }
}
