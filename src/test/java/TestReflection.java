import io.nambm.excel.util.ReflectUtil;
import lombok.SneakyThrows;
import model.Student;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;

public class TestReflection {
    private static final Student s = TestWriter.students.get(0);

    @SneakyThrows
    public static void main(String[] args) {
//        direct();
//        lambdaFactory();
//        reflection();
        beanUtils();
    }

    private static void lambdaFactory() throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        CallSite site = LambdaMetafactory.metafactory(
                lookup,
                "apply",
                MethodType.methodType(Function.class),
                MethodType.methodType(Object.class, Object.class),
                lookup.findVirtual(Student.class, "getFirstName", MethodType.methodType(String.class)),
                MethodType.methodType(String.class, Student.class));
        Function f = (Function) site.getTarget().invokeExact();
        long start = System.nanoTime();
        System.out.println(f.apply(s));
        System.out.println((System.nanoTime() - start) / 1000);
    }

    private static void direct() {
        long start = System.nanoTime();
        System.out.println(s.getFirstName());
        System.out.println((System.nanoTime() - start) / 1000);
    }

    @SneakyThrows
    private static void reflection() {
        Field field = Student.class.getDeclaredField("firstName");
        field.setAccessible(true);
        long start = System.nanoTime();
        System.out.println(field.get(s));
        System.out.println((System.nanoTime() - start) / 1000);
    }

    @SneakyThrows
    private static void beanUtils() {
        Method method = ReflectUtil.getField("firstName", Student.class).getReadMethod();
        System.out.println();
        long start = System.nanoTime();
        System.out.println(method.invoke(s));
        System.out.println((System.nanoTime() - start) / 1000);
    }
}
