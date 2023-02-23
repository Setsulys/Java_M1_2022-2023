# TP8 Trop graph
###### LY-IENG Steven

## Exercice 2 - MatrixGraph

> #### Q1
> Indiquer comment trouver la case (i, j) dans un tableau à une seule dimension de taille nodeCount * nodeCount.<br>
> Si vous n'y arrivez pas, faites un dessin !

Pour retrouver la case (i,j) dans un tableau à une dimension il faut faire ``i * nodeCount + j``

> #### Q2
> Rappeler pourquoi, en Java, il n'est pas possible de créer des tableaux de variables de type puis implanter la classe MatrixGraph et son constructeur.<br>
> Pouvez-vous supprimer le warning à la construction ? Pourquoi?

En java il n'est pas possible de creer des tableaux de variables, à cause de l'erasure, les types arguments sont perdus a l'execution, la VM ne peux pas lever une ArrayStoreException<br>
Il faut faire un array d'object et caster l'array comme ci-dessous<br>
On peut supprimer le warning car le cast est sur
```java
public class ArrayList<E> {
private E[] elements;

 public ArrayList() {
 @SuppressWarnings("unchecked")
 E[] elements = (E[]) new Object[16];
 this.elements = elements;
 }
}
```

- MatrixGraph.java
```java
public class MatrixGraph<T> implements Graph<T>{

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
}
```

> #### Q3
> On peut remarquer que la classe MatrixGraph n'apporte pas de nouvelles méthodes par rapport aux méthodes de l'interface Graph donc il n'est pas nécessaire que la classe MatrixGraph soit publique.<br>
> Ajouter une méthode factory nommée createMatrixGraph dans l'interface Graph et déclarer la classe MatrixGraph non publique.

- MatrixGraph.java

```java
final class MatrixGraph<T> implements Graph<T>{

  private final T[] array;
    
  public MatrixGraph(int integer) {
    ...
  }
}
```

- Graph.java
```java
public interface Graph<T> {

  //addEdge(src, dst, weight); 

  //getWeight(src, dst);

  public static <T>Graph<T> createMatrixGraph(int nodeCount) {
    return new MatrixGraph<>(nodeCount);
  }
  
  @FunctionalInterface
  interface EdgeConsumer<T> {

    public void edge(int src, int dst, T weight);
  }
  
   //void edges(src, edgeConsumer);
  
  //neighborIterator(src);

  //neighborStream(src);
}
```
> #### Q4
> Afin d'implanter correctement la méthode getWeight, rappeler à quoi sert la classe java.util.Optional en Java.<br>
> Implanter la méthode addEdge sachant que l'on ne peut pas créer un arc sans valeur.<br>
> Implanter la méthode getWeight.

La classe ``java.util.Optional`` en Java permet d'avoir des valeurs qui peuvent exister ou non
- MatrixGraph.java

```java
final class MatrixGraph<T> implements Graph<T>{

  private final T[] array;
  private final int nodeCount;
  public MatrixGraph(int nodeCount) {
    ...
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
}
```
- Graph.java
```java
public interface Graph<T> {

  public void addEdge(int src,int dst,T weight); 

  public Optional<T> getWeight(int src, int dst);

  public static <T>Graph<T> createMatrixGraph(int nodeCount) {
    return new MatrixGraph<>(nodeCount);
  }
  
  @FunctionalInterface
  interface EdgeConsumer<T> {

    public void edge(int src, int dst, T weight);
  }
  
   //void edges(src, edgeConsumer);
  
  //neighborIterator(src);

  //neighborStream(src);
}
```
> #### Q5
> Implanter la méthode edges puis vérifier que les tests marqués "Q5" passent.

- MatrixGraph.java

```java
final class MatrixGraph<T> implements Graph<T>{

  private final T[] array;
  private final int nodeCount;
  public MatrixGraph(int nodeCount) {
    ...
  }

  
  public void addEdge(int src,int dst, T weight) {
    ...
  }
  
  public Optional<T> getWeight(int src,int dst) {
    ...
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
}
```

