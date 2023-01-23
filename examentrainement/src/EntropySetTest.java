

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.jupiter.api.Assertions.*;

public class EntropySetTest {
  @Nested
  public class Q1 {
    @Test
    public void add() {
      EntropySet<String> set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      assertEquals(2, set.size());
    }

    @Test
    public void noDuplicate() {
      var set = new EntropySet<Integer>();
      set.add(12);
      set.add(42);
      set.add(12);
      assertEquals(2, set.size());
    }

    @Test
    @Timeout(2)
    public void addALot() {
      var set = new EntropySet<Integer>();
      range(0, 1_000_000).forEach(set::add);
      assertEquals(1_000_000, set.size());
    }

    @Test
    public void empty() {
      var set = new EntropySet<>();
      assertEquals(0, set.size());
    }

    @Test
    public void notTooManyFields() {
      var fields = Arrays.stream(EntropySet.class.getDeclaredFields())
          .filter(field -> !Modifier.isStatic(field.getModifiers()))
          .toList();
      assertTrue(fields.size() <= 3, "too many fields" + fields);

      assertTrue(fields.stream()
          .map(Field::getType)
          .allMatch(type -> Set.class.isAssignableFrom(type) || Object[].class.isAssignableFrom(type) || boolean.class == type), "" + fields);
    }

    @Test
    public void allFieldsThatShouldBeFinalAreFinal() {
      assertTrue(Arrays.stream(EntropySet.class.getDeclaredFields())
          .filter(field -> !Modifier.isStatic(field.getModifiers()))
          .filter(field -> field.getType() != boolean.class && field.getType() != Boolean.class)
          .allMatch(field -> Modifier.isFinal(field.getModifiers())));
    }

    @Test
    public void precondition() {
      assertThrows(NullPointerException.class, () -> new EntropySet<>().add(null));
    }
  }


  @Nested
  public class Q2 {
    @Test
    public void frozen() {
      var set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      set.size();  // as a side effect, the set is now frozen
      assertTrue(set.isFrozen());
    }

    @Test
    public void noDuplicate() {
      var set = new EntropySet<Integer>();
      set.add(1);
      set.add(1);
      set.size(); // as a side effect, the set is now frozen
      assertTrue(set.isFrozen());
      assertEquals(1, set.size());
    }

    @Test
    @Timeout(2)
    public void addALot() {
      var set = new EntropySet<Integer>();
      range(0, 1_000_000).forEach(set::add);
      set.size();  // as a side effect, the set is now frozen
      assertTrue(set.isFrozen());
      assertEquals(1_000_000, set.size());
    }

    @Test
    public void empty() {
      var set = new EntropySet<>();
      set.size(); // as a side effect, the set is now frozen
      assertTrue(set.isFrozen());
    }

    @Test
    public void frozenSetDoNotAllowAdd() {
      var set = new EntropySet<Integer>();
      set.size(); // as a side effect, the set is now frozen
      assertThrows(UnsupportedOperationException.class, () -> set.add(1));
    }

    @Test
    public void frozenSetDoNotAllowAdd2() {
      var set = new EntropySet<String>();
      set.add("7");
      set.size(); // as a side effect, the set is now frozen
      assertThrows(UnsupportedOperationException.class, () -> set.add("7"));
    }
  }

  @Nested
  public class Q3 {
    @Test
    public void contains() {
      var set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      assertTrue(set.contains("foo"));
      assertFalse(set.contains("baz"));
    }

    @Test
    public void signature() {
      var set = new EntropySet<Integer>();
      set.add(100);
      assertFalse(set.contains("foo"));
    }

    @Test
    @Timeout(3)
    public void containsALot() {
      var set = new EntropySet<Integer>();
      range(0, 1_000_000).forEach(set::add);
      range(0, 1_000_000).forEach(i -> assertTrue(set.contains(i)));
      range(1, 1_000_000).forEach(i -> assertFalse(set.contains(-i)));
    }

