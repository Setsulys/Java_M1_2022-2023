package numVec;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.LongFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

public class NumericVec <T>{

	  private long [] array;
	  public int size;
	  private final ToLongFunction<T> into;
	  private final LongFunction<T> from;

	  private NumericVec(long[] values,ToLongFunction<T> into, LongFunction<T> from) {
		  Objects.requireNonNull(values);
		  Objects.requireNonNull(into);
		  Objects.requireNonNull(from);
		  this.array = values;
		  this.size = values.length;
		  this.into = into;
		  this.from = from;
	  }
	  
	  @SafeVarargs
	  public static NumericVec<Long> longs(long ... values) {
		  Objects.requireNonNull(values);
		  return new NumericVec<>(Arrays.copyOf(values, values.length), x->x, x->x);
	  }
	  
	  @SafeVarargs
	  public static NumericVec<Integer> ints(int ... values) {
		  Objects.requireNonNull(values);
		  var tmplongs = Arrays.stream(values).mapToLong(e ->e).toArray();
		  return new NumericVec<>(Arrays.copyOf(tmplongs, tmplongs.length), x->(long)x, x->(int)x);
	  }
	  
	  @SafeVarargs
	  public static NumericVec<Double> doubles(double ... values) {
		  Objects.requireNonNull(values);
		  var tmp = Arrays.stream(values).mapToLong(e -> Double.doubleToRawLongBits(e) ).toArray();
		  return new NumericVec<Double>(Arrays.copyOf(tmp, tmp.length), x->Double.doubleToRawLongBits(x), x->Double.longBitsToDouble(x));
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
	      this.array = new long[array.length*2 + 1];
	      for(var i = 0; i < e.length ; i++) {
	        this.array[i] = e[i];
	      }
	    }
	    array[size] = into.applyAsLong(element);
	    size++;
	  }
	  
	  @Override
	  public String toString() {
		  return Arrays.stream(array).limit(size()).mapToObj(e -> String.valueOf(from.apply(e))).collect(Collectors.joining(", ","[","]"));
	  }
	}