- Graph.java
```java
public interface Graph<T> {

  public void addEdge(int src,int dst,T weight); 

  public Optional<T> getWeight(int src, int dst);

  public static <T>Graph<T> createMatrixGraph(int nodeCount) {
    return new MatrixGraph<>(nodeCount);
  }
  
  @FunctionalInterface
  interface EdgeConsumer<T> {

    public void edge(int src, int dst, T weight);
  }
  
  public void edges(int src, EdgeConsumer<? super T> edgeConsumer);
  
  //neighborIterator(src);

  //neighborStream(src);
}
```

> #### Q6
> Rappeler le fonctionnement d'un itérateur et de ses méthodes hasNext et next.<br>

hasNext verifie que l'on ne dépasse pas l'array ou l'element qu'on inter
Next renvoie l'element itéré dans l'itérateur.
> Que renvoie next si hasNext retourne false ?<br>

Si hasNext retourne False next doit envoyer une exception ```NoSuchElementException``
> Expliquer pourquoi il n'est pas nécessaire, dans un premier temps, d'implanter la méthode remove qui fait pourtant partie de l'interface.<br>
> Implanter la méthode neighborsIterator(src) qui renvoie un itérateur sur tous les nœuds ayant un arc dont la source est src.

- MatrixGraph.java
```java
final class MatrixGraph<T> implements Graph<T>{

  private final T[] array;
  private final int nodeCount;
  public MatrixGraph(int nodeCount) {
    ...
  }

  
  public void addEdge(int src,int dst, T weight) {
    ...
  }
  
  public Optional<T> getWeight(int src,int dst) {
    ...
  }
  
  public void edges(int src, EdgeConsumer<? super T> edgeConsumer) {
    ...
  }


  @Override
  public Iterator<Integer> neighborIterator(int src) {
    return new Iterator<>(){

      public int count =incrementIf(0);
      
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
        count++;
        count = incrementIf(count);
        return rep;
      }
      
    };
  }
}

```

- Graph.java
```java
public interface Graph<T> {

  public void addEdge(int src,int dst,T weight); 

  public Optional<T> getWeight(int src, int dst);

  public static <T>Graph<T> createMatrixGraph(int nodeCount) {
    return new MatrixGraph<>(nodeCount);
  }
  
  @FunctionalInterface
  interface EdgeConsumer<T> {

    public void edge(int src, int dst, T weight);
  }
  
  public void edges(int src, EdgeConsumer<? super T> edgeConsumer);
  
  neighborIterator(src);

  //neighborStream(src);
}
```

> #### Q7
> Pourquoi le champ nodeCount ne doit pas être déclaré private avant Java 11 ?<br>

Avant Java 11 la VM génere des accesseurs pour les classes internes

> Est-ce qu'il y a d'autres champs qui ne doivent pas être déclarés private avant Java 11 ?
Les autres champs qui ne doivent pas être déclarer private sont ceux de classes internes dont on ne veut pas obtenir d'accesseur

> #### Q8
> On souhaite écrire la méthode neighborStream(src) qui renvoie un IntStream contenant tous les nœuds ayant un arc sortant par src.<br>
> Pour créer le Stream ,nous allons utiliser StreamSupport.intStream qui prend en paramètre un Spliterator.OfInt. Rappeler ce qu'est un Spliterator, à quoi sert le OfInt et quelles sont les méthodes qu'il va falloir redéfinir.<br>
> Écrire la méthode neighborStream sachant que l'on implantera le Spliterator en utilisant l'itérateur défini précédemment.

- MatrixGraph.java
```java
final class MatrixGraph<T> implements Graph<T>{

  private final T[] array;
  private final int nodeCount;
  public MatrixGraph(int nodeCount) {
    ...
  }

  
  public void addEdge(int src,int dst, T weight) {
    ...
  }
  
  public Optional<T> getWeight(int src,int dst) {
    ...
  }
  
  public void edges(int src, EdgeConsumer<? super T> edgeConsumer) {
    ...
  }


  @Override
  public Iterator<Integer> neighborIterator(int src) {
    ...
  }
  