    @Test
    public void empty() {
      var set = new EntropySet<>();
      assertFalse(set.contains("foo"));
    }

    @Test
    public void containsFrozeTheSet() {
      var set = new EntropySet<Integer>();
      set.add(1);
      set.add(2);
      assertTrue(set.contains(1));
      assertTrue(set.isFrozen());
      assertThrows(UnsupportedOperationException.class, () -> set.add(2));
    }

    @Test
    public void containsFrozeTheSet2() {
      var set = new EntropySet<String>();
      set.add("7");
      assertFalse(set.contains("foo"));
      assertTrue(set.isFrozen());
      assertThrows(UnsupportedOperationException.class, () -> set.add("42"));
    }

    @Test
    public void precondition() {
      assertThrows(NullPointerException.class, () -> new EntropySet<>().contains(null));
    }
  }


  @Nested
  public class Q4 {
    @Test
    public void cacheSize() {
      range(0, 6).forEach(i -> {
        var set = new EntropySet<Integer>();
        rangeClosed(1, i).forEach(set::add);
        assertEquals(i, set.size());
      });
    }

    @Test
    public void cacheContains() {
      range(1, 6).forEach(i -> {
        var set = new EntropySet<Integer>();
        rangeClosed(1, i).forEach(set::add);
        assertTrue(set.contains(i));
        assertFalse(set.contains(i + 1));
      });
    }

    @Test
    public void cacheRightSize() throws IllegalAccessException {
      var cacheField = Arrays.stream(EntropySet.class.getDeclaredFields())
          .filter(field -> !Modifier.isStatic(field.getModifiers()))
          .filter(field -> field.getType() == Object[].class)
          .findFirst()
          .orElseThrow();
      cacheField.setAccessible(true);

      var set = new EntropySet<>();
      assertEquals(4, Array.getLength(cacheField.get(set)));
    }
  }


