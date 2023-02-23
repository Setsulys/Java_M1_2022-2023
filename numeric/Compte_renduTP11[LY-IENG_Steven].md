# TP11 	Structure de données spécialisée pour les types primitifs
###### LY-IENG Steven

## Exercice 2 - NumericVec


> #### Q1
> Dans la classe ``fr.uge.numeric.NumericVec``, on souhaite écrire une méthode longs sans paramètre qui permet de créer un ``NumericVec`` vide ayant pour l'instant une capacité fixe de 16 valeurs. Cela doit être la seule façon de pouvoir créer un ``NumericVec``.
Écrire la méthode longs puis ajouter les méthodes ``add(value)``, ``get(index)`` et ``size``.

- NumericVec.java

```java
public class NumericVec <T>{

  private final long [] array;
  public int size;

  private NumericVec() {
    this.array = new long[16];
    this.size = 0;
  }
  
  public static <T>NumericVec<T> longs() {
    return new NumericVec<T>();
  }
  
  public int size() {
    return size;
  }
  
  public long get(int index) {
    Objects.checkIndex(index, size);
    return array[index];
  }
  
  public void add(T element) {
    Objects.requireNonNull(element);
    array[size] = (long) element;
    size++;
  }
}
```

> #### Q2
> On veut maintenant que le tableau utilisé par ``NumericVec`` puisse s'agrandir pour permettre d'ajouter un nombre arbitraire de valeurs.
On veut, de plus, que NumericVec soit économe en mémoire, donc la capacité du tableau d'un ``NumericVec`` vide doit être 0 (si vous n'y arrivez pas, faites la suite).

```java
public class NumericVec <T>{

  private long [] array;
  public int size;

  private NumericVec() {
    ...
  }
  
  public static <T>NumericVec<T> longs() {
    ...
  }
  
  public int size() {
    ...
  }
  
  public long get(int index) {
    ...
  }
  
  public void add(T element) {
    Objects.requireNonNull(element);
    if(size == array.length) {
      var e = this.array;
      this.array = new long[array.length*2 + 1];
      for(var i = 0; i < e.length ; i++) {
        this.array[i] = e[i];
      }
    }
    array[size] = (long) element;
    size++;
  }
}

```

> #### Q3
> Faire en sorte d'utiliser un Stream pour que l'on puisse afficher un NumericVec avec le format attendu.

- NumericVec.java

```java
public class NumericVec <T>{

  private long [] array;
  public int size;

  private NumericVec() {
    ...
  }
  
  public static <T>NumericVec<T> longs() {
    ...
  }
  
  public int size() {
    ...
  }
  
  public long get(int index) {
    ...
  }
  
  public void add(T element) {
    ...
  }
  
  @Override
  public String toString() {
    return Arrays.stream(array).limit(size).mapToObj(e -> String.valueOf(e)).collect(Collectors.joining(", ","[","]"));
  }
}
```

> #### Q4
> On veut maintenant ajouter les 3 méthodes ints, longs et doubles qui permettent respectivement de créer des NumericVec d'int, de long ou de double en prenant en paramètre des valeurs séparées par des virgules.
> En termes d'implantation, l'idée est de convertir les int ou les double en long avant de les insérer dans le tableau. Et dans l'autre sens, lorsque l'on veut lire une valeur, 
> c'est à dire quand on prend un long dans le tableau, on le convertit en le type numérique attendu. Pour cela, l'idée est de stocker dans chaque NumericVec une fonction into qui sait convertir une valeur en long, et une fonction from qui sait convertir un long vers la valeur attendue.

- NumericVec.java

