# TP3 Slice of bread
###### LY-IENG Steven

## Exercice 2

> #### Q1 
> On va dans un premier temps créer une interface Slice avec une méthode array qui permet de créer un slice à partir d'un tableau en Java.
>```java
>    String[] array = new String[] { "foo", "bar" };
>    Slice<String> slice = Slice.array(array);
>```
> ---
 - Slice.java
```java
public sealed interface Slice<E> permits ArraySlice<E>{

  public final class ArraySlice<E> implements Slice<E>{

    private final E[] list;
		
    private ArraySlice(E[] obj) {
      list = obj;
    }
		
    @Override
    public int size() {
      return list.length;	
    }
    
    @Override
		public E get(int a) {
      return list[a];
      }
	}
	
  public static <E> Slice<E> array(E[] array) {
    Objects.requireNonNull(array);
    return new ArraySlice<E>(array);
  }
	
  int size();
  E get(int a);
}
```

> #### Q2
> On souhaite que l'affichage d'un slice affiche les valeurs séparées par des virgules avec un '[' et un ']' comme préfixe et suffixe.
> Par exemple,
>```java
>      var array = new String[] { "foo", "bar" };
>      var slice = Slice.array(array);
>      System.out.println(slice);   // [foo, bar]
>```
> ---

 - Slice.java
```java
public sealed interface Slice<E> permits ArraySlice<E>{

  public final class ArraySlice<E> implements Slice<E>{
    
    ...

    @Override
    public String toString() {
      return Arrays.stream(list).map(e -> String.valueOf(e)).collect(Collectors.joining(", ","[","]")) ;
    }
  }

  ...

}
J'ai fais un stream avec un map(e ->String.valueOf(e)), j'avais essayé avec map(e -> e.toString) mais cela n'a pas marché,
car il y avait un problème avec null

```
> #### Q3
> On souhaite ajouter une surcharge à la méthode array qui, en plus de prendre le tableau en paramètre, prend deux indices from et to et montre les éléments du tableau entre from inclus et to exclus.
> Par exemple
>```java
>       String[] array = new String[] { "foo", "bar", "baz", "whizz" };
>       Slice<String> slice = Slice.array(array, 1, 3);
>```
> ---

 - Slice.java

```java
public sealed interface Slice<E> permits ArraySlice<E>,SubArraySlice<E>{

  ...
  
    public final class SubArraySlice<E> implements Slice<E>{
    
    private final E[] list;
    private final int fromList;
    private final int toList;
    
    private SubArraySlice(E[] obj,int from, int to) {
      Objects.requireNonNull(obj);
      Objects.checkFromToIndex(from, to,obj.length);
      list = obj;
      fromList = from;
      toList = to;
    }
    
    @Override
    public int size() {
      return toList - fromList; 
    }
    
    @Override
    public E get(int a) {
      Objects.checkIndex(a, size());
      return list[a+fromList];
      }
    @Override
    public String toString() {
      return Arrays.stream(list,fromList,toList).map(e -> String.valueOf(e)).collect(Collectors.joining(", ","[","]")) ;
    }
  }

  ...
  
  public static <E> Slice<E> array(E[] array, int from, int to){
    Objects.requireNonNull(array);
    return new SubArraySlice<E>(array,from,to);
  }
  
  ...

}
```
La surcharge de array fait la meme chose que le array précedent mais avec la nouvelle classe SubArraySlice
Il faut des checkIndex pour eviter des out of bound et un checkFromToIndex

> #### Q4
> On souhaite enfin ajouter une méthode subSlice(from, to) à l'interface Slice qui renvoie un sous-slice restreint aux valeurs entre from inclus et to exclu.
> Par exemple,
>```java
>        String[] array = new String[] { "foo", "bar", "baz", "whizz" };
>        Slice<String> slice = Slice.array(array);
>        Slice<String> slice2 = slice.subSlice(1, 3);
>```
> ---

 - Slice.java

```java
public sealed interface Slice<E> permits ArraySlice<E>,SubArraySlice<E>{

  public final class ArraySlice<E> implements Slice<E>{

		...

    public Slice<E> subSlice(int from,int to){
      return new SubArraySlice<>(list, from, to); 
    }
    
    ...
 
  }
  
  public final class SubArraySlice<E> implements Slice<E>{
    ...
    
    private SubArraySlice(E[] obj,int from, int to) {
      Objects.checkFromToIndex(from, to,obj.length);
      list = obj;
      fromList = from;
      toList = to;
    }
    
    ...
    
    @Override
    public E get(int a) {
      Objects.checkIndex(a, size());
      return list[a+fromList];
      }
    
    public Slice<E> subSlice(int from,int to){
      Objects.checkFromToIndex(from, to, size());
      return new SubArraySlice(list, from+this.fromList, to+this.fromList);
    }
    
    ...

  }
  
  ...

  Slice<E> subSlice(int from,int to);
  
  ...
  
}
```
---
## Exercice 3

> #### Q1
> Recopier l'interface Slice de l'exercice précédent dans une interface Slice2. Vous pouvez faire un copier-coller de Slice dans même package, votre IDE devrait vous proposer de renommer la copie. Puis supprimer la classe interne SubArraySlice ainsi que la méthode array(array, from, to) car nous allons les réimplanter et commenter la méthode subSlice(from, to) de l'interface, car nous allons la ré-implanter aussi, mais plus tard.
- Slice2.java
```java
public sealed interface Slice2<E> permits ArraySlice<E>{

  public final class ArraySlice<E> implements Slice2<E>{

    private final E[] list;
    
    private ArraySlice(E[] obj) {
      list = obj;
    }
    
    @Override
    public int size() {
      return list.length; 
    }
    
    @Override
    public E get(int a) {
      return list[a];
      }
    
    /*public Slice2<E> subSlice(int from,int to){
      return new SubArraySlice<>(list, from, to); 
    }*/
    @Override
    public String toString() {
      return Arrays.stream(list).map(e -> String.valueOf(e)).collect(Collectors.joining(", ","[","]")) ;
    }
  }
  
  public static <E> Slice2<E> array(E[] array) {
    Objects.requireNonNull(array);
    return new ArraySlice<E>(array);
  }
  
  //Slice2<E> subSlice(int from,int to);
  int size();
  E get(int a);
}
```

