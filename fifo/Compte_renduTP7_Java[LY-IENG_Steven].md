# TP6 Faites la queue
###### LY-IENG Steven

## Exercice 2 - Fifo

> #### Q1
> Cette représentation peut poser problème, car si la tête et la queue correspondent au même indice, il n'est pas facile de détecter si cela veux dire que la file est pleine ou vide.
<br>Comment doit-on faire pour détecter si la file est pleine ou vide ?

On doit utiliser deux pointeurs indiquant le début et la fin de la liste et une taille de liste, 
la fin de la liste augmentera lorsque l'on ajoutera un element, 
si on retire un element on augmentera la valeur de l'element pointant sur l'avant de la liste.
La taille de liste augmentera et diminuera selon la situation.

> #### Q2
> Écrire une classe Fifo générique (avec une variable de type E) dans le package fr.uge.fifo prenant en paramètre le nombre maximal d’éléments que peut stocker la structure de données. Pensez à vérifier les préconditions.

- Fifo.java
```java
public class Fifo<E> {

  private int head; // 1e pos libre pour inserer
  private int tail; // 1e pos pour retirer
  private int size = 0;
  private final E[] queue;
  
  public Fifo(int value) {
    if( value <= 0) {
      throw new IllegalArgumentException();
    }
    @SuppressWarnings("unchecked")
    E[] queue = (E[]) new Object[value];
    this.queue = queue;
    this.head = 0;
    this.tail = 0;
  }
}
```
On a mis le tableau en ``private final``, lorsque l'on ajoute des element ils sont toujours dans le type du tableau
et on a aucun accesseur sur le tableau donc je peux faire le supress SuppressWarnings


> #### Q3
> Écrire la méthode offer qui ajoute un élément de type E dans la file. Pensez à vérifier les préconditions sachant que, notamment, on veut interdire le stockage de null.
<br>Comment détecter que la file est pleine ?

On peut voir si la file est pleine si la taille est egale a la longeur de la file
> Que faire si la file est pleine ?

Si la file est pleine on envoi une exception

- Fifo.java

```java
public class Fifo<E> {

  private int head; // 1e pos libre pour inserer
  private int tail; // 1e pos pour retirer
  private int size = 0;
  private final E[] queue;
  
  public Fifo(int value) {
    ...
  }

  public void offer(E obj) {
    Objects.requireNonNull(obj);
    if(size == queue.length) {
      throw new IllegalStateException();
    }
    queue[tail]=obj;
    this.tail = this.tail % queue.length;
    this.size++;
  }
}
```

> #### Q4
> Écrire une méthode poll qui retire un élément de type E de la file. Penser à vérifier les préconditions.
<br>Que faire si la file est vide ?

lorsque la file est vide on envoie une exception

- Fifo.java
```java
public class Fifo<E> {

  private int head; // 1e pos libre pour inserer
  private int tail; // 1e pos pour retirer
  private int size = 0;
  private final E[] queue;
  
  public Fifo(int value) {
    ...
  }

  public void offer(E obj) {
    ...
  }
  
  public E poll() {
    if(size<= 0) {
      throw new IllegalStateException();
    }
    var r = queue[head];
    this.head = (this.head+1) % queue.length;
    this.size--;
    return r;
  }
}
```


> #### Q5
> Ajouter une méthode d'affichage qui affiche les éléments dans l'ordre dans lequel ils seraient sortis en utilisant poll. L'ensemble des éléments devra être affiché entre crochets ('[' et ']') avec les éléments séparés par des virgules (suivies d'un espace).

- Fifo.java

```java
public class Fifo<E> {

  private int head; // 1e pos libre pour inserer
  private int tail; // 1e pos pour retirer
  private int size = 0;
  private final E[] queue;
  
  public Fifo(int value) {
    ...
  }

  public void offer(E obj) {
    ...
  }
  
  public E poll() {
    ...
  }
  
  public String toString() {
    int elem = this.head;
    var joiner = new StringJoiner(", ","[","]");
    for(var i=0 ;i < size; i++) {
      joiner.add(String.valueOf(queue[elem]));
      elem =(elem +1) %queue.length;
    }
    return joiner.toString();
  }
}

```
> #### Q6
> Rappelez ce qu'est un ``memory leak`` en Java et assurez-vous que votre implantation n'a pas ce comportement indésirable.

> #### Q7 
> Ajouter une méthode ``size`` et une méthode ``isEmpty``.

- Fifo.java
```java
package fr.uge.fifo;


import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public class Fifo<E> {

  private int head; // 1e pos libre pour inserer
  private int tail; // 1e pos pour retirer
  private int size = 0;
  private final E[] queue;
  
  public Fifo(int value) {
    ...
  }

  public void offer(E obj) {
    ...
  }
  
  public E poll() {
    ...
  }
  
  public Integer size() {
    return size;
  } 
  
  public boolean isEmpty() {
    return size == 0;
  }
  
  public String toString() {
    ...
  }
}

```

> #### Q8
> Rappelez quel est le principe d'un itérateur.
Quel doit être le type de retour de la méthode iterator() ?

Le type de retour de la methode iterator est un Iterator<E>

> #### Q9
> Implanter la méthode iterator().

```java
public class Fifo<E> {

  private int head; // 1e pos libre pour inserer
  private int tail; // 1e pos pour retirer
  private int size = 0;
  private final E[] queue;
  
  public Fifo(int value) {
    ...
  }

  public void offer(E obj) {
    ...
  }
  
  public E poll() {
    ...
  }
  
  public Integer size() {
    ...
  } 
  
  public boolean isEmpty() {
    ...
  }
  
  public Iterator<E> iterator() {
    return new Iterator<E>(){
      private int cpt = 0;
      private int tmp = head;
      @Override
      public boolean hasNext() {
        return cpt != size;
      }

      @Override
      public E next() {
        if(!hasNext()) {
          throw new NoSuchElementException();
        }
        var q = queue[tmp];
        tmp = (tmp+1) % queue.length;
        cpt++;
        return q;
      }
      
    };
  }
  
  public String toString() {
    ...
  }


}

```

> #### Q10
> Rappeler à quoi sert l'interface Iterable.
Faire en sorte que votre file soit Iterable.

```java
public class Fifo<E> implements Iterable<E>{
    ...
}


```