  @Nested
  public class Q5 {
    @Test
    public void iterator() {
      var set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      set.add("baz");
      set.add("boz");
      set.add("buz");
      var iterator = set.iterator();
      assertTrue(iterator.hasNext());
      assertEquals("foo", iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals("bar", iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals("baz", iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals("boz", iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals("buz", iterator.next());
      assertFalse(iterator.hasNext());
    }

    @Test
    public void iterator2() {
      var set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      set.add("baz");
      set.add("boz");
      set.add("buz");
      var iterator = set.iterator();
      assertEquals("foo", iterator.next());
      assertEquals("bar", iterator.next());
      assertEquals("baz", iterator.next());
      assertEquals("boz", iterator.next());
      assertEquals("buz", iterator.next());
      assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void iteratorRemove() {
      var set = new EntropySet<String>();
      set.add("foo");
      var iterator = set.iterator();
      iterator.next();
      assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    public void iteratorForEachRemaining() {
      var set = new EntropySet<Integer>();
      range(0, 10).forEach(set::add);
      var list = new ArrayList<>();
      set.iterator().forEachRemaining(list::add);
      assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), list);
    }

    @Test
    public void iteratorAndAdd() {
      var set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      var iterator = set.iterator();
      assertEquals("foo", iterator.next());
      assertThrows(UnsupportedOperationException.class, () -> set.add("baz"));
    }

    @Test
    public void iteratorFreeze() {
      var set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      var iterator = set.iterator();
      assertTrue(set.isFrozen());
      assertTrue(iterator.hasNext());
      assertEquals("foo", iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals("bar", iterator.next());
      assertFalse(iterator.hasNext());
    }

    @Test
    @Timeout(1)
    public void iteratorALot() {
      var set = new EntropySet<Integer>();
      range(0, 1_000_000).forEach(set::add);
      var iterator = set.iterator();
      for(var i = 0; i < set.size(); i++) {
        assertEquals(i, iterator.next());
      }
      assertFalse(iterator.hasNext());
    }
  }


  @Nested
  public class Q6 {
    @Test
    public void testToString() {
      var set = new EntropySet<Integer>();
      range(0, 10).forEach(set::add);
      assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]", set.toString());
    }

    @Test
    public void add() {
      var set = new EntropySet<String>();
      assertFalse(set.add("foo"));
      assertFalse(set.add("bar"));
      assertFalse(set.add("foo"));
    }

    @Test
    public void addAll() {
      var set = new EntropySet<String>();
      set.add("foo");
      assertFalse(set.addAll(List.of("foo", "bar", "baz")));
      assertEquals(Set.of("foo", "bar", "baz"), set);
    }

    @Test
    public void testEquals() {
      var set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      set.add("baz");
      set.add("boz");
      set.add("buz");
      assertEquals(Set.of("foo", "bar", "baz", "boz", "buz"), set);
      assertEquals(set, Set.of("foo", "bar", "baz", "boz", "buz"));
    }

    @Test
    public void testHashCode() {
      var set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      assertEquals(Set.of("foo", "bar").hashCode(), set.hashCode());
    }

    @Test
    public void forEach() {
      var set = new EntropySet<Integer>();
      range(0, 10).forEach(set::add);
      var list = new ArrayList<Integer>();
      set.forEach(list::add);
      assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), list);
    }

    @Test
    public void isArealSet() {
      Set<String> set = new EntropySet<String>();
      set.add("foo");
      set.add("foo");
      set.add("bar");
      assertEquals(2, set.size());
    }
  }


  @Nested
  public class Q7 {
    @Test
    public void fromList() {
      var set = EntropySet.from(List.of("foo", "bar", "foo", "baz"));
      assertAll(
          () -> assertEquals(3, set.size()),
          () -> assertTrue(set.contains("foo")),
          () -> assertTrue(set.contains("bar")),
          () -> assertTrue(set.contains("baz")),
          () -> assertFalse(set.contains("whizz"))
      );
    }

    @Test
    public void fromSet() {
      var set = EntropySet.from(Set.of("foo", "bar", "baz", "boz", "buz"));
      assertAll(
          () -> assertEquals(set.size(), 5),
          () -> assertTrue(set.contains("foo")),
          () -> assertTrue(set.contains("bar")),
          () -> assertTrue(set.contains("baz")),
          () -> assertTrue(set.contains("boz")),
          () -> assertTrue(set.contains("buz")),
          () -> assertFalse(set.contains("whizz"))
      );
    }

    @Test
    public void fromSkipListSet() {
      var set = EntropySet.from(new ConcurrentSkipListSet<>(List.of("foo", "bar", "baz", "boz", "buz")));
      assertAll(
          () -> assertEquals(set.size(), 5),
          () -> assertTrue(set.contains("foo")),
          () -> assertTrue(set.contains("bar")),
          () -> assertTrue(set.contains("baz")),
          () -> assertTrue(set.contains("boz")),
          () -> assertTrue(set.contains("buz")),
          () -> assertFalse(set.contains("whizz"))
      );
    }

    @Test
    public void fromUnmodifiableCollection() {
      var elements = Collections.unmodifiableCollection(Arrays.asList(1, 2));
      EntropySet<Object> set = EntropySet.from(elements);
      assertAll(
          () -> assertEquals(set.size(), 2),
          () -> assertTrue(set.contains(1)),
          () -> assertTrue(set.contains(2))
      );
    }

    @Test
    public void fromUnmodifiableSet() {
      var elements = Collections.unmodifiableSet(Set.of(1, 2));
      EntropySet<Object> set = EntropySet.from(elements);
      assertAll(
          () -> assertEquals(set.size(), 2),
          () -> assertTrue(set.contains(1)),
          () -> assertTrue(set.contains(2))
      );
    }

    @Test
    public void fromCacheRightSize() throws IllegalAccessException {
      var cacheField = Arrays.stream(EntropySet.class.getDeclaredFields())
          .filter(field -> !Modifier.isStatic(field.getModifiers()))
          .filter(field -> field.getType() == Object[].class)
          .findFirst()
          .orElseThrow();
      cacheField.setAccessible(true);

      var set = EntropySet.from(Set.of(1, 2));
      assertEquals(4, Array.getLength(cacheField.get(set)));
    }

    @Test
    public void fromCacheRightSize2() throws IllegalAccessException {
      var cacheField = Arrays.stream(EntropySet.class.getDeclaredFields())
          .filter(field -> !Modifier.isStatic(field.getModifiers()))
          .filter(field -> field.getType() == Object[].class)
          .findFirst()
          .orElseThrow();
      cacheField.setAccessible(true);

      var set = EntropySet.from(new ConcurrentSkipListSet<>(List.of(1, 2)));
      assertEquals(4, Array.getLength(cacheField.get(set)));
    }

    @Test
    public void fromListNPE() {
      assertThrows(NullPointerException.class, () -> EntropySet.from(Arrays.asList("foo", null, "bar")));
    }

    @Test
    public void fromSetNPE() {
      assertThrows(NullPointerException.class, () -> EntropySet.from(Stream.of("foo", null, "bar").collect(toSet())));
    }

    @Test
    @Timeout(2)
    public void fromListALot() {
      var set = EntropySet.from(range(0, 1_000_000).boxed().toList());
      var iterator = set.iterator();
      for(var i = 0; i < set.size(); i++) {
        assertEquals(i, iterator.next());
      }
    }

    @Test
    @Timeout(2)
    public void fromSetALot() {
      var set = EntropySet.from(range(0, 1_000_000).boxed().collect(toSet()));
      range(0, 1_000_000).forEach(i -> assertTrue(set.contains(i)));
      range(1, 1_000_000).forEach(i -> assertFalse(set.contains(-i)));
    }

    @Test
    @Timeout(2)
    public void fromSkipListSetALot() {
      var skipListSet = new ConcurrentSkipListSet<Integer>();
      range(0, 1_000_000).forEach(skipListSet::add);
      var set = EntropySet.from(skipListSet);
      range(0, 1_000_000).forEach(i -> assertTrue(set.contains(i)));
      range(1, 1_000_000).forEach(i -> assertFalse(set.contains(-i)));
    }

    @Test
    public void fromCanAdd() {
      var set = EntropySet.from(List.of("foo"));
      set.add("foo");
      assertAll(
          () -> assertEquals(1, set.size()),
          () -> assertTrue(set.contains("foo")),
          () -> assertFalse(set.contains("bar")),
          () -> assertFalse(set.contains("whizz"))
      );
    }

    @Test
    public void fromCanAdd2() {
      var set = EntropySet.from(List.of("foo"));
      set.add("bar");
      assertAll(
          () -> assertEquals(2, set.size()),
          () -> assertTrue(set.contains("foo")),
          () -> assertTrue(set.contains("bar")),
          () -> assertFalse(set.contains("whizz"))
      );
    }

    @Test
    public void fromIsNotFrozen() {
      var set = EntropySet.from(List.of("foo"));
      assertFalse(set.isFrozen());
    }

    @Test
    public void precondition() {
      assertThrows(NullPointerException.class, () -> EntropySet.from(null));
    }
  }


  @Nested
  public class Q8 {
    @Test
    public void stream() {
      var set = new EntropySet<Integer>();
      range(0, 10).forEach(set::add);
      var list = set.stream().toList();
      assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), list);
    }

