# Antigravity Prompt: Java Mastery Reference Project

# Project: Java Mastery Reference — Complete Concepts in One Codebase

## Objective
Build a single, well-organized Java project that serves as a living reference and interview-prep resource covering the *entire* breadth of core Java — from absolute basics to advanced/expert-level internals and patterns. Every concept should live in its own runnable, self-contained example so a reader can open one file, read it top to bottom, run it, and fully understand that concept — no external context needed.

## Audience
Must work for all levels simultaneously:
- A beginner should be able to follow the comments and run the code without prior Java knowledge.
- An experienced engineer should be able to jump straight to a topic for a quick refresher or interview review without wading through beginner explanation.
Achieve this by separating "what/why" (concept explainer) from "how" (code) clearly, and tagging each topic with a difficulty level.

## Tech Setup
- Java 17+ (prefer the latest LTS available in the environment).
- Build tool: Maven (single `pom.xml`, no external dependencies except JUnit 5 and, where genuinely useful, Mockito for testing examples).
- One project, organized by package per topic — not multiple repos/modules.
- Each topic package should compile and run independently via its own `main` method, in addition to having JUnit tests where it adds value (esp. for collections, generics, concurrency).

## Project Structure
```
java-mastery-reference/
├── pom.xml
├── README.md                         <- master index, learning path, how to navigate
├── src/main/java/com/mastery/
│   ├── basics/                       <- variables, data types, operators, control flow, arrays, strings
│   ├── oop/                          <- classes, objects, encapsulation, inheritance, polymorphism, abstraction, interfaces vs abstract classes, composition
│   ├── exceptions/                   <- checked/unchecked, custom exceptions, try-with-resources, finally semantics
│   ├── collections/                  <- List, Set, Map, Queue, Deque, Iterator, Comparable vs Comparator, fail-fast vs fail-safe, internal workings (HashMap, TreeMap, LinkedHashMap)
│   ├── generics/                     <- bounded types, wildcards, type erasure, generic methods/classes
│   ├── functional/                   <- lambdas, functional interfaces, method references, Optional
│   ├── streams/                      <- Stream API, collectors, parallel streams, lazy evaluation
│   ├── concurrency/                  <- Thread, Runnable, synchronized, volatile, locks, ExecutorService, CompletableFuture, concurrent collections, producer-consumer
│   ├── io/                           <- I/O streams, NIO.2, file handling, serialization
│   ├── datetime/                     <- java.time API (LocalDate, ZonedDateTime, Duration, Period)
│   ├── reflection/                   <- annotations (built-in + custom), reflection API, dynamic proxies
│   ├── enums_records/                <- enums (with methods/abstract methods), records, sealed classes
│   ├── designpatterns/               <- Singleton, Factory, Builder, Observer, Strategy, Decorator, Adapter (each in own sub-package)
│   ├── jvm_internals/                <- memory areas, class loading, garbage collection (conceptual + demonstrable where possible), String pool & immutability
│   ├── jdbc/                         <- basic JDBC CRUD example against an in-memory DB (H2), connection pooling concept
│   └── interview/                    <- classic interview coding problems solved using the concepts above (e.g., custom LinkedList, LRU cache, producer-consumer, thread-safe Singleton), each annotated with approach + complexity
└── src/test/java/com/mastery/...      <- JUnit 5 tests mirroring main structure where applicable
```

## Per-File Documentation Standard
Every example file must follow this consistent header/comment template:

```java
/**
 * CONCEPT: <name>
 * DIFFICULTY: Beginner | Intermediate | Advanced | Expert
 *
 * WHAT: <1-2 sentence plain-English definition>
 * WHY IT MATTERS: <real-world relevance / when you'd use it>
 *
 * KEY POINTS:
 *  - point 1
 *  - point 2
 *
 * COMMON INTERVIEW QUESTIONS:
 *  Q: ...
 *  A: <short answer>
 *
 * COMMON PITFALLS:
 *  - ...
 */
```
Followed by clean, well-commented, runnable code with a `main` method (or test) that prints/demonstrates the behavior clearly, including expected output noted in comments.

## README Requirements
The root `README.md` must include:
- A short intro explaining the project's purpose.
- A full table of contents linking to every package/topic, grouped by difficulty (Beginner → Intermediate → Advanced → Expert) and by category (Language Basics, OOP, Collections, Concurrency, Modern Java, Design Patterns, JVM, Interview Problems).
- A suggested "learning path" order for someone studying sequentially.
- A suggested "quick interview refresh" path for someone with limited time before an interview, pointing only to the highest-yield topics.
- Instructions to build/run (`mvn compile`, how to run an individual example's main method).

## Quality Bar
- No half-finished topics — every package listed above must be fully implemented before considered done.
- Code must compile and run cleanly with no warnings.
- Avoid external dependencies beyond JUnit/Mockito/H2 (keep it lightweight and runnable anywhere).
- Consistent naming and package conventions throughout.
- Favor clarity over cleverness — this is a teaching/reference codebase, not production-optimized code.

## Execution Plan (build incrementally, don't dump everything at once)
1. Scaffold the Maven project and folder/package structure first; verify it builds empty.
2. Implement Language Basics + OOP packages, verify compile/run, commit conceptually before moving on.
3. Implement Collections + Generics + Exceptions.
4. Implement Functional/Streams + Concurrency (these are usually the most interview-heavy — give them extra depth and more interview Q&A).
5. Implement I/O, Date/Time, Reflection/Annotations, Enums/Records.
6. Implement Design Patterns and JVM Internals.
7. Implement JDBC example.
8. Implement the Interview Problems package last, since it draws on everything above.
9. Write/finalize the master README index.
10. Do a final pass to ensure every file follows the documentation template and difficulty tagging is consistent.

## Deliverables Checklist
- [ ] Full Maven project that builds successfully
- [ ] Every topic package fully implemented per the structure above
- [ ] Every file follows the documentation header template
- [ ] Master README with navigation, learning path, and quick-revision path
- [ ] JUnit tests for collections, generics, and concurrency examples at minimum
