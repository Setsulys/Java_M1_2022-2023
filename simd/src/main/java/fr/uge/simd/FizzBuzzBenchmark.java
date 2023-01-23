package fr.uge.simd;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"--add-modules", "jdk.incubator.vector"})
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@SuppressWarnings("static-method")
public class FizzBuzzBenchmark {
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

  private static int[] fizzbuzz(int length) {
    var array = new int[length];
    for(var i = 0; i < length; i++) {
      array[i] = fizzbuzzAt(i);
    }
    return array;
  }

//  @Benchmark
//  public void fizzbuzz_novector(Blackhole blackhole) {
//    var result = fizzbuzz(1_000);
//    blackhole.consume(result);
//  }
//
//  //@Benchmark
//  public void fizzbuzz_vector128(Blackhole blackhole) {
//    var result = FizzBuzz.fizzBuzzVector128(1_000);
//    blackhole.consume(result);
//  }
//
//  //@Benchmark
//  public void fizzbuzz_vector256(Blackhole blackhole) {
//    var result = FizzBuzz.fizzBuzzVector256(1_000);
//    blackhole.consume(result);
//  }
//
//  //@Benchmark
//  public void fizzbuzz_vector128_addmask(Blackhole blackhole) {
//    var result = FizzBuzz.fizzBuzzVector128AddMask(1_000);
//    blackhole.consume(result);
//  }
}
