package ClassParser;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import kingstabyou.javaparser.Main;
import org.junit.Assert;
import org.junit.Test;
import java.util.*;

public class Test06_JavaLangObjectUp {

    @Test
    public void testObjectMethods() {
        Main main = new Main();
        main.processClass("ClassParser.Test06_JavaLangObjectUp");

        {
            // The program `A a = ...;  a.toString();` can only call method `Object.toString()`
            Set<String> result = main.explain("A", "toString");
            Assert.assertEquals(Set.of("java.lang.Object"), result);
        }

        {
            // The program `A a = ...;  a.hashCode();` can only call method `Object.hashCode()`
            Set<String> result = main.explain("A", "hashCode");
            Assert.assertEquals(Set.of("java.lang.Object"), result);
        }

        {
            // The program `A a = ...;  a.equals(new B());` can only call method `Object.equals(Object)`
            Set<String> result = main.explain("A", "equals", "java.lang.Object");
            Assert.assertEquals(Set.of("java.lang.Object"), result);
        }

        {
            // The program `A a = ...;  a.wait();` can only call method `Object.wait()`
            Set<String> result = main.explain("A", "wait");
            Assert.assertEquals(Set.of("java.lang.Object"), result);
        }

        {
            // The program `B b = ...;  b.wait();` can only call method `Object.wait()`
            Set<String> result = main.explain("B", "wait");
            Assert.assertEquals(Set.of("java.lang.Object"), result);
        }

        {
            // The program `A a = ...;  a.notify();` can only call method `Object.notify()`
            Set<String> result = main.explain("A", "notify");
            Assert.assertEquals(Set.of("java.lang.Object"), result);
        }

        {
            // The program `B b = ...;  b.notifyAll();` can only call method `Object.notifyAll()`
            Set<String> result = main.explain("B", "notifyAll");
            Assert.assertEquals(Set.of("java.lang.Object"), result);
        }

    }

    @Test
    public void testOverridenObjectMethods() {
        Main main = new Main();
        main.processClass("ClassParser.Test06_JavaLangObjectUp");

        {
            // The program `B b = ...;  b.toString();` can only call method `B.toString()`
            Set<String> result = main.explain("B", "toString");
            Assert.assertEquals(Set.of("B"), result);
        }

        {
            // The program `B b = ...;  b.hashCode();` can only call method `B.hashCode()`
            Set<String> result = main.explain("B", "hashCode");
            Assert.assertEquals(Set.of("B"), result);
        }

        {
            // The program `B b = ...;  b.equals(new B());` can only call method `B.equals(Object)`
            Set<String> result = main.explain("B", "equals", "java.lang.Object");
            Assert.assertEquals(Set.of("B"), result);
        }

        {
            // The program `B b = ...;  b.equals();` cannot call any method
            Set<String> result = main.explain("B", "equals");
            Assert.assertEquals(Set.of(), result);
        }
    }

    public static class A {
        public boolean equals() { return false; }
    }

    public static class B {
        public int hashCode() { return 0; }

        public boolean equals(Object obj) { return false; }

        public String toString() { return ""; }
    }

}
