package ru.spbau.bogomolov;

import com.sun.istack.internal.NotNull;

import java.util.function.Supplier;

/**
 * Class that provides different implementations of Lazy interface.
 */
public class LazyFactory {

    /**
     * Creates an instance of {@link NonConcurrentLazy}.
     * It is a simple lazy evaluation that doesn't provide any guarantees when used by multiple threads.
     * @param supplier evaluation to be done
     * @param <T> the type of evaluation result
     * @return an instance of {@link NonConcurrentLazy}
     */
    public static <T> Lazy<T> createNonConcurrentLazy(@NotNull Supplier<T> supplier) {
        return new NonConcurrentLazy<>(supplier);
    }

    /**
     * Creates an instance of {@link ConcurrentLazy}.
     * It is a thread-safe lazy evaluation that uses synchronization to achieve thread-safety.
     * @param supplier evaluation to be done
     * @param <T> the type of evaluation result
     * @return an instance of {@link ConcurrentLazy}
     */
    public static <T> Lazy<T> createConcurrentLazy(@NotNull Supplier<T> supplier) {
        return new ConcurrentLazy<>(supplier);
    }

    /**
     * Creates an instance of {@link ConcurrentLockFreeLazy}.
     * It is a thread-safe lock-free lazy evaluation.
     * @param supplier evaluation to be done
     * @param <T> the type of evaluation result
     * @return an instance of {@link ConcurrentLockFreeLazy}
     */
    public static <T> Lazy<T> createConcurrentLockFreeLazy(@NotNull Supplier<T> supplier) {
        return new ConcurrentLockFreeLazy<>(supplier);
    }
}