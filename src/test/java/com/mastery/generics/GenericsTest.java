package com.mastery.generics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class GenericsTest {

    @Test
    public void testBoundedNumericBox() {
        GenericBasics.NumericBox<Integer> intBox = new GenericBasics.NumericBox<>(42);
        GenericBasics.NumericBox<Double> doubleBox = new GenericBasics.NumericBox<>(3.14);

        assertEquals(42.0, intBox.doubleValue());
        assertEquals(3.14, doubleBox.doubleValue());
    }

    @Test
    public void testPecsWildcardConstraints() {
        List<Integer> intList = List.of(1, 2, 3);
        
        // Sum reads from list (Producer extends)
        double sum = sumOfList(intList);
        assertEquals(6.0, sum);

        // Add writes to list (Consumer super)
        List<Number> numList = new ArrayList<>();
        addNumbers(numList);
        assertEquals(5, numList.size());
        assertEquals(1, numList.get(0));
    }

    private static double sumOfList(List<? extends Number> src) {
        double sum = 0.0;
        for (Number num : src) {
            sum += num.doubleValue();
        }
        return sum;
    }

    private static void addNumbers(List<? super Integer> dest) {
        for (int i = 1; i <= 5; i++) {
            dest.add(i);
        }
    }
}
