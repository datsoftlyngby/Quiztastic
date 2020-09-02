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
     //nyt map til at læse ind i
        HashMap<Category, List<Question>> questionsByCategory = new HashMap<>();
        //lav nyt repository
        MapQuestionRepository mapQuestionRepository=new MapQuestionRepository(questionsByCategory);
        //læs første question
        Question question = reader.readQuestion();
        //så længe der er flere spørgsmål
        while(question!=null) {
            //hvis kategorien fra question findes i map add questio til listen i map
            if(questionsByCategory.containsKey(question.getCategory())){
                questionsByCategory.get(question.getCategory()).add(question);
            }else{
                //ellers tilføj question til map med kategory som key
               ArrayList<Question> arrayList = new ArrayList<>();
               arrayList.add(question);
               questionsByCategory.put(question.getCategory(),arrayList);
            }
            question = reader.readQuestion();
        }

        return mapQuestionRepository;

    }
    @Override
    public List<Category> getCategories() {
         ArrayList<Category> categoryArrayList=new ArrayList<>();
        //lav set af keys om til arrayliste
        for (Category category:this.questionsByCategory.keySet()){
            categoryArrayList.add(category);

        }
        return categoryArrayList;

    }

    @Override
    public List<Question> getQuestionsWithCategory(Category category) {
        //slå op i map og returner listen...
        return this.questionsByCategory.get(category);

    }
}
