package quiztastic.app;

import quiztastic.core.Category;
import quiztastic.core.Question;
import quiztastic.domain.QuestionRepository;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class MapQuestionRepository implements QuestionRepository {
    private final HashMap<Category, List<Question>> questionsByCategory;

    public MapQuestionRepository(HashMap<Category, List<Question>> questionsByCategory) {
        this.questionsByCategory = questionsByCategory;
    }

    public static MapQuestionRepository fromQuestionReader(QuestionReader reader)
            throws IOException, ParseException {
        HashMap<Category, List<Question>> questionsByCategory = new HashMap<>();
        Question q;
        while ((q = reader.readQuestion()) != null) {
            List<Question> questions =
                    questionsByCategory.computeIfAbsent(q.getCategory(),
                            (Category c) -> new ArrayList<Question>());
            questions.add(q);
        }
        return new MapQuestionRepository(questionsByCategory);
    }

    @Override
    public List<Category> getCategories() {
        ArrayList<Category> list = new ArrayList<>(questionsByCategory.keySet());
        Collections.shuffle(list);
        return list;
    }

    @Override
    public List<Question> getQuestionsWithCategory(Category category) {
        ArrayList<Question> list = new ArrayList<>(questionsByCategory.get(category));
        Collections.shuffle(list);
        return list;
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
