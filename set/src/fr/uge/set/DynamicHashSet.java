package fr.uge.set;

import java.util.Objects;
import java.util.function.Consumer;

public class DynamicHashSet<T> {
  private static int SIZE =2;
  private int size;
  private Entry<T>[] array;
  

  public DynamicHashSet(){
    @SuppressWarnings("unchecked")
    Entry<T>[] array = (Entry<T>[])new Entry[SIZE];
    this.array=array;
    this.size=0;
  }
  
  private record Entry<U>(U value,Entry<U> next) {
    
  }
  
  public int hash(Object value) {
    Objects.requireNonNull(value);
    return value.hashCode() & (SIZE-1);
  }
  
  public void add(T obj) {
    Objects.requireNonNull(obj);
    var hashValue = hash(obj);
    Entry<T> element;
    for(element=array[hashValue] ; element!=null;element=element.next) {
      if(element.value.equals(obj)) {
        return;
      }
    }
    this.size++;
    if(size>=SIZE/2) {
      SIZE = SIZE*2;
      @SuppressWarnings("unchecked")
      var array2 = (Entry<T>[])new Entry[SIZE];
      for(var i=0; i < SIZE/2; i++) {
        for(var el = array[i]; el!= null;el=el.next) {
         array2[hash(el)] = new Entry<T>(el.value,array2[hash(el)] );
        }
      }
      this.array=array2;
    }
    array[hashValue]=new Entry<T>(obj,array[hashValue]);
  }

  public int size() {
    return size;
  }

  public void forEach(Consumer<T> object) {
    Objects.requireNonNull(object);
    for(var i=0; i < SIZE; i++) {
      for(var element = array[i]; element!= null;element=element.next) {
        object.accept(element.value);
      }
    }
    
  }

  public boolean contains(Object value) {
    for(var element = array[hash(value)];element!=null;element=element.next) {
      if(element.value.equals(value)) {
        return true;
      }
    }
    return false;
    }
}
