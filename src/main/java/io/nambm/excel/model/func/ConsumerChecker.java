package io.nambm.excel.model.func;

import io.nambm.excel.util.ReflectUtil;

import java.util.function.BiConsumer;

public class ConsumerChecker {
    public static <T> ReflectUtil.Type determineSecondParamType(BiConsumer<T, ?> biConsumer) {
        if (biConsumer instanceof ConsumerString) {
            return ReflectUtil.Type.STRING;
        }
        if (biConsumer instanceof ConsumerDouble) {
            return ReflectUtil.Type.DOUBLE;
        }
        if (biConsumer instanceof ConsumerBoolean) {
            return ReflectUtil.Type.BOOLEAN;
        }
        if (biConsumer instanceof ConsumerDate) {
            return ReflectUtil.Type.DATE;
        }
        return ReflectUtil.Type.OBJECT;
    }
}