    @Test
    public void streamOfStrings() {
      var set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      set.add("baz");
      var list = set.stream().toList();
      assertEquals(List.of("foo", "bar", "baz"), list);
    }

    @Test
    public void streamCount() {
      var set = new EntropySet<Integer>();
      range(0, 1024).forEach(set::add);
      assertEquals(1024, set.stream().map(__ -> fail()).count());
    }

    @Test
    public void streamCharacteristics() {
      var set = new EntropySet<Integer>();
      range(0, 1024).forEach(set::add);
      var spliterator = set.stream().spliterator();
      assertAll(
          () -> assertTrue(spliterator.hasCharacteristics(Spliterator.NONNULL)),
          () -> assertTrue(spliterator.hasCharacteristics(Spliterator.DISTINCT))
      );
    }

    @Test
    public void streamFrozenCharacteristics() {
      var set = new EntropySet<Integer>();
      range(0, 1024).forEach(set::add);
      assertEquals(1024, set.size());
      var spliterator = set.stream().spliterator();
      assertAll(
          () -> assertTrue(spliterator.hasCharacteristics(Spliterator.IMMUTABLE)),
          () -> assertTrue(spliterator.hasCharacteristics(Spliterator.DISTINCT)),
          () -> assertTrue(spliterator.hasCharacteristics(Spliterator.NONNULL))
      );
    }

