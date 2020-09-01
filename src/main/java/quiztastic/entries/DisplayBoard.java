package quiztastic.entries;

import quiztastic.app.ListQuestionRepository;
import quiztastic.core.Board;
import quiztastic.domain.BoardController;
import quiztastic.domain.QuestionRepository;

public class DisplayBoard {
    private final BoardController boardController;

    public DisplayBoard(){
        QuestionRepository qRepo = new ListQuestionRepository();

        this.boardController = new BoardController(qRepo);
    }


    public void displayBoard(){
        Board board = this.boardController.makeBoard();
        System.out.println(board);
    }

    public static void main(String[] args){
        new DisplayBoard().displayBoard();
    }
}
