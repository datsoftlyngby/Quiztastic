package quiztastic.ui;

import quiztastic.app.Quiztastic;
import quiztastic.core.Board;
import quiztastic.core.Player;
import quiztastic.domain.Game;
import quiztastic.domain.InvalidAnswer;

import java.io.PrintWriter;
import java.io.Reader;
import java.text.ParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class Protocol {
    private final Quiztastic quiz;
    private final PrettyPrinter printer;
    private final Scanner in;
    private final PrintWriter out;

    public Protocol(Scanner in, PrintWriter out) {
        this.in = in;
        this.out = out;
        this.printer = new PrettyPrinter(out);
        this.quiz = Quiztastic.getInstance();
    }

    public Protocol(Reader in, PrintWriter out) {
        this(new Scanner(in), out);
    }

    public void run() {
        out.println("Welcome to Quiztastic!");
        Player player = fetchPlayer();
        out.println("- press [h]elp for, you know, help.");
        try {
            quiz.getCurrentGame().addPlayer(player);
            while (true) {
                out.print("> ");
                out.flush();
                try {
                    Command cmd = fetchCommand();
                    in.nextLine();
                    if (cmd == null) {
                        out.println("Thank you, " + player + " next!");
                        out.flush();
                        return;
                    } else {
                        cmd.doIt(player);
                    }
                } catch (ParseException e) {
                    out.println("Invalid command: " + e.getMessage());
                }
            }
        } finally {
            quiz.getCurrentGame().removePlayer(player);
        }
    }

    private Player fetchPlayer() {
        out.print("What is your name? ");
        out.flush();
        String playerId = in.nextLine();
        return new Player(playerId);
    }

    private Command fetchCommand() throws ParseException {
        List<Callable<Command>> parsers =
                List.of(this::parseHelp,
                        this::parseDraw,
                        this::parseAnswer,
                        this::parseReset,
                        this::parseScore,
                        this::parseQuit);
        for (Callable<Command> cmd : parsers) {
            try {
                return cmd.call();
            } catch (NoSuchElementException ignored) {
            } catch (ParseException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new ParseException("Could not match: " + in.nextLine(), 0);
    }

    public Command parseHelp() {
        in.next("h|help");
        return new HelpCommand();
    }

    public Command parseDraw() {
        in.next("d|draw");
        return new DrawCommand();
    }

    public Command parseScore() {
        in.next("s|score");
        return new ScoreCommand();
    }

    private static final String LETTER_LOOKUP = "abcdef";
    private static final String NUMBER_LOOKUP = "12345";
    public Command parseAnswer() throws ParseException {
        in.next("a|answer");
        try {
            String id = in.next("[abcdefABCDEF][12345]00").toLowerCase();
            int category = LETTER_LOOKUP.lastIndexOf(id.substring(0,1));
            int question = NUMBER_LOOKUP.lastIndexOf(id.substring(1,2));
            return new AnswerCommand(Board.indexOf(category, question));
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new ParseException("While parsing an answer, got: " + in.nextLine(), 0);
        }
    }

    public Command parseReset() {
        in.next("r|reset");
        return new ResetCommand();
    }

    public Command parseQuit() {
        in.next("q|quit");
        return null;
    }

    public Command parsePlay() {
        in.next("p|play");
        return new PlayCommand();
    }


    public interface Command {
        void doIt(Player player);
    }

    public class PlayCommand implements Command {

        @Override
        public void doIt(Player player) {
            Game game = quiz.getCurrentGame();
            game.playRound(player);
        }
    }

    public class HelpCommand implements Command {
        @Override
        public void doIt(Player player) {
            out.println("This is the help page:");
            out.println("- [h]elp: for this help-page.");
            out.println("- [r]eset: for a new board.");
            out.println("- [d]raw: to see the current board.");
            out.println("- [a]nswer [A-F][1-5]00: to try to answer a question.");
            out.println("- [q]uit: to quit the game.");
        }
    }

    public class DrawCommand implements Command {
        @Override
        public void doIt(Player player) {
            printer.printBoard(quiz.getCurrentGame());
        }
    }

    public class ResetCommand implements Command {
        @Override
        public void doIt(Player player) {
            quiz.resetGame();
        }
    }

    public class AnswerCommand implements Command {
        private final Board.Index index;

        public AnswerCommand(int category, int number) {
            this.index = Board.indexOf(category, number);
        }

        public AnswerCommand(Board.Index index) {
            this.index = index;
        }

        @Override
        public void doIt(Player player) {
            Game game = quiz.getCurrentGame();
            if (game.isAnswered(index)) {
                out.println("Already answered, choose another one.");
            } else {
                Game.ActiveQuestion aq = game.selectQuestion(index);
                out.println(aq.getQuestionText());
                out.print("? ");
                out.flush();
                String answer = in.nextLine();
                try {
                    aq.answerQuestion(player, answer);
                    out.println("Correct!");
                } catch (InvalidAnswer invalidAnswer) {
                    out.println("Sorry, the correct answer was " + invalidAnswer.getCorrectAnswer());
                }
            }
        }
    }

    private class ScoreCommand implements Command {
        @Override
        public void doIt(Player player) {
            Game game = quiz.getCurrentGame();
            game.getScores().forEach((p, i) ->
                    out.println("- " + p.getId() + " has score: " + i ));
        }
    }
}
