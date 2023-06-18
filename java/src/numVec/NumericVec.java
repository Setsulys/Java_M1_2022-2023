package numVec;

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
import java.util.stream.Collector.Characteristics;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NumericVec <T> implements Iterable<T>{

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

	@Override
	public Iterator<T> iterator() {
		var size = size();
		return new Iterator<T>() {
			private int cpt=0;
			@Override
			public boolean hasNext() {
				return cpt < size;
			}

			@Override
			public T next() {
				if(!hasNext()) {
					throw new NoSuchElementException();
				}
				var e = cpt;
				cpt++;
				return from.apply(array[e]);
			}
			
		};
	}
	public void addAll(NumericVec<T> numVec) {
		Objects.requireNonNull(numVec);
		for( var e : numVec) {
			this.add(e);
		}
	}
	
	public <U>NumericVec<U> map(Function<? super T,? extends U> function,Supplier<? extends NumericVec<U>> factory) {
		Objects.requireNonNull(function);
		Objects.requireNonNull(factory);
		var seq =factory.get();
		Arrays.stream(array).limit(size).forEach(e ->seq.add(function.apply(from.apply(e))));
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
	  
	  
	 public Spliterator<T> spliter(int start,int end,long[] array){
		 return new Spliterator<T>(){
				private Iterator<T> it = iterator();
				private int i = start;
				@Override
				public boolean tryAdvance(Consumer<? super T> action) {
					Objects.requireNonNull(action);
					if(it.hasNext()) {
						action.accept(it.next());
						return true;
					}
					return false;
				}

				@Override
				public Spliterator<T> trySplit() {
					var middle = (i + end) >>> 1;
					if(middle == i || size < 1024) {
						return null;
					}
					var spliterator = spliter(i,middle,array);
					i= middle;
					return spliterator;
				}

				@Override
				public long estimateSize() {
					// TODO Auto-generated method stub
					return size();
				}

				@Override
				public int characteristics() {
					// TODO Auto-generated method stub
					return NONNULL | ORDERED | IMMUTABLE;
				}
				
			};
	 }
	  
	 public Stream<T> stream(){
		if(size < 1024) {
			return StreamSupport.stream(spliter(0,size,array),false);
		}
		return StreamSupport.stream(spliter(0,size,array),true);
	 }
	
}