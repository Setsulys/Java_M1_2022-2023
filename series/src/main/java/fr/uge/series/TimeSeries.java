  package fr.uge.series;
  
  import java.util.ArrayList;
  import java.util.Arrays;
  import java.util.Iterator;
  import java.util.NoSuchElementException;
  import java.util.Objects;
  import java.util.function.Consumer;
  import java.util.function.Predicate;
  import java.util.stream.Collectors;
  import java.util.stream.IntStream;
  
  public class TimeSeries<E> {
    
    private final ArrayList<Data<E>> datas = new ArrayList<>();
    private long LastTimeStamp;
    
    public record Data<T>(long timestamp ,T element){
      
      public Data{
        Objects.requireNonNull(element);
      }
      
      public String toString() {
        return timestamp + " | " + element;
      }
    }
    
    public class Index implements Iterable<Data<E>>{
      private int [] indexes;
      private TimeSeries<E> timeSeriesOfThis;
      
      private Index(int [] indexes) {
        Objects.requireNonNull(indexes);
        this.indexes = indexes;
        this.timeSeriesOfThis = thisTimeseries();
      }
      
      public int size() {
        return indexes.length;
      }
      
      public void forEach(Consumer<? super Data<E>> consumer) {
        Objects.requireNonNull(consumer);
        Arrays.stream(indexes).mapToObj(e->datas.get(e)).forEach(consumer);;
      }
      
      @Override
      public String toString() {
        return Arrays.stream(indexes).mapToObj(e -> datas.get(e).toString()).collect(Collectors.joining("\n"));
      }
  
      public Iterator<Data<E>> iterator() {
        return new Iterator<Data<E>>() {
          private int cpt =0;
          
          @Override
          public boolean hasNext() {
            return cpt < size();
          }
  
          @Override
          public Data<E> next() {
            if(!hasNext()) {
              throw new NoSuchElementException();
            }
            var element = cpt;
            cpt++;
            return datas.get(indexes[element]);
          }
        };
      }
      
      public Index or (TimeSeries<? extends E>.Index index) {
        Objects.requireNonNull(index);
        if(!index.timeSeriesOfThis.equals(this.timeSeriesOfThis)) {
          throw new IllegalArgumentException();
        }
        return new Index(IntStream.concat(Arrays.stream(indexes),Arrays.stream(index.indexes)).distinct().sorted().toArray());
      }
      
      public Index and (TimeSeries<? extends E>.Index index) {
        Objects.requireNonNull(index);
        if(!index.timeSeriesOfThis.equals(this.timeSeriesOfThis)) {
          throw new IllegalArgumentException();
        }
        return new Index(Arrays.stream(index.indexes).filter(Arrays.stream(indexes).mapToObj(e -> e).collect(Collectors.toSet())::contains).toArray());
      }
    }
    
    public void add(long timestamp, E element) {
      Objects.requireNonNull(element);
      if(datas.isEmpty()) {
        this.LastTimeStamp = timestamp;
      }
      if(this.LastTimeStamp > timestamp) {
        throw new IllegalStateException();
      }
      datas.add(new Data<E>(timestamp,element));
      this.LastTimeStamp = timestamp; 
    }
    
    public int size() {
      return datas.size();
    }
    
    public Data<E> get(int index) {
     return datas.get(index);
    }
    
    public Index index() {
      return new Index(IntStream.range(0,size()).toArray());
    }
    
    public <T> Index index(Predicate <? super E> element) {
      return new Index(IntStream.range(0, size()).filter( e -> element.test(datas.get(e).element())).toArray());
    }
    
    public TimeSeries<E> thisTimeseries(){
      return this;
    }
    
  }
