package quiztastic.app;

import quiztastic.core.Category;
import quiztastic.core.Question;
import quiztastic.domain.QuestionRepository;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapQuestionRepository implements QuestionRepository {
    private final HashMap<Category, List<Question>> questionsByCategory;

    public MapQuestionRepository(HashMap<Category, List<Question>> questionsByCategory) {
        this.questionsByCategory = questionsByCategory;
    }

    public static MapQuestionRepository fromQuestionReader(QuestionReader reader) throws IOException, ParseException {
        reader.readQuestion();
        //throw new UnsupportedOperationException("Not implemented yet!");
        return new MapQuestionRepository(new HashMap<>());
    }

    @Override
    public List<Category> getCategories() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public List<Question> getQuestionsWithCategory(Category category) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public Iterable<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();
        for (List<Question> l : questionsByCategory.values()) {
            questions.addAll(l);
        }
        return questions;
    }
}
