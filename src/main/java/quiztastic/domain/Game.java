package quiztastic.domain;

import quiztastic.core.Board;
import quiztastic.core.Category;
import quiztastic.core.Player;
import quiztastic.core.Question;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Game {
    private final int id;
    private final Board board;
    private final List<Answer> answers;
    private final List<Player> players;

    public Game(int id, Board board, List<Answer> answers, List<Player> players) {
        this.id = id;
        this.board = board;
        this.answers = answers;
        this.players = players;
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

    public void removePlayer(Player player) {
        synchronized (players) {
            players.remove(player);
        }
    }

    public void addPlayer(Player player) {
        synchronized (players) {
            players.add(player);
        }
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

    public void playRound(Player player, Interaction in) {

    }

    public class ActiveQuestion {
        private final Board.Index index;

        public ActiveQuestion(Board.Index index) {
            this.index = index;
        }

        public void answerQuestion(Player player, String answer) throws InvalidAnswer {
            Question q = getQuestion();
            boolean isCorrect = q.getAnswer().toLowerCase().equals(answer.toLowerCase());
            synchronized (answers) {
                answers.add(new Answer(index, player, isCorrect));
            }
            if (!isCorrect) {
                throw new InvalidAnswer(answer, q.getAnswer());
            }
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
        private final Boolean correct;

        public Answer(Board.Index index, Player player, Boolean correct) {
            this.index = index;
            this.player = player;
            this.correct = correct;
        }

        public boolean hasIndex(Board.Index index) {
            return this.index.equals(index);
        }
    }
}
