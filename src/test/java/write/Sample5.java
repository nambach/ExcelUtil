package write;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

interface Pipe<I, O> {
    O transform(I input);
}

public class Sample5 {
}

class Chain<S, T> {
    private List<Pipe<?, ?>> pipes;

    private Chain() {
    }

    public static <K, L> Chain<K, L> start(Pipe<K, L> pipe) {
        Chain<K, L> chain = new Chain<K, L>();
        chain.pipes = Collections.singletonList(pipe);
        ;
        return chain;
    }

    public static void main(String[] args) {
        Pipe<String, Integer> pipe1 = new Pipe<String, Integer>() {
            @Override
            public Integer transform(String s) {
                return Integer.valueOf(s);
            }
        };
        Pipe<Integer, Long> pipe2 = new Pipe<Integer, Long>() {
            @Override
            public Long transform(Integer s) {
                return s.longValue();
            }
        };
        Pipe<Long, BigInteger> pipe3 = new Pipe<Long, BigInteger>() {
            @Override
            public BigInteger transform(Long s) {
                return new BigInteger(s.toString());
            }
        };
        Chain<String, BigInteger> chain = Chain.start(pipe1).append(pipe2).append(pipe3);
        BigInteger result = chain.run("12");
        System.out.println(result);
    }

    public <V> Chain<S, V> append(Pipe<T, V> pipe) {
        Chain<S, V> chain = new Chain<S, V>();
        chain.pipes = new ArrayList<>(pipes);
        chain.pipes.add(pipe);
        return chain;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public T run(S s) {
        Object source = s;
        Object target = null;
        for (Pipe p : pipes) {
            target = p.transform(source);
            source = target;
        }
        return (T) target;
    }
}