package fr.uge.simd;

import java.util.Arrays;

import jdk.incubator.vector.*;
public class FizzBuzz { 
  private final static int[] v = new int[]{ -3,  1,  2, -1,  4, -2, -1,  7,  8, -1, -2, 11, -1, 13, 14};
  private final static int[] delta = new int[] {0, 15, 15,  0, 15,  0,  0, 15, 15,  0,  0, 15,  0, 15, 15};
  
  public static int[] fizzBuzzVector128(int n) {
    var species = IntVector.SPECIES_128;
    var resultat = new int[n];
    var length = species.length();
    var mask15 = species.indexInRange(length * 3, length * 4 - 1);
    
    var v1 = IntVector.fromArray(species, v, length * 0);
    var v2 = IntVector.fromArray(species, v, length * 1);
    var v3 = IntVector.fromArray(species, v, length * 2);
    var v4 = IntVector.fromArray(species, v, length * 3, mask15);
    
    v1.intoArray(resultat, length * 0);
    v2.intoArray(resultat, length * 1);
    v3.intoArray(resultat, length * 2);
    v4.intoArray(resultat, length * 3,mask15);
      
    return resultat;
  }
  
  public static void main(String[] args) {
    Arrays.stream(fizzBuzzVector128(15)).forEach(str -> System.out.println(str+""));
  }
}
