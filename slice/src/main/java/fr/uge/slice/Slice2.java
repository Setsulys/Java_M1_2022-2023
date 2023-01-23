package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import fr.uge.slice.Slice2.ArraySlice;
import fr.uge.slice.Slice2.ArraySlice.SubArraySlice;

public sealed interface Slice2<E> permits Slice2.ArraySlice<E>,Slice2.ArraySlice<E>.SubArraySlice{

  public final class ArraySlice<E> implements Slice2<E>{
       
    public final class SubArraySlice implements Slice2<E>{
      private final int fromList;
      private final int toList;
      
      
      private SubArraySlice(int from, int to) {
        fromList = from;
        toList = to;
      }
      
      
      public Slice2<E> subSlice(int from,int to){
        Objects.checkFromToIndex(from, to, size());
        return new ArraySlice<E>(list).new SubArraySlice( from+this.fromList, to+this.fromList);
      }
      
      @Override
      public int size() {
        return toList - fromList; 
      }
      
      @Override
      public E get(int a) {
        Objects.checkIndex(a, size());
        return list[a+fromList];
        }
      
      @Override
      public String toString() {
        return Arrays.stream(list,fromList,toList).map(e -> String.valueOf(e)).collect(Collectors.joining(", ","[","]")) ;
      }
    }

    private final E[] list;
    
    private ArraySlice(E[] obj) {
      list = obj;
    }
    
    @Override
    public int size() {
      return list.length; 
    }
    
    @Override
    public E get(int a) {
      return list[a];
      }
    
    public Slice2<E> subSlice(int from,int to){
      return Slice2.array(list, from, to); 
    }
    
    @Override
    public String toString() {
      return Arrays.stream(list).map(e -> String.valueOf(e)).collect(Collectors.joining(", ","[","]")) ;
    }
  }
  
  public static <E> Slice2<E> array(E[] array) {
    Objects.requireNonNull(array);
    return new ArraySlice<E>(array);
  }
  
  
  public static <E> Slice2<E> array(E[] array, int from, int to){
    Objects.requireNonNull(array);
    Objects.checkFromToIndex(from, to, array.length);
    return new ArraySlice<E>(array).new SubArraySlice(from,to);
  }
  
  Slice2<E> subSlice(int from,int to);
  int size();
  E get(int a);
}

