package org.osgl.aaa;

import org.osgl._;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.S;

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
     * @return
     */
    String getName();

    /**
     * Set property value to the {@code AAAObject}.
     *
     * @param key the property key
     * @param value the property value
     */
    void setProperty(String key, String value);

    /**
     * Return property value of the {@code AAAObject}
     *
     * @param key the property key
     * @return the property value
     */
    String getProperty(String key);

    public static abstract class F {
        public static <T extends AAAObject> _.Predicate<T> nameMatcher(final String name) {
            return new _.Predicate<T>() {
                @Override
                public boolean test(T aaaObject) {
                    return S.eq(name, aaaObject.getName());
                }
            };
        }

        public static <T extends AAAObject> _.Visitor<T> nameVisitor(final _.Function<String, ?> visitor) {
            return new _.Visitor<T>() {
                @Override
                public void visit(T t) throws _.Break {
                    visitor.apply(t.getName());
                    return;
                }
            };
        }

        public static _.F1<AAAObject, String> NAME_FETCHER = new _.F1<AAAObject, String>() {
            @Override
            public String apply(AAAObject aaaObject) throws NotAppliedException, _.Break {
                return aaaObject.getName();
            }
        };

        public static <T extends AAAObject> _.F1<T, String> nameFetcher() {
            return (_.F1<T, String>)NAME_FETCHER;
        }
    }
}
