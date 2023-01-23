import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class EntropySet<T> extends AbstractSet<T> {

  private final LinkedHashSet<T> set ;
  private boolean frozen;
  private final T[] cache;
  private static final int CACHE_SIZE =4;
  
  private EntropySet(LinkedHashSet<T> set, T[] cache) {
    this.set = set;
    this.cache = cache;
  }
  
  
  public EntropySet(){
    this.set = new LinkedHashSet<>();
    @SuppressWarnings("unchecked")
    var cache = (T[])new Object[CACHE_SIZE];
    this.cache = cache;
  }
  
  
  @Override
  public boolean add(T element) {
    Objects.requireNonNull(element);
    if(frozen) {
      throw new UnsupportedOperationException();
    }
    for(var i=0; i < cache.length; i++) {
      var value = cache[i];
      if( value == null) {
        cache[i] = element;
        return false;
      }
      if(value.equals(element)) {
        return false;
      }
    }
    set.add(element);
    return false;
  }
  public int size() {
    frozen = true;
    for(var i=0; i < cache.length; i++) {
      if(cache[i] == null) {
        return i;
      }
    }
    return CACHE_SIZE + set.size();
  }
  
  public boolean isFrozen() {
    return frozen;
  }
  
  public boolean contains(Object element) {
    Objects.requireNonNull(element);
    frozen =true;
    for(var i=0; i < cache.length; i++) {
      if(cache[i] == element) {
        return true;
      }
    }
    return set.contains(element);
  }

  public Iterator<T> iterator(){
    frozen =true;
    var it = set.iterator();
    var size = this.size();
    
    return new Iterator<>() {
      private int index = 0;
      
      @Override
      public boolean hasNext() {
        return index < size ;
      }

      @Override
      public T next() {
        if(!hasNext()) {
          throw new NoSuchElementException();
        }
        if(index < CACHE_SIZE) {
          return cache[index++];
        }
        index++;
        return it.next();
      }
    };
  }


  public static <E> EntropySet<E> from(Collection<? extends E> of) {
    Objects.requireNonNull(of);
    var spliterator = of.spliterator();
    Stream<? extends E> stream;
    Function<Stream<? extends E>, Stream<? extends E>> function = s -> s;
    if(!spliterator.hasCharacteristics(Spliterator.DISTINCT)){
      function = function.andThen(s -> s.distinct());
    }
    if(!spliterator.hasCharacteristics(Spliterator.NONNULL)){
      function = function.andThen(s -> s.filter(Objects::nonNull));
    }
    var cache = function.apply(of.stream()).skip(CACHE_SIZE).toArray(size -> new Object[CACHE_SIZE]);
    var set = function.apply(of.stream()).skip(CACHE_SIZE).collect(toCollection(LinkedHashSet))
    
    // TODO Auto-generated method stub
    return new EntropySet<E>();
  }
}
