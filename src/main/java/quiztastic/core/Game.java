package quiztastic.core;

import java.util.ArrayList;
import java.util.List;

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

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }
}
