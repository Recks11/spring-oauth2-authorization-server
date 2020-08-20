package com.benoly.auth.util;

import java.util.Collection;
import java.util.function.Consumer;

public class ObjectUtils {

    /**
     * Helper method to apply a function on an object if it is not null or empty.
     * The object if not null is then passed as a parameter in the function.
     *
     * @param object   the object to check
     * @param function the function to apply
     * @param <T>      The type of the object
     */
    public static <T> void applyIfNonNull(T object, Consumer<T> function) {
        if (object == null) return;
        if (object instanceof Collection) {
            if (!((Collection<?>) object).isEmpty()) {
                function.accept(object);
            }
        }
        function.accept(object);
    }

    public static <T> void applyIfNonEmpty(Collection<T> object, Consumer<Collection<T>> function) {
        if (object == null) return;
        if (object.isEmpty()) return;
        function.accept(object);
    }
}
