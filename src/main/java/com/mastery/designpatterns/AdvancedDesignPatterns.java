package com.mastery.designpatterns;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@JavaConcept(
    name = "Advanced Design Patterns (Proxy, Chain of Responsibility, Template Method, State)",
    difficulty = Difficulty.ADVANCED,
    what = "Advanced design patterns orchestrate sophisticated object structures. Proxy controls access to target objects via wrapper layers; Chain of Responsibility forwards requests along independent handlers; Template Method specifies fixed algorithm workflows in base classes; State enables dynamic behavioral changes based on internal context state.",
    whyItMatters = "These patterns form the baseline of core enterprise Java frameworks. Spring's AOP and @Transactional proxying rely directly on Dynamic Proxies. Servlet Filter chains and Spring Security rely on Chain of Responsibility. Hibernate templates use Template Method. State patterns streamline intricate business workflows, avoiding bloated conditional blocks.",
    keyPoints = {
        "JDK Dynamic Proxy allows runtime proxy class generation implementing target interfaces without writing boilerplate code.",
        "Chain of Responsibility promotes low coupling, letting individual links decide if they should handle a request or delegate.",
        "Template Method fixes structural algorithms using a 'final' method template, letting subclasses specialize hooks/abstract steps.",
        "State pattern models distinct lifecycle behaviors, allowing objects to dynamically alter their logic as internal states transition."
    },
    interviewQuestions = {
        @Question(
            question = "Contrast JDK Dynamic Proxy with CGLIB Proxy in Java.",
            answer = "JDK Dynamic Proxy is built into the JDK and requires the target class to implement an interface, dynamically generating a class that implements those interfaces. CGLIB (Code Generation Library) generates subclasses of the target class at runtime by modifying bytecode. CGLIB does not require interfaces but cannot proxy final classes or final methods."
        ),
        @Question(
            question = "How does Template Method differ from the Strategy pattern?",
            answer = "Template Method relies on inheritance; it defines the skeleton structure of an algorithm in a base class and forces subclasses to implement specific steps. Strategy relies on composition; it defines a behavior contract (interface), allowing the client to swap interchangeable strategy implementations dynamically at runtime."
        ),
        @Question(
            question = "How do you prevent circular dependency issues in the Chain of Responsibility?",
            answer = "By ensuring the chain is configured as a directed acyclic graph (DAG)—typically a linear linked list. Handlers should only possess references to their immediate successors and must not point back to preceding links or form loops."
        ),
        @Question(
            question = "In the State pattern, should states contain transitions or should the Context hold them?",
            answer = "If state transitions are static and simple, letting the Context transition them keeps states decoupled. If transitions are dynamic and depend on external inputs or the state itself, states can perform the transition by calling context.setState(nextState), though this introduces coupling between the concrete state classes."
        )
    },
    pitfalls = {
        "Failing to handle checked exceptions in JDK dynamic proxies, which can trigger UndeclaredThrowableException at runtime.",
        "Overusing Chain of Responsibility for trivial flows, which can make debugging request executions difficult and obscure stack traces.",
        "Tightly coupling concrete state objects to one another, making it difficult to inject new steps or modify existing state sequences."
    }
)
@SuppressWarnings("all")
public class AdvancedDesignPatterns {

    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Advanced Design Patterns");

        // 1. PROXY PATTERN (Virtual Proxy & JDK Dynamic Proxy)
        runProxyDemo();

        ConsoleFormatter.printDivider();

        // 2. CHAIN OF RESPONSIBILITY PATTERN
        runChainOfResponsibilityDemo();

        ConsoleFormatter.printDivider();

        // 3. TEMPLATE METHOD PATTERN
        runTemplateMethodDemo();

        ConsoleFormatter.printDivider();

        // 4. STATE PATTERN
        runStateDemo();

