package quiztastic.entries;

import quiztastic.ui.Protocol;

import java.io.PrintWriter;

public class RunTUI {
    public static void main(String[] args) {
        new Protocol(System.in, new PrintWriter(System.out)).run();
    }
}
