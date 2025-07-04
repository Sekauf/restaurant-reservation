package com.restaurant.reservation.ui;

import java.lang.reflect.Field;

/** Utility methods for testing Swing components via reflection. */
public class TestUtils {
    public static Object getField(Object target, String name) {
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
