package fr.uge.seq;

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

public class Seq<T> implements Iterable<T>{

  private final List<Object> list ;
  private final Function<? super Object,? extends T> function;
  private final int size;

  private Seq(List<?> lst,Function<Object,T> function) {
    Objects.requireNonNull(lst);
    Objects.requireNonNull(function);
    this.list = List.copyOf(lst);
    this.function = function;
    this.size = list.size();
  }
  
  @SuppressWarnings("unchecked")
  public static <E> Seq<E> from(List<? extends E> lst) {
    Objects.requireNonNull(lst);
    return new Seq<E>(lst, e -> (E)e );
  }

  public Integer size() {
    return this.size;
  }
  
  public T get(int i) {
    Objects.checkIndex(i, size());
    return function.apply(list.get(i));
  }

  
  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <E>Seq<E> of(E... values) {
    return new Seq<E>(List.of(values),e -> (E)e);
    
  }
  
  public void forEach(Consumer<? super T>consumer) {
    Objects.requireNonNull(consumer);
    list.stream().map(e->function.apply(e)).forEach(consumer);
  }
  
  public <E>Seq<E> map(Function<? super T,? extends E> functions) {
    Objects.requireNonNull(functions);
    return new Seq<E>(this.list,e -> functions.apply(this.function.apply(e)));
  }
  
  public Optional<T> findFirst() {
    return list.isEmpty()? Optional.empty(): Optional.ofNullable(get(0));
  }
  
  public Iterator<T> iterator() {
    return new Iterator<>() {
      private int count =0;
      @Override
      public boolean hasNext() {
        return count < size();
      }
      
      @Override
      public T next() {
        if(!hasNext()) {
          throw new NoSuchElementException();
        }
        var elem = count;
        count++;
        return function.apply(list.get(elem));
      }
    };
  }
  
  @Override
  public String toString() {
    return list.stream().map(e -> function.apply(e).toString()).collect(Collectors.joining(", ","<",">"));
  }

  public Stream<T> stream() {
    return StreamSupport.stream(
        new Spliterator<T>() {
      private Iterator<T> iter = iterator();

      @Override
      public boolean tryAdvance(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        if(! iter.hasNext()) {
          return false;
        }
        action.accept(iter.next());
        return true;
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
        return IMMUTABLE | ORDERED | NONNULL;
      }
    }
    , false);
  }


}