```java
public class NumericVec <T>{

  private long [] array;
  private int size;
  private ToLongFunction<T> into;
  private LongFunction<T> from;
  
  private NumericVec(long[] elements,ToLongFunction<T> into,LongFunction<T> from) {
    Objects.requireNonNull(elements);
    Objects.requireNonNull(into);
    Objects.requireNonNull(from);
    
    this.array = elements;
    this.size = elements.length;
    this.from = from;
    this.into = into;
  }
  
  @SafeVarargs
  public static NumericVec<Long> longs(long... elements) {
    Objects.requireNonNull(elements);
    return new NumericVec<>(Arrays.copyOf(elements,elements.length),x->x,x->x);
  }
  
  @SafeVarargs
  public static NumericVec<Integer> ints(int... elements) {
    Objects.requireNonNull(elements);
    var array = Arrays.stream(elements).mapToLong(e -> e).toArray();
    return new NumericVec<Integer>(Arrays.copyOf(array,array.length),x->x,x->(int)x);
  }

  @SafeVarargs
  public static NumericVec<Double> doubles(double... elements) {
    Objects.requireNonNull(elements);
    var array = Arrays.stream(elements).mapToLong(e -> Double.doubleToRawLongBits(e)).toArray();
    return new NumericVec<Double>(Arrays.copyOf(array, array.length),x->Double.doubleToRawLongBits(x),x->Double.longBitsToDouble(x));
  }
  
  public int size() {
    ...
  }
  
  public T get(int index) {
    Objects.checkIndex(index, size);
    return from.apply(array[index]);
  }
  
  public void add(T element) {
    Objects.requireNonNull(element);
    if(size == array.length) {
      var e = this.array;
      this.array = new long[array.length*2+1];
      for(var i = 0; i < e.length ; i++) {
        this.array[i] = e[i];
      }
    }
    array[size] = into.applyAsLong(element);
    size++;
  }
  
  @Override
  public String toString() {
    return Arrays.stream(array).limit(size).mapToObj(e -> String.valueOf(from.apply(e))).collect(Collectors.joining(", ","[","]"));
  }
}
```

> #### Q5
> On souhaite maintenant pouvoir parcourir un NumericVec avec une boucle ``for(:)``. Dans le cas où l'on modifie un NumericVec avec la méthode add lors de l'itération, les valeurs ajoutées ne sont pas visibles pour la boucle.
Modifier la classe NumericVec pour implanter le support de la boucle ``for(:)``.

- NumericVec.java

```java
public class NumericVec <T> implements Iterable<T>{

  private long [] array;
  private int size;
  private ToLongFunction<T> into;
  private LongFunction<T> from;
  
  private NumericVec(long[] elements,ToLongFunction<T> into,LongFunction<T> from) {
    ...
  }
  
  @SafeVarargs
  public static NumericVec<Long> longs(long... elements) {
    ...
  }
  
  @SafeVarargs
  public static NumericVec<Integer> ints(int... elements) {
    ...
  }

  @SafeVarargs
  public static NumericVec<Double> doubles(double... elements) {
    ...
  }
  
  public int size() {
    ...
  }
  
  public T get(int index) {
    ...
  }
  
  public void add(T element) {
    ...
  }

  public Iterator<T> iterator() {
    var size =this.size;
    return new Iterator<>() {
      private int  cpt= 0;
      @Override
      public boolean hasNext() {
        return cpt != size;
      }

      @Override
      public T next() {
        if(!hasNext()) {
          throw new NoSuchElementException();
        }
        var rt = cpt;
        cpt++;
        return get(rt);
      }
    };
  }

  @Override
  public String toString() {
    ...
  }
}
```

> #### Q6
> On souhaite ajouter une méthode addAll qui permet d'ajouter un NumericVec à un NumericVec déjà existant.
Écrire le code de la méthode addAll.

Comme on a crée un iterator qui implemente sur la classe on peut fair une boucle for classique qui ajoute chaque elements du NumericVec passé en parametre