        ConsoleFormatter.printSuccess("All advanced design pattern demonstrations completed successfully!");
    }

    // =========================================================================
    // 1. Proxy Pattern Demo & Implementations
    // =========================================================================
    private static void runProxyDemo() {
        ConsoleFormatter.printStep("Proxy Pattern", "Testing Virtual Proxy (Lazy Loading) followed by JDK Dynamic Proxy");

        // A. Virtual Proxy
        System.out.println("[Virtual Proxy] Creating HeavyService Virtual Proxy reference...");
        HeavyService lazyProxy = new HeavyServiceVirtualProxy();
        System.out.println("[Virtual Proxy] Reference created. Real service has NOT been initialized yet.");
        
        System.out.println("[Virtual Proxy] Invoking service method...");
        String output1 = lazyProxy.process("InputData_1");
        System.out.println("[Virtual Proxy] Method output: " + output1);

        System.out.println("[Virtual Proxy] Invoking service method again...");
        String output2 = lazyProxy.process("InputData_2");
        System.out.println("[Virtual Proxy] Method output: " + output2);

        // B. JDK Dynamic Proxy
        System.out.println("\n[JDK Dynamic Proxy] Constructing dynamic logging proxy...");
        HeavyService realService = new HeavyServiceImpl();
        HeavyService dynamicProxy = (HeavyService) Proxy.newProxyInstance(
                HeavyService.class.getClassLoader(),
                new Class<?>[]{HeavyService.class},
                new LoggingInvocationHandler(realService)
        );

        System.out.println("[JDK Dynamic Proxy] Invoking dynamic proxy method...");
        String proxyOutput = dynamicProxy.process("DynamicRequest");
        System.out.println("[JDK Dynamic Proxy] Method output: " + proxyOutput);
    }

    public interface HeavyService {
        String process(String input);
    }

    public static class HeavyServiceImpl implements HeavyService {
        public HeavyServiceImpl() {
            System.out.println("  --> [HeavyServiceImpl] Performing heavy initialization (e.g. acquiring DB resources)...");
            try {
                Thread.sleep(300); // Simulate expensive work
            } catch (InterruptedException ignored) {}
        }

        @Override
        public String process(String input) {
            return "SUCCESSFULLY_PROCESSED: " + input;
        }
    }

    // Virtual Proxy controls instantiation
    public static class HeavyServiceVirtualProxy implements HeavyService {
        private HeavyServiceImpl realService;

        @Override
        public String process(String input) {
            if (realService == null) {
                System.out.println("  --> [Virtual Proxy] Real service is null. Instantiating on demand...");
                realService = new HeavyServiceImpl();
            }
            return realService.process(input);
        }
    }

    // JDK Dynamic Proxy InvocationHandler
    public static class LoggingInvocationHandler implements InvocationHandler {
        private final Object target;

        public LoggingInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("  --> [Dynamic Proxy Log] BEFORE invoking: " + method.getName());
            long start = System.nanoTime();
            
            Object result = method.invoke(target, args);
            
            long duration = System.nanoTime() - start;
            System.out.println("  --> [Dynamic Proxy Log] AFTER invoking: " + method.getName() + " (Time: " + (duration / 1_000_000.0) + " ms)");
            return result;
        }
    }

    // =========================================================================
    // 2. Chain of Responsibility Demo & Implementations
    // =========================================================================
    private static void runChainOfResponsibilityDemo() {
        ConsoleFormatter.printStep("Chain of Responsibility Pattern", "Constructing handler pipeline for security/validation filters");

        // Build Chain: Auth -> Role Verification -> Logging
        Handler authHandler = new AuthenticationHandler();
        Handler roleHandler = new RoleValidationHandler("ADMIN");
        Handler loggingHandler = new LoggingHandler();

        authHandler.setNext(roleHandler).setNext(loggingHandler);

        // Scenario A: Valid Admin request
        Request validRequest = new Request("alice", "ADMIN", "SECURE_TOKEN_XYZ");
        System.out.println("[Chain] Dispatching valid admin request:");
        boolean aPassed = authHandler.handle(validRequest);
        System.out.println("[Chain] Final result: " + (aPassed ? "Request Approved" : "Request Blocked") + "\n");

        // Scenario B: Request with missing token
        Request missingTokenRequest = new Request("bob", "ADMIN", null);
        System.out.println("[Chain] Dispatching request with missing token:");
        boolean bPassed = authHandler.handle(missingTokenRequest);
        System.out.println("[Chain] Final result: " + (bPassed ? "Request Approved" : "Request Blocked") + "\n");

        // Scenario C: Request with invalid role
        Request invalidRoleRequest = new Request("charlie", "GUEST", "SECURE_TOKEN_XYZ");
        System.out.println("[Chain] Dispatching request with guest role (requires ADMIN):");
        boolean cPassed = authHandler.handle(invalidRoleRequest);
        System.out.println("[Chain] Final result: " + (cPassed ? "Request Approved" : "Request Blocked"));
    }

    public record Request(String username, String role, String token) {}

    public static abstract class Handler {
        protected Handler next;

        public Handler setNext(Handler next) {
            this.next = next;
            return next;
        }

        public abstract boolean handle(Request request);
    }

    public static class AuthenticationHandler extends Handler {
        @Override
        public boolean handle(Request request) {
            System.out.println("  --> [AuthHandler] Verifying credentials token...");
            if (request.token() == null || request.token().isBlank()) {
                System.out.println("  --> [AuthHandler] FAILED: Missing authorization token!");
                return false;
            }
            System.out.println("  --> [AuthHandler] SUCCESS.");
            return next == null || next.handle(request);
        }
    }

    public static class RoleValidationHandler extends Handler {
        private final String requiredRole;

        public RoleValidationHandler(String requiredRole) {
            this.requiredRole = requiredRole;
        }

        @Override
        public boolean handle(Request request) {
            System.out.println("  --> [RoleValidationHandler] Checking role privileges. Needs: " + requiredRole);
            if (!requiredRole.equalsIgnoreCase(request.role())) {
                System.out.println("  --> [RoleValidationHandler] FAILED: Access denied for role: " + request.role());
                return false;
            }
            System.out.println("  --> [RoleValidationHandler] SUCCESS.");
            return next == null || next.handle(request);
        }
    }

    public static class LoggingHandler extends Handler {
        @Override
        public boolean handle(Request request) {
            System.out.println("  --> [LoggingHandler] Request fully authorized. Logging audit trail details for user: " + request.username());
            return next == null || next.handle(request);
        }
    }

    // =========================================================================
    // 3. Template Method Demo & Implementations
    // =========================================================================
    private static void runTemplateMethodDemo() {
        ConsoleFormatter.printStep("Template Method Pattern", "Running template algorithm workflow for CSV vs JSON parsers");

        System.out.println("[Template] Running CSV Processor pipeline...");
        DataProcessor csvProcessor = new CsvDataProcessor();
        csvProcessor.process("data.csv");

        System.out.println("\n[Template] Running JSON Processor pipeline...");
        DataProcessor jsonProcessor = new JsonDataProcessor();
        jsonProcessor.process("{\"validJson\": true}");

        System.out.println("\n[Template] Running JSON Processor with invalid structure...");
        jsonProcessor.process("invalid_file_contents");
    }

    public static abstract class DataProcessor {
        // Template Method is final to lock the algorithm outline
        public final void process(String sourcePath) {
            loadSource(sourcePath);
            if (isValid()) {
                parseData();
                saveToDatabase();
            } else {
                System.out.println("  --> [Template Method] FAILED: Validation check rejected the source.");
            }
        }

        protected abstract void loadSource(String sourcePath);
        protected abstract void parseData();

        protected void saveToDatabase() {
            // Default step in abstract class
            System.out.println("  --> [Template Method Base] Default step: Saving parsed records to relational SQL database.");
        }

        // Hook method (overridable, default implementation provided)
        protected boolean isValid() {
            return true;
        }
    }

    public static class CsvDataProcessor extends DataProcessor {
        @Override
        protected void loadSource(String sourcePath) {
            System.out.println("  --> [CsvProcessor] Opening file reader and loading lines: " + sourcePath);
        }

        @Override
        protected void parseData() {
            System.out.println("  --> [CsvProcessor] Splitting lines by commas and generating rows...");
        }
    }

    public static class JsonDataProcessor extends DataProcessor {
        private boolean isSchemaValid = false;

        @Override
        protected void loadSource(String sourcePath) {
            System.out.println("  --> [JsonProcessor] Ingesting raw buffer stream: " + sourcePath);
            // Simulate a simple syntax check
            isSchemaValid = sourcePath.startsWith("{") && sourcePath.endsWith("}");
        }

        @Override
        protected void parseData() {
            System.out.println("  --> [JsonProcessor] Building AST nodes and serializing fields...");
        }

        @Override
        protected void saveToDatabase() {
            // Override default database destination
            System.out.println("  --> [JsonProcessor] Overriding default DB step: Saving JSON documents to NoSQL MongoDB instance.");
        }

        @Override
        protected boolean isValid() {
            return isSchemaValid;
        }
    }

    // =========================================================================
    // 4. State Pattern Demo & Implementations
    // =========================================================================
    private static void runStateDemo() {
        ConsoleFormatter.printStep("State Pattern", "Transitioning an order context through Created, Paid, and Shipped states");

        OrderContext order = new OrderContext();
        order.printCurrentStatus();

        System.out.println("[State] Proceeding to next step...");
        order.nextState();
        order.printCurrentStatus();

        System.out.println("[State] Proceeding to next step...");
        order.nextState();
        order.printCurrentStatus();

        System.out.println("[State] Trying to proceed past the final shipped state...");
        order.nextState();

        System.out.println("[State] Moving backwards in the order lifecycle...");
        order.prevState();
        order.printCurrentStatus();
    }

    public interface OrderState {
        void next(OrderContext context);
        void prev(OrderContext context);
        void printStatus();
    }

    public static class OrderContext {
        private OrderState currentState = new CreatedState();

        public void setState(OrderState state) {
            this.currentState = state;
        }

        public void nextState() {
            currentState.next(this);
        }

        public void prevState() {
            currentState.prev(this);
        }

        public void printCurrentStatus() {
            currentState.printStatus();
        }
    }

    public static class CreatedState implements OrderState {
        @Override
        public void next(OrderContext context) {
            System.out.println("  --> [CreatedState] Transitioning order: Created -> Paid.");
            context.setState(new PaidState());
        }

        @Override
        public void prev(OrderContext context) {
            System.out.println("  --> [CreatedState] Already in base state. Cannot regress.");
        }

        @Override
        public void printStatus() {
            System.out.println("  --> [State Status] Order registered in system. Awaiting payment processing.");
        }
    }

    public static class PaidState implements OrderState {
        @Override
        public void next(OrderContext context) {
            System.out.println("  --> [PaidState] Transitioning order: Paid -> Shipped.");
            context.setState(new ShippedState());
        }

        @Override
        public void prev(OrderContext context) {
            System.out.println("  --> [PaidState] Regressing order: Paid -> Created.");
            context.setState(new CreatedState());
        }

        @Override
        public void printStatus() {
            System.out.println("  --> [State Status] Order paid successfully. Packaging and preparing for shipping.");
        }
    }

    public static class ShippedState implements OrderState {
        @Override
        public void next(OrderContext context) {
            System.out.println("  --> [ShippedState] Already in terminal Shipped state. Transition complete.");
        }

        @Override
        public void prev(OrderContext context) {
            System.out.println("  --> [ShippedState] Regressing order: Shipped -> Paid.");
            context.setState(new PaidState());
        }

        @Override
        public void printStatus() {
            System.out.println("  --> [State Status] Order shipped. Carrier out for delivery.");
        }
    }
}
