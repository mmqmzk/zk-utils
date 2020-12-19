package me.zhoukun;

import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.*;

/**
 * Created on 2020/12/19 18:14.
 *
 * @author 周锟
 */
public enum Utils {
    ;
    public static <T> Consumer<T> empty() {
        return t -> {
        };
    }

    public static <T, V> BiConsumer<T, V> bEmpty() {
        return (t, v) -> {
        };
    }

    public static <T, V, R> Function<T, R> join(@NonNull Function<T, V> before,
            @NonNull Function<V, R> after) {
        return before.andThen(after);
    }

    public static <T, U, V, R> Function<T, R> join(@NonNull Function<T, U> func1,
            @NonNull Function<U, V> func2,
            @NonNull Function<V, R> func3) {
        return func1.andThen(func2).andThen(func3);
    }

    public static <T, U, V, W, R> Function<T, R> join(@NonNull Function<T, U> func1,
            @NonNull Function<U, V> func2,
            @NonNull Function<V, W> func3,
            @NonNull Function<W, R> func4) {
        return func1.andThen(func2).andThen(func3).andThen(func4);
    }

    public static <T, V> Predicate<T> joinP(@NonNull Function<T, V> function,
            @NonNull Predicate<V> predicate) {
        return t -> predicate.test(function.apply(t));
    }

    public static <T, U, V> Predicate<T> joinP(@NonNull Function<T, U> func1,
            @NonNull Function<U, V> func2,
            @NonNull Predicate<V> predicate) {
        return t -> predicate.test(func2.apply(func1.apply(t)));
    }

    public static <T, U, V, W> Predicate<T> joinP(@NonNull Function<T, U> func1,
            @NonNull Function<U, V> func2,
            @NonNull Function<V, W> func3,
            @NonNull Predicate<W> predicate) {
        return t -> predicate.test(func3.apply(func2.apply(func1.apply(t))));
    }

    public static <T, U, V, W, X> Predicate<T> joinP(@NonNull Function<T, U> func1,
            @NonNull Function<U, V> func2,
            @NonNull Function<V, W> func3,
            @NonNull Function<W, X> func4,
            @NonNull Predicate<X> predicate) {
        return t -> predicate.test(func4.apply(func3.apply(func2.apply(func1.apply(t)))));
    }

    public static <T, R> Consumer<T> joinC(@NonNull Function<T, R> function,
            @NonNull Consumer<R> consumer) {
        return t -> consumer.accept(function.apply(t));
    }

    public static <T, U, V> Consumer<T> joinC(@NonNull Function<T, U> func1,
            @NonNull Function<U, V> func2,
            @NonNull Consumer<V> consumer) {
        return t -> consumer.accept(func2.apply(func1.apply(t)));
    }

    public static <T, U, V, W> Consumer<T> joinC(@NonNull Function<T, U> func1,
            @NonNull Function<U, V> func2,
            @NonNull Function<V, W> func3,
            @NonNull Consumer<W> consumer) {
        return t -> consumer.accept(func3.apply(func2.apply(func1.apply(t))));
    }

    @SafeVarargs
    public static <T> Consumer<T> joinCC(@NonNull Consumer<T>... consumers) {
        return Arrays.stream(consumers)
                .filter(Objects::nonNull)
                .reduce(Consumer::andThen)
                .orElseGet(Utils::empty);
    }

    public static <T> Consumer<T> joinCC(@Nullable Collection<Consumer<T>> consumers) {
        if (consumers == null || consumers.isEmpty()) {
            return empty();
        }
        return consumers.stream()
                .filter(Objects::nonNull)
                .reduce(Consumer::andThen)
                .orElseGet(Utils::empty);
    }

    @SafeVarargs
    public static <T, R> Function<T, R> joinCS(@NonNull Supplier<R> supplier, @NonNull Consumer<T>... consumers) {
        return t -> {
            joinCC(consumers).accept(t);
            return supplier.get();
        };
    }

    public static <T, R> Function<T, R> joinCS(@NonNull Supplier<R> supplier,
            @NonNull Collection<Consumer<T>> consumers) {
        return t -> {
            joinCC(consumers).accept(t);
            return supplier.get();
        };
    }

    @SafeVarargs
    public static <T> UnaryOperator<T> c2f(@NonNull Consumer<T>... consumers) {
        return t -> {
            joinCC(consumers).accept(t);
            return t;
        };
    }

    public static <T> UnaryOperator<T> c2f(@Nullable Collection<Consumer<T>> consumers) {
        return t -> {
            joinCC(consumers).accept(t);
            return t;
        };
    }

    @SafeVarargs
    public static <T, R> Function<T, R> joinCF(@NonNull Function<T, R> func, @NonNull Consumer<T>... consumers) {
        return t -> {
            joinCC(consumers).accept(t);
            return func.apply(t);
        };
    }

    public static <T, R> Function<T, R> joinCF(@NonNull Function<T, R> func,
            @NonNull Collection<Consumer<T>> consumers) {
        return t -> {
            joinCC(consumers).accept(t);
            return func.apply(t);
        };
    }

    public static <T> Function<T, Boolean> p2F(@NonNull Predicate<T> predicate) {
        return predicate::test;
    }

    public static <T> Predicate<T> f2P(@NonNull Function<T, Boolean> func) {
        return t -> Optional.ofNullable(func.apply(t)).orElse(Boolean.FALSE);
    }

    public static <T> Consumer<T> ifThen(@NonNull Predicate<T> predicate, @NonNull Consumer<T> thenFunc) {
        return t -> {
            if (predicate.test(t)) {
                thenFunc.accept(t);
            }
        };
    }

    public static <T> Consumer<T> ifThenElse(@NonNull Predicate<T> predicate,
            @NonNull Consumer<T> thenFunc, @NonNull Consumer<T> elseFunc) {
        return t -> {
            if (predicate.test(t)) {
                thenFunc.accept(t);
            } else {
                elseFunc.accept(t);
            }
        };
    }

    public static <T> Supplier<T> constant(T value) {
        return () -> value;
    }

    public static IntSupplier constantInt(int value) {
        return () -> value;
    }

    public static LongSupplier constantLong(long value) {
        return () -> value;
    }

    public static DoubleSupplier constantDouble(double value) {
        return () -> value;
    }

    public static <T> Predicate<T> always(boolean value) {
        return t -> value;
    }

    public static <T> Predicate<T> alwaysTrue() {
        return always(true);
    }

    public static <T> Predicate<T> alwaysFalse() {
        return always(false);
    }

    public static <T, R> Function<T, R> always(R value) {
        return t -> value;
    }

    public static <T> UnaryOperator<T> identity() {
        return t -> t;
    }

    public static <T> Predicate<T> equal(T t) {
        return Predicate.isEqual(t);
    }