- NumericVec.java
```java
public class NumericVec <T> implements Iterable<T>{

  private long [] array;
  private int size;
  private ToLongFunction<T> into;
  private LongFunction<T> from;
  
  private NumericVec(long[] elements,ToLongFunction<T> into,LongFunction<T> from) {
    ...
  }
  
  @SafeVarargs
  public static NumericVec<Long> longs(long... elements) {
    ...
  }
  
  @SafeVarargs
  public static NumericVec<Integer> ints(int... elements) {
    ...
  }

  @SafeVarargs
  public static NumericVec<Double> doubles(double... elements) {
    ...
  }
  
  public int size() {
    ...
  }
  
  public T get(int index) {
    ...
  }
  
  public void add(T element) {
    ...
  }
  
  public Iterator<T> iterator() {
    ...
  }
  
  public void addAll(NumericVec<T> numvec) {
    for(var elements:numvec) {
      this.add(elements);
    }
  }
  
  @Override
  public String toString() {
   ...
  }

}
```

> #### Q7
> On souhaite maintenant écrire une méthode map(function, factory) qui prend en paramètre une fonction qui peut prendre en paramètre un élément du NumericVec et renvoie une nouvelle valeur ainsi qu' une référence de méthode permettant de créer un nouveau NumericVec qui contiendra les valeurs renvoyées par la fonction.
Écrire la méthode map.

- NumericVec.java

```java
public class NumericVec <T> implements Iterable<T>{

  private long [] array;
  private int size;
  private ToLongFunction<T> into;
  private LongFunction<T> from;
  
  private NumericVec(long[] elements,ToLongFunction<T> into,LongFunction<T> from) {
    ...
  }
  
  @SafeVarargs
  public static NumericVec<Long> longs(long... elements) {
    ...
  }
  
  @SafeVarargs
  public static NumericVec<Integer> ints(int... elements) {
    ...
  }

  @SafeVarargs
  public static NumericVec<Double> doubles(double... elements) {
    ...
  }
  
  public int size() {
    ...
  }
  
  public T get(int index) {
    ...
  }
  
  public void add(T element) {
    ...
  }
  
  public Iterator<T> iterator() {
    var size = this.size;
    return new Iterator<>() {
      private int  cpt= 0;
      @Override
      public boolean hasNext() {
        ...
      }

      @Override
      public T next() {
        ...
      }
    };
  }
  
  public void addAll(NumericVec<T> numvec) {
    ...
  }
  

  public <U> NumericVec<U> map(Function<? super T,? extends U> function, Supplier<? extends NumericVec<U>> factory) {
    Objects.requireNonNull(function);
    Objects.requireNonNull(factory);
    var seq = factory.get();
    Arrays.stream(array).limit(size).forEach(e -> seq.add(function.apply(from.apply(e))));
    return seq;
  }

    @Override
  public String toString() {
    ...
  }

}
```

> #### Q8 
> On souhaite écrire une méthode toNumericVec(factory) qui prend en paramètre une référence de méthode permettant de créer un NumericVec et renvoie un Collector qui peut être utilisé pour collecter des valeurs numériques dans un/des NumericVec créés par la référence de méthode.
Écrire la méthode toNumericVec.

- NumericVec.java

```java
public class NumericVec <T> implements Iterable<T>{

  private long [] array;
  private int size;
  private ToLongFunction<T> into;
  private LongFunction<T> from;
  
  private NumericVec(long[] elements,ToLongFunction<T> into,LongFunction<T> from) {
    ...
  }
  
  @SafeVarargs
  public static NumericVec<Long> longs(long... elements) {
    ...
  }
  
  @SafeVarargs
  public static NumericVec<Integer> ints(int... elements) {
    ...
  }

  @SafeVarargs
  public static NumericVec<Double> doubles(double... elements) {
    ...
  }
  
  public int size() {
    ...
  }
  
  public T get(int index) {
    ...
  }
  
  public void add(T element) {
    ...
  }
  
  public Iterator<T> iterator() {
    var size = this.size;
    return new Iterator<>() {
      private int  cpt= 0;
      @Override
      public boolean hasNext() {
        ...
      }

      @Override
      public T next() {
        ...
      }
    };
  }
  
  public void addAll(NumericVec<T> numvec) {
    ...
  }
  
  public <U> NumericVec<U> map(Function<? super T,? extends U> function, Supplier<? extends NumericVec<U>> factory) {
    ...
  }

  public static <U>Collector<? super U,NumericVec<U>,NumericVec<U>> toNumericVec(Supplier<? extends NumericVec<U>> factory) {
    Objects.requireNonNull(factory);
    return new Collector<>(){

      @Override
      public Supplier<NumericVec<U>> supplier() {
        return ()-> factory.get();
      }

      @Override
      public BiConsumer<NumericVec<U>, U> accumulator() {
        return (seq,element) ->seq.add(element);
      }

      @Override
      public BinaryOperator<NumericVec<U>> combiner() {
        return (seq1,seq2) ->{
          seq1.addAll(seq2);
          return seq1;
        };
      }

      @Override
      public Function<NumericVec<U>, NumericVec<U>> finisher() {
        return func-> func;
      }

      @Override
      public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
      }
    };
  }
  
  @Override
  public String toString() {
    ...
  }
}


```

