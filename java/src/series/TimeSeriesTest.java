package series;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TimeSeriesTest {
  @Nested
  public class Q1 {

    @Test
    public void timeSerieData() {
      TimeSeries.Data<String> data = new TimeSeries.Data<>(42L, "foo");
      assertEquals("foo", data.element());
      assertEquals(42L, data.timestamp());
    }

    @Test
    public void timeSerieDataNullNPE() {
      assertThrows(NullPointerException.class, () -> new TimeSeries.Data<Object>(24L, null));
    }
  }


  @Nested
  public class Q2 {

    @Test
    public void timeSerie() {
      TimeSeries<String> timeSerie = new TimeSeries<String>();
      assertNotNull(timeSerie);
    }

    @Test
    public void addAndSize() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(12L, "foo");
      timeSerie.add(15L, "bar");
      assertEquals(2, timeSerie.size());
    }

    @Test
    public void addNegative() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(-12L, "foo");
    }

    @Test
    public void addNullNPE() {
      var timeSerie = new TimeSeries<>();
      assertThrows(NullPointerException.class, () -> timeSerie.add(12L, null));
    }

    @Test
    public void addTimeOrdered() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(12L, "foo");
      assertThrows(IllegalStateException.class, () -> timeSerie.add(9L, "bar"));
    }

    @Test
    public void addTimeOrderedSameTimeOk() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(12L, "foo");
      timeSerie.add(12L, "bar");
    }

    @Test
    public void timeSerieEmpty() {
      var timeSerie = new TimeSeries<Integer>();
      assertEquals(0, timeSerie.size());
    }

    @Test
    public void addAndGet() {
      var timeSerie = new TimeSeries<Integer>();
      timeSerie.add(12L, 32);
      timeSerie.add(14L, 21);
      timeSerie.add(17L, 25);
      assertEquals(3, timeSerie.size());
      assertEquals(new TimeSeries.Data<Integer>(12L, 32), timeSerie.get(0));
      assertEquals(new TimeSeries.Data<Integer>(14L, 21), timeSerie.get(1));
      assertEquals(new TimeSeries.Data<Integer>(17L, 25), timeSerie.get(2));
    }

    @Test
    public void getOutOfBound() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(23L, "a");
      timeSerie.add(67L, "b");
      assertThrows(IndexOutOfBoundsException.class, () -> timeSerie.get(-1));
      assertThrows(IndexOutOfBoundsException.class, () -> timeSerie.get(2));
    }

    @Test
    public void addAndGetALot() {
      var timeSerie = new TimeSeries<Integer>();
      IntStream.range(0, 1_000_000).forEach(i -> timeSerie.add(i, i));
      assertEquals(1_000_000, timeSerie.size());
      for(var i = 0; i < 1_000_000; i++) {
        assertEquals(new TimeSeries.Data<Integer>(i, i), timeSerie.get(i));
      }
    }
  }

  @Nested
  public class Q3 {

    @Test
    public void index() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(23L, "a");
      timeSerie.add(67L, "b");
      TimeSeries<String>.Index index = timeSerie.index();
      assertEquals(2, index.size());
    }

    @Test
    public void indexEmpty() {
      var timeSerie = new TimeSeries<>();
      TimeSeries<Object>.Index index = timeSerie.index();
      assertEquals(0, index.size());
    }

    @Test
    public void indexALot() {
      var timeSerie = new TimeSeries<Integer>();
      IntStream.range(0, 1_000_000).forEach(i -> timeSerie.add(i, i));
      var index = timeSerie.index();
      assertEquals(1_000_000, index.size());
    }

    @Test
    public void indexNoPublicConstructor() {
      assertEquals(0, TimeSeries.Index.class.getConstructors().length);
    }
  }


  @Nested
  public class Q4 {

    @Test
    public void indexToString() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(23L, "a");
      timeSerie.add(67L, "b");
      TimeSeries<String>.Index index = timeSerie.index();
      assertEquals("""
        23 | a
        67 | b\
        """, index.toString());
    }

    @Test
    public void indexNegativeToString() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(-70L, "a");
      timeSerie.add(-30L, "b");
      timeSerie.add(10L, "c");
      TimeSeries<String>.Index index = timeSerie.index();
      assertEquals("""
        -70 | a
        -30 | b
        10 | c\
        """, index.toString());
    }

    @Test
    public void indexEmptyToString() {
      var timeSerie = new TimeSeries<>();
      var index = timeSerie.index();
      assertEquals("", index.toString());
    }
  }


  @Nested
  public class Q5 {

    @Test
    public void indexWithFilter() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(21L, "foo");
      timeSerie.add(24L, "bar");
      timeSerie.add(27L, "baz");
      TimeSeries<String>.Index index = timeSerie.index(value -> value.startsWith("ba"));
      assertEquals(2, index.size());
      assertEquals("""
        24 | bar
        27 | baz\
        """, index.toString());
    }

    @Test
    public void indexWithEmptyFilter() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(67L, "foo");
      timeSerie.add(89L, "bar");
      timeSerie.add(123L, "baz");
      var index = timeSerie.index(__ -> false);
      assertEquals(0, index.size());
      assertEquals("", index.toString());
    }

    @Test
    public void indexWithFilterEvenNumber() {
      var timeSerie = new TimeSeries<Integer>();
      timeSerie.add(67L, 24);
      timeSerie.add(89L, 17);
      timeSerie.add(123L, 26);
      var index = timeSerie.index(value -> value % 2 == 0);
      assertEquals(2, index.size());
      assertEquals("""
        67 | 24
        123 | 26\
        """, index.toString());
    }

    @Test
    public void indexWithFilterNullNPE() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(7L, "foo");
      timeSerie.add(189L, "bar");
      assertThrows(NullPointerException.class, () -> timeSerie.index(null));
    }

    @Test
    public void indexWithFilterObject() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(7L, "foo");
      timeSerie.add(189L, "bar");
      TimeSeries<String>.Index index = timeSerie.index((Object o) -> o.toString().length() == 3);
      assertEquals(2, index.size());
    }
  }


  @Nested
  public class Q6 {

    @Test
    public void indexForEach() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(121L, "foo");
      timeSerie.add(221L, "bar");
      timeSerie.add(321L, "baz");
      var index = timeSerie.index();
      var timeList = new ArrayList<Long>();
      var elementList = new ArrayList<String>();
      index.forEach(data -> {
        timeList.add(data.timestamp());
        elementList.add(data.element());
      });
      assertEquals(List.of(121L, 221L, 321L), timeList);
      assertEquals(List.of("foo", "bar", "baz"), elementList);
    }

    @Test
    public void indexWithFilterForEach() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(121L, "foo");
      timeSerie.add(221L, "bar");
      timeSerie.add(321L, "baz");
      var index = timeSerie.index(s -> s.startsWith("f"));
      index.forEach(data -> {
        assertEquals(121L, data.timestamp());
        assertEquals("foo", data.element());
      });
    }

    @Test
    public void indexForEachALot() {
      var timeSerie = new TimeSeries<Integer>();
      IntStream.range(0, 1_000_000).forEach(i -> timeSerie.add(i, i));
      var index = timeSerie.index();
      var box = new Object() { int i; };
      index.forEach(data -> {
        assertEquals(box.i, data.timestamp());
        assertEquals(box.i, data.element());
        box.i++;
      });
    }

    @Test
    public void indexWithFilterForEachNullNPE() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(12L, "foo");
      timeSerie.add(21L, "bar");
      timeSerie.add(22L, "baz");
      var index = timeSerie.index(s -> s.length() == 3);
      assertThrows(NullPointerException.class, () -> index.forEach(null));
    }

    @Test
    public void indexForEachWithObject() {
      var timeSerie = new TimeSeries<Integer>();
      var index = timeSerie.index();
      index.forEach((Object data) -> fail());
    }
  }


  @Nested
  public class Q7 {

    @Test
    public void indexWithFilterIterator() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(12L, "foo");
      timeSerie.add(21L, "bar");
      timeSerie.add(22L, "baz");
      var index = timeSerie.index(s -> s.startsWith("b"));
      Iterator<TimeSeries.Data<String>> iterator = index.iterator();
      assertTrue(iterator.hasNext());
      assertEquals("bar", iterator.next().element());
      assertTrue(iterator.hasNext());
      assertEquals("baz", iterator.next().element());
      assertFalse(iterator.hasNext());
    }

    @Test
    public void indexWithFilterIteratorALot() {
      var timeSerie = new TimeSeries<Integer>();
      IntStream.range(0, 1_000_000).forEach(i -> timeSerie.add(i, i));
      var index = timeSerie.index();
      var iterator = index.iterator();
      for(var i = 0; i < 1_000_000; i++) {
        assertTrue(iterator.hasNext());
        assertEquals(new TimeSeries.Data<Integer>(i, i), iterator.next());
      }
      assertFalse(iterator.hasNext());
    }

    @Test
    public void indexWithFilterIteratorEmpty() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(42L, "foo");
      var index = timeSerie.index(__ -> false);
      var iterator = index.iterator();
      assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void indexWithFilterIteratorAfterEnd() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(42L, "foo");
      var index = timeSerie.index();
      var iterator = index.iterator();
      iterator.next();
      assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void indexWithFilterIteratorRemove() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(42L, "foo");
      var index = timeSerie.index();
      var iterator = index.iterator();
      iterator.next();
      assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    public void indexForLoop() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(42L, "foo");
      timeSerie.add(45L, "bar");
      timeSerie.add(99L, "baz");
      var timeList = new ArrayList<Long>();
      var elementList = new ArrayList<String>();
      for(TimeSeries.Data<String> data: timeSerie.index()) {
        timeList.add(data.timestamp());
        elementList.add(data.element());
      }
      assertEquals(List.of(42L, 45L, 99L), timeList);
      assertEquals(List.of("foo", "bar", "baz"), elementList);
    }

    @Test
    public void indexWithFilterForLoop() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(42L, "foo");
      timeSerie.add(45L, "bar");
      timeSerie.add(99L, "baz");
      for(var data: timeSerie.index("baz"::equals)) {
        assertEquals(99L, data.timestamp());
        assertEquals("baz", data.element());
      }
    }

    @Test
    public void indexWithFilterEmptyForLoop() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(42L, "foo");
      timeSerie.add(45L, "bar");
      timeSerie.add(99L, "baz");
      for(var data: timeSerie.index(__ -> false)) {
        fail();
      }
    }

    @Test
    public void indexForLoopStreamIteratorTooSlow() {
      var timeSerie = new TimeSeries<>();
      timeSerie.add(42L, "foo");
      var index = timeSerie.index();
      var packageName = index.iterator().getClass().getPackageName();
      assertAll(
          () -> assertNotEquals("java.util", packageName),
          () -> assertNotEquals("java.util.stream", packageName)
      );
    }
  }


  @Nested
  public class Q8 {

    @Test
    public void indexOr() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(12L, "foo");
      timeSerie.add(21L, "bar");
      timeSerie.add(22L, "baz");
      timeSerie.add(30L, "whizz");
      var index1 = timeSerie.index(s -> s.startsWith("w"));
      var index2 = timeSerie.index(s -> s.startsWith("b"));
      var index = index1.or(index2);
      assertEquals(index.size(), 3);
      var timeList = new ArrayList<Long>();
      var elementList = new ArrayList<String>();
      for(var data: index) {
        timeList.add(data.timestamp());
        elementList.add(data.element());
      }
      assertEquals(List.of(21L, 22L, 30L), timeList);
      assertEquals(List.of("bar", "baz", "whizz"), elementList);
    }

    @Test
    public void indexOrNotTheSameTimeSerie() {
      var timeSerie1 = new TimeSeries<String>();
      timeSerie1.add(12L, "foo");
      var timeSerie2 = new TimeSeries<String>();
      timeSerie2.add(45L, "foo");
      assertThrows(IllegalArgumentException.class, () -> timeSerie1.index().or(timeSerie2.index()));
    }

    @Test
    public void indexOrWithAnEmptyIndex() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(12L, "foo");
      timeSerie.add(21L, "bar");
      timeSerie.add(22L, "baz");
      timeSerie.add(30L, "whizz");
      var index1 = timeSerie.index(__ -> false);
      var index2 = timeSerie.index(s -> s.startsWith("w"));
      var index = index1.or(index2);
      assertEquals(index.size(), 1);
      for(var data: index) {
        assertEquals(30L, data.timestamp());
        assertEquals("whizz", data.element());
      }
    }

    @Test
    public void indexOrWithAnEmptyIndex2() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(12L, "foo");
      timeSerie.add(21L, "bar");
      timeSerie.add(22L, "baz");
      timeSerie.add(30L, "whizz");
      var index1 = timeSerie.index(s -> s.startsWith("f"));
      var index2 = timeSerie.index(__ -> false);
      var index = index1.or(index2);
      assertEquals(index.size(), 1);
      for(var data: index) {
        assertEquals(12L, data.timestamp());
        assertEquals("foo", data.element());
      }
    }

    @Test
    public void indexOrWithBothEmptyIndexes() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(12L, "foo");
      timeSerie.add(21L, "bar");
      timeSerie.add(22L, "baz");
      timeSerie.add(30L, "whizz");
      var index1 = timeSerie.index(__ -> false);
      var index2 = timeSerie.index(__ -> false);
      var index = index1.or(index2);
      assertEquals(index.size(), 0);
      for(var data: index) {
        fail();
      }
    }

    @Test
    public void indexOrALot() {
      var timeSerie = new TimeSeries<Integer>();
      IntStream.range(0, 1_000_000).forEach(i -> timeSerie.add(i, i));
      var index1 = timeSerie.index(i -> i % 2 == 0);
      var index2 = timeSerie.index(i -> i % 2 == 1);
      var index = index1.or(index2);
      assertEquals(index.size(), 1_000_000);
      var i = 0;
      for(var data: index) {
        assertEquals(i, data.timestamp());
        assertEquals(i, data.element());
        i++;
      }
    }

    @Test
    public void indexOrNullNPE() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(23L, "foo");
      var index = timeSerie.index();
      assertThrows(NullPointerException.class, () -> index.or(null));
    }
  }


  @Nested
  public class Q9 {

    @Test
    public void indexAnd() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(12L, "foo");
      timeSerie.add(21L, "bar");
      timeSerie.add(22L, "baz");
      timeSerie.add(30L, "whizz");
      var index1 = timeSerie.index(s -> s.startsWith("w") || s.startsWith("f"));
      var index2 = timeSerie.index(s -> s.startsWith("f"));
      var index = index1.and(index2);
      assertEquals(index.size(), 1);
      for(var data: index) {
        assertEquals(12L, data.timestamp());
        assertEquals("foo", data.element());
      }
    }

    @Test
    public void indexAndNotTheSameTimeSerie() {
      var timeSerie1 = new TimeSeries<String>();
      timeSerie1.add(12L, "foo");
      var timeSerie2 = new TimeSeries<String>();
      timeSerie2.add(45L, "foo");
      assertThrows(IllegalArgumentException.class, () -> timeSerie1.index().and(timeSerie2.index()));
    }

    @Test
    public void indexAnd2() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(12L, "foo");
      timeSerie.add(21L, "bar");
      timeSerie.add(22L, "baz");
      timeSerie.add(30L, "whizz");
      var index1 = timeSerie.index(s -> s.endsWith("z"));
      var index2 = timeSerie.index(s -> s.startsWith("ba"));
      var index = index1.and(index2);
      assertEquals(index.size(), 1);
      for(var data: index) {
        assertEquals(22L, data.timestamp());
        assertEquals("baz", data.element());
      }
    }

    @Test
    public void indexAndWithAnEmptyIndex() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(12L, "foo");
      timeSerie.add(21L, "bar");
      timeSerie.add(22L, "baz");
      timeSerie.add(30L, "whizz");
      var index1 = timeSerie.index();
      var index2 = timeSerie.index(s -> s.startsWith("f"));
      var index = index1.and(index2);
      assertEquals(index.size(), 1);
      for(var data: index) {
        assertEquals(12L, data.timestamp());
        assertEquals("foo", data.element());
      }
    }

    @Test
    public void indexAndWithTheOtherEmptyIndex() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(12L, "foo");
      timeSerie.add(21L, "bar");
      timeSerie.add(22L, "baz");
      timeSerie.add(30L, "whizz");
      var index1 = timeSerie.index();
      var index2 = timeSerie.index(__ -> false);
      var index = index1.and(index2);
      assertEquals(index.size(), 0);
      for(var data: index) {
        fail();
      }
    }

    @Test
    public void indexAndWithBothEmptyIndexes() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(12L, "foo");
      timeSerie.add(21L, "bar");
      timeSerie.add(22L, "baz");
      timeSerie.add(30L, "whizz");
      var index1 = timeSerie.index(__ -> false);
      var index2 = timeSerie.index(__ -> false);
      var index = index1.and(index2);
      assertEquals(index.size(), 0);
      for(var data: index) {
        fail();
      }
    }

    @Test
    public void indexAndALot() {
      var timeSerie = new TimeSeries<Integer>();
      IntStream.range(0, 1_000_000).forEach(i -> timeSerie.add(i, i));
      var index1 = timeSerie.index();
      var index2 = timeSerie.index();
      var index = index1.and(index2);
      assertEquals(index.size(), 1_000_000);
      var i = 0;
      for(var data: index) {
        assertEquals(i, data.timestamp());
        assertEquals(i, data.element());
        i++;
      }
    }

    @Test
    public void indexAndNullNPE() {
      var timeSerie = new TimeSeries<String>();
      timeSerie.add(23L, "foo");
      var index = timeSerie.index();
      assertThrows(NullPointerException.class, () -> index.and(null));
    }
  }


  @Nested
  public class Q10 {

    @Test
    public void indexOrCovariant() {
      TimeSeries<String> timeSerie = new TimeSeries<String>();
      TimeSeries<? extends String> timeSerie2 = timeSerie;
      var index = timeSerie.index().or(timeSerie2.index());
      assertEquals(0, index.size());
    }

    @Test
    public void indexAndCovariant() {
      TimeSeries<String> timeSerie = new TimeSeries<String>();
      TimeSeries<? extends String> timeSerie2 = timeSerie;
      var index = timeSerie.index().and(timeSerie2.index());
      assertEquals(0, index.size());
    }
  }
}