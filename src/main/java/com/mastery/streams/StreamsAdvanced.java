package com.mastery.streams;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JavaConcept(
    name = "Advanced Stream Operations (reduction, grouping, custom collectors, infinite streams)",
    difficulty = Difficulty.ADVANCED,
    what = "Advanced streams analyze collections using complex collectors. Reductions aggregate stream elements; grouping/partitioning splits elements into categorized maps; custom collectors define specific fold routines; infinite streams generate open data feeds.",
    whyItMatters = "Understanding advanced collectors avoids tedious manual nested map loops. Collectors.groupingBy enables grouping SQL-style classifications instantly. Custom collectors shape complex data aggregations cleanly.",
    keyPoints = {
        "reduce() folds elements into a single value using an identity and an associative accumulator.",
        "groupingBy groups elements by keys; partitioningBy divides elements into a Map with Boolean keys (true/false).",
        "Infinite streams (iterate, generate) produce continuous data feeds, requiring short-circuit operations (limit, findFirst) to prevent infinite loops."
    },
    interviewQuestions = {
        @Question(
            question = "What is the difference between groupingBy and partitioningBy in Java Collectors?",
            answer = "groupingBy groups elements based on a classification function, outputting a Map<K, List<T>> with dynamic keys. partitioningBy splits elements based on a Predicate, outputting a Map<Boolean, List<T>> containing exactly two keys (true and false)."
        ),
        @Question(
            question = "How do you create an infinite stream and safely process it?",
            answer = "By using Stream.iterate() or Stream.generate(). You must apply a short-circuiting operation (like limit(n)) before executing any terminal operations, or the JVM will experience CPU exhaustion and lock."
        )
    },
    pitfalls = {
        "Running sorting or counting terminal operations on infinite streams without applying limit() first.",
        "Performing stateful, non-associative calculations in reduce(), producing wrong results during parallel runs."
    }
)
@SuppressWarnings("all")
public class StreamsAdvanced {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Advanced Streams (grouping/reduction/custom)");

        // 1. Stream Reduction
        ConsoleFormatter.printStep("Stream Reduction", "Folding integers to compute sum");
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        int sum = numbers.stream().reduce(0, Integer::sum);
        System.out.println("Sum of list: " + sum);

        // 2. Grouping & Partitioning
        ConsoleFormatter.printStep("Grouping & Partitioning", "Categorizing employees by department and splitting by salary threshold");
        Employee e1 = new Employee("Alice", "Engineering", 120000);
        Employee e2 = new Employee("Bob", "Engineering", 80000);
        Employee e3 = new Employee("Charlie", "Sales", 95000);
        Employee e4 = new Employee("David", "Sales", 60000);
        List<Employee> employees = List.of(e1, e2, e3, e4);

        // Grouping by department
        Map<String, List<Employee>> deptMap = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));
        System.out.println("Employees by Department: " + deptMap);

        // Partitioning by salary (> 90000)
        Map<Boolean, List<Employee>> salaryPartition = employees.stream()
                .collect(Collectors.partitioningBy(e -> e.getSalary() > 90000));
        System.out.println("Employees high earners (true) vs lower (false): " + salaryPartition);

        if (deptMap.containsKey("Engineering") && salaryPartition.get(true).size() == 2) {
            ConsoleFormatter.printSuccess("Grouping and Partitioning logic validated successfully.");
        }

        // 3. Custom Collector using Collector.of()
        ConsoleFormatter.printStep("Custom Collector", "Building a custom collector to join strings using a separator");
        Collector<String, StringBuilder, String> commaJoiner = Collector.of(
                StringBuilder::new,                    // Supplier
                (sb, s) -> {                           // Accumulator
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(s);
                },
                StringBuilder::append,                 // Combiner (for parallel streams)
                StringBuilder::toString                // Finisher
        );

        List<String> items = List.of("A", "B", "C");
        String resultString = items.stream().collect(commaJoiner);
        System.out.println("Joined via custom collector: " + resultString);

        if ("A, B, C".equals(resultString)) {
            ConsoleFormatter.printSuccess("Custom string collector worked!");
        }

        // 4. Infinite Streams
        ConsoleFormatter.printStep("Infinite Streams Slicing", "Generating and limiting sequence of numbers");
        List<Integer> firstTenEvens = Stream.iterate(0, n -> n + 2)
                .limit(10) // Short-circuiting operator
                .collect(Collectors.toList());

        System.out.println("First 10 even numbers: " + firstTenEvens);
        if (firstTenEvens.size() == 10) {
            ConsoleFormatter.printSuccess("Infinite stream limited and processed safely.");
        }
    }

    static class Employee {
        private final String name;
        private final String department;
        private final double salary;

        Employee(String name, String department, double salary) {
            this.name = name;
            this.department = department;
            this.salary = salary;
        }

        public String getName() { return name; }
        public String getDepartment() { return department; }
        public double getSalary() { return salary; }

        @Override
        public String toString() {
            return name + " (" + salary + ")";
        }
    }
}
