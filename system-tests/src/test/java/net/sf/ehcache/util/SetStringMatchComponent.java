package net.sf.ehcache.util;

import org.hamcrest.Description;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by CGRE on 04/07/2016.
 */
public class SetStringMatchComponent implements StringMatchComponent {
    private final Set<String> expectedValues;
    private final String terminator;
    private final String separator;

    public SetStringMatchComponent(String terminator, String separator, String... expectedValues) {
        this(terminator, separator, new HashSet<String>(Arrays.asList(expectedValues)));
    }

    public SetStringMatchComponent(String terminator, String separator, Set<String> expectedValues) {
        this.expectedValues = expectedValues;
        this.terminator = terminator;
        this.separator = separator;
    }

    @Override
    public String consume(String s)
            throws StringMismatchException {
        int terminatorIndex = s.indexOf(terminator);

        if (terminatorIndex == -1) {
            throw new StringMismatchException("Unable to find terminator <" + terminator + "> to match reorderable component");
        }

        String match = s.substring(0, terminatorIndex);
        Set<String> actualValues = new HashSet<String>(Arrays.asList(match.split(separator)));

        if (!actualValues.containsAll(expectedValues) || actualValues.size() != expectedValues.size()) {
            throw new StringMismatchException("Unable to match reorderable component <" + expectedValues.toString() + ">");
        }

        return s.substring(terminatorIndex);
    }

    @Override
    public void describeTo(Description description) {
        description.appendValueList("{in-any-order: ", ", ", "}", expectedValues);
    }
}
