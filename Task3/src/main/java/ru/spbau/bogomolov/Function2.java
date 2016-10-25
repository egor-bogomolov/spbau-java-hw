package ru.spbau.bogomolov;

/**
 * Function f, takes 2 arguments of types T and V, returns value of type R
 */
@FunctionalInterface
public interface Function2<T, V, R> {
    /**
     * f(t, v)
     */
    R apply(T t, V v);

    /**
     * Returns new function g(f(x, y))
     */
    default <U> Function2<T, V, U> compose(Function1<? super R, ? extends U> g) {
        return (t, v) -> g.apply(apply(t, v));
    }

    /**
     * Returns new function f(_, y)
     */
    default Function1<V, R> bind1(T t) {
        return v -> apply(t, v);
    }

    /**
     * Returns new function f(x, _)
     */
    default Function1<T, R> bind2(V v) {
        return t -> apply(t, v);
    }

    /**
     * Returns new function T -> (V -> R)
     */
    default Function1<T, Function1<V, R>> curry() {
        return t -> v -> Function2.this.apply(t, v);
    }
}
