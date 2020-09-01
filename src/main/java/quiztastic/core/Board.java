package quiztastic.core;

import java.util.ArrayList;
import java.util.List;

/** A Jeopardy Board
 *
 */
public class Board {
    private final List<Group> groups;

    public Board(List<Group> groups){
        if(groups.size() != 6){
            throw new IllegalArgumentException("Should be 6 groups where " + groups.size());
        }
        this.groups = groups;
    }

    public static class Group{
        private final Category category;
        private final List<Question> questions;

        public Group(Category category, List<Question> questions) {
            this.category = category;
            this.questions = new ArrayList<>(questions);

            validate();
        }

        private void validate(){
            if(this.questions.size() != 5){
                throw new IllegalArgumentException("Should be 5 where " + this.questions.size());
            }

            for (Question q: questions){
                if(q.getCategory() != this.category){
                    throw new IllegalArgumentException("Expected all categories to be " + category + " but was " + q.getCategory());
                }
            }

        }
    }
}
