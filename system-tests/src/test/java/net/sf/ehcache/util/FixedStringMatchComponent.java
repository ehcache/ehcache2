package net.sf.ehcache.util;

import org.hamcrest.Description;

/**
 * Created by CGRE on 04/07/2016.
 */
public class FixedStringMatchComponent implements StringMatchComponent {
    private final String expected;

    public FixedStringMatchComponent(String expected) {
        this.expected = expected;
    }

    @Override
    public String consume(String s)
            throws StringMismatchException {
        if (!s.startsWith(expected)) {
            throw new StringMismatchException("Unable to match fixed component <" + expected + ">");
        }

        return s.substring(expected.length());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(expected);
    }
}
