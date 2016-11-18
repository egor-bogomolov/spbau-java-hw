package ru.spbau.bogomolov;

@FunctionalInterface
public interface Predicate<T> extends Function1<T, Boolean>{

    default Predicate<T> and(Predicate<? super T> predicate) {
        return t -> apply(t) && predicate.apply(t);
    }

    default Predicate<T> or(Predicate<? super T> predicate) {
        return t -> apply(t) || predicate.apply(t);
    }

    default Predicate<T> not() {
        return t -> !apply(t);
    }

    Predicate<?> ALWAYS_TRUE = o -> true;
    Predicate<?> ALWAYS_FALSE = o -> false;
}