    public static <T> Predicate<T> notEqual(T t) {
        return Predicate.<T>isEqual(t).negate();
    }

    public static <T, R> Function<T, R> forSupplier(@NonNull Supplier<R> supplier) {
        return t -> supplier.get();
    }

    public static <T> ToIntFunction<T> forISupplier(@NonNull IntSupplier supplier) {
        return t -> supplier.getAsInt();
    }

    public static <T> ToLongFunction<T> forLSupplier(@NonNull LongSupplier supplier) {
        return t -> supplier.getAsLong();
    }

    public static <T> ToDoubleFunction<T> forDSupplier(@NonNull DoubleSupplier supplier) {
        return t -> supplier.getAsDouble();
    }

    public static <T, R> Function<T, R> forMap(@NonNull Map<T, R> map) {
        return forMapDefault(map, null);
    }

    public static <T, R> Function<T, Optional<R>> forMapOpt(@NonNull Map<T, R> map) {
        return key -> Optional.ofNullable(map.get(key));
    }

    public static <T, R> Function<T, R> forMapDefault(@NonNull Map<T, R> map, R defaultValue) {
        return key -> map.getOrDefault(key, defaultValue);
    }

    public static <T, V, R> Function<V, R> bindFirst(@NonNull BiFunction<T, V, R> func, T first) {
        return v -> func.apply(first, v);
    }

    public static <T, V, R> Function<T, R> bindSecond(@NonNull BiFunction<T, V, R> func, V second) {
        return t -> func.apply(t, second);
    }

    public static <T> UnaryOperator<T> bBindFirst(@NonNull BinaryOperator<T> func, T first) {
        return v -> func.apply(first, v);
    }

    public static <T> UnaryOperator<T> bBindSecond(@NonNull BinaryOperator<T> func, T second) {
        return t -> func.apply(t, second);
    }

    public static <T> Supplier<T> uBindFirst(@NonNull UnaryOperator<T> func, T first) {
        return () -> func.apply(first);
    }

    public static <T, V> Consumer<V> cBindFirst(@NonNull BiConsumer<T, V> func, T first) {
        return v -> func.accept(first, v);
    }

    public static <T, V> Consumer<T> cBindSecond(@NonNull BiConsumer<T, V> func, V second) {
        return t -> func.accept(t, second);
    }

    public static <T, V> Predicate<V> pBindFirst(@NonNull BiPredicate<T, V> func, T first) {
        return v -> func.test(first, v);
    }

    public static <T, V> Predicate<T> pBindSecond(@NonNull BiPredicate<T, V> func, V second) {
        return t -> func.test(t, second);
    }

    public static <T> BooleanSupplier bsBindFirst(@NonNull Predicate<T> predicate, T first) {
        return () -> predicate.test(first);
    }

    public static <T, R> Supplier<R> sBindFirst(@NonNull Function<T, R> func, T first) {
        return () -> func.apply(first);
    }

    public static <T> Runnable rBindFirst(@NonNull Consumer<T> consumer, T first) {
        return () -> consumer.accept(first);
    }

