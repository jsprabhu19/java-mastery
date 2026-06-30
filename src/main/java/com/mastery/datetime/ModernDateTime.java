package com.mastery.datetime;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.time.*;
import java.time.format.DateTimeFormatter;

@JavaConcept(
    name = "Modern Java Time API",
    difficulty = Difficulty.BEGINNER,
    what = "The java.time package (introduced in Java 8) provides thread-safe, immutable time representation classes, replacing legacy, thread-unsafe java.util.Date and Calendar.",
    whyItMatters = "Legacy SimpleDateFormat is thread-unsafe and causes corrupted dates in concurrent environments. Modern java.time formatting is immutable, allowing static instances to be shared across threads safely.",
    keyPoints = {
        "LocalDate, LocalTime, and LocalDateTime are zone-agnostic time structures.",
        "ZonedDateTime manages zone offsets; Instant represents standard UTC timestamps.",
        "Period tracks differences in years/months/days; Duration measures exact hours/minutes/seconds."
    },
    interviewQuestions = {
        @Question(
            question = "Why are modern java.time classes preferred over java.util.Date?",
            answer = "Date objects are mutable and thread-unsafe, necessitating manual locking or cloning. java.time objects are completely immutable, thread-safe, and clearly separate zone-agnostic local calculations from global timestamps (Instants)."
        ),
        @Question(
            question = "What is the difference between Period and Duration?",
            answer = "Period measures date-based differences (e.g., 2 years, 3 months, 10 days). Duration measures time-based differences in nanos or seconds (e.g., 48 hours, 12 seconds)."
        )
    },
    pitfalls = {
        "Assuming LocalDateTime represents a unique physical point in time. Without a ZoneId, it is a conceptual date-time (e.g., Midnight New Years occurs at different physical times globally)."
    }
)
@SuppressWarnings("all")
public class ModernDateTime {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Modern Java Time API");

        // 1. Local vs Zoned vs Instant
        ConsoleFormatter.printStep("Time Zones & Instants", "Comparing Local time against UTC Instant");
        LocalDateTime localNow = LocalDateTime.now();
        Instant utcNow = Instant.now();
        ZonedDateTime zonedNewYork = ZonedDateTime.now(ZoneId.of("America/New_York"));

        System.out.println("Local Now (no zone):   " + localNow);
        System.out.println("UTC Instant (Standard):" + utcNow);
        System.out.println("Zoned New York Time:  " + zonedNewYork);

        // 2. Periods & Durations
        ConsoleFormatter.printStep("Measuring Time Ranges", "Calculating duration difference and period ages");
        LocalDate birthDate = LocalDate.of(1995, Month.OCTOBER, 12);
        LocalDate today = LocalDate.now();
        Period age = Period.between(birthDate, today);
        System.out.printf("  [PERIOD] Age computed: %d years, %d months, and %d days.%n",
                age.getYears(), age.getMonths(), age.getDays());

        LocalTime start = LocalTime.of(9, 0, 0);
        LocalTime end = LocalTime.of(17, 30, 0);
        Duration workDay = Duration.between(start, end);
        System.out.println("  [DURATION] Workday duration: " + workDay.toMinutes() + " minutes (" + workDay.toHours() + " hours)");

        // 3. Thread safe formatting
        ConsoleFormatter.printStep("Thread-safe Formatting", "Parsing strings using DateTimeFormatter");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime parsed = LocalDateTime.parse("2026-06-30 14:30", formatter);
        System.out.println("Parsed Date-Time: " + parsed);

        if (parsed.getYear() == 2026) {
            ConsoleFormatter.printSuccess("Time parsing and duration calculations executed correctly.");
        }
    }
}
