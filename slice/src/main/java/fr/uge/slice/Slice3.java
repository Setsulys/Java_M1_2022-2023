package fr.uge.slice;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Slice3<E>{
  
  public static <E> Slice3<E> array(E[] array) {
    Objects.requireNonNull(array);
    return new Slice3<>(){  
      @Override
      public int size() {
        return array.length; 
      }
      
      @Override
      public E get(int a) {
        Objects.checkIndex(a,size());
        return array[a];
        }
   
      @Override
      public String toString() {
        return Arrays.stream(array).map(e -> String.valueOf(e)).collect(Collectors.joining(", ","[","]")) ;
      }
    };
  }
  
  public default Slice3<E> subSlice(int from,int to){
    Objects.checkFromToIndex(from, to, size());
    return new Slice3<>() {
      @Override
      public int size() {
        return to-from;
      }
      
      @Override
      public E get(int a) {
        Objects.checkIndex(a,size());
        return Slice3.this.get(a+from);
      }
      
      @Override
      public String toString() {
        var build = new StringBuilder();
        var s = "";
        build.append("[");
        for(var i=0;i<this.size();i++) {
          build.append(s).append(String.valueOf(this.get(i)));
          s=", ";
        }
        return build.append("]").toString();
      }

    };
  }
  int size();
  E get(int a);
  
  static <E>Slice3<E> array(E[] array,int from,int to){
    Objects.requireNonNull(array);
    Objects.checkFromToIndex(from, to, array.length);
    return array(array).subSlice(from, to);
  }
}
