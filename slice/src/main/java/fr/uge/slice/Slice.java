package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import fr.uge.slice.Slice.ArraySlice;
import fr.uge.slice.Slice.SubArraySlice;

public sealed interface Slice<E> permits ArraySlice<E>,SubArraySlice<E>{

  public final class ArraySlice<E> implements Slice<E>{

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
		
    public Slice<E> subSlice(int from,int to){
      return new SubArraySlice<>(list, from, to); 
    }
    @Override
    public String toString() {
      return Arrays.stream(list).map(e -> String.valueOf(e)).collect(Collectors.joining(", ","[","]")) ;
    }
  }
  
  public final class SubArraySlice<E> implements Slice<E>{
    
    private final E[] list;
    private final int fromList;
    private final int toList;
    
    private SubArraySlice(E[] obj,int from, int to) {
      Objects.requireNonNull(obj);
      Objects.checkFromToIndex(from, to,obj.length);
      list = obj;
      fromList = from;
      toList = to;
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
    
    public Slice<E> subSlice(int from,int to){
      Objects.checkFromToIndex(from, to, size());
      return new SubArraySlice(list, from+this.fromList, to+this.fromList);
    }
    
    @Override
    public String toString() {
      return Arrays.stream(list,fromList,toList).map(e -> String.valueOf(e)).collect(Collectors.joining(", ","[","]")) ;
    }
  }
  
  public static <E> Slice<E> array(E[] array) {
    Objects.requireNonNull(array);
    return new ArraySlice<E>(array);
  }
  
  
  public static <E> Slice<E> array(E[] array, int from, int to){
    Objects.requireNonNull(array);
    return new SubArraySlice<E>(array,from,to);
  }
  
  Slice<E> subSlice(int from,int to);
  int size();
  E get(int a);
}
