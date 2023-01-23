package fr.uge.simd;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("static-method")
public class FizzBuzzTest {
  private static int fizzbuzzAt(int index) {
    if (index % 15 == 0) {
      return -3;
    }
    if (index % 5 == 0) {
      return -2;
    }
    if (index % 3 == 0) {
      return -1;
    }
    return index;
  }

  private static int[] fizzBuzz(int length) {
    var array = new int[length];
    for(var i = 0; i < length; i++) {
      array[i] = fizzbuzzAt(i);
    }
    return array;
  }

  @Nested
  public class Q1 {
    @ParameterizedTest
    @ValueSource(ints = {1, 10, 15, 30, 100, 1_000, 10_000, 30_000, 100_000, 1_000_000})
    public void testFizzBuzz128(int length) {
      var expected = fizzBuzz(length);
      var result = FizzBuzz.fizzBuzzVector128(length);
      assertArrayEquals(expected, result);
    }
  }

//  @Nested
//  public class Q2 {
//    @ParameterizedTest
//    @ValueSource(ints = {1, 10, 15, 30, 100, 1_000, 10_000, 30_000, 100_000, 1_000_000})
//    public void testFizzBuzz256(int length) {
//      var expected = fizzBuzz(length);
//      var result = FizzBuzz.fizzBuzzVector256(length);
//      assertArrayEquals(expected, result);
//    }
//  }
//
//  @Nested
//  public class Q3 {
//    @ParameterizedTest
//    @ValueSource(ints = {1, 10, 15, 30, 100, 1_000, 10_000, 30_000, 100_000, 1_000_000})
//    public void testFizzBuzz128AddMask(int length) {
//      var expected = fizzBuzz(length);
//      var result = FizzBuzz.fizzBuzzVector128AddMask(length);
//      assertArrayEquals(expected, result);
//    }
//  }
}
