package ru.spbau.bogomolov;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Collections {

    public static <T, R> List<R> map(Function1<? super T, R> function, Iterable<T> iterable) {
        List<R> list = new ArrayList<>();
        for (T element : iterable) {
            list.add(function.apply(element));
        }
        return list;
    }

    public static <T> List<T> filter(Predicate<? super T> predicate, Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T element : iterable) {
            if (predicate.apply(element)) {
                list.add(element);
            }
        }
        return list;
    }

    public static <T> List<T> takeWhile(Predicate<? super T> predicate, Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T element : iterable) {
            if (!predicate.apply(element)) {
                break;
            }
            list.add(element);
        }
        return list;
    }

    public static <T> List<T> takeUnless(Predicate<? super T> predicate, Iterable<T> iterable) {
        return takeWhile(predicate.not(), iterable);
    }

    private static <T, R> R recursiveFoldr(Function2<? super T, ? super R, ? extends R> function,
                                             R init, Iterator<T> iterator) {
        if (!iterator.hasNext()) {
            return init;
        }
        T element = iterator.next();
        return function.apply(element, recursiveFoldr(function, init, iterator));
    }

    public static <T, R> R foldr(Function2<? super T, ? super R, ? extends R> function,
                                 R init, Iterable<T> iterable) {
        return recursiveFoldr(function, init, iterable.iterator());
    }

    public static <T, R> R foldl(Function2<? super R, ? super T, ? extends R> function,
                                 R init, Iterable<T> iterable) {
        R result = init;
        for (T element : iterable) {
            result = function.apply(result, element);
        }
        return result;
    }
}
