package io.plumery.core.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by veniamin on 30/01/2017.
 */
public abstract class AggregateRoot {
    private static final Logger logger = LoggerFactory.getLogger(AggregateRoot.class);

    private static final String APPLY_METHOD_NAME = "apply";
    private final List<Event> changes = new ArrayList<>();

    public ID id;
    public int version;

    protected AggregateRoot() {
        this(null);
    }

    protected AggregateRoot(ID id) {
        this.id = id;
    }

    public void markChangesAsCommitted() {
        changes.clear();
    }

    public final Iterable<? extends Event> getUncommittedChanges() {
        return changes;
    }

    public final void loadFromHistory(Iterable<? extends Event> history) {
        for (Event e : history) {
            if(version < e.version) {
                version = e.version;
            }
            applyChange(e, false);
        }
    }

    protected void applyChange(Event event) {
        applyChange(event, true);
    }

    private void applyChange(Event event, boolean isNew) {
        invokeApplyIfEntitySupports(event);

        if (isNew) {
            changes.add(event);
        }
    }

    private void invokeApplyIfEntitySupports(Event event) {
        Class<?> eventType = nonAnonymous(event.getClass());
        try {
            Method method = this.getClass().getDeclaredMethod(APPLY_METHOD_NAME, eventType);
            method.setAccessible(true);
            method.invoke(this, event);
        } catch (SecurityException | IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchMethodException ex) {
            logger.warn("Event {} not applicable to {}!", event, this);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> nonAnonymous(Class<T> clazz) {
        return clazz.isAnonymousClass() ? (Class<T>) clazz.getSuperclass() : clazz;
    }
}
