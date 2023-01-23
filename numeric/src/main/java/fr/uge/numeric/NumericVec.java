package fr.uge.numeric;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NumericVec <T> implements Iterable<T>{

  private long [] array;
  private int size;
  private ToLongFunction<T> into;
  private LongFunction<T> from;
  
  private NumericVec(long[] elements,ToLongFunction<T> into,LongFunction<T> from) {
    Objects.requireNonNull(elements);
    Objects.requireNonNull(into);
    Objects.requireNonNull(from);
    
    this.array = elements;
    this.size = elements.length;
    this.from = from;
    this.into = into;
  }
  
  @SafeVarargs
  public static NumericVec<Long> longs(long... elements) {
    Objects.requireNonNull(elements);
    return new NumericVec<>(Arrays.copyOf(elements,elements.length),x->x,x->x);
  }
  
  @SafeVarargs
  public static NumericVec<Integer> ints(int... elements) {
    Objects.requireNonNull(elements);
    var array = Arrays.stream(elements).mapToLong(e -> e).toArray();
    return new NumericVec<Integer>(Arrays.copyOf(array,array.length),x->x,x->(int)x);
  }

  @SafeVarargs
  public static NumericVec<Double> doubles(double... elements) {
    Objects.requireNonNull(elements);
    var array = Arrays.stream(elements).mapToLong(e -> Double.doubleToRawLongBits(e)).toArray();
    return new NumericVec<Double>(Arrays.copyOf(array, array.length),x->Double.doubleToRawLongBits(x),x->Double.longBitsToDouble(x));
  }
  
  public int size() {
    return size;
  }
  
  public T get(int index) {
    Objects.checkIndex(index, size);
    return from.apply(array[index]);
  }
  
  public void add(T element) {
    Objects.requireNonNull(element);
    if(size == array.length) {
      var e = this.array;
      this.array = new long[array.length*2+1];
      for(var i = 0; i < e.length ; i++) {
        this.array[i] = e[i];
      }
    }
    array[size] = into.applyAsLong(element);
    size++;
  }
  
  public Iterator<T> iterator() {
    var size = this.size;
    return new Iterator<>() {
      private int  cpt= 0;
      @Override
      public boolean hasNext() {
        return cpt != size;
      }

      @Override
      public T next() {
        if(!hasNext()) {
          throw new NoSuchElementException();
        }
        var rt = cpt;
        cpt++;
        return get(rt);
      }
    };
  }
  
  public void addAll(NumericVec<T> numvec) {
    for(var elements:numvec) {
      this.add(elements);
    }
  }
  
  public <U> NumericVec<U> map(Function<? super T,? extends U> function, Supplier<? extends NumericVec<U>> factory) {
    Objects.requireNonNull(function);
    Objects.requireNonNull(factory);
    var seq = factory.get();
    Arrays.stream(array).limit(size).forEach(e -> seq.add(function.apply(from.apply(e))));
    return seq;
  }

  public static <U>Collector<? super U,NumericVec<U>,NumericVec<U>> toNumericVec(Supplier<? extends NumericVec<U>> factory) {
    Objects.requireNonNull(factory);
    return new Collector<>(){

      @Override
      public Supplier<NumericVec<U>> supplier() {
        return ()-> factory.get();
      }

      @Override
      public BiConsumer<NumericVec<U>, U> accumulator() {
        return (seq,element) ->seq.add(element);
      }

      @Override
      public BinaryOperator<NumericVec<U>> combiner() {
        return (seq1,seq2) ->{
          seq1.addAll(seq2);
          return seq1;
        };
      }

      @Override
      public Function<NumericVec<U>, NumericVec<U>> finisher() {
        return func-> func;
      }

      @Override
      public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
      }
    };
  }

public Spliterator<T> splits(int start,int end,long[]array ){
    return new Spliterator<>() {
      private int i =start;
      
      @Override
      public boolean tryAdvance(Consumer<? super T> action) {
        if(i == end) {
          return false;
        }
        action.accept(get(i));
        i++;
        return true;
      }

      @Override
      public Spliterator<T> trySplit() {
        var middle = (i + end )>>>1;
        if(middle == i || size < 1024) {
          return null;
        }
        var spliterators = splits(i,middle,array);
        i = middle;
        return spliterators;
      }

      @Override
      public long estimateSize() {
        return size;
      }

      @Override
      public int characteristics() {
        return CONCURRENT | NONNULL | ORDERED | IMMUTABLE;
      }

      
    };
  }

  public Stream<T> stream() {
    if(size < 1024) {
      return StreamSupport.stream(splits(0,size,array), false);
    }
    return StreamSupport.stream(splits(0,size,array),true);
        
  }
  
  @Override
  public String toString() {
    return Arrays.stream(array).limit(size).mapToObj(e -> String.valueOf(from.apply(e))).collect(Collectors.joining(", ","[","]"));
  }
}
