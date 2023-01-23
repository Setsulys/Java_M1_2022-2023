package fr.uge.graph;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;


final class MatrixGraph<T> implements Graph<T>{

  private final T[] array;
  private final int nodeCount;
  public MatrixGraph(int nodeCount) {
    if(nodeCount < 0) {
      throw new IllegalArgumentException();
    }
    @SuppressWarnings("unchecked")
    T[] array = (T[]) new Object[nodeCount*nodeCount];
    this.array = array;
    this.nodeCount = nodeCount;
  }

  
  public void addEdge(int src,int dst, T weight) {
    Objects.requireNonNull(weight);
    Objects.checkIndex(src,nodeCount);
    Objects.checkIndex(dst,nodeCount);
    this.array[(src * nodeCount) + dst] = weight;
  }
  
  public Optional<T> getWeight(int src,int dst) {
    Objects.checkIndex(src,nodeCount);
    Objects.checkIndex(dst,nodeCount);
    return Optional.ofNullable(this.array[(src* nodeCount)+dst]);
  }
  
  public void edges(int src, EdgeConsumer<? super T> edgeConsumer) {
    Objects.requireNonNull(edgeConsumer);
    Objects.checkIndex(src,nodeCount);
    for(var dst =0; dst < nodeCount; dst++) {
      if(!this.getWeight(src,dst).isEmpty()) {
        edgeConsumer.edge(src,dst, array[src * nodeCount +dst]);
      }
    }
  }


  @Override
  public Iterator<Integer> neighborIterator(int src) {
    return new Iterator<>(){

      public int count =incrementIf(0);
      public int index = -1;
      
      @Override
      public boolean hasNext() {
        return count < nodeCount;
      }

      private int incrementIf(int count) {
        for(var dst = count;dst < nodeCount; dst++) {
          if(getWeight(src, dst).isPresent()) {
            return dst;
          }
        }
        return nodeCount;
      }
      
      @Override
      public Integer next() {
        if(!hasNext()) {
          throw new NoSuchElementException();
        }
        var rep = count;
        index = count;
        count++;
        count = incrementIf(count);
        return rep;
      }
      
      public void remove() {
        if(index == -1 || array[src * nodeCount + index]==null) {
          throw new IllegalStateException();
        }
        array[src * nodeCount + index]=null;
      }
    };
  }
}
