# TP6 Implantation d'une table de hachage, classe interne.
###### LY-IENG Steven

## Exercice 2 - IntHashSet

> #### Q1
> Quels doivent être les champs de la classe ``Entry`` correspondant à une case de la table de hachage sachant que l'on veut stocker les collisions dans une liste chaînée (que l'on va fabriquer nous-même, et pas en utilisant LinkedList).

Les champs de la classe ``Entry`` sont la valeur de l'element et un pointeur vers l'element suivant
> On cherche à écrire la classe Java ``Entry`` correspondante dans le package ``fr.umlv.set.``<br> Quelle doit être la visibilité de cette classe ?<br> 
Quels doivent être les modificateurs pour chaque champ ?<br> En fait, ne pourrait-on pas utiliser un record plutôt qu'une classe, ici ? Pourquoi ?

La visibilité de la classe ``Entry`` doit etre private<br> 
On peut utiliser un record à la place d'une classe ici car on cherche uniquement à stocker des données dans la classe.
> Pour finir, il vaut mieux déclarer Entry en tant que classe interne de la classe IntHashSet plutôt qu'en tant que type dans le package ``fr.umlv.set.``
<br>Pourquoi ? <br>Quelle doit être la visibilité de Entry ?

La visibilité de ``Entry`` doit être private
> Écrire la classe IntHashSet dans le package fr.umlv.set et ajouter Entry en tant que classe interne

- IntHashSet.java
```Java
public class IntHashSet {

  private final Entry[] array;
  private record Entry(int val,Entry next) {
    
  }
}
```
Comme on veut que ``Entry`` se comporte comme une liste chainée on doit lui donner en parametre la valeur et l'element suivant sur lequel pointer.<br>
Aussi comme ``Entry`` est une liste, j'ai décidé de créér un champ array prennant ayant pour type ``Entry`` qu'on utilisera probablement avec les methodes suivantes.

> #### Q2
> Comment écrire la fonction de hachage dans le cas où la taille de la table est 2 ? Pourquoi est-ce que ça n'est pas une bonne idée d'utiliser l'opérateur % ? Écrire une version "rapide" de la fonction de hachage<br>
Indice : on peut regarder le bit le plus à droite (celui des unités) pour savoir si l'on doit stocker les éléments dans la case 0 ou 1.<br>
Et si la taille de la table est 8 ?<br>
En suivant la même idée, modifier votre fonction de hachage dans le cas où la taille de la table est une puissance de 2.


- IntHashSet.java
```java
public class IntHashSet {
  private static int SIZE=2;
  private final Entry[] array;
  private record Entry(int value,Entry next) {

  }
  
  private int hash(int value) {
    return value & (SIZE-1);
  }
}
```
En suivant la methode indiqué en indice j'ai fait ca

> #### Q3
> Dans la classe IntHashSet, implanter la méthode add.
Écrire également la méthode size avec une implantation en O(1).

 - IntHashSet.java
 ```java
public class IntHashSet {
  private static int SIZE=2;
  private int size;
  private final Entry[] array;
  
  public IntHashSet() {
    this.array = new Entry[SIZE];
    this.size = 0;
  }
  private record Entry(int value,Entry next) {
    
  }
  
  private int hash(int value) {
    ...
  }
  
  public void add(int value) {
   var hashvalue = hash(value);
   Entry element;
   for(element=array[hashvalue] ; element!=null;element=element.next) {
      if(element.value==value) {
        return;
      }
    }
    this.size++;
    array[hashvalue]= new Entry(value,array[hashvalue]);
    
  }

  public int size() {
    return size;
  }
}

 ```

 > #### Q4
 > On cherche maintenant à implanter la méthode forEach. Quelle doit être la signature de la functional interface prise en paramètre de la méthode forEach ?
Quel est le nom de la classe du package java.util.function qui a une méthode ayant la même signature ?

- IntHashSet.java
```java
public class IntHashSet {
  private static int SIZE=2;
  private int size;
  private Entry[] array;
  
  public IntHashSet() {
    ...
  }
  private record Entry(int value,Entry next) {
    
  }
  
  private int hash(int value) {
    ...
  }
  
  public void add(int value) {
   ...
    
  }

  public int size() {
   ...
  }

  public void forEach(IntConsumer object) {
    Objects.requireNonNull(object);
    for(var i=0; i < SIZE; i++) {
      for(var element = array[i]; element!= null;element=element.next) {
        object.accept(element.value);
      }
    }
    
  }
}
```
Pour chaque element du tableau et pour chaque element de la table de hachage de chaque element du tableau on va appliquer le consumer dessus

> #### Q5
> Ecrire la methode ``contains``

 - IntHashSet.java
