package io.plumery.eventstore.utils;

import io.plumery.core.AggregateRoot;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * Created by veniamin on 30/01/2017.
 */
public class ReflectionHelper {
    public static Class<? extends AggregateRoot> getParameterizedClass(Class clazz) {
        ParameterizedType pt = (ParameterizedType) clazz.getGenericSuperclass();
        @SuppressWarnings("unchecked")
        Class<? extends AggregateRoot> parametrizedClazz = (Class<? extends AggregateRoot>) pt.getActualTypeArguments()[0];

        return parametrizedClazz;
    }

    /**
     * Creates class instance by calling the default constructor.
     */
    public static <T> T instantiate(final Class<T> clazz) {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<T>() {
                @Override
                public T run() throws Exception {
                    Constructor<T> constructor = clazz.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    return constructor.newInstance();
                }
            });
        } catch (PrivilegedActionException e) {
            if (e.getCause() instanceof NoSuchMethodException) {
                throw new RuntimeException(clazz + " must have a default constructor");
            }
            throw new RuntimeException(e);
        }
    }
}
