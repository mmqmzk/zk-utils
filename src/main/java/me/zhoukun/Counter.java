package me.zhoukun;

import lombok.Data;
import java.util.function.*;

/**
 * Created on 2020/12/19 18:51.
 *
 * @author 周锟
 */
@Data
public class Counter implements IntSupplier, IntConsumer, IntUnaryOperator, IntPredicate {
    public static final int STEP = 1;

    public static final int ZERO = 0;

    int count;

    public Counter() {
        this(ZERO);
    }

    public Counter(int count) {
        this.count = count;
    }

    public int get() {
        return count;
    }

    public void set(int count) {
        this.count = count;
    }

    public int getAndInc() {
        return getAndInc(STEP);
    }

    public int getAndInc(int i) {
        int count = this.count;
        this.count += i;
        return count;
    }

    public int getAndDec() {
        return getAndDec(STEP);
    }

    public int getAndDec(int i) {
        int count = this.count;
        this.count -= i;
        return count;
    }

    public int incAndGet() {
        return incAndGet(STEP);
    }

    public int incAndGet(int i) {
        return count += i;
    }

    public int decAndGet() {
        return decAndGet(STEP);
    }

    public int decAndGet(int i) {
        return count -= i;
    }

    public boolean eq(int i) {
        return count == i;
    }

    public boolean eq0() {
        return eq(ZERO);
    }

    public boolean gt(int i) {
        return count > i;
    }

    public boolean ge(int i) {
        return count >= i;
    }

    public boolean lt(int i) {
        return count < i;
    }

    public boolean le(int i) {
        return count <= i;
    }

    public boolean gt0() {
        return gt(ZERO);
    }

    public boolean ge0() {
        return ge(ZERO);
    }

    public boolean lt0() {
        return lt(ZERO);
    }

    public boolean le0() {
        return le(ZERO);
    }

    @Override
    public void accept(int value) {
        count = value;
    }

    @Override
    public int getAsInt() {
        return count;
    }

    @Override
    public int applyAsInt(int operand) {
        return incAndGet(operand);
    }

    @Override
    public boolean test(int value) {
        return eq(value);
    }
}
