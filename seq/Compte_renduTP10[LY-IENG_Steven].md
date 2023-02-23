# TP10 Trop graph
###### LY-IENG Steven

## Exercice 2 - seq 
> #### Q1
> Écrire le code de la classe ``Seq`` dans le package ``fr.uge.seq``.

- Seq.java

```java
public class Seq<T> {

  private final List<T> list ;
  private final int size;
  
  private Seq(List<? extends T> lst) {
    Objects.requireNonNull(lst);
    this.list = List.copyOf(lst);
    this.size = list.size();
  }
  
  public static <E> Seq<E> from(List<? extends E> lst) {
    Objects.requireNonNull(lst);
    return new Seq<E>(lst);
  }

  public Integer size() {
    return list.size();
  }
  
  public T get(int i) {
    Objects.checkIndex(i, list.size());
    return list.get(i);
  }
}
```

> #### Q2
> Écrire une méthode d'affichage permettant d'afficher les valeurs d'un ``Seq`` séparées par des virgules (suivies d'un espace), l'ensemble des valeurs étant encadré par des chevrons ('<' et '>').

- Seq.java

```java
public class Seq<T> {

  private final List<T> list ;
  private final int size;
  
  private Seq(List<? extends T> lst) {
    ...
  }
  
  public static <E> Seq<E> from(List<? extends E> lst) {
    ...
  }

  public Integer size() {
    ...
  }
  
  public T get(int i) {
    ...
  }

  @Override
  public String toString() {
    return list.stream().map(e -> e.toString()).collect(Collectors.joining(", ","<",">"));
  }
}
```

> #### Q3
> Écrire une méthode of permettant d'initialiser un ``Seq`` à partir de valeurs séparées par des virgules.

- Seq.java

```java
public class Seq<T> {

  private final List<T> list ;
  private final int size;
  
  private Seq(List<? extends T> lst) {
    ...
  }
  
  public static <E> Seq<E> from(List<? extends E> lst) {
    ...
  }

  public Integer size() {
    ...
  }
  
  public T get(int i) {
    ...
  }

  @SafeVarargs
  public static <E>Seq<E> of(E... values) {
    return new Seq<E>(List.of(values));
    
  }

  @Override
  public String toString() {
    ...
  }
}
```

> #### Q4 
> Écrire une méthode ``forEach`` qui prend en paramètre une fonction qui prend en paramètre chaque élément un par un et fait un effet de bord.

- Seq.java
```java
public class Seq<T> {

  private final List<T> list ;
  private final int size;
  
  private Seq(List<? extends T> lst) {
    ...
  }
  
  public static <E> Seq<E> from(List<? extends E> lst) {
    ...
  }

  public Integer size() {
    return list.size();
  }
  
  public T get(int i) {
    ...
  }

  @SafeVarargs
  public static <E>Seq<E> of(E... values) {
    ...
  }
  
  public void forEach(Consumer<? super T>consumer) {
    Objects.requireNonNull(consumer);
    list.stream().map(e->e).forEach(consumer);
  }
  
  @Override
  public String toString() {
    ...
  }
}
```

> #### Q5
> On souhaite écrire une méthode ``map`` qui prend en paramètre une fonction à appliquer à chaque élément d'un ``Seq`` pour créer un nouveau ``Seq``. On souhaite avoir une implantation paresseuse, c'est-à-dire une implantation qui ne fait pas de calcul si ce n'est pas nécessaire. Par exemple, tant que personne n'accède à un élément du nouveau Seq, il n'est pas nécessaire d'appliquer la fonction. L'idée est de stoker les anciens éléments ainsi que la fonction et de l'appliquer seulement si c'est nécessaire.
Bien sûr, cela va nous obliger à changer l'implantation déjà existante de ``Seq`` car maintenant tous les ``Seq`` vont stocker une liste d'éléments ainsi qu'une fonction de transformation (de mapping)<br>
> Avant de se lancer dans l'implantation de map, quelle doit être sa signature ?

``public <E>Seq<E> map(Function<? super T,? extends E> functions)``

> Quel doit être le type des éléments de la liste ? 

Le type des éléménts de la liste doivent etre en ``Object``
> Et le type de la fonction stockée ?

Le type de la fonction stocké sera ``Function<? super T,? extends E>``

- Seq.java
```java
public class Seq<T> {

  private final List<Object> list ;
  private final int size;
  private final Function<? super Object,? extends T> function;
  
  private Seq(List<?> lst,Function<Object,T> function) {
    Objects.requireNonNull(lst);
    Objects.requireNonNull(function);
    this.list = List.copyOf(lst);
    this.size = list.size();
    this.function = function;
  }
  
  @SuppressWarnings("unchecked")
  public static <E> Seq<E> from(List<? extends E> lst) {
    Objects.requireNonNull(lst);
    return new Seq<E>(lst, e -> (E)e );
  }

  public Integer size() {
    ...
  }
  
  public T get(int i) {
    Objects.checkIndex(i, size());
    return function.apply(list.get(i));
  }

  
  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <E>Seq<E> of(E... values) {
    return new Seq<E>(List.of(values),e -> (E)e);
  }
  
  public void forEach(Consumer<? super T>consumer) {
    Objects.requireNonNull(consumer);
    list.stream().map(e->function.apply(e)).forEach(consumer);
  }
  
  public <E>Seq<E> map(Function<? super T,? extends E> functions) {
    Objects.requireNonNull(functions);
    return new Seq<E>(this.list,e -> functions.apply(this.function.apply(e)));
  }
  
  @Override
  public String toString() {
    return list.stream().map(e -> function.apply(e).toString()).collect(Collectors.joining(", ","<",">"));
  }
}
````
On ajoute un champ contenant la fonction au seq et 
On renvoi un Seq dans lequel on applique la fonction dans le champs fonction, et a chaque return ou iteration d'un élément du seq on applique la fonction qui a été gardé dans le champs

> #### Q6
> Écrire une méthode findFirst qui renvoie le premier élément du Seq si celui-ci existe.

- Seq.java
```java
public class Seq<T> {

