package net.sf.ehcache.util;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Created by CGRE on 04/07/2016.
 */
public class FlexibleOrderStringMatcher extends TypeSafeDiagnosingMatcher<String> {
    private final StringMatchComponent[] components;

    public FlexibleOrderStringMatcher(StringMatchComponent... components) {
        this.components = components;
    }

    @Override
    protected boolean matchesSafely(String s, Description description) {
        String remaining = s;
        try {
            for (StringMatchComponent component : components) {
                remaining = component.consume(remaining);
            }

            return true;
        } catch (StringMismatchException e) {
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        for (StringMatchComponent component : components) {
            component.describeTo(description);
        }
    }
}
