package examples;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmutabilityTest {

    @Test
    void simpleImmutabilityTest () {
        List<String> a = List.of("1", "2");
        assertThrows(UnsupportedOperationException.class, () ->
                a.add("Hello")
        );
        assertThrows(UnsupportedOperationException.class, () ->
                a.set(0, "Other")
        );
    }
}