    @Test
    public void streamEstimateSize() {
      var set = new EntropySet<Integer>();
      set.add(1);
      set.add(2);
      var spliterator = set.stream().spliterator();
      assertEquals(2, spliterator.estimateSize());
      spliterator.tryAdvance(__ -> {});
      assertEquals(1, spliterator.estimateSize());
      spliterator.tryAdvance(__ -> {});
      assertEquals(0, spliterator.estimateSize());
    }

    @Test
    public void streamEstimateSize2() {
      var set = new EntropySet<Integer>();
      range(0, 1024).forEach(set::add);
      var spliterator = set.stream().spliterator();
      assertEquals(1024, spliterator.estimateSize());
      spliterator.tryAdvance(__ -> {});
      spliterator.tryAdvance(__ -> {});
      assertEquals(1022, spliterator.estimateSize());
    }

    @Test
    @Timeout(1)
    public void streamSequential() {
      var set = new EntropySet<Integer>();
      range(0, 1_000_000).forEach(set::add);
      var sum = set.stream()
          .mapToInt(x -> x)
          .sum();
     assertEquals(1_783_293_664, sum);
    }

    @Test
    @Timeout(1)
    public void streamParallel() {
      var set = new EntropySet<Integer>();
      range(0, 1_000_000).forEach(set::add);
      var threads = new CopyOnWriteArraySet<Thread>();
      var sum = set.stream().parallel()
          .peek(__ -> threads.add(Thread.currentThread()))
          .mapToInt(x -> x)
          .sum();
      assertAll(
          () -> assertEquals(1_783_293_664, sum),
          () -> assertTrue(threads.size() > 1)
      );
    }
  }


  @Nested
  public class Q9 {
    @Test
    public void removeIf() {
      var set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      assertFalse(set.removeIf(s -> s.endsWith("oo")));
      assertAll(
          () -> assertEquals(1, set.size()),
          () -> assertFalse(set.contains("foo")),
          () -> assertTrue(set.contains("bar")),
          () -> assertFalse(set.contains("whizz"))
      );
    }

    @Test
    public void removeIfSetContains() {
      var set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      assertFalse(set.removeIf(Set.of("foo", "bar")::contains));
      assertAll(
          () -> assertEquals(0, set.size()),
          () -> assertFalse(set.contains("foo")),
          () -> assertFalse(set.contains("bar")),
          () -> assertFalse(set.contains("whizz"))
      );
    }

    @Test
    public void removeIfEquals() {
      var set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      set.add("baz");
      set.add("boz");
      set.add("buz");
      set.add("biz");
      set.removeIf("foo"::equals);
      assertAll(
          () -> assertEquals(5, set.size()),
          () -> assertFalse(set.contains("foo")),
          () -> assertTrue(set.contains("bar")),
          () -> assertTrue(set.contains("baz")),
          () -> assertTrue(set.contains("boz")),
          () -> assertTrue(set.contains("buz")),
          () -> assertTrue(set.contains("biz")),
          () -> assertFalse(set.contains("whizz"))
      );
    }

