package quiztastic.app;

import quiztastic.core.Category;
import quiztastic.core.Question;
import quiztastic.domain.QuestionRepository;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapQuestionRepository implements QuestionRepository {
    private final HashMap<Category, List<Question>> questionsByCategory;


    public MapQuestionRepository(HashMap<Category, List<Question>> questionsByCategory) {
        this.questionsByCategory = questionsByCategory;
    }

    public static MapQuestionRepository fromQuestionReader(QuestionReader reader) throws IOException, ParseException {
        HashMap<Category, List<Question>> questionsByCategory = new HashMap<>();
        Question q;

        while ((q = reader.readQuestion()) != null) {
            List<Question> questions = questionsByCategory.get(q.getCategory());
            if (questions == null) {
                questions = new ArrayList<>();
                questionsByCategory.put(q.getCategory(),questions);
            }
            questions.add(q);
        }

        return new MapQuestionRepository(questionsByCategory);
    }

    @Override
    public List<Category> getCategories() {
        return List.copyOf(questionsByCategory.keySet());
    }

    @Override
    public List<Question> getQuestionsWithCategory(Category category) {
        List<Question> questions = questionsByCategory.get(category);
        return questions;

    }
}
