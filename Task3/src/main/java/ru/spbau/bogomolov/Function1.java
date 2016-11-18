package ru.spbau.bogomolov;

/**
 * Function f, takes 1 argument of type V, returns value of type R
 */
@FunctionalInterface
public interface Function1<V, R> {
    /**
     * f(v)
     */
    R apply(V v);

    /**
     * returns new function g(f(x))
     */
    default <T> Function1<V, T> compose(Function1<? super R, ? extends T> g) {
        return v -> g.apply(apply(v));
    }
}
