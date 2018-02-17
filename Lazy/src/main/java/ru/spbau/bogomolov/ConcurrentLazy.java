package ru.spbau.bogomolov;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Thread-safe lazy evaluation that uses synchronization to achieve thread-safety.
 * @param <T> the type of evaluation result
 */
public class ConcurrentLazy<T> implements Lazy<T> {

    /**
     * Evaluation that will be done.
     */
    private Supplier<T> supplier;
    /**
     * Result of the evaluation.
     */
    private volatile T result;

    ConcurrentLazy(@NotNull Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Returns result of the evaluation.
     * @return result of the evaluation
     */
    @Override
    public T get() {
        if (supplier == null) {
            return result;
        }
        synchronized (this) {
            if (supplier != null) {
                result = supplier.get();
                supplier = null;
            }
        }
        return result;
    }
}