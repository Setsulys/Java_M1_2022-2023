package seq;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Seq <T> implements Iterable<T>{
	
	private final List<Object> lst;
	private final int size;
	private final Function <? super Object,? extends T> function;
	
	private Seq(List<?> copyOf,Function<Object,T>function) {
		Objects.requireNonNull(copyOf);
		Objects.requireNonNull(function);
		this.lst = List.copyOf(copyOf);
		this.size = lst.size();
		this.function = function;
	}

	@SuppressWarnings("unchecked")
	public static <E>Seq<E> from(List<? extends E> lst){
		Objects.requireNonNull(lst);
		return new Seq<E>(List.copyOf(lst), e->(E)e);
	}
	
	public T get(int index) {
		Objects.checkIndex(index, lst.size());
		return function.apply(lst.get(index));
	}
	
	public int size() {
		return size;
	}
	
	@Override
	public String toString() {
		return lst.stream().map(e->function.apply(e).toString()).collect(Collectors.joining(", ","<",">"));
	}
	
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <E> Seq<E> of(E ... values) {
		return new Seq<E>(List.of(values), e-> (E) e );
		
	}
	
	public void forEach(Consumer<? super T> consumer) {
		lst.stream().map(e -> function.apply(e)).forEach(consumer);;
	}
	
	public <E> Seq <E> map(Function<? super T, ? extends E> functions){
		Objects.requireNonNull(functions);
		return new Seq<E>(this.lst,e -> functions.apply(function.apply(e)));
	}
	
	public Optional<T> findFirst() {
		return lst.isEmpty()? Optional.empty() : Optional.ofNullable(get(0));
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<>() {
			private int cpt=0;
			
			@Override
			public boolean hasNext() {
				return cpt < size();
			}

			@Override
			public T next() {
				if(!hasNext()) {
					throw new NoSuchElementException();
				}
				var e = cpt;
				cpt++;
				return function.apply(lst.get(e));
			}
			
		};
	}
	
	public Stream<T> stream(){
		return StreamSupport.stream(
			new Spliterator<T>() {
				private Iterator<T> it = iterator();
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
					// TODO Auto-generated method stub
					return null;
				}
	
				@Override
				public long estimateSize() {
					return size();
				}
	
				@Override
				public int characteristics() {
					// TODO Auto-generated method stub
					return IMMUTABLE |ORDERED |NONNULL;
				}
			}
		,false);
	}
}
