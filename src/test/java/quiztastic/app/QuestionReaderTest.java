package quiztastic.app;

import org.junit.jupiter.api.Test;
import quiztastic.core.Question;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class QuestionReaderTest {

    @Test
    void shouldReadALine() throws IOException {
        BufferedReader a = new BufferedReader(new StringReader("Hello, World!"));
        assertEquals(a.readLine(), "Hello, World!");
    }

    @Test
    void shouldReadMultipleLines() throws IOException {
        BufferedReader x = new BufferedReader(new StringReader("Hello, World!\nOther Line"));
        assertEquals(x.readLine(), "Hello, World!");
        assertEquals(x.readLine(), "Other Line");
        assertNull(x.readLine());
    }

    @Test
    void shouldSetBufferedReader () {
        BufferedReader x = new BufferedReader(new StringReader("Hello, World!\nOther Line"));
        QuestionReader a = new QuestionReader(x);
        assertEquals(x, a.getUnderlyingReader());
    }

    @Test
    void shouldReadSingleQuestion() throws IOException {
        String questionText = "100\tLAKES & RIVERS\tRiver mentioned most often in the Bible\tthe Jordan\n";
        QuestionReader reader = new QuestionReader(new StringReader(questionText));
        Question q = reader.readQuestion();
        assertNotNull(q);
        // Insert more tests
        assertEquals(q.getScore(), 100);

        Question end = reader.readQuestion();
        assertNull(end);
    }

    @Test
    void shouldReadManyQuestions() throws IOException {
        InputStream s = this.getClass()
                .getClassLoader()
                .getResourceAsStream("question-small.tsv");

        if (s == null) fail();
        QuestionReader reader = new QuestionReader(new InputStreamReader(s));
        int count = 0;
        while (reader.readQuestion() != null) {
            count++;
        }

        assertEquals(13, count);
    }

}