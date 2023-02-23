# TP5 Single instruction, Multiple Data (SIMD)
###### LY-IENG Steven

## Exercice 2 - Vectorized Add / Vectorized Min

> #### Q1
> On cherche à écrire une fonction sum qui calcule la somme des entiers d'un tableau passé en paramètre. Pour cela, nous allons utiliser l'API de vectorisation pour calculer la somme sur des vecteurs.<br>


> Quelle est la classe qui représente des vecteurs d'entiers ?<br>

La classe représentant les vecteurs d'entier est ``ÌntVector``
> Qu'est ce qu'un ``VectorSpecies`` et quelle est la valeur de ``VectorSpecies`` que nous allons utiliser dans notre cas ?<br>

Un ``VectorSpecies`` est un value based objet on va l'utiliser en Integer
> Comment créer un vecteur contenant des zéros et ayant un nombre préféré de ``lanes`` ?<br>
````java
VectorSpecies<Integer> species = IntVector.SPECIES_PREFERRED;
IntVector v1 = IntVector.zero(species);
````

> Comment calculer la taille de la boucle sur les vecteurs (loopBound) ?<br>

```java
var loopBound = SPECIES.loopBound(length);
```
> Comment faire la somme de deux vecteurs d'entiers ?<br>

Pour faire la somme de deux vecteurs d'entiers il faut un ``lanewise`` avec comme arguments  un binaryOperator, un vecteur et le masque de vecteur
> Comment faire la somme de toutes les ``lanes`` d'un vecteur d'entiers ?<br>
 
Pour faire la somme de toutes les ``lanes`` d'un vecteur d'entier on utilise reduceLane
> Si la longueur du tableau n'est pas un multiple du nombre de ``lanes``, on va utiliser une ``post-loop``, quel doit être le code de la post-loop ?<br>
On va faire une boucle classique
```java
    var result = vector.reduceLanes(VectorOperators.ADD);
    for(; i < length; i++) {
    result+= array[i];
    }
```
- VectorComputation.java
```java
public class VectorComputation {
  
  public static int sum(int[] array) {
    var length = array.length;
    var species = IntVector.SPECIES_PREFERRED;
    var loopBound = species.loopBound(length);
    
    var vector = IntVector.zero(species);
    var i = 0;
    for(; i < loopBound; i += species.length()) { 
      var v = IntVector.fromArray(species, array, i);
      vector = vector.add(v);
    }
    var result = vector.reduceLanes(VectorOperators.ADD);
    for(; i < length; i++) {
    result+= array[i];
    }
    return result;
  }
}
```
> #### Q2
> On souhaite écrire une méthode sumMask qui évite d'utiliser une post-loop et utilise un mask à la place.

> Comment peut-on faire une addition de deux vecteurs avec un mask ?

On peut utiliser ``vecteur1.add(vecteur2,mask)``
> Comment faire pour créer un mask qui allume les bits entre i la variable de boucle et length la longueur du tableau ?

```java
species.indexInRange(i, <taille du tableau>);
```
- VectorComputation.java
```java
public class VectorComputation {
  
  public static int sum(int[] array) {
  ...
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
} 
```
> #### Q3
> On souhaite maintenant écrire une méthode min qui calcule le minimum des valeurs d'un tableau en utilisant des vecteurs et une post-loop.
Contrairement à la somme qui a 0 comme élément nul, le minimum n'a pas d'élément nul... Quelle doit être la valeur utilisée pour initialiser toutes les lanes du vecteur avant la boucle principale ?
 - VectorComputation.java
```java
public class VectorComputation {
  
  public static int sum(int[] array) {
    ...
  }
  
  public static int sumMask(int[] array) {
    ...
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
} 

```
On prend le premier element de l'array pour initialiser le vecteur a l'aide de ``broadcast``, puis j'ai essayé de faire comme pour la methode sum
Pour le post loop au lieu de add on compare les elements du reste au resultat

> #### Q4 
> On souhaite enfin écrire une méthode minMask qui au lieu d'utiliser une post-loop comme dans le code précédent, utilise un mask à la place.
Attention, le minimum n'a pas d’élément nul (non, toujours pas !), donc on ne peut pas laisser des zéros "traîner" dans les lanes lorsque l'on fait un minimum sur deux vecteurs.
- VectorComputation.java
```java
public class VectorComputation {
  
  public static int sum(int[] array) {
    ...
  }
  
  public static int sumMask(int[] array) {
    ...
  }
  
  public static int min(int[] array) {
    ...
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
```
Pareil que pour la question précedente j'ai pris pour initialiser le vecteur le premier element  de l'array a l'aide de ``broadcast``,
puis j'ai fais quelque chose de similaire a ``sumMask`` en changeant les add par des mins

> Que pouvez-vous dire en termes de performance entre min et minMask en utilisant les tests de performances JMH ?

minMask est plus performant que min car une operation vectorisé est plus rapide de plus ceci est indiqué dans le cours, faire un if dans une boucle qui ne fait que des calculs est lent.

## Exercice 3- Fizzbuzz

> #### Q1
> On souhaite écrire dans la classe FizzBuzz une méthode fizzBuzzVector128 qui prend en paramètre une longueur et renvoie un tableau d'entiers de taille longueur contenant les valeurs de FizzBuzz en utilisant des vecteurs 128 bits d'entiers.

Malheureusement j'ai essayé de faire cette question sans resultats pertinants il y a que le test avec 15 qui passe pour l'instant
> #### Q2
> On souhaite maintenant écrire une méthode fizzBuzzVector256 qui utilise des vecteurs 256 bits.


> #### Q3
> Écrire la méthode fizzBuzzVector128AddMask qui utilise des vecteurs 128 bits et l'addition avec un mask, puis vérifier avec le test que votre code fonctionne correctement. Comme précédemmment, le tableau des masques doit être une constante.