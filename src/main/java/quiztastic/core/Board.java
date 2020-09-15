package quiztastic.core;

import java.util.List;
import java.util.Objects;

/** A Jeopardy Board
 *
 */
public class Board {
    private final List<Group> groups;

    public Board(List<Group> groups) {
        this.groups = List.copyOf(groups);
        if (this.groups.size() != 6) {
            throw new IllegalArgumentException(
                    "Should be 6 groups, there were " + groups.size());
        }
    }

    public static Index indexOf(int category, int number) {
        return new Board.Index(category, number);
    }

    public List<Group> getGroups() {
        return groups;
    }

    public Question getQuestion(Index index) {
        return groups.get(index.categoryNumber).questions.get(index.questionNumber);
    }

    @Override
    public String toString() {
        return "Board{" +
                "groups=" + groups +
                '}';
    }

    public static class Index {
        private final int categoryNumber;
        private final int questionNumber;


        public Index(int categoryNumber, int questionNumber) {
            this.categoryNumber = categoryNumber;
            this.questionNumber = questionNumber;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Index index = (Index) o;
            return categoryNumber == index.categoryNumber &&
                    questionNumber == index.questionNumber;
        }

        @Override
        public int hashCode() {
            return Objects.hash(categoryNumber, questionNumber);
        }

        @Override
        public String toString() {
            return "Index{" +
                    "categoryNumber=" + categoryNumber +
                    ", questionNumber=" + questionNumber +
                    '}';
        }
    }

    public static class Group {
        private final Category category;
        private final List<Question> questions;

        public Group(Category category, List<Question> questions) {
            this.category = category;
            this.questions = List.copyOf(questions);
            validate();
        }

        private void validate() {
            if (questions.size() != 5) {
                throw new IllegalArgumentException(
                        "Should be 5 groups, there were " + questions.size());
            }
            for (Question q : questions) {
                if (!q.getCategory().equals(category)) {
                    throw new IllegalArgumentException("Expected all categories to be "
                            + category + " but was " + q.getCategory());
                }
            }
        }

        public Category getCategory() {
            return category;
        }

        public List<Question> getQuestions() {
            return questions;
        }

        @Override
        public String toString() {
            return "Group{" +
                    "category=" + category +
                    ", questions=" + questions +
                    '}';
        }
    }
}
