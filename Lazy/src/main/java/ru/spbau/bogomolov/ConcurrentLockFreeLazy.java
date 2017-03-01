package ru.spbau.bogomolov;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

/**
 * Thread-safe lock-free lazy evaluation.
 * Supplier's {@link #get()} method can be called multiple times.
 * @param <T> the type of evaluation result
 */
public class ConcurrentLockFreeLazy<T> implements Lazy<T> {

    /**
     * An object indicating that the result hasn't been computed yet.
     */
    private static final Object emptyResult = new Object();
    /**
     * An atomic updater of the {@link #result} field.
     */
    private static final AtomicReferenceFieldUpdater<ConcurrentLockFreeLazy, Object> updater =
            AtomicReferenceFieldUpdater.newUpdater(ConcurrentLockFreeLazy.class, Object.class, "result");

    /**
     * Evaluation that will be done.
     */
    private Supplier<T> supplier;
    /**
     * Result of the evaluation.
     */
    private volatile Object result = emptyResult;

    ConcurrentLockFreeLazy(@NotNull Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Returns result of the evaluation.
     * @return result of the evaluation
     */
    @Override
    public T get() {
        if (result == emptyResult) {
            updater.compareAndSet(this, emptyResult, supplier.get());
        }
        return (T) result;
    }
}