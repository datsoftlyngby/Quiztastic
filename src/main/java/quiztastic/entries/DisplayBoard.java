package quiztastic.entries;

import quiztastic.app.MapQuestionRepository;
import quiztastic.app.QuestionReader;
import quiztastic.app.Quiztastic;
import quiztastic.core.Board;
import quiztastic.domain.BoardController;
import quiztastic.domain.QuestionRepository;

import java.io.InputStream;
import java.io.InputStreamReader;

public class DisplayBoard {
    public static void main(String[] args) {
        Quiztastic quiz = Quiztastic.getInstance();
        System.out.println(quiz.getBoard());
    }
}
