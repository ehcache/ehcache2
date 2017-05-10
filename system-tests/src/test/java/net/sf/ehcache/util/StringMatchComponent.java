package net.sf.ehcache.util;

import org.hamcrest.Description;

/**
 * Created by CGRE on 04/07/2016.
 */
public interface StringMatchComponent {
    /**
     * @param s the string to partially match on
     * @return the remaining string that has not been matched by this component
     */
    String consume(String s)
        throws StringMismatchException;

    void describeTo(Description description);
}
