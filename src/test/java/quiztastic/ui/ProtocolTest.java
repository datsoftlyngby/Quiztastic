package quiztastic.ui;

import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class ProtocolTest {

    @Test
    void parseAnswer() throws ParseException {
        Protocol p = new Protocol (new StringReader("answer A100"), null);
        p.parseAnswer();
    }
}