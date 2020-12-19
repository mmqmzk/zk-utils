package me.zhoukun;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Created on 2020/12/19 18:52.
 *
 * @author 周锟
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Holder<T> implements
                             UnaryOperator<T>, Consumer<T>,
                             Supplier<T>, Predicate<Object> {
    private T value;

    public Holder<T> setValue(T value) {
        this.value = value;
        return this;
    }

    @Override
    public T apply(T t) {
        T oldValue = value;
        value = t;
        return oldValue;
    }

    @Override
    public void accept(T t) {
        value = t;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public boolean test(Object o) {
        return value != null;
    }
}