> #### Q2 
> Déclarer une classe SubArraySlice à l'intérieur de la classe ArraySlice comme une inner class donc pas comme une classe statique et implanter cette classe et la méthode array(array, from, to).

 - Slice2.java

```java
public sealed interface Slice2<E> permits Slice2.ArraySlice<E>,Slice2.ArraySlice<E>.SubArraySlice{

  public final class ArraySlice<E> implements Slice2<E>{
       
    public final class SubArraySlice implements Slice2<E>{
      private final int fromList;
      private final int toList;
      
      
      private SubArraySlice(int from, int to) {
        fromList = from;
        toList = to;
      }
      
      @Override
      public int size() {
        return toList - fromList; 
      }
      
      @Override
      public E get(int a) {
        Objects.checkIndex(a, size());
        return list[a+fromList];
        }
      
      @Override
      public String toString() {
        return Arrays.stream(list,fromList,toList).map(e -> String.valueOf(e)).collect(Collectors.joining(", ","[","]")) ;
      }
    }
    
   ...

  }

  ...
  
  public static <E> Slice2<E> array(E[] array, int from, int to){
    Objects.requireNonNull(array);
    Objects.checkFromToIndex(from, to, array.length);
    return new ArraySlice<E>(array).new SubArraySlice(from,to);
  }
  
  ...

}



...

}
```

On fait la meme chose que pour l'exercice 1 mais, le array se trouve dans le ArraySlice et la classe SubArraySlice est dans ArraySlice,
Donc la methode array doit retourner un element passant par les deux classes.
Aussi L'array est passé dans la inner class via la class ArraySlice donc on a pas besoin de lui donner un champ ni l'utiliser dans le constructeur

> #### Q3
> Dé-commenter la méthode subSlice(from, to) de l'interface et fournissez une implantation de cette méthode dans les classes ArraySlice et SubArraySlice.
> On peut noter qu'il est désormais possible de simplifier le code de array(array, from, to).

Slice2.java
```java
public sealed interface Slice2<E> permits Slice2.ArraySlice<E>,Slice2.ArraySlice<E>.SubArraySlice{

  public final class ArraySlice<E> implements Slice2<E>{
       
    public final class SubArraySlice implements Slice2<E>{
      
      ...

      public Slice2<E> subSlice(int from,int to){
        Objects.checkFromToIndex(from, to, size());
        return new ArraySlice<E>(list).new SubArraySlice( from+this.fromList, to+this.fromList);
      }
      
     ...

    }

    ...
    
    public Slice2<E> subSlice(int from,int to){
      return Slice2.array(list, from, to); 
    }

    ...

  }

  ...
  
  Slice2<E> subSlice(int from,int to);
  
  ...

}
```
On a le meme changement pour subSlice que pour array on doit appeler les deux classes pour une methode interne

> #### Q4
> Dans quel cas va-t-on utiliser une inner class plutôt qu'une classe interne ?
Lorque l'on veut uniquement definir des methodes internes

---
## Exercice 4

> #### Q1
> Recopier l'interface Slice du premier exercice dans une interface Slice3. Supprimer la classe interne SubArraySlice ainsi que la méthode array(array, from, to) car nous allons les réimplanter et commenter la méthode subSlice(from, to) de l'interface, car nous allons la réimplanter plus tard.
> Puis déplacer la classe ArraySlice à l'intérieur de la méthode array(array) et transformer celle-ci en classe anonyme.

Slice3.java
```java

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
        return array[a];
        }

      @Override
      public String toString() {
        return Arrays.stream(array).map(e -> String.valueOf(e)).collect(Collectors.joining(", ","[","]")) ;
      }
    };
  }

  int size();
  E get(int a);
}

```
Pour la classe anonyme on renvoye l'interface avec les methodes implémenté dans la classe

> #### Q2
> On va maintenant chercher à implanter la méthode subSlice(from, to) directement dans l'interface Slice3. Ainsi, l'implantation sera partagée.
> Écrire la méthode subSlice(from, to) en utilisant là encore une classe anonyme.
> Comme l'implantation est dans l'interface, on n'a pas accès au tableau qui n'existe que dans l'implantation donnée dans la méthode array(array)... mais ce n'est pas grave, car on peut utiliser les méthodes de l'interface.
> Puis fournissez une implantation à la méthode array(array, from, to).

```java

public interface Slice3<E>{
  
  public static <E> Slice3<E> array(E[] array) {
    ...
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
  
  ...
  
  static <E>Slice3<E> array(E[] array,int from,int to){
    Objects.requireNonNull(array);
    Objects.checkFromToIndex(from, to, array.length);
    return array(array).subSlice(from, to);
  }
}
```
Pour avoir subSlice on fait la meme chose  que pour array, c'est a dire utiliser un classe anonyme.
Cependant on n'utilise plus d'array dans subSlice mais tout est utilisé implicitement avec les this, on a pas besoin d'utiliser l'array 

> #### Q3
> Dans quel cas va-t-on utiliser une classe anonyme plutôt qu'une classe interne ?
On va utiliser une classe anonyme plutôt qu'une classe interne pour rendre le code plus concis