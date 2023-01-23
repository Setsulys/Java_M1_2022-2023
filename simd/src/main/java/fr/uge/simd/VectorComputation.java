package fr.uge.simd;

import java.util.Arrays;

import jdk.incubator.vector.*;


public class VectorComputation {
  
  public static int sum(int[] array) {
    var length = array.length;
    var species = IntVector.SPECIES_PREFERRED;
    var loopBound = species.loopBound(length);
    
    var vector = IntVector.zero(species);
    var i = 0;
    for(; i < loopBound; i += species.length()) { 
      //on recupere les 4*iemes elements de la liste 
      //et on les ajoutes aux vecteurs jusqu'a qu'il 
      //reste moins de 4 elements a la liste 
      var v = IntVector.fromArray(species, array, i);
      vector = vector.add(v);
    }
    // On réduit les vecteurs a un int en les additionnants
    var result = vector.reduceLanes(VectorOperators.ADD);
    //on ajoute les valeurs de l'array restant
    for(; i < length; i++) {
    result+= array[i];
    }
    return result;
  }
  
  public static int sumMask(int[] array) {
    var length = array.length;
    var species = IntVector.SPECIES_PREFERRED;
    var loopBound = species.loopBound(length);
    
    var vector = IntVector.zero(species);
    var i = 0;
    for(; i < loopBound; i += species.length()) { 
      var v = IntVector.fromArray(species, array, i);
      vector = vector.add(v);
    }

    var mask = species.indexInRange(i, length);
    var v = IntVector.fromArray(species, array, i, mask);
    vector= vector.add( v, mask);
    var result =vector.reduceLanes(VectorOperators.ADD);
    return result;
  }
  
  public static int min(int[] array) {
    var length = array.length;
    var species = IntVector.SPECIES_PREFERRED;
    var loopBound = species.loopBound(length);
    var vector = IntVector.broadcast(species,array[0]);
    var i = 0;
    for(; i < loopBound; i += species.length()) { 
      var v = IntVector.fromArray(species, array, i);
      vector = vector.min(v);
    }
    var result = vector.reduceLanes(VectorOperators.MIN);
    for(; i < length; i++) {
     result = result > array[i]? array[i]: result;
    }
    return result;
  }
  
  public static int minMask(int[] array) {
    var length = array.length;
    var species = IntVector.SPECIES_PREFERRED;
    var loopBound = species.loopBound(length);
    
    var vector = IntVector.broadcast(species,array[0]);
    var i = 0;
    for(; i < loopBound; i += species.length()) { 
      var v = IntVector.fromArray(species, array, i);
      vector = vector.min(v);
    }

    var mask = species.indexInRange(i, length);
    var v = IntVector.fromArray(species, array, i, mask);
    vector= vector.lanewise(VectorOperators.MIN,v,mask);
    var result =vector.reduceLanes(VectorOperators.MIN);
    return result;
  }
} 
