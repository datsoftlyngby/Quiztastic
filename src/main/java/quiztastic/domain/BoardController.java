package quiztastic.domain;

import quiztastic.core.Board;
import quiztastic.core.Category;
import quiztastic.core.Question;

import java.util.List;

public class BoardController {

    private final QuestionRepository qRepo;
    public final Sampler sampler;

    public BoardController(QuestionRepository qRepo){
        this.qRepo = qRepo;
        this.sampler = null;
    }

    public Board.Group makeGroup(Category c){
        List<Question> questionList = qRepo.getQuestionsWithCategory(c);
        List<Question> sampledQuestions = sampler.sample(questionList,5);
       return new Board.Group(c,sampledQuestions);
    }

    public Board makeBoard() {
        return null;
    }
}
