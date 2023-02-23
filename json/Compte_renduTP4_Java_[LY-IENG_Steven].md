# TP4 JSON, reflexions et annotations
###### LY-IENG Steven

## Exercice 2
> #### Q1
> Écrire la méthode toJSON qui prend en paramètre un ``java.lang.Record``, utilise la réflexion pour accéder à l'ensemble des composants d'un record ``(java.lang.Class.getRecordComponent)``, sélectionne les accesseurs, puis affiche les couples nom du composant, valeur associée.
- JSONPrinter.java
```java
public class JSONPrinter {

  private static String escape(Object o) {
    return o instanceof String s ? "\"" + s + "\"": "" + o;
  }
  
  public static String toJSON(Object obj) {
    return Arrays.stream(obj.getClass().getRecordComponents())
        .map(component -> {
          try {
            return " " + escape(component.getName()) + ": " + escape(component.getAccessor().invoke(obj));
          } catch (InvocationTargetException | IllegalAccessException er) {
            var cause = er.getCause();
            if (cause instanceof RuntimeException exception) {
              throw exception;
            }
            if (cause instanceof Error error) {
              throw error;
            }
            throw new UndeclaredThrowableException(er);
          }
        }
      ).collect(Collectors.joining(",\n","{\n","\n}"));
    
  }
}
```
ou
```java
public class JSONPrinter {
  
  private static String escape(Object o) {
    return o instanceof String s ? "\"" + s + "\"": "" + o;
  }
  
  public static String toJSON(Object obj) {
    return Arrays.stream(obj.getClass().getRecordComponents())
        .map(component ->  escape(component.getName()) + ": " + escape(invokeBis(component.getAccessor(),obj)))
        .collect(Collectors.joining(",\n","{\n","\n}"));
  }     

  private static Object invokeBis(Method met, Object rec) {
    try {
    return met.invoke(rec);
    } catch (InvocationTargetException | IllegalAccessException er) {
      var cause = er.getCause();
      if (cause instanceof RuntimeException exception) {
        throw exception;
      }
      if (cause instanceof Error error) {
        throw error;
      }
      throw new UndeclaredThrowableException(er);
    }
  }
}
```
On utilise donc un stream qui prend les composants du record et affiches les champs et a l'aide d'invoke et les accesseurs on affiche les valeurs des champs.<br>
On peut faire l'un ou l'autre code, cela ne change que le visuel cependant ici on va préferer utiliser le deuxième code pour plus de lisibilité.

> #### Q2
> En fait, on peut avoir des noms de clé d'objet JSON qui ne sont pas des noms valides en Java, par exemple "book-title", pour cela on se propose d'utiliser un annotation pour indiquer quel doit être le nom de clé utilisé pour générer le JSON.
- JSONPrinter
```java
public class JSONPrinter {
  
  private static String escape(Object o) {
    ...
  }
  
  public static String toJSON(Object obj) {
    return Arrays.stream(obj.getClass().getRecordComponents())
        .map(component ->  escape(getNameBis(component)) + ": " + escape(invokeBis(component.getAccessor(),obj)))
        .collect(Collectors.joining(",\n","{\n","\n}"));
  }     
          
  private static String getNameBis(RecordComponent component) {
    return component.getAccessor().isAnnotationPresent(JSONProperty.class)? component.getAccessor().getAnnotation(JSONProperty.class).value() :  component.getName();
  }

  private static Object invokeBis(Method met, Object rec) {
  ...
  }
}
```

- JSONProperty.java
```java
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface JSONProperty {
  String value();
}
```
ici comme c'est marqué dans le cours on va creer une annotation JSONProperty 
Mais du coup pour afficher la classe on doit changer le getName qu'on avait précedement par une Méthode que j'aurai nomé getNameBis qui verifie si il y a une annotation,
si oui on renvera la valeur de l'annotation donc la ``classe-nomDeChamp`` sinon on renvoie juste le nom du champ

> #### Q3
> En fait, on veut aussi gérer le fait que l'annotation peut ne pas être présente et aussi le fait que si l'annotation est présente mais sans valeur spécifiée alors le nom du composant est utilisé avec les '_' réécrits en '-'.
Modifier le code dans JSONPrinter et la déclaration de l'annotation en conséquence.

- JSONProperty.java
```java
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface JSONProperty {
  String value() default "_";
}
```
- JSONPrinter.java
```java
public class JSONPrinter {
  
  
  private static String escape(Object o) {
  ...
  }
  
  public static String toJSON(Object obj) {
  ...
  }     
          
  private static String getNameBis(RecordComponent component) { 
    return component.getAccessor().isAnnotationPresent(JSONProperty.class)? 
        component.getAccessor().getAnnotation(JSONProperty.class).value().equals("_")? 
            component.getName().replace("_", "-") : component.getAccessor().getAnnotation(JSONProperty.class).value() :component.getName();
  }
  
  private static Object invokeBis(Method met, Object rec) {
  ...
  }
}
```


> #### Q4
> En fait, l'appel à ``getRecordComponents`` est lent ; regardez la signature de cette méthode et expliquez pourquoi...

La raison pour laquelle l'appel à ``getRecordComponents`` est lente en regardant le code,
il y a beaucoup de condition et de sécurités dans le code, de plus on recupere tout les elements du record mais aussi 
on passe aussi par plusieurs sous methodes ce qui ralentirais la methode.

> #### Q5
> Nous allons donc limiter les appels à ``getRecordComponents`` en stockant le résultat de ``getRecordComponents`` dans un cache pour éviter de faire l'appel à chaque fois qu'on utilise toJSON.
Utilisez la classe ``java.lang.ClassValue`` pour mettre en cache le résultat d'un appel à ``getRecordComponents`` pour une classe donnée.
- JSONPrinter.java
```java
public class JSONPrinter {
  
  private final static ClassValue<RecordComponent[]> cache = new ClassValue<RecordComponent[]>() {

    @Override
    protected RecordComponent[] computeValue(Class<?> type) {
      return type.getRecordComponents();
    }};
  
  private static String escape(Object o) {
    ...
  }
  
  public static String toJSON(Object obj) {
    return Arrays.stream(cache.get(obj.getClass()))
        .map(component ->  escape(getNameBis(component)) + ": " + escape(invokeBis(component.getAccessor(),obj)))
        .collect(Collectors.joining(",\n","{\n","\n}"));
  }     
          
  private static String getNameBis(RecordComponent component) {  
    ...
  }
  
  private static Object invokeBis(Method met, Object rec) {
  ...
  }
}
```
Nous allons creer une classe vide ClassValue qui prend des RecordComponent, on insere les records component dans la classe
(la classe agit un peu comme une map)
et on get le record dans la ClassValue

> #### Q6
> En fait, on peut mettre en cache plus d'informations que juste les méthodes, on peut aussi pré-calculer le nom des propriétés pour éviter d'accéder aux annotations à chaque appel.
Écrire le code qui pré-calcule le maximum de choses pour que l'appel à toJSON soit le plus efficace possible.