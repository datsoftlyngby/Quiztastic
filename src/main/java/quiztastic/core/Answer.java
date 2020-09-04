package quiztastic.core;


public class Answer {
    private final Question question;
    private final String answer;

    public Answer(Question question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public Question getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
