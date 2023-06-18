package series;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TimeSeries <T> {
	
	public record Data<E>(long timestamp,E element) {	
		public Data{
			Objects.requireNonNull(element);
		}
		
		@Override
		public String toString() {
			return timestamp + " | " + element;
		}
	}
	
	private final ArrayList<Data<T>> data = new ArrayList<>();
	private long lastTimeStamp;
	
	
	public void add(long timeStamp,T element) {
		Objects.requireNonNull(element);
		if(data.isEmpty()) {
			lastTimeStamp = timeStamp;
		}
		else if(timeStamp < lastTimeStamp) {
			throw new IllegalStateException();
		}
		data.add(new Data<T>(timeStamp,element));
	}
	
	public int size () {
		return data.size();
	}
	
	public Data<T> get(int pos){
		Objects.checkIndex(pos, data.size());
		return data.get(pos);
	}
	
	public class Index implements Iterable<Data<T>>{
		private int[] array;
		private TimeSeries<T> myTimeSeries;
		private Index(int[] array) {
			Objects.requireNonNull(array);
			this.array = array;
			this.myTimeSeries = thisTimeSeries();
		}
		
		public int size() {
			return array.length;
		}
		
		@Override
		public String toString() {
			return Arrays.stream(array).mapToObj(e -> data.get(e).toString()).collect(Collectors.joining("\n"));
		}
		
		public void forEach(Consumer<? super Data<T>> consumer) {
			Arrays.stream(array).mapToObj(e -> data.get(e)).forEach(consumer);
		}
		
		@Override
		public Iterator<Data <T>> iterator() {
			return new Iterator<Data<T>>() {
				private int cpt = 0;
				@Override
				public boolean hasNext() {
					return cpt < size();
				}

				@Override
				public Data<T> next() {
					if(!hasNext()) {
						throw new NoSuchElementException();
					}
					var e  = cpt;
					cpt++;
					return data.get(array[e]);
				}
				
			};
		}
		
		public Index or(TimeSeries<? extends T>.Index ind) {
			Objects.requireNonNull(ind);
			if(!ind.myTimeSeries.equals(this.myTimeSeries)) {
				throw new IllegalArgumentException();
			}
			return new Index(IntStream.concat(Arrays.stream(array), Arrays.stream(ind.array)).distinct().sorted().toArray());
		}
		
		public Index and(TimeSeries<? extends T>.Index ind) {
			Objects.requireNonNull(ind);
			if(!ind.myTimeSeries.equals(this.myTimeSeries)) {
				throw new IllegalArgumentException();
			}
			return new Index(Arrays.stream(ind.array).filter(Arrays.stream(array).mapToObj(e->e).collect(Collectors.toSet())::contains).toArray());
		}
	}
	
	public Index index() {
		return new Index(IntStream.range(0, size()).toArray());
	}
	
	public Index index(Predicate<? super T> predicate) {
		return new Index(IntStream.range(0, size()).filter(e -> predicate.test(data.get(e).element())).toArray());
		
	}


	public TimeSeries<T> thisTimeSeries(){
		return this;
	}
	
	
}
