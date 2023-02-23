# TP8 Faites la queue
###### LY-IENG Steven

## Exercice 2 - Times Series

> #### Q1
>Dans un premier temps, on va créer une classe TimeSeries ainsi qu'un record Data à l'intérieur de la classe TimeSeries qui représente une paire contenant une valeur de temps (timestamp) et un élément (element).
Le record Data est paramétré par le type de l'élément qu'il contient.

- TimeSeries.java
```java
public class TimeSeries {

  public record Data<T>(long timestamp ,T element){
    
    public Data{
      Objects.requireNonNull(element);
    }
  }
}
```
On ne doit pas oublier de faire les requireNonNull sur les deux elements

> #### Q2
> On souhaite maintenant écrire les méthodes dans TimeSeries :
> - add(timestamp, element) qui permet d'ajouter un élément avec son timestamp.
> - La valeur de timestamp doit toujours être supérieure ou égale à la valeur du timestamp précédemment inséré (s'il existe).
> - size qui renvoie le nombre d'éléments ajoutés.
> - get(index) qui renvoie l'objet Data se trouvant à la position indiquée par l'index (de 0 à size - 1).

- TimeSeries.java
```java
public class TimeSeries<E> {
  
  private final ArrayList<Data<E>> datas = new ArrayList<>();
  private long LastTimeStamp;
  
  public record Data<T>(long timestamp ,T element){
    
    public Data{
      ...
    }
  }
  
  public void add(long timestamp, E element) {
    Objects.requireNonNull(element);
    if(datas.isEmpty()) {
      this.LastTimeStamp = timestamp;
    }
    if(this.LastTimeStamp > timestamp) {
      throw new IllegalStateException();
    }
    datas.add(new Data<E>(timestamp,element));
    this.LastTimeStamp = timestamp;
  }
  
  public int size() {
    return datas.size();
  }
  
  public Data<E> get(int index) {
   return datas.get(index);
  }
}

```
Pour le add j'ai essayé d'utiliser directement avec size mais j'ai vu que le compilateur ne voulait pas et me renvoyait toujours des ``index i out of bound i``
du coup j'ai crée un champ ``LastTimeStamp`` qui contient le dernier Long mis en date, si l'array est vide on remplace le ``LastTimeStamp`` par celui actuel pour eviter que 
lorsque l'on va comparer que le compilateur rale.

> #### Q3
> On souhaite maintenant créer une classe interne publique Index ainsi qu'une méthode index permettant de créer un Index stockant les indices des données de la TimeSeries sur laquelle la méthode index est appelée. L'objectif est de pouvoir ensuite accéder aux Data correspondantes dans le TimeSeries. Un Index possède une méthode size indiquant combien d'indices il contient.<br>
>Seuls les indices des éléments ajoutés avant l'appel à la méthode ``index()`` doivent être présents dans l'Index.
En interne, un Index stocke un tableau d'entiers correspondants à chaque indice.

- TimeSeries.java

```java
public class TimeSeries<E> {
  
  private final ArrayList<Data<E>> datas = new ArrayList<>();
  private long LastTimeStamp;
  
  public record Data<T>(long timestamp ,T element){
    
    public Data{
      ...
    }
  }
  
  public class Index{
    private int [] indexes;
    
    private Index(int [] indexes) {
      Objects.requireNonNull(indexes);
      this.indexes = indexes;
    }
    
    public int size() {
      return indexes.length;
    }
    
  }
  
  public void add(long timestamp, E element) {
    ...
  }
  
  public int size() {
    ...
  }
  
  public Data<E> get(int index) {
   ...
  }
  
  public Index index() {
    return new Index(IntStream.range(0,size()).toArray());
  }
}
```
Comme dans les test de la question 3 il y avait un test demandant a ce que le constructeur ne soit pas public j'ai mis en private

> #### Q4
> On souhaite pouvoir afficher un Index, c'est à dire afficher les éléments (avec le timestamp) référencés par un Index, un par ligne avec un pipe (|) entre le timestamp et l'élément.

- TimeSeries.java
```java
public class TimeSeries<E> {
  
  private final ArrayList<Data<E>> datas = new ArrayList<>();
  private long LastTimeStamp;
  
  public record Data<T>(long timestamp ,T element){
    
    public Data{
      ...
    }
    
    public String toString() {
      return timestamp + " | " + element;
    }
  }
  
  public class Index{
    private int [] indexes;
    
    private Index(int [] indexes) {
      ...
    }
    
    public int size() {
      ...
    }
    
    @Override
    public String toString() {
      return Arrays.stream(ind).mapToObj(e -> datas.get(e).toString()).collect(Collectors.joining("\n"));
    }
  }
  
  public void add(long timestamp, E element) {
    ...
  }
  
  public int size() {
    ...
  }
  
  public Data<E> get(int index) {
    ...
  }
  
  public Index index() {
    ...
  }
}

```
Comme on a des int dans l'index on doit passer par un ``mapToObj`` pour le convertir en objet et pouvoir l'utiliser sur l'ArrayList,
pour se simplifier les choses on fait un ``toString`` dans ``Data`` et un autre ``toString`` dans ``ìndex``

> #### Q5
> On souhaite ajouter une autre méthode ``index(lambda)`` qui prend en paramètre une fonction/lambda qui est appelée sur chaque élément de la TimeSeries et indique si l'élément doit ou non faire partie de l'index.<br>
> Par exemple, avec une TimeSeries contenant les éléments "hello", "time" et "series" et une lambda ``s -> s.charAt(1) == 'e'`` qui renvoie vrai si le deuxième caractère est un 'e', l'Index renvoyé contient [0, 2].<br>
> Quel doit être le type du paramètre de la méthode ``index(lambda)`` ?

Le type du paramètre de la methode ``ìndex(lambda)`` doit être ``Predicate<? super E>``

- TimeSeries.java
```java
public class TimeSeries<E> {
  
  private final ArrayList<Data<E>> datas = new ArrayList<>();
  private long LastTimeStamp;
  
  public record Data<T>(long timestamp ,T element){
    
    public Data{
      ...
    }
    
    public String toString() {
      ...
    }
  }
  
  public class Index{
    private int [] indexes;
    
    private Index(int [] indexes) {
      ...
    }
    
    public int size() {
      ...
    }
    
    @Override
    public String toString() {
      ...
    }
  }
  
  public void add(long timestamp, E element) {
    ...
  }
  
  public int size() {
    ...
  }
  
  public Data<E> get(int index) {
   ...
  }
  
  public Index index() {
    ...
  }
  
  public <T> Index index(Predicate <? super E> element) {
    return new Index(IntStream.range(0, size()).filter( e -> element.test(datas.get(e).element())).toArray());
  }
}
```
Je renvoi un Index en utilisant ``IntStream.range`` et j'utilise un ``filter`` sur le quel sur chaque élément j'applique le prédicat


> #### Q6
> Dans la classe Index, écrire une méthode forEach(lambda) qui prend en paramètre une fonction/lambda qui est appelée avec chaque Data référencée par les indices de l'Index.
Par exemple, avec la TimeSeries contenant les Data (24 | "hello"), (34 | "time") et (70 | "series") et un Index [0, 2], la fonction sera appelée avec les Data (24 | "hello") et (70 | "series").
> Quel doit être le type du paramètre de la méthode forEach(lambda) ?
Le type du paramètre de la methode ``forEach(lambda)``est``Consumer<? super Data<E>>``

- TimeSeries.java
```java
public class TimeSeries<E> {
  
  private final ArrayList<Data<E>> datas = new ArrayList<>();
  private long LastTimeStamp;
  
  public record Data<T>(long timestamp ,T element){
    
    public Data{
      ...
    }
    
    public String toString() {
      ...
    }
  }
  
  public class Index{
    private int [] indexes;
    
    private Index(int [] indexes) {
      ...
    }
    
    public int size() {
      ...
    }
    
    public void forEach(Consumer<? super Data<E>> consumer) {
      Objects.requireNonNull(consumer);
      Arrays.stream(indexes).mapToObj(e->datas.get(e)).forEach(consumer);
    }

    @Override
    public String toString() {
      ...
    }
  }
  
  public void add(long timestamp, E element) {
    ...
  }
  
  public int size() {
    ...
  }
  
  public Data<E> get(int index) {
   ...
  }
  
  public Index index() {
    ...
  }
  
  public <T> Index index(Predicate <? super E> element) {
    ...
  }
}
```
> #### Q7
> On souhaite maintenant pouvoir parcourir tous les Data d'un Index en utilisant une boucle for comme ceci
>```java
>      ...
>      var index = timeSeries.index();
>      for(var data: index) {
>        System.out.println(data);
>      }
>```     
>
> Quelle interface doit implanter la classe Index pour pouvoir être utilisée dans une telle boucle ?<br>
> Quelle méthode de l'interface doit-on implanter ?<br> Et quel est le type de retour de cette méthode ?<br>

- TimeSeries.java
```java
public class TimeSeries<E> {
  
  private final ArrayList<Data<E>> datas = new ArrayList<>();
  private long LastTimeStamp;
  
  public record Data<T>(long timestamp ,T element){
    
    public Data{
      ...
    }
    
    public String toString() {
      ...
    }
  }
  
  public class Index implements Iterable<Data<E>>{
    private int [] indexes;
    
    private Index(int [] indexes) {
      ...
    }
    
    public int size() {
      ...
    }
    
    public void forEach(Consumer<? super Data<E>> consumer) {
      ...
    }

    @Override
    public String toString() {
      ...
    }
    public Iterator<Data<E>> iterator() {
      return new Iterator<Data<E>>() {
        private int cpt =0;
        
        @Override
        public boolean hasNext() {
          return cpt < size();
        }

        @Override
        public Data<E> next() {
          if(!hasNext()) {
            throw new NoSuchElementException();
          }
          var element = cpt;
          cpt++;
          return datas.get(indexes[element]);
        }
        
        
      };
    }
  }
  
  public void add(long timestamp, E element) {
    ...
  }
  
  public int size() {
    ...
  }
  
  public Data<E> get(int index) {
   ...
  }
  
  public Index index() {
    ...
  }
  
  public <T> Index index(Predicate <? super E> element) {
    ...
  }
}
```

> #### Q8
> On veut ajouter une méthode or sur un Index qui prend en paramètre un Index et renvoie un nouvel Index qui contient à la fois les indices de l'Index courant et les indices de l'Index passé en paramètre.<br>
> Il ne doit pas être possible de faire un or avec deux Index issus de TimeSeries différentes.<br>
> En termes d'implantation, on peut faire une implantation en O(n) mais elle est un peu compliquée à écrire. On se propose d'écrire une version en O(n.log(n)) en concaténant les Stream de chaque index puis en triant les indices et en retirant les doublons.<br>
>Expliquer pourquoi on ne peut pas juste concaténer les deux tableaux d'indices ?

On ne peux pas juste concaténer les deux tableaux car les element du nouvel index ne seront pas dans l'ordre et il y a aura possiblilité de doublons dans l'index


- TimeSeries.java

```java
public class TimeSeries<E> {
  
  private final ArrayList<Data<E>> datas = new ArrayList<>();
  private long LastTimeStamp;
  
  public record Data<T>(long timestamp ,T element){
    
    public Data{
      ...
    }
    
    public String toString() {
      ...
    }
  }
  
  public class Index implements Iterable<Data<E>>{
    private int [] indexes;
    private TimeSeries<E> timeSeriesOfThis;
    
    private Index(int [] indexes) {
      ...
    }
    
    public int size() {
      ...
    }
    
    public void forEach(Consumer<? super Data<E>> consumer) {
      ...
    }
    
    @Override
    public String toString() {
      ...
    }

    public Iterator<Data<E>> iterator() {
      return new Iterator<Data<E>>() {
        private int cpt =0;
        
        @Override
        public boolean hasNext() {
          ...
        }

        @Override
        public Data<E> next() {
          ...
        }
      };
    }
    

    
    public Index or (Index index) {
      Objects.requireNonNull(i);
      if(!index.timeSeriesOfThis.equals(this.timeSeriesOfThis)) {
        throw new IllegalArgumentException();
      }
      return new Index(IntStream.concat(Arrays.stream(indexes),Arrays.stream(index.indexes)).distinct().sorted().toArray());
    }
  }
  
  public void add(long timestamp, E element) {
    ...
  }
  
  public int size() {
    ...
  }
  
  public Data<E> get(int index) {
   ...
  }
  
  public Index index() {
    ...
  }
  
  public <T> Index index(Predicate <? super E> element) {
    ...
  }
  
  public TimeSeries<E> thisTimeseries(){
    return this;
  }
  
}

```
Pour se faire on doit d'abord faire un accesseur sur ``TimeSeries`` ce qui va nous permettre de verifier si les index viennent du même TimeSeries
Ensuite on concatenne les indexes  en ajoutant ``sorted`` et ``distinct``


> #### Q9
> Même question que précédemment, mais au lieu de vouloir faire un or, on souhaite faire un and entre deux Index.<br>
> En termes d'implantation, il existe un algorithme en O(n) qui est en O(1) en mémoire. À la place, nous allons utiliser un algorithme en O(n) mais qui utilise O(n) en mémoire. <br>
> L'idée est de prendre un des tableaux d'indices et de stocker tous les indices dans un ensemble sans doublons puis de parcourir l'autre tableau d'indices et de vérifier que chaque indice est bien dans l'ensemble.


- TimeSeries.java

```java
public class TimeSeries<E> {
  
  private final ArrayList<Data<E>> datas = new ArrayList<>();
  private long LastTimeStamp;
  
  public record Data<T>(long timestamp ,T element){
    
    public Data{
      ...
    }
    
    public String toString() {
      ...
    }
  }
  
  public class Index implements Iterable<Data<E>>{
    private int [] indexes;
    private TimeSeries<E> timeSeriesOfThis;
    
    private Index(int [] indexes) {
      ...
    }
    
    public int size() {
      ...
    }
    
    public void forEach(Consumer<? super Data<E>> consumer) {
      ...
    }
    
    @Override
    public String toString() {
      ...
    }

    public Iterator<Data<E>> iterator() {
      return new Iterator<Data<E>>() {
        private int cpt =0;
        
        @Override
        public boolean hasNext() {
          ...
        }

        @Override
        public Data<E> next() {
          ...
        }
      };
    }
    

    
    public Index or (Index index) {
      ...
    }

    public Index and (Index index) {
        Objects.requireNonNull(index);
        if(!index.timeSeriesOfThis.equals(this.timeSeriesOfThis)) {
          throw new IllegalArgumentException();
        }
        return new Index(Arrays.stream(index.indexes).filter(Arrays.stream(indexes).mapToObj(e -> e).collect(Collectors.toSet())::contains).toArray());
      }
  }
  
  public void add(long timestamp, E element) {
    ...
  }
  
  public int size() {
    ...
  }
  
  public Data<E> get(int index) {
   ...
  }
  
  public Index index() {
    ...
  }
  
  public <T> Index index(Predicate <? super E> element) {
    ...
  }
  
  public TimeSeries<E> thisTimeseries(){
    return this;
  }
  
}

```
Dans le meme cas que la question précédente on doit faire des verifications sur les indexes 
ensuite on crée un set avec l'un des index et on filtre l'autre index avec le set précédement fait

> #### Q10
> Pour les plus balèzes, faites en sorte que les tests marqués "Q10" passent.

- TimeSeries.java

```java
public class TimeSeries<E> {
  
  private final ArrayList<Data<E>> datas = new ArrayList<>();
  private long LastTimeStamp;
  
  public record Data<T>(long timestamp ,T element){
    
    public Data{
      ...
    }
    
    public String toString() {
      ...
    }
  }
  
  public class Index implements Iterable<Data<E>>{
    private int [] indexes;
    private TimeSeries<E> timeSeriesOfThis;
    
    private Index(int [] indexes) {
      ...
    }
    
    public int size() {
      ...
    }
    
    public void forEach(Consumer<? super Data<E>> consumer) {
      ...
    }
    
    @Override
    public String toString() {
      ...
    }

    public Iterator<Data<E>> iterator() {
      return new Iterator<Data<E>>() {
        private int cpt =0;
        
        @Override
        public boolean hasNext() {
          ...
        }

        @Override
        public Data<E> next() {
          ...
        }
      };
    }
    
    public Index or (TimeSeries<? extends E>.Index index) {
      Objects.requireNonNull(i);
      if(!index.timeSeriesOfThis.equals(this.timeSeriesOfThis)) {
        throw new IllegalArgumentException();
      }
      return new Index(IntStream.concat(Arrays.stream(indexes),Arrays.stream(index.indexes)).distinct().sorted().toArray());
    }

    public Index and (TimeSeries<? extends E>.Index index) {
        Objects.requireNonNull(index);
        if(!index.timeSeriesOfThis.equals(this.timeSeriesOfThis)) {
          throw new IllegalArgumentException();
        }
        return new Index(Arrays.stream(index.indexes).filter(Arrays.stream(indexes).mapToObj(e -> e).collect(Collectors.toSet())::contains).toArray());
      }

  }
  
  public void add(long timestamp, E element) {
    ...
  }
  
  public int size() {
    ...
  }
  
  public Data<E> get(int index) {
   ...
  }
  
  public Index index() {
    ...
  }
  
  public <T> Index index(Predicate <? super E> element) {
    ...
  }
  
  public TimeSeries<E> thisTimeseries(){
    return this;
  }
  
}

```
En m'aidant du compilateur et de l'autocompletion eclipse m'a demandé de changer le type de paramètre de l'index dans or et and
dans les deux cas j'ai changé avec ``TimeSeries<? extends E>.Index``