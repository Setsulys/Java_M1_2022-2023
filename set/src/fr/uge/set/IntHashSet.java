package fr.uge.set;

import java.util.Objects;
import java.util.function.IntConsumer;

public class IntHashSet {
  private static int SIZE=2;
  private int size;
  private final Entry[] array;
  
  public IntHashSet() {
    this.array = new Entry[SIZE];
    this.size = 0;
  }
  private record Entry(int value,Entry next) {
    
  }
  
  private int hash(int value) {
    return value & (SIZE-1);
  }
  
  public void add(int value) {
   var hashValue = hash(value);
   Entry element;
   for(element=array[hashValue] ; element!=null;element=element.next) {
      if(element.value==value) {
        return;
      }
    }
    this.size++;
    array[hashValue]= new Entry(value,array[hashValue]);
    
  }

  public int size() {
    return size;
  }

  public void forEach(IntConsumer object) {
    Objects.requireNonNull(object);
    for(var i=0; i < SIZE; i++) {
      for(var element = array[i]; element!= null;element=element.next) {
        object.accept(element.value);
      }
    }
    
  }

  public boolean contains(int value) {
  for(var element = array[hash(value)];element!=null;element=element.next) {
    if(element.value == value) {
      return true;
    }
  }
  return false;
  }
}
