package quiztastic.domain;

import quiztastic.core.Board;
import quiztastic.core.Category;
import quiztastic.core.Question;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Game {
    private final int id;
    private final Board board;
    private final List<Answer> answers;

    public Game(int id, Board board, List<Answer> answers) {
        this.id = id;
        this.board = board;
        this.answers = answers;
    }

    public Game(int id, Board board) {
        this(id, board, new ArrayList<>());
    }

    public List<Category> getCategories() {
        return this.board.getGroups()
                .stream()
                .map(Board.Group::getCategory)
                .collect(Collectors.toList());
    }

    public boolean isAnswered(Board.Index index) {
        return answers.stream().anyMatch(a -> a.hasIndex(index));
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

    public class ActiveQuestion {
        private final Board.Index index;

        public ActiveQuestion(Board.Index index) {
            this.index = index;
        }

        public void answerQuestion(String answer) throws InvalidAnswer {
            Question q = getQuestion();
            answers.add(new Answer(index));
            if (!q.getAnswer().toLowerCase().equals(answer.toLowerCase())) {
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

        public Answer(Board.Index index) {
            this.index = index;
        }

        public boolean hasIndex(Board.Index index) {
            return this.index.equals(index);
        }
    }
}
