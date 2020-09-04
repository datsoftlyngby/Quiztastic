package examples;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IteratorTest {

    @Test
    void letsIterate() {
        List<String> list = List.of("A", "B", "C");

        // We can get an iterator from the list
        Iterator<String> strings = list.iterator();

        // We expect the iterator to have more ellements
        assertTrue(strings.hasNext());

        // The first element in the iterations should be the first element
        // in the list
        assertEquals(strings.next(), "A");
        assertEquals(strings.next(), "B");
        assertEquals(strings.next(), "C");

        // Now there are no more elements
        assertFalse(strings.hasNext());
    }

    @Test
    void iteratingOverAnIterableTheHardWay() {
        Iterable<Integer> numbers = List.of(1, 2, 3, 4);
        int sum = 0;
        for (Iterator<Integer> iter = numbers.iterator(); iter.hasNext(); ) {
            Integer number = iter.next();
            sum += number;
        }
        assertEquals(10, sum);
    }

    @Test
    void iteratingOverAnIterableTheEasyWay() {
        Iterable<Integer> numbers = List.of(1, 2, 3, 4);
        int sum = 0;
        for (Integer number : numbers) {
            sum += number;
        }
        assertEquals(10, sum);
    }
}