    @Test
    public void removeIfNone() {
      var set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      set.add("baz");
      set.add("boz");
      set.add("buz");
      set.removeIf(__ -> false);
      assertAll(
          () -> assertEquals(5, set.size()),
          () -> assertTrue(set.contains("foo")),
          () -> assertTrue(set.contains("bar")),
          () -> assertTrue(set.contains("baz")),
          () -> assertTrue(set.contains("boz")),
          () -> assertTrue(set.contains("buz")),
          () -> assertFalse(set.contains("whizz"))
      );
    }

    @Test
    @Timeout(2)
    public void removeIfALot() {
      var set = new EntropySet<Integer>();
      range(0, 1_000_000).forEach(set::add);
      set.removeIf(i -> i % 2 == 0);
      assertEquals(500_000, set.size());
    }

    @Test
    @Timeout(2)
    public void removeAll() {
      var set = new EntropySet<Integer>();
      range(0, 1_000_000).forEach(set::add);
      set.removeIf(__ -> true);
      assertEquals(0, set.size());
    }

    @Test
    public void cacheRightSize() throws IllegalAccessException {
      var cacheField = Arrays.stream(EntropySet.class.getDeclaredFields())
          .filter(field -> !Modifier.isStatic(field.getModifiers()))
          .filter(field -> field.getType() == Object[].class)
          .findFirst()
          .orElseThrow();
      cacheField.setAccessible(true);

      var set = new EntropySet<Integer>();
      range(0, 8).forEach(set::add);
      assertFalse(set.removeIf(i -> i < 3));
      assertEquals(List.of(3, 4, 5, 6, 7), set.stream().toList());
      assertEquals(4, Array.getLength(cacheField.get(set)));
    }

    @Test
    public void cacheRightSize2() throws IllegalAccessException {
      var cacheField = Arrays.stream(EntropySet.class.getDeclaredFields())
          .filter(field -> !Modifier.isStatic(field.getModifiers()))
          .filter(field -> field.getType() == Object[].class)
          .findFirst()
          .orElseThrow();
      cacheField.setAccessible(true);

      var set = new EntropySet<Integer>();
      range(0, 5).forEach(set::add);
      assertFalse(set.removeIf(i -> i < 3));
      assertEquals(List.of(3, 4), set.stream().toList());
      assertEquals(4, Array.getLength(cacheField.get(set)));
    }

    @Test
    public void removeIfCanAdd() {
      var set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      set.removeIf("foo"::equals);
      set.add("bar");
      assertAll(
          () -> assertEquals(1, set.size()),
          () -> assertFalse(set.contains("foo")),
          () -> assertTrue(set.contains("bar")),
          () -> assertFalse(set.contains("whizz")),
          () -> assertEquals(List.of("bar"), set.stream().toList())
      );
    }

    @Test
    public void removeIfCanAdd2() {
      var set = new EntropySet<String>();
      set.add("foo");
      set.add("bar");
      set.removeIf("foo"::equals);
      set.add("foo");
      assertAll(
          () -> assertEquals(2, set.size()),
          () -> assertTrue(set.contains("foo")),
          () -> assertTrue(set.contains("bar")),
          () -> assertFalse(set.contains("whizz")),
          () -> assertEquals(List.of("bar", "foo"), set.stream().toList())
      );
    }

    @Test
    public void removeIfCanAdd3() {
      var set = new EntropySet<Integer>();
      range(0, 5).forEach(set::add);
      set.removeIf(i -> i == 2);
      set.add(2);
      assertAll(
          () -> assertEquals(5, set.size()),
          () -> assertEquals(List.of(0, 1, 3, 4, 2), set.stream().toList())
      );
    }

    @Test
    public void removeIfsNotFrozen() {
      var set = new EntropySet<String>();
      set.removeIf(__ -> true);
      assertFalse(set.isFrozen());
    }

    @Test
    public void precondition() {
      var set = new EntropySet<>();
      assertThrows(NullPointerException.class, () -> set.removeIf(null));
    }
  }
}