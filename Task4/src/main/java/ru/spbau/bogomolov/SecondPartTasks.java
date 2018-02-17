package ru.spbau.bogomolov;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SecondPartTasks {

    private SecondPartTasks() {}

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        return paths
                .stream()
                .flatMap(
                        path -> {
                            Stream<String> lines = null;
                            try {
                                lines = Files.lines(Paths.get(path));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return lines;
                        }
                )
                .filter(line -> line.contains(sequence))
                .collect(Collectors.toList());
    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать, какова вероятность попасть в мишень.
    public static double piDividedBy4() {
        class Pair {
            private double x, y;
            private Pair (double x, double y) {
                this.x = x;
                this.y = y;
            }
            private boolean check() {
                return (x - 0.5) * (x - 0.5) + (y - 0.5) * (y - 0.5) <= 0.25;
            }
        }
        final int POINTS = 10000000;
        Random random = new Random();
        return Stream.generate(() -> new Pair(random.nextDouble(), random.nextDouble()))
                .limit(POINTS)
                .filter(Pair::check)
                .count() * 1. / POINTS;
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
        return compositions
                .entrySet()
                .stream()
                .max(
                    Comparator.comparingInt(
                            comps -> comps
                                    .getValue()
                                    .stream()
                                    .mapToInt(String::length)
                                    .sum()
                    )
                )
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders
                .stream()
                .flatMap(
                        order -> order.entrySet().stream()
                )
                .collect(
                        Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.summingInt(Map.Entry::getValue)
                        )
                );
    }
}