    public static <T> Stream<T> copyStream(@Nullable Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Stream.empty();
        }
        return new ArrayList<>(collection).stream();
    }

    @SafeVarargs
    public static <T> Stream<T> copyStream(T... array) {
        if (array == null || array.length == 0) {
            return Stream.empty();
        }
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static IntStream copyIntStream(int... array) {
        if (array == null || array.length == 0) {
            return IntStream.empty();
        }
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static LongStream copyLongStream(long... array) {
        if (array == null || array.length == 0) {
            return LongStream.empty();
        }
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static DoubleStream copyDoubleStream(double... array) {
        if (array == null || array.length == 0) {
            return DoubleStream.empty();
        }
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    @SafeVarargs
    public static <T> Stream<T> joinStream(Collection<? extends T>... collections) {
        return Arrays.stream(collections).filter(Objects::nonNull).flatMap(Collection::stream);
    }

    @SafeVarargs
    public static <T, V extends T> Stream<T> joinStream(V[]... arrays) {
        return Arrays.stream(arrays).filter(Objects::nonNull).flatMap(Arrays::stream);
    }

    @SafeVarargs
    public static <T, V extends T, S extends T> Stream<T> addStream(@Nullable Collection<V> collection, S... values) {
        Stream<V> stream;
        if (collection == null || collection.isEmpty()) {
            if (values.length == 0) {
                return Stream.empty();
            }
            stream = Stream.empty();
        } else {
            stream = collection.stream();
        }
        return Stream.concat(stream, Arrays.stream(values));
    }

    @SafeVarargs
    public static <T, V extends T, S extends T> Stream<T> addStream(@Nullable V[] array, S... values) {
        Stream<V> stream;
        if (array == null || array.length == 0) {
            if (values.length == 0) {
                return Stream.empty();
            }
            stream = Stream.empty();
        } else {
            stream = Arrays.stream(array);
        }
        return Stream.concat(stream, Arrays.stream(values));
    }

    public static <T> Predicate<T> negate(@NonNull Predicate<T> predicate) {
        return predicate.negate();
    }

    @SafeVarargs
    public static <T> Predicate<T> or(@NonNull Predicate<? super T>... predicates) {
        if (predicates == null || predicates.length == 0) {
            return alwaysFalse();
        }
        return t ->
                Arrays.stream(predicates)
                        .filter(Objects::nonNull)
                        .anyMatch(pBindSecond(Predicate::test, t));
    }

    public static <T> Predicate<T> or(@Nullable Collection<Predicate<? super T>> predicates) {
        if (predicates == null || predicates.isEmpty()) {
            return alwaysFalse();
        }
        return t ->
                predicates.stream()
                        .filter(Objects::nonNull)
                        .anyMatch(pBindSecond(Predicate::test, t));
    }

    @SafeVarargs
    public static <T> Predicate<T> and(@NonNull Predicate<? super T>... predicates) {
        if (predicates == null || predicates.length == 0) {
            return alwaysTrue();
        }
        return t ->
                Arrays.stream(predicates)
                        .filter(Objects::nonNull)
                        .allMatch(pBindSecond(Predicate::test, t));
    }

    public static <T> Predicate<T> and(@Nullable Collection<Predicate<? super T>> predicates) {
        if (predicates == null || predicates.isEmpty()) {
            return alwaysTrue();
        }
        return t ->
                predicates.stream()
                        .filter(Objects::nonNull)
                        .allMatch(pBindSecond(Predicate::test, t));
    }

    @SafeVarargs
    public static <T> Predicate<T> xor(@NonNull Predicate<? super T>... predicates) {
        if (predicates == null || predicates.length == 0) {
            return alwaysFalse();
        }
        return t ->
                Arrays.stream(predicates)
                        .filter(Objects::nonNull)
                        .map(bindSecond(Predicate::test, t))
                        .reduce(Boolean.FALSE, Boolean::logicalXor);
    }

    public static <T> Predicate<T> xor(@Nullable Collection<Predicate<? super T>> predicates) {
        if (predicates == null || predicates.isEmpty()) {
            return alwaysFalse();
        }
        return t ->
                predicates.stream()
                        .filter(Objects::nonNull)
                        .map(bindSecond(Predicate::test, t))
                        .reduce(Boolean.FALSE, Boolean::logicalXor);
    }

    public static <T, R> Function<T, R> sIgnoreFirst(@NonNull Supplier<R> supplier) {
        return t -> supplier.get();
    }

    public static <T, V, R> BiFunction<T, V, R> ignoreFirst(@NonNull Function<V, R> func) {
        return (t, v) -> func.apply(v);
    }

    public static <T, V, R> BiFunction<T, V, R> ignoreSecond(@NonNull Function<T, R> func) {
        return (t, v) -> func.apply(t);
    }

    public static <T> BinaryOperator<T> bIgnoreFirst(@NonNull UnaryOperator<T> func) {
        return (t, v) -> func.apply(v);
    }

    public static <T> BinaryOperator<T> bIgnoreSecond(@NonNull UnaryOperator<T> func) {
        return (t, v) -> func.apply(t);
    }

    public static <T> Predicate<T> pIgnoreFirst(@NonNull BooleanSupplier supplier) {
        return t -> supplier.getAsBoolean();
    }

    public static <T, V> BiPredicate<T, V> pIgnoreFirst(@NonNull Predicate<V> func) {
        return (t, v) -> func.test(v);
    }

    public static <T, V> BiPredicate<T, V> pIgnoreSecond(@NonNull Predicate<T> func) {
        return (t, v) -> func.test(t);
    }

    public static <T> Consumer<T> cIgnoreFirst(@NonNull Runnable runnable) {
        return t -> runnable.run();
    }

    public static <T, V> BiConsumer<T, V> cIgnoreFirst(@NonNull Consumer<V> consumer) {
        return (t, v) -> consumer.accept(v);
    }

    public static <T, V> BiConsumer<T, V> cIgnoreSecond(@NonNull Consumer<T> consumer) {
        return (t, v) -> consumer.accept(t);
    }

    public static UnaryOperator<Integer> plusNBoxed(int n) {
        return i -> i == null ? null : i + n;
    }

    public static IntUnaryOperator plusN(int n) {
        return i -> i + n;
    }

    public static UnaryOperator<Integer> negateBoxed() {
        return i -> i == null ? null : -i;
    }

    public static IntUnaryOperator negate() {
        return i -> -i;
    }

    public static UnaryOperator<Long> plusNBoxed(long n) {
        return l -> l == null ? null : l + n;
    }

    public static LongUnaryOperator plusN(long n) {
        return l -> l + n;
    }

    public static UnaryOperator<Long> negateLBoxed() {
        return l -> l == null ? null : -l;
    }

    public static LongUnaryOperator negateL() {
        return l -> -l;
    }

    public static UnaryOperator<Double> plusNBoxed(double n) {
        return d -> d == null ? null : d + n;
    }

    public static DoubleUnaryOperator plusN(double n) {
        return d -> d + n;
    }

    public static UnaryOperator<Double> negateDBoxed() {
        return d -> d == null ? null : -d;
    }

    public static DoubleUnaryOperator negateD() {
        return d -> -d;
    }

    public static Predicate<Integer> greaterBoxed(int i) {
        return t -> t != null && t > i;
    }

    public static IntPredicate greater(int i) {
        return t -> t > i;
    }

    public static Predicate<Integer> greaterOrEqualBoxed(int i) {
        return t -> t != null && t >= i;
    }

    public static IntPredicate greaterOrEqual(int i) {
        return t -> t >= i;
    }

    public static Predicate<Integer> lessBoxed(int i) {
        return t -> t != null && t < i;
    }

    public static IntPredicate less(int i) {
        return t -> t < i;
    }

    public static Predicate<Integer> lessOrEqualBoxed(int i) {
        return t -> t != null && t <= i;
    }

    public static IntPredicate lessOrEqual(int i) {
        return t -> t <= i;
    }

    public static Predicate<Long> greaterBoxed(long l) {
        return t -> t != null && t > l;
    }

    public static LongPredicate greater(long l) {
        return t -> t > l;
    }

    public static Predicate<Long> greaterOrEqualBoxed(long l) {
        return t -> t != null && t >= l;
    }

    public static LongPredicate greaterOrEqual(long l) {
        return t -> t >= l;
    }

    public static Predicate<Long> lessBoxed(long l) {
        return t -> t != null && t < l;
    }

    public static LongPredicate less(long l) {
        return t -> t < l;
    }

    public static Predicate<Long> lessOrEqualBoxed(long l) {
        return t -> t != null && t <= l;
    }

    public static LongPredicate lessOrEqual(long l) {
        return t -> t <= l;
    }

    public static Predicate<Double> greaterBoxed(double d) {
        return t -> t != null && t > d;
    }

    public static DoublePredicate greater(double d) {
        return t -> t > d;
    }

    public static Predicate<Double> greaterOrEqualBoxed(double d) {
        return t -> t != null && t >= d;
    }

    public static DoublePredicate greaterOrEqual(double d) {
        return t -> t >= d;
    }

    public static Predicate<Double> lessBoxed(double d) {
        return t -> t != null && t < d;
    }

    public static DoublePredicate less(double d) {
        return t -> t < d;
    }

    public static Predicate<Double> lessOrEqualBoxed(double d) {
        return t -> t != null && t <= d;
    }

    public static DoublePredicate lessOrEqual(double d) {
        return t -> t <= d;
    }

    @SafeVarargs
    public static <T> Predicate<T> inArray(T... array) {
        return pBindFirst(ArrayUtils::contains, array);
    }

    public static IntPredicate inInts(int... array) {
        return unBoxIP(pBindFirst(ArrayUtils::contains, array));
    }

    public static LongPredicate inLongs(long... array) {
        return unBoxLP(pBindFirst(ArrayUtils::contains, array));
    }

    public static DoublePredicate inDoubles(double... array) {
        return unBoxDP(pBindFirst(ArrayUtils::contains, array));
    }

    public static <T> Predicate<T> inCollection(@Nullable Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return alwaysFalse();
        }
        return pBindFirst(Collection::contains, collection);
    }

    public static <T> Predicate<T> inString(@Nullable String str) {
        if (str == null) {
            return alwaysFalse();
        }
        return and(Objects::nonNull, joinP(Object::toString, pBindFirst(String::contains, str)));
    }

    public static <T> Predicate<T> inStringIgnoreCase(@Nullable String str) {
        if (str == null) {
            return alwaysFalse();
        }
        return and(Objects::nonNull, joinP(Object::toString, pBindFirst(StringUtils::containsIgnoreCase, str)));
    }

    public static <T> ToIntFunction<T[]> indexOf(@Nullable T value) {
        return array -> ArrayUtils.indexOf(array, value);
    }

    public static <T> ToIntFunction<List<T>> listIndexOf(@Nullable T value) {
        return list -> list == null || list.isEmpty() ? -1 : list.indexOf(value);
    }

    public static Function<String, Integer> stringIndexOf(Object value) {
        if (value == null) {
            return always(-1);
        }
        return bindSecond(StringUtils::indexOf, String.valueOf(value));
    }

    public static ToIntFunction<int[]> indexOf(int value) {
        return array -> ArrayUtils.indexOf(array, value);
    }

    public static ToIntFunction<long[]> indexOf(long value) {
        return array -> ArrayUtils.indexOf(array, value);
    }

    public static ToIntFunction<double[]> indexOf(double value) {
        return array -> ArrayUtils.indexOf(array, value);
    }

    public static <T> Predicate<T[]> arrayContains(T value) {
        return pBindSecond(ArrayUtils::contains, value);
    }

    public static Predicate<int[]> intsContains(int value) {
        return pBindSecond(ArrayUtils::contains, value);
    }

    public static Predicate<long[]> longsContains(long value) {
        return pBindSecond(ArrayUtils::contains, value);
    }

    public static Predicate<double[]> doublesContains(double value) {
        return pBindSecond(ArrayUtils::contains, value);
    }

    public static <T> Predicate<Collection<T>> collectionContains(@Nullable T value) {
        return and(Objects::nonNull, negate(Collection::isEmpty), pBindSecond(Collection::contains, value));
    }

    public static Predicate<String> stringContains(@Nullable Object value) {
        if (value == null) {
            return alwaysFalse();
        }
        return pBindSecond(StringUtils::contains, String.valueOf(value));
    }

    public static Predicate<Collection<String>> containsIgnoreCase(@Nullable String value) {
        if (value == null) {
            return alwaysFalse();
        }
        return and(Objects::nonNull, negate(Collection::isEmpty),
                joinP(Collection::stream, pBindSecond(Stream::anyMatch, pBindFirst(String::equalsIgnoreCase, value))));
    }

    public static BinaryOperator<String> stringJoinBy(@Nullable String separator) {
        String sep = separator == null ? StringUtils.EMPTY : separator;
        return (t, s) -> t + sep + s;
    }

    public static Function<String, String[]> splitBy(@Nullable String separator) {
        if (separator == null) {
            separator = StringUtils.EMPTY;
        }
        Pattern pattern = Pattern.compile(separator);
        return pattern::split;
    }

    public static BiFunction<String, Integer, String[]> splitWithLimit(@Nullable String separator) {
        if (separator == null) {
            separator = StringUtils.EMPTY;
        }
        Pattern pattern = Pattern.compile(separator);
        return pattern::split;
    }

    public static Function<String, Stream<String>> splitToStream(@Nullable String separator) {
        if (separator == null) {
            separator = StringUtils.EMPTY;
        }
        Pattern pattern = Pattern.compile(separator);
        return join(pattern::split, Arrays::stream);
    }

    public static Function<CharSequence, List<String>> splitToList(@Nullable String separator) {
        if (separator == null) {
            separator = StringUtils.EMPTY;
        }
        Pattern pattern = Pattern.compile(separator);
        return input -> {
            if (input == null) {
                return Collections.emptyList();
            }
            return pattern.splitAsStream(input).collect(Collectors.toList());
        };
    }

    public static Function<String, Map<String, String>> splitToMap(
            @Nullable String keyValueSeparator,
            @Nullable String entrySeparator) {
        if (keyValueSeparator == null) {
            keyValueSeparator = StringUtils.EMPTY;
        }
        Pattern kvPattern = Pattern.compile(keyValueSeparator);
        if (entrySeparator == null) {
            entrySeparator = StringUtils.EMPTY;
        }
        Pattern entryPattern = Pattern.compile(entrySeparator);
        Function<Stream<String>, String> keyFunc = bindSecond(Utils::first, StringUtils.EMPTY);
        Function<Stream<String>, String> valueFunc = bindSecond(Utils::second, StringUtils.EMPTY);
        return input -> {
            if (input == null || input.isEmpty()) {
                return Collections.emptyMap();
            }
            return entryPattern.splitAsStream(input)
                    .map(kvPattern::splitAsStream)
                    .collect(Collectors.toMap(keyFunc, valueFunc, bSecondArg()));
        };
    }

    public static Function<Object[], String> arrayJoinBy(@Nullable String separator) {
        return bindSecond(StringUtils::join, separator);
    }

    public static <T> Function<Iterable<T>, String> iterableJoinBy(@Nullable String separator) {
        return bindSecond(StringUtils::join, separator);
    }

    public static <T, V> Function<Map<T, V>, String> mapJoinBy(
            @NonNull String keyValueSeparator,
            @NonNull String entrySeparator) {
        return map -> {
            if (map == null || map.isEmpty()) {
                return StringUtils.EMPTY;
            }
            return map.entrySet().stream()
                    .map(entry -> entry.getKey() + keyValueSeparator + entry.getValue())
                    .collect(Collectors.joining(entrySeparator));
        };
    }

    public static UnaryOperator<String> append(String suffix) {
        return bBindSecond(String::concat, suffix);
    }

    public static UnaryOperator<String> prepend(String prefix) {
        return bBindFirst(String::concat, prefix);
    }

    public static <T> Stream<T> reverse(@Nullable Stream<T> stream) {
        if (stream == null) {
            return Stream.empty();
        }
        Deque<T> deque = new ArrayDeque<>();
        stream.sequential().forEachOrdered(deque::addFirst);
        return deque.stream().sequential();
    }

    public static IntStream reverse(@Nullable IntStream stream) {
        if (stream == null) {
            return IntStream.empty();
        }
        return reverse(stream.boxed()).mapToInt(Integer::intValue);
    }

    public static LongStream reverse(@Nullable LongStream stream) {
        if (stream == null) {
            return LongStream.empty();
        }
        return reverse(stream.boxed()).mapToLong(Long::longValue);
    }

    public static DoubleStream reverse(@Nullable DoubleStream stream) {
        if (stream == null) {
            return DoubleStream.empty();
        }
        return reverse(stream.boxed()).mapToDouble(Double::doubleValue);
    }

    public static <T> Stream<T> reverseStream(@Nullable Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Stream.empty();
        }
        Deque<T> deque = new ArrayDeque<>(collection.size());
        collection.stream().sequential().forEach(deque::addFirst);
        return deque.stream().sequential();
    }

    @SafeVarargs
    public static <T> Stream<T> reverseStream(T... array) {
        if (array == null || array.length == 0) {
            return Stream.empty();
        }
        ArrayUtils.reverse(Arrays.copyOf(array, array.length));
        return Arrays.stream(array).sequential();
    }

    public static IntStream reverseStream(int... array) {
        if (array == null || array.length == 0) {
            return IntStream.empty();
        }
        ArrayUtils.reverse(Arrays.copyOf(array, array.length));
        return Arrays.stream(array).sequential();
    }

    public static LongStream reverseStream(long... array) {
        if (array == null || array.length == 0) {
            return LongStream.empty();
        }
        ArrayUtils.reverse(Arrays.copyOf(array, array.length));
        return Arrays.stream(array).sequential();
    }

    public static DoubleStream reverseStream(double... array) {
        if (array == null || array.length == 0) {
            return DoubleStream.empty();
        }
        ArrayUtils.reverse(Arrays.copyOf(array, array.length));
        return Arrays.stream(array).sequential();
    }

    public static <R> IntFunction<R> unBoxI(@NonNull Function<Integer, R> func) {
        return func::apply;
    }

    public static <R> LongFunction<R> unBoxL(@NonNull Function<Long, R> func) {
        return func::apply;
    }

    public static <R> DoubleFunction<R> unBoxD(@NonNull Function<Double, R> func) {
        return func::apply;
    }

    public static <T> ToIntFunction<T> unBoxTI(@NonNull Function<T, Integer> func) {
        return t -> Optional.ofNullable(func.apply(t)).orElse(0);
    }

    public static <T> ToLongFunction<T> unBoxTL(@NonNull Function<T, Long> func) {
        return t -> Optional.ofNullable(func.apply(t)).orElse(0L);
    }

    public static <T> ToDoubleFunction<T> unBoxTD(@NonNull Function<T, Double> func) {
        return t -> Optional.ofNullable(func.apply(t)).orElse(0.0);
    }

    public static IntUnaryOperator unBoxIU(@NonNull UnaryOperator<Integer> func) {
        return i -> Optional.ofNullable(func.apply(i)).orElse(0);
    }

    public static LongUnaryOperator unBoxLU(@NonNull UnaryOperator<Long> func) {
        return i -> Optional.ofNullable(func.apply(i)).orElse(0L);
    }

    public static DoubleUnaryOperator unBoxDU(@NonNull UnaryOperator<Double> func) {
        return i -> Optional.ofNullable(func.apply(i)).orElse(0.0);
    }

    public static IntPredicate unBoxIP(Predicate<Integer> predicate) {
        return predicate::test;
    }

    public static LongPredicate unBoxLP(Predicate<Long> predicate) {
        return predicate::test;
    }

    public static DoublePredicate unBoxDP(Predicate<Double> predicate) {
        return predicate::test;
    }

    public static <R> Function<Integer, R> box(@NonNull IntFunction<R> func) {
        return i -> func.apply(i == null ? 0 : i);
    }

    public static <R> Function<Long, R> box(@NonNull LongFunction<R> func) {
        return l -> func.apply(l == null ? 0L : l);
    }

    public static <R> Function<Double, R> box(@NonNull DoubleFunction<R> func) {
        return d -> func.apply(d == null ? 0.0 : d);
    }

    public static <T> Function<T, Integer> box(@NonNull ToIntFunction<T> func) {
        return func::applyAsInt;
    }

    public static <T> Function<T, Long> box(@NonNull ToLongFunction<T> func) {
        return func::applyAsLong;
    }

    public static <T> Function<T, Double> box(@NonNull ToDoubleFunction<T> func) {
        return func::applyAsDouble;
    }

    public static UnaryOperator<Integer> box(@NonNull IntUnaryOperator func) {
        return i -> func.applyAsInt(i == null ? 0 : i);
    }

    public static UnaryOperator<Long> box(@NonNull LongUnaryOperator func) {
        return l -> func.applyAsLong(l == null ? 0L : l);
    }

    public static UnaryOperator<Double> box(@NonNull DoubleUnaryOperator func) {
        return d -> func.applyAsDouble(d == null ? 0.0 : d);
    }

    public static Predicate<Integer> box(@NonNull IntPredicate predicate) {
        return and(Objects::nonNull, predicate::test);
    }

    public static Predicate<Long> box(@NonNull LongPredicate predicate) {
        return and(Objects::nonNull, predicate::test);
    }

    public static Predicate<Double> box(@NonNull DoublePredicate predicate) {
        return and(Objects::nonNull, predicate::test);
    }

    public static <T, R> Optional<R> foldSequential(Stream<T> stream, BiFunction<R, T, R> accumulator) {
        return Optional.ofNullable(foldSequential(stream, null, accumulator));
    }

    public static <T, R> R foldSequential(Stream<T> stream, R identity, BiFunction<R, T, R> accumulator) {
        if (stream == null) {
            return identity;
        }
        return fold(stream.sequential(), identity, accumulator, bFirstArg());
    }

    public static <T, R> Optional<R> fold(Stream<T> stream,
            BiFunction<R, T, R> accumulator, BinaryOperator<R> selector) {
        return Optional.ofNullable(fold(stream, null, accumulator, selector));
    }

    public static <T, R> R fold(Stream<T> stream, R identity,
            BiFunction<R, T, R> accumulator, BinaryOperator<R> selector) {
        if (stream == null) {
            return identity;
        }
        Holder<R> holder = new Holder<>(identity);
        BiFunction<Holder<R>, T, Holder<R>> wrapAccumulator = (h, t) ->
                new Holder<>(accumulator.apply(h.getValue(), t));
        BinaryOperator<Holder<R>> combiner = (h1, h2) ->
                h1.setValue(selector.apply(h1.getValue(), h2.getValue()));
        return stream.reduce(holder, wrapAccumulator, combiner).getValue();
    }

    public static <T, V> BiFunction<T, V, T> firstArg() {
        return (t, v) -> t;
    }

    public static <T> BinaryOperator<T> bFirstArg() {
        return (t, v) -> t;
    }

    public static <T, V> BiFunction<T, V, V> secondArg() {
        return (t, v) -> v;
    }

    public static <T> BinaryOperator<T> bSecondArg() {
        return (v, t) -> t;
    }

    public static <T> Optional<T> first(Stream<T> stream) {
        if (stream == null) {
            return Optional.empty();
        }
        return stream.findFirst();
    }

    public static <T> Optional<T> second(Stream<T> stream) {
        if (stream == null) {
            return Optional.empty();
        }
        return stream.skip(1).findFirst();
    }

    public static <T> Optional<T> third(Stream<T> stream) {
        if (stream == null) {
            return Optional.empty();
        }
        return stream.skip(2).findFirst();
    }

    public static <T> Optional<T> nth(Stream<T> stream, int n) {
        if (stream == null) {
            return Optional.empty();
        }
        return stream.skip(n - 1).findFirst();
    }

    public static <T> T first(Stream<T> stream, T defaultValue) {
        return first(stream).orElse(defaultValue);
    }

    public static <T> T second(Stream<T> stream, T defaultValue) {
        return second(stream).orElse(defaultValue);
    }

    public static <T> T third(Stream<T> stream, T defaultValue) {
        return third(stream).orElse(defaultValue);
    }

    public static <T> T nth(Stream<T> stream, int n, T defaultValue) {
        return nth(stream, n).orElse(defaultValue);
    }

    public static String getStackTrace() {
        return getStackTrace(1, 10);
    }

    public static String getStackTrace(int start, int stop) {
        if (start > stop) {
            throw new IllegalArgumentException("start > stop");
        }
        StringBuilder builder = new StringBuilder((stop - start + 1) * 50);
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int length = 2;
        if (stackTrace.length < start + length) {
            return builder.toString();
        }
        appendElement(builder, stackTrace[start + 1]);
        for (int i = start + length; i < stop + length + 1 && i < stackTrace.length; i++) {
            builder.append("<=");
            appendElement(builder, stackTrace[i]);
        }
        return builder.toString();
    }

    private static void appendElement(StringBuilder builder, StackTraceElement element) {
        String className = element.getClassName();
        String methodName = element.getMethodName();
        int index = className.lastIndexOf('.');
        builder.append(className.substring(index + 1))
                .append('.')
                .append(methodName)
                .append(':')
                .append(element.getLineNumber());
    }

    @SafeVarargs
    public static <T> T firstNonNull(T first, T second, T... rest) {
        if (first != null) {
            return first;
        } else if (second != null) {
            return second;
        } else {
            for (T t : rest) {
                if (t != null) {
                    return t;
                }
            }
            throw new NullPointerException();
        }
    }

    public static int firstNonZero(int first, int second, int... rest) {
        if (first != 0) {
            return first;
        } else if (second != 0) {
            return second;
        } else {
            for (int i : rest) {
                if (i != 0) {
                    return i;
                }
            }
            return 0;
        }
    }

    public static long firstNonZero(long first, long second, long... rest) {
        if (first != 0L) {
            return first;
        } else if (second != 0L) {
            return second;
        } else {
            for (long i : rest) {
                if (i != 0L) {
                    return i;
                }
            }
            return 0L;
        }
    }

    public static int nextInt(int i) {
        return ThreadLocalRandom.current().nextInt(i);
    }

    public static int random(int min, int max) {
        return nextInt(max - min + 1) + min;
    }

    public static boolean isLuck(int rate) {
        return isLuck(rate, 100);
    }

    public static boolean isLuck(int rate, int base) {
        return nextInt(base) < rate;
    }

    public static IntPredicate isLuckP(int base) {
        return rate -> isLuck(rate, base);
    }
    public static IntPredicate isLuck100P() {
        return rate -> isLuck(rate, 100);
    }

    public static OptionalInt randomChoose(@Nullable int[] array) {
        int length;
        if (array == null || (length = array.length) <= 0) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(get(array, nextInt(length)));
    }

    public static OptionalLong randomChoose(@Nullable long[] array) {
        int length;
        if (array == null || (length = array.length) <= 0) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(get(array, nextInt(length)));
    }

    public static OptionalDouble randomChoose(@Nullable double[] array) {
        int length;
        if (array == null || (length = array.length) <= 0) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(get(array, nextInt(length)));
    }

    public static <T> Optional<T> randomChoose(@Nullable T[] array) {
        return randomChoose(array, null);
    }

    public static <T> Optional<T> randomChoose(@Nullable T[] array, @Nullable ToIntFunction<T> weigher) {
        if (array == null || array.length == 0) {
            return Optional.empty();
        }
        int total = calculateTotalWeight(array, weigher);
        return randomChoose(Arrays.stream(array), total, weigher);
    }

    public static <T> Optional<T> randomChoose(@Nullable Collection<T> collection) {
        return randomChoose(collection, null);
    }

    public static <T> Optional<T> randomChoose(@Nullable Collection<T> collection, @Nullable ToIntFunction<T> weigher) {
        if (collection == null) {
            return Optional.empty();
        }
        int total = calculateTotalWeight(collection, weigher);
        return randomChoose(collection.stream(), total, weigher);
    }

    private static <T> Optional<T> randomChoose(Stream<T> stream, int total, ToIntFunction<T> weigher) {
        if (stream == null || total <= 0) {
            return Optional.empty();
        }
        int random = nextInt(total);
        if (weigher == null) {
            return stream.skip(random).findFirst();
        }
        Pair<Integer, T> tuple = Pair.of(random, null);
        BiFunction<Pair<Integer, T>, T, Pair<Integer, T>> accumulator = (t, obj) ->
                t.getLeft() < 0 ? t : Pair.of(t.getLeft() - getWeight(weigher, obj), obj);
        tuple = Utils.foldSequential(stream, tuple, accumulator);
        return Optional.ofNullable(tuple.getRight());
    }

    private static <T> int calculateTotalWeight(T[] array, ToIntFunction<T> weigher) {
        if (array == null) {
            return 0;
        }
        if (weigher == null) {
            return array.length;
        }
        return calculateTotalWeight(Arrays.stream(array), weigher);
    }

    private static <T> int calculateTotalWeight(Collection<T> collection, ToIntFunction<T> weigher) {
        if (collection == null) {
            return 0;
        }
        if (weigher == null) {
            return collection.size();
        }
        return calculateTotalWeight(collection.stream(), weigher);
    }

    private static <T> int calculateTotalWeight(Stream<T> stream, ToIntFunction<T> weigher) {
        if (stream == null) {
            return 0;
        }
        if (weigher == null) {
            return Math.toIntExact(stream.count());
        }
        return stream.mapToInt(weigher).sum();
    }

    public static int randomIndex(@Nullable int[] array) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return -1;
        }
        int total = Arrays.stream(array).sum();
        if (total <= 0) {
            return -1;
        }
        int random = nextInt(total);
        for (int i = 0; i < length; i++) {
            random -= array[i];
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    public static int randomIndex(@Nullable long[] array) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return -1;
        }
        long total = Arrays.stream(array).sum();
        if (total <= 0) {
            return -1;
        }
        long random = ThreadLocalRandom.current().nextLong(total);
        for (int i = 0; i < length; i++) {
            random -= array[i];
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    public static <T> int randomIndex(@Nullable T[] array, @NonNull ToIntFunction<T> weigher) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return -1;
        }
        int total = calculateTotalWeight(array, weigher);
        if (total <= 0) {
            return -1;
        }
        int random = nextInt(total);
        for (int i = 0; i < length; i++) {
            random -= getWeight(weigher, array[i]);
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    public static int randomIndex(@Nullable Integer[] array) {
        return randomIndex(array, Integer::intValue);
    }

    public static <T> int randomIndex(@Nullable List<T> list, @NonNull ToIntFunction<T> weigher) {
        int size;
        if (list == null || (size = list.size()) == 0) {
            return -1;
        }
        int total = calculateTotalWeight(list, weigher);
        if (total <= 0) {
            return -1;
        }
        int random = nextInt(total);
        for (int i = 0; i < size; i++) {
            random -= getWeight(weigher, list.get(i));
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    public static int randomIndex(@Nullable List<Integer> list) {
        return randomIndex(list, Integer::intValue);
    }

    public static <T> List<T> randomChooseN(@Nullable T[] array, int n) {
        return randomChooseN(array, n, null);
    }

    public static <T> List<T> randomChooseN(@Nullable T[] array, int n, @Nullable ToIntFunction<T> weigher) {
        if (array == null || array.length == 0 || n <= 0) {
            return Collections.emptyList();
        }
        int total = calculateTotalWeight(array, weigher);
        return randomChooseN(Arrays.stream(array), n, total, weigher);
    }

    public static <T> List<T> randomChooseN(@Nullable Collection<T> collection, int n) {
        return randomChooseN(collection, n, null);
    }

    public static <T> List<T> randomChooseN(@Nullable Collection<T> collection, int n,
            @Nullable ToIntFunction<T> weigher) {
        if (collection == null || collection.size() == 0 || n <= 0) {
            return Collections.emptyList();
        }
        int total = calculateTotalWeight(collection, weigher);
        return randomChooseN(collection.stream(), n, total, weigher);
    }

    private static <T> List<T> randomChooseN(Stream<T> stream, int n, int total, ToIntFunction<T> weigher) {
        if (stream == null || total <= 0 || n <= 0) {
            return Collections.emptyList();
        }
        Counter count = new Counter(n);
        Counter totalWeight = new Counter(total);
        return stream.filter(obj -> {
            if (count.gt0() && totalWeight.gt0()) {
                int weight = getWeight(weigher, obj);
                int random = nextInt(totalWeight.getAndDec(weight));
                if (random < weight * count.get()) {
                    count.decAndGet();
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    private static <T> int getWeight(ToIntFunction<T> weigher, T obj) {
        if (obj == null) {
            return 0;
        }
        return weigher == null ? 1 : weigher.applyAsInt(obj);
    }

    public static <T> Optional<T> randomRemove(@Nullable List<T> list) {
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(list.remove(nextInt(list.size())));
    }

    public static <T> Optional<T> randomRemove(@Nullable Collection<T> collection) {
        return randomRemove(collection, null);
    }

    public static <T> Optional<T> randomRemove(@Nullable Collection<T> collection, @Nullable ToIntFunction<T> weigher) {
        if (collection == null || collection.isEmpty()) {
            return Optional.empty();
        }
        List<T> list = randomRemoveN(collection, 1, weigher);
        return Optional.ofNullable(get(list, 0));
    }

    public static <T> List<T> randomRemoveN(@Nullable Collection<T> collection, int n) {
        return randomRemoveN(collection, n, null);
    }

    public static <T> List<T> randomRemoveN(@Nullable Collection<T> collection, int n,
            @Nullable ToIntFunction<T> weigher) {
        if (collection == null || collection.size() == 0 || n <= 0) {
            return Collections.emptyList();
        }
        List<T> result = Lists.newArrayListWithExpectedSize(n);
        if (n >= collection.size()) {
            result.addAll(collection);
            collection.clear();
            return result;
        }
        int total = calculateTotalWeight(collection, weigher);
        if (total <= 0) {
            return Collections.emptyList();
        }
        for (; n > 0 && total > 0; n--) {
            int random = nextInt(total);
            for (Iterator<T> iterator = collection.iterator(); iterator.hasNext(); ) {
                T t = iterator.next();
                int weight = getWeight(weigher, t);
                random -= weight;
                if (random < 0) {
                    result.add(t);
                    iterator.remove();
                    total -= weight;
                    break;
                }
            }
        }
        return result;
    }

    public static int get0(@Nullable int[] array) {
        return get(array, 0);
    }

    public static int get(@Nullable int[] array, int index) {
        return get(array, index, 0);
    }

    public static int get(@Nullable int[] array, int index, int defaultValue) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += length;
        }
        if (index < 0 || index >= length) {
            return defaultValue;
        }
        return array[index];
    }

    public static long get0(@Nullable long[] array) {
        return get(array, 0);
    }

    public static long get(@Nullable long[] array, int index) {
        return get(array, index, 0L);
    }

    public static long get(@Nullable long[] array, int index, long defaultValue) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += length;
        }
        if (index < 0 || index >= length) {
            return defaultValue;
        }
        return array[index];
    }

    public static double get0(@Nullable double[] array) {
        return get(array, 0);
    }

    public static double get(@Nullable double[] array, int index) {
        return get(array, index, 0.0);
    }

    public static double get(@Nullable double[] array, int index, double defaultValue) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += length;
        }
        if (index < 0 || index >= length) {
            return defaultValue;
        }
        return array[index];
    }

    public static <T> T get0(@Nullable T[] array) {
        return get(array, 0);
    }

    public static <T> T get(@Nullable T[] array, int index) {
        return get(array, index, null);
    }

    public static <T> T get(@Nullable T[] array, int index, T defaultValue) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += length;
        }
        if (index < 0 || index >= length) {
            return defaultValue;
        }
        T t = array[index];
        return t == null ? defaultValue : t;
    }

    public static <T> T get0(@Nullable Collection<T> collection) {
        return get(collection, 0);
    }

    public static <T> T get(@Nullable Collection<T> collection, int index) {
        return get(collection, index, null);
    }

    public static <T> T get(@Nullable Collection<T> collection, int index, T defaultValue) {
        int size;
        if (collection == null || (size = collection.size()) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += size;
        }
        if (index < 0 || index >= size) {
            return defaultValue;
        }
        T t = Iterables.get(collection, index);
        return t == null ? defaultValue : t;
    }

    public static <T> T get0(Stream<T> stream) {
        return get0(stream, null);
    }

    public static <T> T get0(Stream<T> stream, T defaultValue) {
        if (stream == null) {
            return defaultValue;
        }
        return stream.findFirst().orElse(defaultValue);
    }

    public static <T> T get(Stream<T> stream, int i) {
        return get(stream, i, null);
    }

    public static <T> T get(Stream<T> stream, int i, T defaultValue) {
        if (stream == null) {
            return defaultValue;
        }
        return stream.skip(i).findFirst().orElse(defaultValue);
    }

    public static <T> Function<T[], T> arrayGetI(int i) {
        return bindSecond(Utils::get, i);
    }

    public static <T> Function<Collection<T>, T> collectionGetI(int i) {
        return bindSecond(Utils::get, i);
    }

    public static Function<int[], Integer> intsGetI(int i) {
        return bindSecond(Utils::get, i);
    }

    public static <T, R> Ordering<T> orderingFromToIntFunction(@NonNull ToIntFunction<T> function) {
        return orderingFromToIntFunction(function, true);
    }

    public static <T, R> Ordering<T> orderingFromToIntFunction(@NonNull ToIntFunction<T> function, boolean nullsFirst) {
        Ordering<T> ordering = Ordering.from((a, b) ->
                a == b ? 0 : Integer.compare(function.applyAsInt(a), function.applyAsInt(b)));
        return nullsFirst ? ordering.nullsFirst() : ordering.nullsLast();
    }

    public static <T, R> Ordering<T> orderingFromFunction(@NonNull Function<T, Comparable<R>> function) {
        return orderingFromFunction(function, Ordering.natural(), true);
    }

    public static <T, R> Ordering<T> orderingFromFunction(@NonNull Function<T, Comparable<R>> function,
            boolean nullsFirst) {
        return orderingFromFunction(function, Ordering.natural(), nullsFirst);
    }

    public static <T, R> Ordering<T> orderingFromFunction(@NonNull Function<T, R> function, Comparator<R> comparator) {
        return orderingFromFunction(function, comparator, true);
    }

    public static <T, R> Ordering<T> orderingFromFunction(@NonNull Function<T, R> function,
            @NonNull Comparator<R> comparator, boolean nullsFirst) {
        Ordering<T> ordering = Ordering.from((a, b) ->
                a == b ? 0 : comparator.compare(function.apply(a), function.apply(b)));
        return nullsFirst ? ordering.nullsFirst() : ordering.nullsLast();
    }

    /**
     * 返回list中第一个小于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexLessThan(@Nullable List<Integer> list, @Nullable Integer target) {
        return index(list, target, true, false);
    }

    /**
     * 返回list中第一个小于等于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexLessThanOrEqualTo(@Nullable List<Integer> list, @Nullable Integer target) {
        return index(list, target, true, true);
    }

    /**
     * 返回list中第一个小于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */

    public static int indexLessThan(@Nullable int[] list, int target) {
        return index(list, target, true, false);
    }

    /**
     * 返回list中第一个小于等于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */

    public static int indexLessThanOrEqualTo(@Nullable int[] list, int target) {
        return index(list, target, true, true);
    }

    /**
     * 返回list中第一个大于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */

    public static int indexMoreThan(@Nullable List<Integer> list, @Nullable Integer target) {
        return index(list, target, false, false);
    }

    /**
     * 返回list中第一个大于等于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */

    public static int indexMoreThanOrEqualTo(@Nullable List<Integer> list, @Nullable Integer target) {
        return index(list, target, false, true);
    }

    /**
     * 返回list中第一个大于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */

    public static int indexMoreThan(@Nullable int[] list, int target) {
        return index(list, target, false, false);
    }

    /**
     * 返回list中第一个大于等于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexMoreThanOrEqualTo(@Nullable int[] list, int target) {
        return index(list, target, false, true);
    }

    private static int index(List<Integer> list, Integer target, boolean less, boolean equal) {
        if (list == null) {
            return -1;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Integer n = list.get(i);
            if (n == null) {
                if (equal && target == null) {
                    return i;
                }
            } else if ((less && target < n) || (!less && target > n) || (equal && n.equals(target))) {
                return i;
            }
        }
        return -1;
    }

    private static int index(int[] list, int target, boolean less, boolean equal) {
        if (list == null) {
            return -1;
        }
        int size = list.length;
        for (int i = 0; i < size; i++) {
            int n = list[i];
            if ((less && target < n) || (!less && target > n) || (equal && target == n)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 将字符串数组转换为int数组
     *
     * @param strings
     * @return
     */
    public static int[] parseToInts(@Nullable String[] strings) {
        if (strings == null) {
            return new int[0];
        }
        int[] ints = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            ints[i] = parseToInt(strings[i]);
        }
        return ints;
    }

    public static int[] parseToInts(@Nullable String data, @NonNull String separator) {
        if (data == null || separator == null) {
            return new int[0];
        }
        return parseToInts(data.trim().split(separator));
    }

    private static Number toNumber(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Number) {
            return (Number) object;
        } else if (object instanceof Boolean) {
            Boolean aBoolean = (Boolean) object;
            return aBoolean ? 1 : 0;
        } else if (object instanceof BigInteger) {
            return (Number) object;
        } else if (object instanceof BigDecimal) {
            return (Number) object;
        }
        return null;
    }

    public static int parseToInt(@Nullable Object object) {
        return Ints.saturatedCast(parseToLong(object));
    }

    public static int parseToInt(@Nullable String data) {
        return Ints.saturatedCast(parseToLong(data));
    }

    public static long parseToLong(@Nullable Object object) {
        if (object == null) {
            return 0L;
        }
        Number number = toNumber(object);
        if (number != null) {
            return number.longValue();
        }
        return parseToLong(object.toString());
    }

    public static long parseToLong(@Nullable String data) {
        if (data == null) {
            return 0L;
        }
        data = data.trim();
        int length = data.length();
        if (length == 0) {
            return 0L;
        }
        int radix = 10;
        if (data.charAt(0) == '0' && length > 1) {
            char c = data.charAt(1);
            switch (c) {
                case 'x':
                case 'X':
                    if (length > 2) {
                        data = data.substring(2);
                    } else {
                        return 0L;
                    }
                    radix = 16;
                    break;
                case 'b':
                case 'B':
                    if (length > 2) {
                        data = data.substring(2);
                    } else {
                        return 0L;
                    }
                    radix = 2;
                    break;
                default:
                    data = data.substring(1);
                    radix = 8;
                    break;
            }
            if (data.isEmpty()) {
                return 0L;
            }
        }
        Long aLong = null;
        try {
            aLong = Long.parseLong(data, radix);
        } catch (Exception ignore) {
        }
        return aLong == null ? 0L : aLong;
    }

    public static double parseToDouble(@Nullable Object object) {
        if (object == null) {
            return 0.0;
        }
        Number number = toNumber(object);
        if (number != null) {
            return number.doubleValue();
        }
        return parseToDouble(object.toString());
    }

    public static double parseToDouble(@Nullable String data) {
        if (data == null) {
            return 0.0;
        }
        data = data.trim();
        if (data.isEmpty()) {
            return 0.0;
        }
        Double aDouble = null;
        try {
            aDouble = Double.parseDouble(data);
        } catch (Exception ignore) {
        }
        return aDouble == null ? 0.0 : aDouble;
    }
}