> #### Q9
> Enfin, écrire une méthode stream() qui renvoie un Stream qui voit l'ensemble des valeurs par ordre d'insertion dans le NumericVec courant.
Le Stream renvoyé devra être parallélisable.

- NumericVec.java

```java
public class NumericVec <T> implements Iterable<T>{

  private long [] array;
  private int size;
  private ToLongFunction<T> into;
  private LongFunction<T> from;
  
  private NumericVec(long[] elements,ToLongFunction<T> into,LongFunction<T> from) {
    ...
  }
  
  @SafeVarargs
  public static NumericVec<Long> longs(long... elements) {
    ...
  }
  
  @SafeVarargs
  public static NumericVec<Integer> ints(int... elements) {
    ...
  }

  @SafeVarargs
  public static NumericVec<Double> doubles(double... elements) {
    ...
  }
  
  public int size() {
    ...
  }
  
  public T get(int index) {
    ...
  }
  
  public void add(T element) {
    ...
  }
  
  public Iterator<T> iterator() {
    var size = this.size;
    return new Iterator<>() {
      private int  cpt= 0;
      @Override
      public boolean hasNext() {
        ...
      }

      @Override
      public T next() {
        ...
      }
    };
  }
  
  public void addAll(NumericVec<T> numvec) {
    ...
  }
  
  public <U> NumericVec<U> map(Function<? super T,? extends U> function, Supplier<? extends NumericVec<U>> factory) {
    ...
  }

  public static <U>Collector<? super U,NumericVec<U>,NumericVec<U>> toNumericVec(Supplier<? extends NumericVec<U>> factory) {
    Objects.requireNonNull(factory);
    return new Collector<>(){

      @Override
      public Supplier<NumericVec<U>> supplier() {
        ...
      }

      @Override
      public BiConsumer<NumericVec<U>, U> accumulator() {
        ...
      }

      @Override
      public BinaryOperator<NumericVec<U>> combiner() {
        ...
      }

      @Override
      public Function<NumericVec<U>, NumericVec<U>> finisher() {
        ...
      }

      @Override
      public Set<Characteristics> characteristics() {
        ...
      }
    };
  }

public Spliterator<T> splits(int start,int end,long[]array ){
    return new Spliterator<>() {
      private int i =start;
      
      @Override
      public boolean tryAdvance(Consumer<? super T> action) {
        if(i == end) {
          return false;
        }
        action.accept(get(i));
        i++;
        return true;
      }

      @Override
      public Spliterator<T> trySplit() {
        var middle = (i + end )>>>1;
        if(middle == i || size < 1024) {
          return null;
        }
        var spliterators = splits(i,middle,array);
        i = middle;
        return spliterators;
      }

      @Override
      public long estimateSize() {
        return size;
      }

      @Override
      public int characteristics() {
        return CONCURRENT | NONNULL | ORDERED | IMMUTABLE;
      }  
    };
  }

  public Stream<T> stream() {
    if(size < 1024) {
      return StreamSupport.stream(splits(0,size,array), false);
    }
    return StreamSupport.stream(splits(0,size,array),true);
        
  }
  
  @Override
  public String toString() {
    ...
  }
}
```

J'ai juste un test qui ne passe pas celui du stream Count je ne sais pas pourquoi