package com.ckontur.pkr.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Try<T> {
    private Try() {}

    public static <T> Try<T> of(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "Supplier is null");
        try {
            return Try.success(supplier.get());
        }
        catch (Throwable t) {
            return Try.failure(t);
        }
    }

    public static <T> Try<List<T>> of(Iterable<? extends Try<? extends T>> tries) {
        Objects.requireNonNull(tries, "Tries is null");
        List<T> results = new ArrayList<>();
        for(Try<? extends T> t: tries) {
            if (t.isFailure()) {
                return Try.failure(t.getCause());
            }
            results.add(t.get());
        }
        return Try.success(results);
    }

    public static <T> Try<T> success(T value) {
        return new Success<>(value);
    }

    public static <T> Try<T> failure(Throwable t) {
        return new Failure<>(t);
    }

    public abstract T get();
    public abstract Throwable getCause();
    public abstract boolean isEmpty();
    public abstract boolean isFailure();
    public abstract boolean isSuccess();

    public final Try<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "Predicate is null");
        if (isFailure()) {
            return this;
        }
        try {
            if (predicate.test(get())) {
                return this;
            }
            return Try.failure(new Exception("Predicate failed"));
        }
        catch (Throwable t) {
            return Try.failure(t);
        }
    }

    public final Try<T> filter(Predicate<? super T> predicate, Supplier<? extends Throwable> onThrow) {
        Objects.requireNonNull(predicate, "Predicate is null");
        Objects.requireNonNull(onThrow, "OnThrow is null");
        if (isFailure()) {
            return this;
        }
        try {
            if (predicate.test(get())) {
                return this;
            }
            return Try.failure(onThrow.get());
        }
        catch (Throwable t) {
            return Try.failure(t);
        }
    }

    public final <U> Try<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "Mapper is null");
        if (isFailure()) {
            return (Failure<U>) this;
        }
        try {
            return Try.success(mapper.apply(get()));
        }
        catch (Throwable t) {
            return Try.failure(t);
        }
    }

    @SuppressWarnings("unchecked")
    public final <U> Try<U> flatMap(Function<? super T, ? extends Try<? extends U>> mapper) {
        Objects.requireNonNull(mapper, "Mapper is null");
        if (isFailure()) {
            return (Failure<U>) this;
        }
        try {
            return (Try<U>) mapper.apply(get());
        }
        catch (Throwable t) {
            return Try.failure(t);
        }
    }

    public final T orElse(T value) {
        Objects.requireNonNull(value, "Value is null");
        return isSuccess() ? get() : value;
    }

    public final T orElseGet(Function<? super Throwable, ? extends T> function) {
        Objects.requireNonNull(function, "Function is null");
        return isSuccess() ? get() : function.apply(getCause());
    }

    public final T orElseThrow() {
        if (isSuccess()) {
            return get();
        }
        throw new RuntimeException(getCause());
    }

    public final <X extends Throwable> T orElseThrow(Function<? super Throwable, ? extends X> exceptionFunction) throws X {
        Objects.requireNonNull(exceptionFunction, "ExceptionFunction is null");
        if (isSuccess()) {
            return get();
        }
        throw exceptionFunction.apply(getCause());
    }

    public static final class Success<T> extends Try<T> {
        private final T value;

        private Success(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public Throwable getCause() {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    public static final class Failure<T> extends Try<T> {
        private final Throwable cause;

        private Failure(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public T get() {
            return null;
        }

        @Override
        public Throwable getCause() {
            return cause;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj == this) || (obj instanceof Failure && Arrays.deepEquals(cause.getStackTrace(), ((Failure<?>) obj).cause.getStackTrace()));
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(cause.getStackTrace());
        }

        @Override
        public String toString() {
            return "Failure {" + cause + "}";
        }
    }
}
