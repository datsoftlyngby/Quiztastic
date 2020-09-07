package quiztastic.ui;

import quiztastic.app.Quiztastic;
import quiztastic.domain.Game;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Scanner;

public class Protocol {
    private final Quiztastic quiz;
    private final Scanner in;
    private final PrintWriter out;

    public Protocol(Scanner in, PrintWriter out) {
        this.in = in;
        this.out = out;
        this.quiz = Quiztastic.getInstance();
    }

    private String fetchCommand () {
        out.print("> ");
        out.flush();
        String word = in.next(); // answer a100 -> answer
        return word;
    }

    public void run () {
        String cmd = fetchCommand();
        while (!cmd.equals("quit")) {
            switch (cmd) {
                case "h":
                case "help":
                   out.println("There are no help!");
                   break;
                case "draw":
                case "d":
                    displayBoard();
                    break;
                case "answer":
                case "a":
                    String question = in.next();
                    String a = question.substring(0, 1).toLowerCase(); // "A100" -> "a"
                    int questionScore = Integer.parseInt(question.substring(1)); // "A100" -> 100
                    answerQuestion("abcdef".indexOf(a), questionScore);
                    break;
                default:
                   out.println("Unknown command! " + cmd);
            }
            in.nextLine();
            out.flush();
            cmd = fetchCommand();
        }

    }

    private void answerQuestion(int categoryNumber, int questionScore) {

    }

    private void displayBoard() {
        Game game = quiz.getCurrentGame();
        List<Integer> scores = List.of(100,200,300,400,500);
        for (int questionNumber = 0; questionNumber < 5; questionNumber++) {
            out.print("|");
            for (int category = 0; category < 6; category++) {
                out.print("    ");
                if (game.isAnswered(category, questionNumber)) {
                    out.print("---");
                } else {
                    out.print(scores.get(questionNumber));
                }
                out.print("    |");
            }
            out.println();
        }
    }

}
