package dev.rexijie.auth.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ObjectUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
            var collection = ((Collection<?>) object);
            if (collection.isEmpty()) return;
        }
        function.accept(object);
    }

    public static <T> void applyIfNonEmpty(Collection<T> object, Consumer<Collection<T>> function) {
        if (object == null) return;
        if (object.isEmpty()) return;
        function.accept(object);
    }

    /**
     * Utility method to remove null elements from map
     */
    public static <T> Map<String, Object> cleanMap(Map<String, T> map) {
        Map<String, Object> returnedMap = new ConcurrentHashMap<>();
        for (String key : map.keySet())
            if (map.get(key) != null)
                returnedMap.put(key, map.get(key));

        return returnedMap;
    }

    public static <T> Map<String, Object> toMap(T object) {
        return objectMapper.convertValue(object, new TypeReference<>() {});
    }


}
