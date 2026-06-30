package com.mastery.streams;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@JavaConcept(
    name = "Intermediate Stream Operations (flatMap, sorting, primitives)",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Intermediate stream operations shape and transform stream data. flatMap() flattens nested streams; sorting orders items; primitive streams (IntStream) prevent wrapper autoboxing memory waste.",
    whyItMatters = "Processing nested collections with map() yields hierarchies (e.g. List<List<User>>). flatMap() flattens this structure into a single stream. Primitive streams offer native operations like average() and avoid boxing delays.",
    keyPoints = {
        "flatMap() maps each element to a stream of sub-elements, then flattens all sub-streams into a single stream.",
        "Primitive streams (IntStream, LongStream, DoubleStream) offer performance gains by avoiding Object wrappers.",
        "SummaryStatistics provides instant calculation of count, sum, min, average, and max values."
    },
    interviewQuestions = {
        @Question(
            question = "What is the difference between map() and flatMap() in Java Streams?",
            answer = "map() performs a 1-to-1 transformation, wrapping output elements directly. flatMap() performs a 1-to-many transformation where each element maps to a Stream, and all these streams are flattened into a single combined output stream."
        ),
        @Question(
            question = "Why should you use IntStream instead of Stream<Integer> for numeric math?",
            answer = "Stream<Integer> forces wrapper object instantiations (Autoboxing), consuming significant heap memory. IntStream is a stream of raw primitive ints, running much faster and providing native math methods (sum(), average(), summaryStatistics())."
        )
    },
    pitfalls = {
        "Using map() when flatMap() is required, producing double-nested arrays that are hard to manipulate.",
        "Attempting to sort infinite streams without pre-slicing using limit()."
    }
)
@SuppressWarnings("all")
public class StreamsIntermediate {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Intermediate Streams (flatMap/sorting/primitives)");

        // 1. flatMap Demo
        ConsoleFormatter.printStep("flatMap Flattening", "Flattening list of course enrolments");
        Student s1 = new Student("Alice", List.of("Math", "Physics"));
        Student s2 = new Student("Bob", List.of("Chemistry", "Physics", "Biology"));
        List<Student> students = List.of(s1, s2);

        // Extract unique courses across all students
        List<String> uniqueCourses = students.stream()
                .flatMap(s -> s.getCourses().stream()) // map String lists to Streams and merge
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        System.out.println("Combined Unique Sorted Courses: " + uniqueCourses);
        if (uniqueCourses.size() == 4) {
            ConsoleFormatter.printSuccess("flatMap merged and flattened courses successfully!");
        }

        // 2. Primitive streams (IntStream) & Summary Statistics
        ConsoleFormatter.printStep("Primitive Streams & Statistics", "Processing IntStream elements");
        int[] scores = {85, 92, 78, 90, 88};

        // Convert raw array to IntStream
        IntStream scoreStream = IntStream.of(scores);
        IntSummaryStatistics stats = scoreStream.summaryStatistics();

        System.out.println("Scores stats count: " + stats.getCount());
        System.out.println("Max score: " + stats.getMax());
        System.out.println("Min score: " + stats.getMin());
        System.out.println("Average score: " + stats.getAverage());
        System.out.println("Sum of scores: " + stats.getSum());

        if (stats.getAverage() == 86.6) {
            ConsoleFormatter.printSuccess("IntStream SummaryStatistics computed average score cleanly.");
        }
    }

    static class Student {
        private final String name;
        private final List<String> courses;

        Student(String name, List<String> courses) {
            this.name = name;
            this.courses = courses;
        }

        public String getName() { return name; }
        public List<String> getCourses() { return courses; }
    }
}
