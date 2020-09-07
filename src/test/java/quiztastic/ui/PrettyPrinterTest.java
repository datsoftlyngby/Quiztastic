package quiztastic.ui;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrettyPrinterTest {

    @Test
    void shouldPrintSpaces () {
        StringWriter s = new StringWriter();
        PrettyPrinter p = new PrettyPrinter(new PrintWriter(s));
        p.printSpace(5);
        assertEquals("     ", s.toString());
    }

    @Test
    void shouldCenterThings () {
        StringWriter s = new StringWriter();
        PrettyPrinter p = new PrettyPrinter(new PrintWriter(s));
        p.printCenter("hello", 10);
        assertEquals(10, s.toString().length());
        assertEquals("  hello   ", s.toString());
    }

    @Test
    void shouldPrintColumns () {
        StringWriter s = new StringWriter();
        PrettyPrinter p = new PrettyPrinter(new PrintWriter(s));
        p.printRow(List.of("Hello, World! This is Cool", "Some", "Test"));
        String expected =
                "│ Hello, World! │      Some     │      Test     │\n" +
                "│ This is Cool  │               │               │\n";

        assertEquals(expected, s.toString());
    }
}