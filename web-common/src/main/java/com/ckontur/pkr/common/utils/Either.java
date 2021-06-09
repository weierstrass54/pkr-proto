package com.ckontur.pkr.common.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Either<L, R> {
    public abstract R get();
    public abstract L getLeft();
    public abstract boolean isLeft();
    public abstract boolean isRight();

    public static <L, R> Either<L, R> right(R right) {
        return new Right<>(right);
    }

    public static <L, R> Either<L, R> left(L left) {
        return new Left<>(left);
    }

    public final <U, V> Either<U, V> bimap(Function<? super L, ? extends U> leftMapper, Function<? super R, ? extends V> rightMapper) {
        Objects.requireNonNull(leftMapper, "leftMapper is null");
        Objects.requireNonNull(rightMapper, "rightMapper is null");

        return isRight() ? new Right<>(rightMapper.apply(get())) : new Left<>(leftMapper.apply(getLeft()));
    }

    public final <U> U fold(Function<? super L, ? extends U> leftMapper, Function<? super R, ? extends U> rightMapper) {
        Objects.requireNonNull(leftMapper, "leftMapper is null");
        Objects.requireNonNull(rightMapper, "rightMapper is null");

        return isRight() ? rightMapper.apply(get()) : leftMapper.apply(getLeft());
    }

    public final <U> U transform(Function<? super Either<L, R>, ? extends U> f) {
        Objects.requireNonNull(f, "f is null");
        return f.apply(this);
    }

    public final R getOrElseGet(Function<? super L, ? extends R> other) {
        Objects.requireNonNull(other, "other is null");

        return isRight() ? get() : other.apply(getLeft());
    }

    public final <X extends Throwable> R getOrElseThrow(Function<? super L, X> exceptionFunction) throws X {
        Objects.requireNonNull(exceptionFunction, "exceptionFunction is null");

        if (isRight()) {
            return get();
        } else {
            throw exceptionFunction.apply(getLeft());
        }
    }

    public final Either<R, L> swap() {
        return isRight() ? new Left<>(get()) : new Right<>(getLeft());
    }

    @SuppressWarnings("unchecked")
    public final <U> Either<L, U> flatMap(Function<? super R, ? extends Either<L, ? extends U>> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");

        return isRight() ? (Either<L, U>) mapper.apply(get()) : (Either<L, U>) this;
    }

    @SuppressWarnings("unchecked")
    public final <U> Either<L, U> map(Function<? super R, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");

        return isRight() ? Either.right(mapper.apply(get())) : (Either<L, U>) this;
    }

    @SuppressWarnings("unchecked")
    public final <U> Either<U, R> mapLeft(Function<? super L, ? extends U> leftMapper) {
        Objects.requireNonNull(leftMapper, "leftMapper is null");

        return isLeft() ? Either.left(leftMapper.apply(getLeft())) : (Either<U, R>) this;
    }

    public final Optional<Either<L, R>> filter(Predicate<? super R> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");

        return isLeft() || predicate.test(get()) ? Optional.of(this) : Optional.empty();
    }

    public final Optional<Either<L, R>> filterNot(Predicate<? super R> predicate) {
        return filter(predicate.negate());
    }

    public final Either<L,R> filterOrElse(Predicate<? super R> predicate, Function<? super R, ? extends L> zero) {
        Objects.requireNonNull(predicate, "predicate is null");
        Objects.requireNonNull(zero, "zero is null");

        return isLeft() || predicate.test(get()) ? this : Either.left(zero.apply(get()));
    }

    @SuppressWarnings("unchecked")
    public final Either<L, R> orElse(Either<? extends L, ? extends R> other) {
        Objects.requireNonNull(other, "other is null");

        return isRight() ? this : (Either<L, R>) other;
    }

    @SuppressWarnings("unchecked")
    public final Either<L, R> orElseGet(Supplier<? extends Either<? extends L, ? extends R>> supplier) {
        Objects.requireNonNull(supplier, "supplier is null");

        return isRight() ? this : (Either<L, R>) supplier.get();
    }

    public final Either<L, R> peek(Consumer<? super L> leftAction, Consumer<? super R> rightAction) {
        Objects.requireNonNull(leftAction, "leftAction is null");
        Objects.requireNonNull(rightAction, "rightAction is null");

        if (isLeft()) {
            leftAction.accept(getLeft());
        } else {
            rightAction.accept(get());
        }
        return this;
    }

    public final Either<L, R> peek(Consumer<? super R> action) {
        Objects.requireNonNull(action, "action is null");

        if (isRight()) {
            action.accept(get());
        }
        return this;
    }

    public final Either<L, R> peekLeft(Consumer<? super L> action) {
        Objects.requireNonNull(action, "action is null");

        if (isLeft()) {
            action.accept(getLeft());
        }
        return this;
    }



    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Left<L, R> extends Either<L, R> {
        private final L value;

        @Override
        public R get() {
            throw new NoSuchElementException("get() on Left");
        }

        @Override
        public L getLeft() {
            return value;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Right<L, R> extends Either<L, R> {
        private final R value;

        @Override
        public R get() {
            return value;
        }

        @Override
        public L getLeft() {
            throw new NoSuchElementException("getLeft() on Right");
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }
    }

}
