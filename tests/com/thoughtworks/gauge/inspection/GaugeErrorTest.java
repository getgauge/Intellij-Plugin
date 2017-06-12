package com.thoughtworks.gauge.inspection;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class GaugeErrorTest {
    @Test
    public void getInstance() throws Exception {
        String message = "Duplicate scenario definition 'Vowel counts in single word' found in the same specification => 'Vowel counts in single word'";
        GaugeError error = GaugeError.getInstance("[ParseError] specs/example.spec:37 " + message);

        assertEquals("[ParseError] line number: 37, " + message, error.getMessage());
        assertTrue(error.isFrom("specs/example.spec"));
        assertFalse(error.isFrom("example.spec"));
    }

    @Test
    public void getInstanceWithErrorInWrongFormat() throws Exception {
        String message = "Duplicate scenario definition 'Vowel counts in single word' found in the same specification => 'Vowel counts in single word'";
        GaugeError error = GaugeError.getInstance("[ParseError] " + message);

        assertNull(error);
    }
}