  @Override
  public IntStream neighborStream(int src) {
    var iter = neighborIterator(src);
    return StreamSupport.intStream(new Spliterator.OfInt() {

      @Override
      public long estimateSize() {
        return 0;
      }

      @Override
      public int characteristics() {
        return 0;
      }

      @Override
      public OfInt trySplit() {
        return null;
      }

      @Override
      public boolean tryAdvance(IntConsumer action) {
        if(!iter.hasNext()) {
          return false;
        }
        action.accept(iter.next());
        return true;
      }
      
    },false);
  };
}
```


- Graph.java
```java
public interface Graph<T> {

  public void addEdge(int src,int dst,T weight); 

  public Optional<T> getWeight(int src, int dst);

  public static <T>Graph<T> createMatrixGraph(int nodeCount) {
    return new MatrixGraph<>(nodeCount);
  }
  
  @FunctionalInterface
  interface EdgeConsumer<T> {

    public void edge(int src, int dst, T weight);
  }
  
  public void edges(int src, EdgeConsumer<? super T> edgeConsumer);
  
  neighborIterator(src);

  neighborStream(src);
}
```

> #### Q9
> On peut remarquer que neighborStream dépend de neighborsIterator et donc pas d'une implantation spécifique. On peut donc écrire neighborStream directement dans l'interface Graph comme ça le code sera partagé.<br>
> Rappeler comment on fait pour avoir une méthode 'instance avec du code dans une interface.<br>

On ajoute le modifieur ``default`` aux modifieurs
> Déplacer neighborStream dans Graph et vérifier que les tests unitaires passent toujours.

- MatrixGraph.java
```java
final class MatrixGraph<T> implements Graph<T>{

  private final T[] array;
  private final int nodeCount;
  public MatrixGraph(int nodeCount) {
    ...
  }

  
  public void addEdge(int src,int dst, T weight) {
    ...
  }
  
  public Optional<T> getWeight(int src,int dst) {
    ...
  }
  
  public void edges(int src, EdgeConsumer<? super T> edgeConsumer) {
    ...
  }


  @Override
  public Iterator<Integer> neighborIterator(int src) {
    return new Iterator<>(){

      public int count =incrementIf(0);
      
      @Override
      public boolean hasNext() {
        ...
      }

      private int incrementIf(int count) {
        ...
      }
      
      @Override
      public Integer next() {
        ...
      }
    };
  }


}
```


- Graph.java
```java
public interface Graph<T> {

  public void addEdge(int src,int dst,T weight); 

  public Optional<T> getWeight(int src, int dst);

  public static <T>Graph<T> createMatrixGraph(int nodeCount) {
    return new MatrixGraph<>(nodeCount);
  }
  
  @FunctionalInterface
  interface EdgeConsumer<T> {

    public void edge(int src, int dst, T weight);
  }
  
  public void edges(int src, EdgeConsumer<? super T> edgeConsumer);
  
  neighborIterator(src);

  public default IntStream neighborStream(int src) {
    var iter = neighborIterator(src);
    return StreamSupport.intStream(new Spliterator.OfInt() {

      @Override
      public long estimateSize() {
        return 0;
      }

      @Override
      public int characteristics() {
        return 0;
      }

      @Override
      public OfInt trySplit() {
        return null;
      }

      @Override
      public boolean tryAdvance(IntConsumer action) {
        if(!iter.hasNext()) {
          return false;
        }
        action.accept(iter.next());
        return true;
      } 
    },false);
  };
}
```


> #### Q10
> Expliquer le fonctionnement précis de la méthode remove de l'interface Iterator.<br>  
La methode remove dans l'interface Iterator supprime l'element qui est actuellemen itéré
> Implanter la méthode remove de l'itérateur.

- MatrixGraph.java
```java
final class MatrixGraph<T> implements Graph<T>{

  private final T[] array;
  private final int nodeCount;
  public MatrixGraph(int nodeCount) {
    ...
  }

  
  public void addEdge(int src,int dst, T weight) {
    ...
  }
  
  public Optional<T> getWeight(int src,int dst) {
    ...
  }
  
  public void edges(int src, EdgeConsumer<? super T> edgeConsumer) {
    ...
  }


  @Override
  public Iterator<Integer> neighborIterator(int src) {
    return new Iterator<>(){

      public int count =incrementIf(0);
      public int index = -1;
      
      @Override
      public boolean hasNext() {
        ...
      }

      private int incrementIf(int count) {
        ...
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
```