package com.mastery.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JavaConcept {
    String name();
    Difficulty difficulty();
    String what();
    String whyItMatters();
    String[] keyPoints();
    Question[] interviewQuestions() default {};
    String[] pitfalls() default {};

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface Question {
        String question();
        String answer();
    }
}
