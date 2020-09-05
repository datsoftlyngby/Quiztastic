package quiztastic.domain;

public class InvalidAnswer extends Exception {
    public final String actualAnswer;
    public final String correctAnswer;

    public InvalidAnswer(String actualAnswer, String correctAnswer) {
        this.actualAnswer = actualAnswer;
        this.correctAnswer = correctAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getActualAnswer() {
        return actualAnswer;
    }
}
