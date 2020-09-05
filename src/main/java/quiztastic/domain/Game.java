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

    public boolean isAnswered(int category, int number) {
        return answers.stream().anyMatch(a -> a.hasIndex(category, number));
    }

    public ActiveQuestion selectQuestion(int category, int number) {
        if (!isAnswered(category, number)) {
            return new ActiveQuestion(category, number);
        } else {
            throw new InvalidParameterException("Questions is already answered");
        }
    }

    private Question getQuestion(int category, int number) {
        return board.getGroups().get(category).getQuestions().get(number);
    }

    public class ActiveQuestion {
        private final int category;
        private final int number;

        public ActiveQuestion(int category, int number) {
            this.category = category;
            this.number = number;
        }

        public void answerQuestion(String answer) throws InvalidAnswer {
            Question q = getQuestion();
            answers.add(new Answer(category, number));
            if (!q.getAnswer().toLowerCase().equals(answer.toLowerCase())) {
                throw new InvalidAnswer(answer, q.getAnswer());
            }
        }

        public String getQuestionText() {
            return getQuestion().getQuestion();
        }

        private Question getQuestion () {
            return Game.this.getQuestion(category, number);
        }
    }

    public static class Answer {
        private final int category;
        private final int number;

        public Answer(int category, int number) {
            this.category = category;
            this.number = number;
        }

        public boolean hasIndex(int category, int number) {
            return this.category == category && this.number == number;
        }
    }
}
