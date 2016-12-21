package org.osgl.aaa;

import org.osgl.$;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.S;

import java.util.Set;

/**
 * Defines the common properties of AAA objects including
 */
public interface AAAObject {

    /**
     * Compare this AAAObject to another object
     *
     * @param another another AAA object
     * @return {@code true} if the two are equals together
     */
    public boolean equals(Object another);

    /**
     * Returns a string representation of this principal.
     *
     * @return a string representation of this principal.
     */
    public String toString();

    /**
     * Returns a hashcode for this principal.
     *
     * @return a hashcode for this principal.
     */
    public int hashCode();

    /**
     * Returns the name of the object. The name shall be unique for certain type of
     * AAAObject
     *
     * @return the name
     */
    String getName();

    /**
     * Set property value to the {@code AAAObject}.
     * <p>Note if the value is {@code null} then the
     * property will be removed from the object</p>
     *
     * @param key the property key
     * @param value the property value
     */
    void setProperty(String key, String value);

    /**
     * Remove specified property from the {@code AAAObject}
     * @param key the property key
     */
    void unsetProperty(String key);

    /**
     * Return property value of the {@code AAAObject}
     *
     * @param key the property key
     * @return the property value
     */
    String getProperty(String key);

    /**
     * Return a set contains the {@link #getProperty(String) property} keys
     * @return a set of strings as described
     */
    Set<String> propertyKeys();

    public static abstract class F {
        public static <T extends AAAObject> $.Predicate<T> nameMatcher(final String name) {
            return new $.Predicate<T>() {
                @Override
                public boolean test(T aaaObject) {
                    return S.eq(name, aaaObject.getName());
                }
            };
        }

        public static <T extends AAAObject> $.Visitor<T> nameVisitor(final $.Function<String, ?> visitor) {
            return new $.Visitor<T>() {
                @Override
                public void visit(T t) throws $.Break {
                    visitor.apply(t.getName());
                    return;
                }
            };
        }

        public static $.F1<AAAObject, String> NAME_FETCHER = new $.F1<AAAObject, String>() {
            @Override
            public String apply(AAAObject aaaObject) throws NotAppliedException, $.Break {
                return aaaObject.getName();
            }
        };

        public static <T extends AAAObject> $.F1<T, String> nameFetcher() {
            return ($.F1<T, String>)NAME_FETCHER;
        }
    }
}
