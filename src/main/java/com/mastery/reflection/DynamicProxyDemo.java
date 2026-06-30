package com.mastery.reflection;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@JavaConcept(
    name = "Dynamic Proxies and AOP Interceptors",
    difficulty = Difficulty.EXPERT,
    what = "Dynamic Proxies allow creating runtime classes that implement specified interfaces. Method calls are routed to a centralized java.lang.reflect.InvocationHandler for processing.",
    whyItMatters = "Dynamic Proxies are core to Spring AOP, transaction management (@Transactional), and Hibernate lazy loading. They intercept method execution to add cross-cutting concerns (logging, security, transaction begin/commit) dynamically.",
    keyPoints = {
        "JDK Dynamic Proxies require target classes to implement at least one interface.",
        "Method interception happens in the invoke() method of the InvocationHandler implementation.",
        "CGLIB is used as an alternative by frameworks to proxy concrete classes without interfaces (by subclassing them at runtime)."
    },
    interviewQuestions = {
        @Question(
            question = "What is the difference between JDK Dynamic Proxy and CGLIB Proxy?",
            answer = "JDK Dynamic Proxy is built into Java and requires the target class to implement interface methods. It creates proxy implementations dynamically. CGLIB dynamically generates a subclass of the target class at runtime using bytecode generation, allowing classes without interfaces to be proxied (though it cannot proxy final classes or methods)."
        ),
        @Question(
            question = "How does Spring use proxies to manage database transactions?",
            answer = "When a method is annotated with @Transactional, Spring wraps the bean inside a transaction proxy. When client invokes the method, the proxy intercepts the call, starts a database transaction, forwards the execution to the actual bean, and commits (or rolls back if an exception occurs) when the target returns."
        )
    },
    pitfalls = {
        "Dynamic proxy calls only trigger when invoked from outside the class. Self-invocation (calling another method inside the same class) bypasses the proxy, disabling aspects like @Transactional."
    }
)
public class DynamicProxyDemo {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Dynamic Proxies & AOP");

        // 1. Target instance
        ConsoleFormatter.printStep("Target Initialization", "Creating concrete target instance");
        DatabaseService target = new RealDatabaseService();

        // 2. Creating Dynamic Proxy wrapper
        ConsoleFormatter.printStep("Proxy Creation", "Generating Dynamic Proxy instance at runtime");
        DatabaseService proxyInstance = (DatabaseService) Proxy.newProxyInstance(
                DatabaseService.class.getClassLoader(),
                new Class<?>[]{ DatabaseService.class },
                new LoggingInterceptor(target) // Route calls through our handler
        );

        // 3. Executing methods via proxy
        ConsoleFormatter.printStep("Interception Execution", "Executing database queries via proxy wrapper");
        String result = proxyInstance.executeQuery("SELECT * FROM users");
        System.out.println("Returned Result: " + result);

        System.out.println("\nExecuting second query...");
        proxyInstance.executeUpdate("UPDATE profiles SET status='ACTIVE'");

        if (result.contains("Data for")) {
            ConsoleFormatter.printSuccess("Proxy intercepted and logged both database executions seamlessly!");
        }
    }

    // Target Interface
    public interface DatabaseService {
        String executeQuery(String sql);
        void executeUpdate(String sql);
    }

    // Concrete implementation
    public static class RealDatabaseService implements DatabaseService {
        @Override
        public String executeQuery(String sql) {
            System.out.println("  [REAL-SERVICE] Executing SQL read: " + sql);
            return "Data for Query: " + sql;
        }

        @Override
        public void executeUpdate(String sql) {
            System.out.println("  [REAL-SERVICE] Executing SQL write: " + sql);
        }
    }

    // Centralized invocation handler (Logging aspect)
    static class LoggingInterceptor implements InvocationHandler {
        private final Object target;

        public LoggingInterceptor(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Pre-processing hook
            long start = System.nanoTime();
            System.out.println("  [PROXY-INTERCEPTOR] [PRE] Logging execution start for method: " + method.getName());
            if (args != null && args.length > 0) {
                System.out.println("    Arguments: " + args[0]);
            }

            // Forward execution to real object
            Object result = method.invoke(target, args);

            // Post-processing hook
            long duration = (System.nanoTime() - start) / 1000;
            System.out.println("  [PROXY-INTERCEPTOR] [POST] Method completed. Execution time: " + duration + " microseconds.");
            return result;
        }
    }
}