  private final List<Object> list ;
  private final int size;
  private final Function<? super Object,? extends T> function;

  private Seq(List<?> lst,Function<Object,T> function) {
    ...
  }
  
  @SuppressWarnings("unchecked")
  public static <E> Seq<E> from(List<? extends E> lst) {
    ...
  }

  public Integer size() {
    ...
  }
  
  public T get(int i) {
    ...
  }

  
  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <E>Seq<E> of(E... values) {
    ...
  }
  
  public void forEach(Consumer<? super T>consumer) {
    ...
  }
  
  public <E>Seq<E> map(Function<? super T,? extends E> functions) {
    ...
  }

  public Optional<T> findFirst() {
    return list.isEmpty()? Optional.empty(): Optional.ofNullable(get(0));
  }
  
  @Override
  public String toString() {
    ...
  }
}
```
On verifie que la liste dans le Seq est non vide pour savoir si celui ci existe si oui on renvoye un ``get`` du premier element dans un ``Optional.ofNullable``
sinon on renvoi un ``Optional.empty``

> #### Q7
> Faire en sorte que l'on puisse utiliser la boucle for-each-in sur un Seq

- Seq.java
```java
public class Seq<T> implements Iterable<T>{

  private final List<Object> list ;
  private final int size;
  private final Function<? super Object,? extends T> function;

  private Seq(List<?> lst,Function<Object,T> function) {
    ...
  }
  
  @SuppressWarnings("unchecked")
  public static <E> Seq<E> from(List<? extends E> lst) {
    ...
  }

  public Integer size() {
    ...
  }
  
  public T get(int i) {
    ...
  }

  
  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <E>Seq<E> of(E... values) {
    ...
  }
  
  public void forEach(Consumer<? super T>consumer) {
    ...
  }
  
  public <E>Seq<E> map(Function<? super T,? extends E> functions) {
    ...
  }
  
  public Optional<T> findFirst() {
    ...
  }
  
  public Iterator<T> iterator() {
    return new Iterator<>() {
      private int count =0;
      @Override
      public boolean hasNext() {
        return count < size();
      }

      @Override
      public T next() {
        if(!hasNext()) {
          throw new NoSuchElementException();
        }
        var elem = count;
        count++;
        return function.apply(list.get(elem));
      }
    };
  }
  
  @Override
  public String toString() {
    ...
  }


}

```
On doit utiliser un iterator pour cela et donc implémenter in iterable sur la classe ayant le type de la classe

> #### Q8
> Enfin, on souhaite implanter la méthode ``stream()`` qui renvoie un Stream des éléments du ``Seq``. Pour cela, on va commencer par implanter un Spliterator que l'on peut construire à partir du Spliterator déjà existant de la liste (que l'on obtient avec la méthode List.spliterator()).
Puis en utilisant la méthode ``StreamSupport.stream``, créer un Stream à partir de ce Spliterator.

- Seq.java
```java

public class Seq<T> implements Iterable<T>{

  private final List<Object> list ;
  private final int size;
  private final Function<? super Object,? extends T> function;


  private Seq(List<?> lst,Function<Object,T> function) {
    ...
  }
  
  @SuppressWarnings("unchecked")
  public static <E> Seq<E> from(List<? extends E> lst) {
    ...
  }

  public Integer size() {
    ...
  }
  
  public T get(int i) {
    ...
  }

  
  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <E>Seq<E> of(E... values) {
    ...
  }
  
  public void forEach(Consumer<? super T>consumer) {
    ...
  }
  
  public <E>Seq<E> map(Function<? super T,? extends E> functions) {
    ...
  }
  
  public Optional<T> findFirst() {
    ...
  }
  
  public Iterator<T> iterator() {
    return new Iterator<>() {
      ...
      }

      @Override
      public T next() {
        ...
      }
    };
  }
  
  @Override
  public String toString() {
    return list.stream().map(e -> function.apply(e).toString()).collect(Collectors.joining(", ","<",">"));
  }

  public Stream<T> stream() {
    return StreamSupport.stream(
        new Spliterator<T>() {
      private Iterator<T> iter = iterator();

      @Override
      public boolean tryAdvance(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        if(! iter.hasNext()) {
          return false;
        }
        action.accept(iter.next());
        return true;
      }

      @Override
      public Spliterator<T> trySplit() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public long estimateSize() {
        return size();
      }

      @Override
      public int characteristics() {
        return IMMUTABLE | ORDERED | NONNULL;
      }
    }
    , false);
  }
}
```
j'ai un petit problème avec le compilateur qui me donne une errreur sur un test que mes camarades n'ont pas
l'erreur me dit ``The method assertEquals(long,long) is ambiguous for the type SeqTest.Q8`` mais je n'ai pas trouvé la raison pourquoi

