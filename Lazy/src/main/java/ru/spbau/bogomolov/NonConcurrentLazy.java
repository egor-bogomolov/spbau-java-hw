package ru.spbau.bogomolov;

import com.sun.istack.internal.NotNull;

import java.util.function.Supplier;

/**
 * Simple lazy evaluation that doesn't provide any guarantees when used by multiple threads.
 * @param <T> the type of evaluation result
 */
public class NonConcurrentLazy<T> implements Lazy<T> {

    /**
     * Evaluation that will be done.
     */
    private Supplier<T> supplier;
    /**
     * Result of the evaluation.
     */
    private T result;

    NonConcurrentLazy(@NotNull Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Returns result of the evaluation.
     * @return result of the evaluation
     */
    @Override
    public T get() {
        if (supplier != null) {
            result = supplier.get();
            supplier = null;
        }
        return result;
    }
}