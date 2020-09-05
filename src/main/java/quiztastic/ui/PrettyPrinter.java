package quiztastic.ui;

import quiztastic.core.Category;
import quiztastic.domain.Game;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PrettyPrinter {
    private final PrintWriter out;
    private final int columnWidth = 15;

    public PrettyPrinter(PrintWriter out) {
        this.out = out;
    }

    public void printBoard(Game game) {
        List<String> headers = game.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        printTableLine(6, columnWidth, "┌", "┬", "┐"  );
        printRow(List.of("A", "B", "C", "D", "E", "F"));
        printRow(headers);
        printTableLine(6, columnWidth, "├", "┼", "┤"  );
        for (int number = 0; number < 5; number++ ) {
            ArrayList<String> row = new ArrayList<>();
            for (int category = 0; category < 6; category++) {
                if (game.isAnswered(category, number)) {
                    row.add("---");
                } else {
                    row.add(String.format("%d", (number + 1) * 100));
                }
            }
            printRow(row);
            if (number < 4)
                printTableLine(6, columnWidth, "├", "┼", "┤"  );
        }
        printTableLine(6, columnWidth, "└", "┴", "┘");
    }

    public void printRow(List<String> args) {
        List<List<String>> columns = new ArrayList<>();
        for (String s : args) {
            List<String> lines = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            for (String word : s.split(" ")) {
                if ( 1 + word.length() + builder.length() > columnWidth) {
                    lines.add(builder.toString());
                    builder = new StringBuilder();
                    builder.append(word);
                } else {
                    builder.append(" ").append(word);
                }

            }
            lines.add(builder.toString());
            columns.add(lines);
        };
        boolean continueRunning = true;
        while (continueRunning) {
            continueRunning = false;
            out.print("│");
            for (List<String> lines : columns) {
                if (!lines.isEmpty()) {
                    printCenter(lines.remove(0), columnWidth);
                    continueRunning |= !lines.isEmpty();
                } else {
                    printSpace(columnWidth);
                }
                out.print("│");
            }
            out.println();
        }
    }

    public void printTableLine(int count, int width, String left, String mid, String right) {
        out.print(left);
        for (int i = 0; i < count; i++) {
            printMany("─", width);
            if (i == count - 1) {
                out.print(right);
            } else {
                out.print(mid);
            }
        }
        out.println();
    }

    public void printSpace(int width) {
        printMany(" ", width);
    }

    public void printMany(String s, int count) {
        for(int i = 0; i < count; i++) out.print(s);
    }

    public void printCenter(String s, int width) {
        int space = width - s.length();
        printSpace(space / 2);
        out.print(s);
        printSpace(width - s.length() - space / 2);
    }
}
