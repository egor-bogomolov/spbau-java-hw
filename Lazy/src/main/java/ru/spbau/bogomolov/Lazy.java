package ru.spbau.bogomolov;

/**
 * An interface representing lazy evaluation.
 * The evaluation won't be done before the first call of {@link #get()}.
 * Multiple calls return the same object.
 *
 * @param <T> the type of evaluation result
 */
public interface Lazy<T> {
    /**
     * Returns result of the evaluation.
     * @return result of the evaluation
     */
    T get();
}