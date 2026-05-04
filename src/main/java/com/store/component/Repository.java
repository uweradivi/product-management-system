package com.store.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Generic repository providing type-safe CRUD operations.
 * Demonstrates Generics for reusability and type safety.
 *
 * @param <T> the type of entity stored
 */
public class Repository<T> {

    private final List<T> items = new ArrayList<>();

    public void add(T item) {
        items.add(item);
    }

    public boolean remove(T item) {
        return items.remove(item);
    }

    public List<T> getAll() {
        return new ArrayList<>(items);
    }

    public List<T> filter(Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : items) {
            if (predicate.test(item)) result.add(item);
        }
        return result;
    }

    public T findFirst(Predicate<T> predicate) {
        for (T item : items) {
            if (predicate.test(item)) return item;
        }
        return null;
    }

    public void setAll(List<T> newItems) {
        items.clear();
        items.addAll(newItems);
    }

    public int size() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }
}
