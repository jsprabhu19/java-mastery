package com.mastery.collections;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("all")
public class CollectionsTest {

    @Test
    public void testInconsistentSetBehavior() {
        // equals checks names, compareTo checks ids
        ListsAndSets.InconsistentItem item1 = new ListsAndSets.InconsistentItem(1, "Apple");
        ListsAndSets.InconsistentItem item2 = new ListsAndSets.InconsistentItem(2, "Apple");

        // HashSet checks equals -> item1 equals item2 -> size 1
        Set<ListsAndSets.InconsistentItem> hashSet = new HashSet<>();
        hashSet.add(item1);
        hashSet.add(item2);
        assertEquals(1, hashSet.size());

        // TreeSet checks compareTo -> 1 != 2 -> size 2
        Set<ListsAndSets.InconsistentItem> treeSet = new TreeSet<>();
        treeSet.add(item1);
        treeSet.add(item2);
        assertEquals(2, treeSet.size());
    }

    @Test
    public void testHashMapCollisionsAndRetrieval() {
        Map<MapsInternals.CollidingKey, String> map = new java.util.HashMap<>();
        MapsInternals.CollidingKey k1 = new MapsInternals.CollidingKey(1);
        MapsInternals.CollidingKey k2 = new MapsInternals.CollidingKey(2);

        map.put(k1, "Val1");
        map.put(k2, "Val2");

        // Asserts separate keys work even under identical hashCodes (collisions)
        assertEquals(2, map.size());
        assertEquals("Val1", map.get(k1));
        assertEquals("Val2", map.get(k2));
    }
}
