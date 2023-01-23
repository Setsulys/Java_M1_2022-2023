package fr.uge.fifo;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;


public class Fifo<E> implements Iterable<E>{

  private int head; // 1e pos libre pour inserer
  private int tail; // 1e pos pour retirer
  private int size = 0;
  private final E[] queue;
  
  public Fifo(int value) {
    if( value <= 0) {
      throw new IllegalArgumentException();
    }
    @SuppressWarnings("unchecked")
    E[] queue = (E[]) new Object[value];
    this.queue = queue;
    this.head = 0;
    this.tail = 0;
  }

  public void offer(E obj) {
    Objects.requireNonNull(obj);
    if(size == queue.length) {
      throw new IllegalStateException();
    }
    queue[tail]=obj;
    this.tail = (this.tail+1) % queue.length;
    this.size++;
  }
  
  public E poll() {
    if(size<= 0) {
      throw new IllegalStateException();
    }
    var r = queue[head];
    this.head = (this.head+1) % queue.length;
    this.size--;
    return r;
  }
  
  public Integer size() {
    return size;
  } 
  
  public boolean isEmpty() {
    return size == 0;
  }
  
  public Iterator<E> iterator() {
    return new Iterator<E>(){
      private int cpt = 0;
      private int tmp = head;
      @Override
      public boolean hasNext() {
        return cpt != size;
      }

      @Override
      public E next() {
        if(!hasNext()) {
          throw new NoSuchElementException();
        }
        var q = queue[tmp];
        tmp = (tmp+1) % queue.length;
        cpt++;
        return q;
      }
      
    };
  }
  
  public String toString() {
    int elem = this.head;
    var joiner = new StringJoiner(", ","[","]");
    for(var i=0 ;i < size; i++) {
      joiner.add(String.valueOf(queue[elem]));
      elem =(elem +1) %queue.length;
    }
    return joiner.toString();
  }


}