```java
public class IntHashSet {
  private static int SIZE=2;
  private int size;
  private final Entry[] array;
  
  public IntHashSet() {
    ...
  }
  private record Entry(int value,Entry next) {
    
  }
  
  private int hash(int value) {
    ...
  }
  
  public void add(int value) {
    ...
  }

  public int size() {
    ...
  }

  public void forEach(IntConsumer object) {
    ...
  }

  public boolean contains(int value) {
  for(var element = array[hash(value)];element!=null;element=element.next) {
    if(element.value == value) {
      return true;
    }
  }
  return false;
  }
}
```
Pour chaque element du hash on verifie si il est pareil que l'element que l'on a.

> ## Exercice 3 DynamicHashSet

> #### Q1
> Avant tout, nous souhaitons générifier notre table de hachage, pour permettre de ne pas stocker uniquement des entiers mais n'importe quel type de valeur.<br>
Avant de générifier votre code, quelle est le problème avec la création de tableau ayant pour type un type paramétré ?<br>

Il nous faut specifier le type lors de la creation de la variable au lieu d'utiliser ``var`` et on doit initialier directement les champs.
> Comment fait-on pour résoudre le problème, même si cela lève un warning.<br>

On fait un cast

> Rappeler pourquoi on a ce warning.<br>

Le compilateur pense que l'on fait un cast non sur

> Peut-on supprimer le warning ici, ou est-ce une bêtise ?<br>

Oui on peut supprimer le warning

> Comment fait-on pour supprimer le warning ?<br>

On place le ``@SuppressWarnings("unchecked")`` juste avant l'element qui possede le warning et non sur le constructeur ou sur la classe

- DynamicHashSet.java
```Java
public class DynamicHashSet<T> {
  private static int SIZE =2;
  private int size;
  private Entry<T>[] array;
  

  public DynamicHashSet(){
    @SuppressWarnings("unchecked")
    Entry<T>[] array = (Entry<T>[])new Entry[SIZE];
    this.array=array;
    this.size=0;
  }
  
  private record Entry<U>(U value,Entry<U> next) {
    
  }
  
  public int hash(Object value) {
    Objects.requireNonNull(value);
    return value.hashCode() & (SIZE-1);
  }
  
  public void add(T obj) {
    Objects.requireNonNull(obj);
    var hashValue = hash(obj);
    Entry<T> element;
    for(element=array[hashValue] ; element!=null;element=element.next) {
      if(element.value.equals(obj)) {
        return;
      }
    }
    this.size++;
    array[hashValue]=new Entry<T>(obj,array[hashValue]);
  }

  public int size() {
    return size;
  }

  public void forEach(Consumer<T> object) {
    Objects.requireNonNull(object);
    for(var i=0; i < SIZE; i++) {
      for(var element = array[i]; element!= null;element=element.next) {
        object.accept(element.value);
      }
    }
  }

  public boolean contains(Object value) {
    for(var element = array[hash(value)];element!=null;element=element.next) {
      if(element.value.equals(value)) {
        return true;
      }
    }
    return false;
    }
}

```
J'ai fais ce qui etait demandé j'ai donc du changer le IntConsumer en Consumer<T> et pour le contains j'ai du changer le parametre en Object

> #### Q2
> Vérifier la signature de la méthode contains de HashSet et expliquer pourquoi on utilise un type plus général que E.

Si on met un type parametre au lieu d'un object ``set.contains(element)`` ne compilera pas c'est pour cela que l'on utilisera  Object

> #### Q3
> Modifier le code de la méthode add pour implanter l'algorithme d'agrandissement de la table.

```java
public class DynamicHashSet<T> {
  private static int SIZE =2;
  private int size;
  private Entry<T>[] array;
  

  public DynamicHashSet(){
    ...
  }
  
  private record Entry<U>(U value,Entry<U> next) {
    
  }
  
  public int hash(Object value) {
    ...
  }
  
  public void add(T obj) {
    Objects.requireNonNull(obj);
    var hashValue = hash(obj);
    Entry<T> element;
    for(element=array[hashValue] ; element!=null;element=element.next) {
      if(element.value.equals(obj)) {
        return;
      }
    }
    this.size++;
    if(size>=SIZE/2) {
      SIZE = SIZE*2;
      @SuppressWarnings("unchecked")
      var array2 = (Entry<T>[])new Entry[SIZE];
      for(var i=0; i < SIZE/2; i++) {
        for(var el = array[i]; el!= null;el=el.next) {
         array2[hash(el)] = new Entry<T>(el.value,array2[hash(el)] );
        }
      }
      this.array=array2;
    }
    array[hashValue]=new Entry<T>(obj,array[hashValue]);
  }

  public int size() {
    ...
  }

  public void forEach(Consumer<T> object) {
    ...
  }

  public boolean contains(Object value) {
    ...
  }
}
```
On augmente d'abord la taille par 2 puis on stocke notre array actuelle dans une nouvelle array qui contient la bonne taille puis on stocke cet array dans notre array de base