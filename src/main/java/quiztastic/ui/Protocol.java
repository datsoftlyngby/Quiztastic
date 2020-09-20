package quiztastic.ui;

import quiztastic.app.Quiztastic;
import quiztastic.core.Board;
import quiztastic.core.Player;
import quiztastic.domain.Game;
import quiztastic.domain.Round;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.ParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;

public class Protocol implements Round.InteractionHandler {
    private final Quiztastic quiz;
    private final PrettyPrinter printer;
    private final Scanner in;
    private final InputStream inputStream;
    private final PrintWriter out;
    private final Protocol superThis;

    public Protocol(InputStream inputStream, PrintWriter out) {
        this.in = new Scanner(inputStream);
        this.inputStream = inputStream;
        this.out = out;
        this.printer = new PrettyPrinter(out);
        this.quiz = Quiztastic.getInstance();
        this.superThis = this;
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
                        out.println("Thank you, " + player.getId() + ", next!");
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
                        this::parseReset,
                        this::parseScore,
                        this::parsePlay,
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

    // public Command parseAnswer() throws ParseException {
    //     in.next("a|answer");
    //     try {
    //         String id = in.next("[abcdefABCDEF][12345]00").toLowerCase();
    //         int category = LETTER_LOOKUP.lastIndexOf(id.substring(0,1));
    //         int question = NUMBER_LOOKUP.lastIndexOf(id.substring(1,2));
    //     } catch (NoSuchElementException e) {
    //         e.printStackTrace();
    //         throw new ParseException("While parsing an answer, got: " + in.nextLine(), 0);
    //     }
    // }

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

    @Override
    public void notifyWaitingForPlayers(Set<Player> players) {
        out.print("Waiting for players: ");
        for (Player p : players) {
            out.print(p.getId() + " ");
        }
        out.println();
        out.flush();
    }

    @Override
    public void notifyRoundPlayerSelected(Player roundPlayer) {
        out.println("Round player is " + roundPlayer.getId());
        out.flush();
    }

    @Override
    public void notifyQuestionSelected(Game.ActiveQuestion question) {
        out.println("Question: " + question.getQuestionText());
        out.flush();
    }

    @Override
    public void notifyBuzzPlayer(Player buzzPlayer) {
        out.println("Player " + buzzPlayer.getId() + " buzzed!");
        out.flush();
    }

    @Override
    public void notifyAnswer(Game.Answer answer) {
        Player p = answer.getPlayer();
        if (p == null) {
            out.println("You are out of time, the correct answer was: " + answer.getAnswer());
        } else {
            out.println(answer.getPlayer().getId() + " answered " + answer.getAnswer() + " which was " +
                    (answer.isCorrect() ? "correct" : "incorrect"));
        }
        out.flush();
    }

    private String promptQuestion(String prompt) {
        out.println(prompt);
        out.print("? ");
        out.flush();
        return in.nextLine();
    }

    private static final String LETTER_LOOKUP = "abcdef";
    private static final String NUMBER_LOOKUP = "12345";
    private Board.Index parseBoardIndex(String line) throws ParseException {
        try {
            String id = new Scanner(new StringReader(line))
                .next("[abcdefABCDEF][12345]00").toLowerCase();
            int category = LETTER_LOOKUP.lastIndexOf(id.substring(0, 1));
            int question = NUMBER_LOOKUP.lastIndexOf(id.substring(1, 2));
            return Board.indexOf(category, question);
        } catch (NoSuchElementException e) {
            throw new ParseException("Could not parse '" + line + "'", 0);
        }
    }

    @Override
    public Board.Index chooseBoardIndex() {
        Game game = quiz.getCurrentGame();
        printer.printBoard(game);
        while (true) {
            try {
                String id = promptQuestion("Choose Question");
                Board.Index index = parseBoardIndex(id);
                if (game.isAnswered(index)) {
                    out.println("That question is already answered, try again...");
                } else {
                    return index;
                }
            } catch (ParseException e) {
                out.println("Cannot understand the input.. try again...");
            }
        }
    }

    @Override
    public String getAnswerToQuestion() {
        return promptQuestion("Answer Question");
    }

    @Override
    public void waitForBuzz() {
        out.println("Press <enter> for buzz!");
        out.flush();
        try {
            Thread myThread = Thread.currentThread();
            while(inputStream.available() == 0 && !myThread.isInterrupted()) {
                Thread.sleep(100);
            }
            if (!myThread.isInterrupted()) {
                in.nextLine();
            }
        } catch (IOException e ) {
            e.printStackTrace();
        } catch (InterruptedException e) {

        }
    }


    public interface Command {
        void doIt(Player player);
    }

    public class PlayCommand implements Command {

        @Override
        public void doIt(Player player) {
            Game game = quiz.getCurrentGame();
            Round round = game.getActiveRound();
            try {
                round.play(player, superThis);
            } catch (InterruptedException e) {
                e.printStackTrace(out);
            }
        }
    }

    public class HelpCommand implements Command {
        @Override
        public void doIt(Player player) {
            out.println("This is the help page:");
            out.println("- [h]elp: for this help-page.");
            out.println("- [p]lay: to join a question round.");
            out.println("- [r]eset: for a new board.");
            out.println("- [d]raw: to see the current board.");
            out.println("- [s]core: get the current score.");
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

    private class ScoreCommand implements Command {
        @Override
        public void doIt(Player player) {
            Game game = quiz.getCurrentGame();
            game.getScores().forEach((p, i) ->
                    out.println("- " + p.getId() + " has score: " + i ));
        }
    }
}
