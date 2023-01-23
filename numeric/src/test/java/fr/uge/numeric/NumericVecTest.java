package fr.uge.numeric;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class NumericVecTest {

  @Nested
  public class Q1 {
    @Test
    public void longs() {
      var seq = NumericVec.longs();
      seq.add(1L);
      seq.add(42L);
      seq.add(747L);
      assertAll(
          () -> assertEquals(3, seq.size()),
          () -> assertEquals(1L, seq.get(0)),
          () -> assertEquals(42L, seq.get(1)),
          () -> assertEquals(747L, seq.get(2))
      );
    }

    @Test
    public void longTyping() {
      NumericVec<Long> seq = NumericVec.longs();
      assertNotNull(seq);
    }

    @Test
    public void emptyLongs() {
      var seq = NumericVec.longs();
      assertEquals(0, seq.size());
    }

    @Test
    public void getOutOfBounds() {
      var seq = NumericVec.longs();
      seq.add(1L);
      seq.add(42L);
      assertAll(
          () -> assertThrows(IndexOutOfBoundsException.class, () -> seq.get(-1)),
          () -> assertThrows(IndexOutOfBoundsException.class, () -> seq.get(2))
      );
    }

    @Test
    public void addPrecondition() {
      var seq = NumericVec.longs();
      assertThrows(NullPointerException.class, () -> seq.add(null));
    }

    @Test
    public void noPublicConstructor() {
      assertEquals(0, NumericVec.class.getConstructors().length);
    }
  }


  @Nested
  public class Q2 {

    @Test
    public void longsResizeable() {
      var seq = NumericVec.longs();
      for(var i = 0L; i < 17L; i++) {
        seq.add(i);
      }
      assertEquals(17, seq.size());
    }

    @Test
    public void longsALotOfValues() {
      var seq = NumericVec.longs();
      LongStream.range(0, 1_000_000).forEach(seq::add);
      assertAll(
          () -> assertEquals(1_000_000, seq.size()),
          () -> IntStream.range(0, 1_000_000).forEach(i -> assertEquals(i, seq.get(i)))
      );
    }
  }


  @Nested
  public class Q3 {

    @Test
    public void longsFormat() {
      var seq = NumericVec.longs();
      seq.add(1L);
      seq.add(42L);
      seq.add(747L);
      assertEquals("[1, 42, 747]", "" + seq);
    }

    @Test
    public void longsToStringOneValue() {
      var seq = NumericVec.longs();
      seq.add(727L);
      assertEquals("[727]", "" + seq);
    }

    @Test
    public void longsEmptyFormat() {
      var seq = NumericVec.longs();
      assertEquals("[]", "" + seq);
    }
  }


  @Nested
  public class Q4 {

    @Test
    public void intsWithValues() {
      var seq = NumericVec.ints(1, 42, 747);
      assertAll(
          () -> assertEquals(3, seq.size()),
          () -> assertEquals(1, seq.get(0)),
          () -> assertEquals(42, seq.get(1)),
          () -> assertEquals(747, seq.get(2))
      );
    }

    @Test
    public void longsWithValues() {
      var seq = NumericVec.longs(1L, 42L, 747L);
      assertAll(
          () -> assertEquals(3, seq.size()),
          () -> assertEquals(1L, seq.get(0)),
          () -> assertEquals(42L, seq.get(1)),
          () -> assertEquals(747L, seq.get(2))
      );
    }

    @Test
    public void doublesWithValues() {
      var seq = NumericVec.doubles(2., 256., 16.);
      assertAll(
          () -> assertEquals(3, seq.size()),
          () -> assertEquals(2., seq.get(0)),
          () -> assertEquals(256., seq.get(1)),
          () -> assertEquals(16., seq.get(2))
      );
    }

    @Test
    public void intsWithValuesToString() {
      var seq = NumericVec.ints(1, 42, 747);
      assertEquals("[1, 42, 747]", seq.toString());
    }

    @Test
    public void longsWithValuesToString() {
      var seq = NumericVec.longs(1L, 42L, 747L);
      assertEquals("[1, 42, 747]", seq.toString());
    }

    @Test
    public void doublesWithValuesToString() {
      var seq = NumericVec.doubles(2., 256., 16.);
      assertEquals("[2.0, 256.0, 16.0]", seq.toString());
    }

    @Test
    public void intsOrLongsOrDoublesWithValuesTyping() {
      NumericVec<Integer> intSeq = NumericVec.ints(42);
      NumericVec<Long> longSeq = NumericVec.longs(42L);
      NumericVec<Double> doubleSeq = NumericVec.doubles(256.);
      assertAll(
          () -> assertNotNull(intSeq),
          () -> assertNotNull(longSeq),
          () -> assertNotNull(doubleSeq)
      );
    }

    @Test
    public void intsWithValuesAdd() {
      var seq = NumericVec.ints(1, 42);
      seq.add(0);
      seq.add(-31);
      assertAll(
          () -> assertEquals(4, seq.size()),
          () -> assertEquals(1, seq.get(0)),
          () -> assertEquals(42, seq.get(1)),
          () -> assertEquals(0, seq.get(2)),
          () -> assertEquals(-31, seq.get(3))
      );
    }

    @Test
    public void longsWithValuesAdd() {
      var seq = NumericVec.longs(1L, 42L);
      seq.add(0L);
      seq.add(-31L);
      assertAll(
          () -> assertEquals(4, seq.size()),
          () -> assertEquals(1L, seq.get(0)),
          () -> assertEquals(42L, seq.get(1)),
          () -> assertEquals(0L, seq.get(2)),
          () -> assertEquals(-31L, seq.get(3))
      );
    }

    @Test
    public void doubleWithValuesAdd() {
      var seq = NumericVec.doubles(2., 16.);
      seq.add(0.);
      seq.add(-32.);
      assertAll(
          () -> assertEquals(4, seq.size()),
          () -> assertEquals(2., seq.get(0)),
          () -> assertEquals(16., seq.get(1)),
          () -> assertEquals(0., seq.get(2)),
          () -> assertEquals(-32., seq.get(3))
      );
    }

    @Test
    public void intsWithValuesBig() {
      var seq = NumericVec.ints(IntStream.range(0, 1_000_000).toArray());
      assertAll(
          () -> assertEquals(1_000_000, seq.size()),
          () -> IntStream.range(0, 1_000_000).forEach(i -> assertEquals(i, seq.get(i)))
      );
    }

    @Test
    public void longsWithValuesBig() {
      var seq = NumericVec.longs(LongStream.range(0, 1_000_000).toArray());
      assertAll(
          () -> assertEquals(1_000_000, seq.size()),
          () -> IntStream.range(0, 1_000_000).forEach(i -> assertEquals((long) i, seq.get(i)))
      );
    }

    @Test
    public void doublesWithValuesBig() {
      var seq = NumericVec.doubles(IntStream.range(0, 1_000_000).mapToDouble(i -> i).toArray());
      assertAll(
          () -> assertEquals(1_000_000, seq.size()),
          () -> IntStream.range(0, 1_000_000).forEach(i -> assertEquals((double) i, seq.get(i)))
      );
    }

    @Test
    public void longsSideMutation() {
      var array = new long[] { 12L, 80L, 128L };
      var seq = NumericVec.longs(array);
      array[1] = 64L;
      assertEquals(80L, seq.get(1));
    }

    @Test
    public void onlyOneArray() {
      assertTrue(Arrays.stream(NumericVec.class.getDeclaredFields())
          .noneMatch(field -> field.getType().isArray() && field.getType() != long[].class));
    }

    @Test
    public void intsOrLongsOrDoublesWithValuesPrecondition() {
      assertAll(
          () -> assertThrows(NullPointerException.class, () -> NumericVec.ints(null)),
          () -> assertThrows(NullPointerException.class, () -> NumericVec.longs(null)),
          () -> assertThrows(NullPointerException.class, () -> NumericVec.doubles(null))
      );
    }
  }


  @Nested
  public class Q5 {

    @Test
    public void longsFor() {
      var seq = NumericVec.longs();
      seq.add(23L);
      seq.add(99L);
      var list = new ArrayList<Long>();
      for(var value: seq) {
        list.add(value);
      }
      assertEquals(List.of(23L, 99L), list);
    }

    @Test
    public void emptyFor() {
      var seq = NumericVec.longs();
      for(var value: seq) {
        fail();
      }
    }

    @Test
    public void typedIntsWithValuesFor() {
      var seq = NumericVec.ints(23, 99, -14, 66);
      var list = new ArrayList<Integer>();
      for(int value: seq) {
        list.add(value);
      }
      assertEquals(List.of(23, 99, -14, 66), list);
    }

    @Test
    public void typedLongsWithValuesFor() {
      var seq = NumericVec.longs(23L, 99L, -14L, 66L);
      var list = new ArrayList<Long>();
      for(long value: seq) {
        list.add(value);
      }
      assertEquals(List.of(23L, 99L, -14L, 66L), list);
    }

    @Test
    public void typedDoublesWithValuesFor() {
      var seq = NumericVec.doubles(23., 99., -14., 66.);
      var list = new ArrayList<Double>();
      for(double value: seq) {
        list.add(value);
      }
      assertEquals(List.of(23., 99., -14., 66.), list);
    }

    @Test
    public void intsForBig() {
      var seq = NumericVec.ints();
      IntStream.range(0, 1_000_000).forEach(seq::add);
      var sum = 0;
      for(var value: seq) {
        sum += value;
      }
      assertEquals(IntStream.range(0, 1_000_000).sum(), sum);
    }

    @Test
    public void longsForBig() {
      var seq = NumericVec.longs();
      LongStream.range(0, 1_000_000).forEach(seq::add);
      var sum = 0L;
      for(var value: seq) {
        sum += value;
      }
      assertEquals(LongStream.range(0, 1_000_000).sum(), sum);
    }

    @Test
    public void doublesForBig() {
      var seq = NumericVec.doubles();
      IntStream.range(0, 1_000_000).mapToDouble(i -> i).forEach(seq::add);
      var sum = 0.;
      for(var value: seq) {
        sum += value;
      }
      assertEquals(IntStream.range(0, 1_000_000).mapToDouble(i -> i).sum(), sum);
    }

    @Test
    public void intsForWithAdd() {
      var seq = NumericVec.ints();
      seq.add(3);
      var list = new ArrayList<Integer>();
      for(var value: seq) {
        list.add(value);
        seq.add(10);
      }
      assertAll(
          () -> assertEquals(List.of(3), list),
          () -> assertEquals(2, seq.size()),
          () -> assertEquals(3, seq.get(0)),
          () -> assertEquals(10, seq.get(1))
      );
    }

    @Test
    public void longsForWithAdd() {
      var seq = NumericVec.longs();
      seq.add(3L);
      var list = new ArrayList<Long>();
      for(var value: seq) {
        list.add(value);
        seq.add(10L);
      }
      assertAll(
          () -> assertEquals(List.of(3L), list),
          () -> assertEquals(2, seq.size()),
          () -> assertEquals(3L, seq.get(0)),
          () -> assertEquals(10L, seq.get(1))
      );
    }

    @Test
    public void doublesForWithAdd() {
      var seq = NumericVec.doubles();
      seq.add(3.0);
      var list = new ArrayList<Double>();
      for(var value: seq) {
        list.add(value);
        seq.add(10.0);
      }
      assertAll(
          () -> assertEquals(List.of(3.0), list),
          () -> assertEquals(2, seq.size()),
          () -> assertEquals(3.0, seq.get(0)),
          () -> assertEquals(10.0, seq.get(1))
      );
    }

    @Test
    public void iteratorIntsWithValuesFor() {
      var seq = NumericVec.ints(23, 99, -14, 66);
      var iterator = seq.iterator();
      assertEquals(23, iterator.next());
      assertEquals(99, iterator.next());
      assertEquals(-14, iterator.next());
      assertEquals(66, iterator.next());
      assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void iteratorLongsWithValuesFor() {
      var seq = NumericVec.longs(23L, 99L, -14L, 66L);
      var iterator = seq.iterator();
      assertEquals(23L, iterator.next());
      assertEquals(99L, iterator.next());
      assertEquals(-14L, iterator.next());
      assertEquals(66L, iterator.next());
      assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void iteratorDoublesWithValuesFor() {
      var seq = NumericVec.doubles(23., 99., -14., 66.);
      var iterator = seq.iterator();
      assertEquals(23., iterator.next());
      assertEquals(99., iterator.next());
      assertEquals(-14., iterator.next());
      assertEquals(66., iterator.next());
      assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void iteratorIntsWithValuesSeveralHasNext() {
      var seq = NumericVec.ints();
      seq.add(314);
      seq.add(17);
      var iterator = seq.iterator();
      assertTrue(iterator.hasNext());
      assertTrue(iterator.hasNext());
      assertEquals(314, iterator.next());
      assertTrue(iterator.hasNext());
      assertTrue(iterator.hasNext());
      assertEquals(17, iterator.next());
      assertFalse(iterator.hasNext());
      assertFalse(iterator.hasNext());
    }

    @Test
    public void iteratorLongsWithValuesSeveralHasNext() {
      var seq = NumericVec.longs();
      seq.add(314L);
      seq.add(17L);
      var iterator = seq.iterator();
      assertTrue(iterator.hasNext());
      assertTrue(iterator.hasNext());
      assertEquals(314L, iterator.next());
      assertTrue(iterator.hasNext());
      assertTrue(iterator.hasNext());
      assertEquals(17L, iterator.next());
      assertFalse(iterator.hasNext());
      assertFalse(iterator.hasNext());
    }

    @Test
    public void iteratorDoublesWithValuesSeveralHasNext() {
      var seq = NumericVec.doubles();
      seq.add(314.);
      seq.add(17.);
      var iterator = seq.iterator();
      assertTrue(iterator.hasNext());
      assertTrue(iterator.hasNext());
      assertEquals(314., iterator.next());
      assertTrue(iterator.hasNext());
      assertTrue(iterator.hasNext());
      assertEquals(17., iterator.next());
      assertFalse(iterator.hasNext());
      assertFalse(iterator.hasNext());
    }

    @Test
    public void iteratorIntsRemove() {
      var seq = NumericVec.ints(12);
      var iterator = seq.iterator();
      assertEquals(12, iterator.next());
      assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    public void iteratorLongsRemove() {
      var seq = NumericVec.longs(12L);
      var iterator = seq.iterator();
      assertEquals(12L, iterator.next());
      assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    public void iteratorDoublesRemove() {
      var seq = NumericVec.doubles(12.);
      var iterator = seq.iterator();
      assertEquals(12., iterator.next());
      assertThrows(UnsupportedOperationException.class, iterator::remove);
    }
  }


  @Nested
  public class Q6 {

    @Test
    public void addAll() {
      var seq = NumericVec.longs();
      seq.add(44L);
      seq.add(666L);
      var seq2 = NumericVec.longs();
      seq2.add(77L);
      seq2.add(666L);
      seq.addAll(seq2);
      assertAll(
          () -> assertEquals(4, seq.size()),
          () -> assertEquals(44L, seq.get(0)),
          () -> assertEquals(666L, seq.get(1)),
          () -> assertEquals(77L, seq.get(2)),
          () -> assertEquals(666L, seq.get(3))
      );
    }

    @Test
    public void addAllInts() {
      var seq = NumericVec.ints(44, 666);
      var seq2 = NumericVec.ints(77, 666);
      seq.addAll(seq2);
      assertAll(
          () -> assertEquals(4, seq.size()),
          () -> assertEquals(44, seq.get(0)),
          () -> assertEquals(666, seq.get(1)),
          () -> assertEquals(77, seq.get(2)),
          () -> assertEquals(666, seq.get(3))
      );
    }

    @Test
    public void addAllLongs() {
      var seq = NumericVec.longs(44, 666);
      var seq2 = NumericVec.longs(77, 666);
      seq.addAll(seq2);
      assertAll(
          () -> assertEquals(4, seq.size()),
          () -> assertEquals(44L, seq.get(0)),
          () -> assertEquals(666L, seq.get(1)),
          () -> assertEquals(77L, seq.get(2)),
          () -> assertEquals(666L, seq.get(3))
      );
    }

    @Test
    public void addAllDoubles() {
      var seq = NumericVec.doubles(44., 666.);
      var seq2 = NumericVec.doubles(77., 666.);
      seq.addAll(seq2);
      assertAll(
          () -> assertEquals(4, seq.size()),
          () -> assertEquals(44., seq.get(0)),
          () -> assertEquals(666., seq.get(1)),
          () -> assertEquals(77., seq.get(2)),
          () -> assertEquals(666., seq.get(3))
      );
    }

    @Test
    public void addAllIntsEmpty() {
      var seq = NumericVec.ints(32);
      var seq2 = NumericVec.ints();
      seq.addAll(seq2);
      assertAll(
          () -> assertEquals(1, seq.size()),
          () -> assertEquals(32, seq.get(0))
      );
    }

    @Test
    public void addAllLongsEmpty() {
      var seq = NumericVec.longs(32L);
      var seq2 = NumericVec.longs();
      seq.addAll(seq2);
      assertAll(
          () -> assertEquals(1, seq.size()),
          () -> assertEquals(32L, seq.get(0))
      );
    }

    @Test
    public void addAllDoublesEmpty() {
      var seq = NumericVec.doubles(32.);
      var seq2 = NumericVec.doubles();
      seq.addAll(seq2);
      assertAll(
          () -> assertEquals(1, seq.size()),
          () -> assertEquals(32., seq.get(0))
      );
    }

    @Test
    public void addAllIntsBig() {
      var seq = NumericVec.ints();
      IntStream.range(0, 1_000_000).forEach(seq::add);
      var seq2 = NumericVec.ints();
      IntStream.range(1_000_000, 2_000_000).forEach(seq2::add);
      seq.addAll(seq2);
      assertAll(
          () -> assertEquals(2_000_000, seq.size()),
          () -> IntStream.range(0, 2_000_000).forEach(i -> assertEquals(i, seq.get(i)))
      );
    }

    @Test
    public void addAllLongsBig() {
      var seq = NumericVec.longs();
      LongStream.range(0, 1_000_000).forEach(seq::add);
      var seq2 = NumericVec.longs();
      LongStream.range(1_000_000, 2_000_000).forEach(seq2::add);
      seq.addAll(seq2);
      assertAll(
          () -> assertEquals(2_000_000, seq.size()),
          () -> IntStream.range(0, 2_000_000).forEach(i -> assertEquals((long) i, seq.get(i)))
      );
    }

    @Test
    public void addAllDoublesBig() {
      var seq = NumericVec.doubles();
      IntStream.range(0, 1_000_000).mapToDouble(i -> i).forEach(seq::add);
      var seq2 = NumericVec.doubles();
      IntStream.range(1_000_000, 2_000_000).mapToDouble(i -> i).forEach(seq2::add);
      seq.addAll(seq2);
      assertAll(
          () -> assertEquals(2_000_000, seq.size()),
          () -> IntStream.range(0, 2_000_000).forEach(i -> assertEquals((double) i, seq.get(i)))
      );
    }

    @Test
    public void addAllPrecondition() {
      var seq = NumericVec.longs(65L, 67L);
      assertThrows(NullPointerException.class, () -> seq.addAll(null));
    }
  }


  @Nested
  public class Q7 {

    @Test
    public void map() {
      var seq = NumericVec.longs();
      seq.add(45L);
      seq.add(37L);
      NumericVec<Double> seq2 = seq.map(l -> (double) l, NumericVec::doubles);
      assertAll(
          () -> assertEquals(2, seq2.size()),
          () -> assertEquals(45., seq2.get(0)),
          () -> assertEquals(37., seq2.get(1))
      );
    }

    @Test
    public void mapOfInts() {
      var seq = NumericVec.ints(45, 37);
      NumericVec<Integer> seq2 = seq.map(i -> 2 * i, NumericVec::ints);
      assertAll(
          () -> assertEquals(2, seq2.size()),
          () -> assertEquals(90, seq2.get(0)),
          () -> assertEquals(74, seq2.get(1)),
          () -> assertNotSame(seq, seq2)
      );
    }

    @Test
    public void mapOfLongs() {
      var seq = NumericVec.longs(45L, 37L);
      NumericVec<Long> seq2 = seq.map(l -> 2L * l, NumericVec::longs);
      assertAll(
          () -> assertEquals(2, seq2.size()),
          () -> assertEquals(90L, seq2.get(0)),
          () -> assertEquals(74L, seq2.get(1)),
          () -> assertNotSame(seq, seq2)
      );
    }

    @Test
    public void mapOfDoubles() {
      var seq = NumericVec.doubles(45., 37.);
      NumericVec<Double> seq2 = seq.map(d -> 2.0 * d, NumericVec::doubles);
      assertAll(
          () -> assertEquals(2, seq2.size()),
          () -> assertEquals(90., seq2.get(0)),
          () -> assertEquals(74., seq2.get(1)),
          () -> assertNotSame(seq, seq2)
      );
    }

    @Test
    public void mapOfIntsToOtherPrimitives() {
      var seq = NumericVec.ints(45, 37);
      var seq2 = seq.map(i -> (long) i, NumericVec::longs);
      var seq3 = seq.map(i -> (double) i, NumericVec::doubles);
      assertAll(
          () -> assertEquals(2, seq2.size()),
          () -> assertEquals(45L, seq2.get(0)),
          () -> assertEquals(37L, seq2.get(1)),
          () -> assertEquals(2, seq3.size()),
          () -> assertEquals(45., seq3.get(0)),
          () -> assertEquals(37., seq3.get(1))
      );
    }

    @Test
    public void mapOfLongsToOtherPrimitives() {
      var seq = NumericVec.longs(45L, 37L);
      var seq2 = seq.map(l -> (int) (long) l, NumericVec::ints);
      var seq3 = seq.map(l -> (double) l, NumericVec::doubles);
      assertAll(
          () -> assertEquals(2, seq2.size()),
          () -> assertEquals(45, seq2.get(0)),
          () -> assertEquals(37, seq2.get(1)),
          () -> assertEquals(2, seq3.size()),
          () -> assertEquals(45., seq3.get(0)),
          () -> assertEquals(37., seq3.get(1))
      );
    }

    @Test
    public void mapOfDoublesToOtherPrimitives() {
      var seq = NumericVec.doubles(45., 37.);
      var seq2 = seq.map(d -> (int) (double) d, NumericVec::ints);
      var seq3 = seq.map(d -> (long) (double) d, NumericVec::longs);
      assertAll(
          () -> assertEquals(2, seq2.size()),
          () -> assertEquals(45, seq2.get(0)),
          () -> assertEquals(37, seq2.get(1)),
          () -> assertEquals(2, seq3.size()),
          () -> assertEquals(45L, seq3.get(0)),
          () -> assertEquals(37L, seq3.get(1))
      );
    }

    @Test
    public void mapNotTheSame() {
      var seq = NumericVec.longs(42L);
      var seq2 = seq.map((Object o) -> 42, NumericVec::ints);
      assertNotSame(seq, seq2);
    }

    @Test
    public void mapPreconditions() {
      var seq = NumericVec.longs();
      assertAll(
          () -> assertThrows(NullPointerException.class, () -> seq.map(null, NumericVec::ints)),
          () -> assertThrows(NullPointerException.class, () -> seq.map(i -> i, null))
      );
    }
  }


  @Nested
  public class Q8 {

    @Test
    public void toNumericSeq() {
      var seq = IntStream.range(0, 10).boxed().collect(NumericVec.toNumericVec(NumericVec::ints));
      assertAll(
          () -> assertEquals(10, seq.size()),
          () -> IntStream.range(0, 10).forEach(i -> assertEquals(i, seq.get(i)))
      );
    }

    @Test
    public void toNumericSeqMutable() {
      var seq = Stream.of(12L, 45L).collect(NumericVec.toNumericVec(NumericVec::longs));
      seq.add(99L);
      assertAll(
          () -> assertEquals(3, seq.size()),
          () -> assertEquals(12L, seq.get(0)),
          () -> assertEquals(45L, seq.get(1)),
          () -> assertEquals(99L, seq.get(2))
      );
    }

    @Test
    public void toNumericSeqParallelInts() {
      var seq = IntStream.range(0, 1_000_000).parallel()
          .boxed()
          .collect(NumericVec.toNumericVec(NumericVec::ints));
      assertAll(
          () -> assertEquals(1_000_000, seq.size()),
          () -> IntStream.range(0, 1_000_000).forEach(i -> assertEquals(i, seq.get(i)))
      );
    }

    @Test
    public void toNumericSeqParallelLongs() {
      var seq = LongStream.range(0, 1_000_000).parallel()
          .boxed()
          .collect(NumericVec.toNumericVec(NumericVec::longs));
      assertAll(
          () -> assertEquals(1_000_000, seq.size()),
          () -> IntStream.range(0, 1_000_000).forEach(i -> assertEquals((long) i, seq.get(i)))
      );
    }

    @Test
    public void toNumericSeqParallelDoubles() {
      var seq = IntStream.range(0, 1_000_000).parallel()
          .mapToObj(i -> (double) i)
          .collect(NumericVec.toNumericVec(NumericVec::doubles));
      assertAll(
          () -> assertEquals(1_000_000, seq.size()),
          () -> IntStream.range(0, 1_000_000).forEach(i -> assertEquals((double) i, seq.get(i)))
      );
    }

    @Test
    public void toNumericSeqPreconditions() {
      assertThrows(NullPointerException.class, () -> NumericVec.toNumericVec(null));
    }

    @Test
    public void toNumericVersusNull() {
      assertAll(
          () -> assertThrows(NullPointerException.class, () -> Stream.of(12, null).collect(NumericVec.toNumericVec(NumericVec::ints))),
          () -> assertThrows(NullPointerException.class, () -> Stream.of(12L, null).collect(NumericVec.toNumericVec(NumericVec::longs))),
          () -> assertThrows(NullPointerException.class, () -> Stream.of(12., null).collect(NumericVec.toNumericVec(NumericVec::doubles)))
      );
    }
  }


  @Nested
  public class Q9 {

    @Test
    public void stream() {
      var seq = NumericVec.longs();
      seq.add(12L);
      seq.add(1L);
      assertEquals(List.of(12L, 1L), seq.stream().toList());
    }

    @Test
    public void streamCount() {
      var seq = NumericVec.ints(2, 3, 4);
        assertEquals(3, seq.stream().map(__ -> fail()).count());
    }

    @Test
    public void streamParallel() {
      var seq = IntStream.range(0, 1_000_000).boxed().collect(NumericVec.toNumericVec(NumericVec::ints));
      var thread = Thread.currentThread();
      var otherThreadCount = seq.stream().parallel().mapToInt(__ -> thread != Thread.currentThread()? 1: 0).sum();
      assertNotEquals(0, otherThreadCount);
    }

    @Test
    public void streamMutation() {
      var seq = NumericVec.doubles();
      seq.add(32.);
      var stream = seq.stream();
      seq.add(64.);
      assertEquals(List.of(32.), stream.toList());
    }

    @Test
    public void streamDontSplitIfNotEnoughElements() {
      var seq = NumericVec.ints();
      IntStream.range(0, 512).forEach(seq::add);
      assertNull(seq.stream().spliterator().trySplit());
    }

    @Test
    public void streamSplitIfEnoughElements() {
      var seq = NumericVec.ints();
      IntStream.range(0, 2_048).forEach(seq::add);
      assertNotNull(seq.stream().spliterator().trySplit());
    }

    @Test
    public void streamNotParallelByDefault() {
      var stream = NumericVec.longs(200L).stream();
      assertFalse(stream.isParallel());
    }

    @Test
    public void streamCharacteristics() {
      var spliterator = NumericVec.longs().stream().spliterator();
      assertAll(
          () -> spliterator.hasCharacteristics(Spliterator.NONNULL),
          () -> spliterator.hasCharacteristics(Spliterator.ORDERED),
          () -> spliterator.hasCharacteristics(Spliterator.IMMUTABLE)
      );
    }
  }
}