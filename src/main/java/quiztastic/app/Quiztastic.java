package quiztastic.app;

import quiztastic.core.Board;
import quiztastic.core.Question;
import quiztastic.domain.BoardFactory;
import quiztastic.domain.Game;
import quiztastic.domain.QuestionRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

public class Quiztastic {
    private static Quiztastic instance;

    public static Quiztastic getInstance() {
        if (instance == null) {
            InputStream s = Quiztastic.class
                    .getClassLoader()
                    .getResourceAsStream("master_season1-35clean.tsv");
            QuestionReader reader = new QuestionReader(new InputStreamReader(s));
            try {
                instance = makeAPI(reader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    private final QuestionRepository questions;
    private final Game game;

    private Quiztastic(QuestionRepository questions, Game game) {
        this.questions = questions;
        this.game = game;
    }

    private static Quiztastic makeAPI(QuestionReader reader) throws IOException, ParseException {
        QuestionRepository repo = MapQuestionRepository.fromQuestionReader(reader);
        return new Quiztastic(repo, new Game(0, new BoardFactory(repo).makeBoard()));
    }

    public Iterable<Question> getQuestions() {
        return questions.getQuestions();
    }

    public Board getBoard() {
        return new BoardFactory(questions).makeBoard();
    }

    public Game getCurrentGame() {
        return game;
    }

}
