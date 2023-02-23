# TP1 Rappel de notions de programmation objet
###### LY-IENG Steven
---
## Exercice 2

>#### Q1
>  Écrire le code de VillagePeople tel que l'on puisse créer des VillagePeople avec leur nom et >leur sorte. Par exemple,
>```java
>var lee = new VillagePeople("Lee", Kind.BIKER);
>       System.out.println(lee);  // Lee (BIKER)
>```
> ---


- VillagePeople.java
```java
package fr.uge.ymca;
import java.util.Objects;

public record VillagePeople(String name,Kind kind){
	public VillagePeople{
		Objects.requireNonNull(name);
		Objects.requireNonNull(kind);
	}

	@Override
	public String toString(){
		return name + " (" + kind + ")";
	}
}
```


> #### Q2 
>On veut maintenant introduire une maison House qui va contenir des VillagePeople. 
>Une maison possède une méthode add qui permet d'ajouter un VillagePeople dans la maison 
(note : il est possible d'ajouter plusieurs fois le même). L'affichage d'une maison doit renvoyer le texte "House with" 
suivi des noms des VillagePeople ajoutés à la maison, séparés par une virgule. Dans le cas où une maison est vide, le texte est "Empty House".
>```java
>       var house = new House();
>       System.out.println(house);  // Empty House
>        var david = new VillagePeople("David", Kind.COWBOY);
>        var victor = new VillagePeople("Victor", Kind.COP);
>        house.add(david);
>        house.add(victor);
>        System.out.println(house);  // House with David, Victor
>```
> ---

- House.java
```java
package fr.uge.ymca;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public final class House {
	private final ArrayList<VillagePeople> house;	
	
	public House() {
		house = new ArrayList<VillagePeople>();
	}
	
	public void add(People people) {
		Objects.requireNonNull(Villagepeople);
		house.add(people);
	}

	@Override
	public String toString() {
		if (!house.isEmpty()) {
			return "House with " + house.stream().map(VillagePeople::name).collect(Collectors.joining(", ")).toString();
		}
		return "Empty House";
	}
}
```


>#### Q3
>En fait on veut que l'affichage affiche les noms des VillagePeople dans l'ordre alphabétique, il va donc falloir trier les noms avant de les afficher. On pourrait créer une liste intermédiaire des noms puis les trier avec un appel à list.sort(null) mais cela commence à faire beaucoup de code pour un truc assez simple. Heureusement, il y a plus simple, on va utiliser un Stream pour faire l'affichage.  
Dans un premier temps, ré-écrire le code de l'affichage (commenter l'ancien) pour utiliser un Stream sans se préoccuper du tri et vérifier que les tests de la question précédente passent toujours. Puis demander au Stream de se trier et vérifier que les tests marqués "Q3" passent.

- House.java
```java
	@Override
	public String toString() {
		if (!house.isEmpty()) {
			return "House with " + house.stream().map(VillagePeople::name).sorted().collect(Collectors.joining(", ")).toString();
		}
		return "Empty House";
	}
```


>#### Q4
>En fait, avoir une maison qui ne peut accepter que des VillagePeople n'est pas une bonne décision en termes de business, ils ne sont pas assez nombreux. YMCA décide donc qu'en plus des VillagePeople ses maisons permettent maintenant d'accueillir aussi des Minions, une autre population sans logement.  
On souhaite donc ajouter un type Minion (qui possède juste un nom name) et changer le code de House pour permettre d'ajouter des VillagePeople ou des Minion. Un Minion affiche son nom suivi entre parenthèse du texte "MINION".

- Minion.java
```java
package fr.uge.ymca;
import java.util.Objects;

public record Minion(String name) implements People{

	public Minions{
		Objects.requireNonNull(name);
	}

	@Override
	public String toString() {
		return name + " (MINION)";
	}
}
```

- VillagePeople.java
```java
package fr.uge.ymca;
import java.util.Objects;

public record VillagePeople(String name,Kind kind) implements People{

	public VillagePeople{
		Objects.requireNonNull(name);
		Objects.requireNonNull(kind);
	}

	@Override
	public String toString(){
		return name + " (" + kind + ")";
	}
}
```

- House.java
```java
package fr.uge.ymca;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public final class House {
	private final ArrayList<People> house;
	
	public House() {
		house = new ArrayList<People>();
	}
	
	public void add(People people) {
		Objects.requireNonNull(people);
		house.add(people);
	}
	
	public int  averagePrice() {
		if (house.isEmpty()){
			throw new IllegalArgumentException("NaN");
		}
		return 0;	
	}
	
	
	@Override
	public String toString() {
		if (!house.isEmpty()) {
			return "House with " + house.stream().map(People::name).sorted().collect(Collectors.joining(", ")).toString();
		}
		return "Empty House";
	}
}
```


>#### Q5
> On cherche à ajouter une méthode averagePrice à House qui renvoie le prix moyen pour une nuit sachant que le prix pour une nuit pour un VillagePeople est 100 et le prix pour une nuit pour un Minion est 1 (il vaut mieux être du bon côté du pistolet à prouts). Le prix moyen (renvoyé par averagePrice) est la moyenne des prix des VillagePeople et Minion présent dans la maison.  
Écrire la méthode averagePrice en utilisant le polymorphisme (_late dispatch_) pour trouver le prix de chaque VillagePeople ou Minion.

- VillagePeople.java
```java
package fr.uge.ymca;
import java.util.Objects;

public record VillagePeople(String name,Kind kind) implements People{

	
	public VillagePeople{
		Objects.requireNonNull(name);
		Objects.requireNonNull(kind);
	}
	
	public int Price() {
		return 100;
	}
	
	@Override
	public String toString(){
		return name + " (" + kind + ")";
	}
}
```
- Minion.java
```java
package fr.uge.ymca;
import java.util.Objects;

public record Minion(String name) implements People{

	public Minion{
		Objects.requireNonNull(name);
	}
	
	public int Price() {
		return 1;
	}
	
	@Override
	public String toString() {
		return name + " (MINION)";
	}
}

```

- People.java
```java
package fr.uge.ymca;

public sealed interface People permits VillagePeople,Minion{
	
	public String name();
	
	public int Price();
}
```

- House.java
```java
	public double averagePrice() {
		return house.stream().mapToDouble(People::Price).average().orElse(Double.NaN);	
	}
```


>#### Q6
>En fait, cette implantation n'est pas satisfaisante car elle ajoute une méthode publique dans VillagePeople et Minion alors que c'est un détail d'implantation. Au lieu d'utiliser la POO (programmation orienté objet), on va utiliser la POD (programmation orienté _data_) qui consiste à utiliser le _pattern matching_ pour connaître le prix par nuit d'un VillagePeople ou un Minion.  
Modifier votre code pour introduire une méthode privée qui prend en paramètre un VillagePeople ou un Minion et renvoie son prix par nuit puis utilisez cette méthode pour calculer le prix moyen par nuit d'une maison.
- House.java
```java
	public double averagePrice() {
		return house.stream().mapToDouble( f ->
			switch(f) {
			case VillagePeople k -> 100;
			case Minion m-> 1;
			
		}
				).average().orElse(Double.NaN);	
	}
```
- People.java
```java
package fr.uge.ymca;

public sealed interface People permits VillagePeople,Minion{
	public String name();
}
```


>#### Q7
> L'implantation précédente pose problème : il est possible d'ajouter une autre personne qu'un VillagePeople ou un Minion, mais celle-ci ne sera pas prise en compte par le _pattern matching_. Pour cela, on va interdire qu'une personne soit autre chose qu'un VillagePeople ou un Minion en scellant le super type commun.

Sans Avoir Lu la question précédente j'ai décidé de directement utiliser un type scellé sur mon interface ce qui n'amène à aucun code pour cette question.


>#### Q8
> On veut périodiquement faire un geste commercial pour une maison envers une catégorie/sorte de VillagePeople en appliquant une réduction de 80% pour tous les VillagePeople ayant la même sorte (par exemple, pour tous les BIKERs). Pour cela, on se propose d'ajouter une méthode addDiscount qui prend une sorte en paramètre et offre un discount pour tous les VillagePeople de cette sorte. Si l'on appelle deux fois addDiscount avec la même sorte, le discount n'est appliqué qu'une fois.

- House.java
```java
	public double averagePrice() {
		return house.stream().mapToDouble( f ->
			switch(f) {
			case VillagePeople k -> (discount.contains(k.kind())) ? 20 : 100;
			case Minion m-> 1;
		}).average().orElse(Double.NaN);	
	}
	
	public void addDiscount(Kind k) {
		Objects.requireNonNull(k);
		discount.add(k);
	}
```


>#### Q9
> Enfin, on souhaite pouvoir supprimer l'offre commerciale (discount) en ajoutant la méthode removeDiscount qui supprime le discount si celui-ci a été ajouté précédemment ou plante s'il n'y a pas de discount pour la sorte prise en paramètre.

- House.java
```java
	public void removeDiscount(Kind k) {
		Objects.requireNonNull(k);
		if(!discount.contains(k)) {
			throw new IllegalStateException();
		}
		discount.removeIf(e -> (e.equals(k)));
	}
```
>#### Q10
> **Optionnellement**, faire en sorte que l'on puisse ajouter un discount suivi d'un pourcentage de réduction, c'est à dire un entier entre 0 et 100, en implantant une méthode addDiscount(kind, percent). Ajouter également une méthode priceByDiscount qui renvoie une table associative qui a un pourcentage renvoie la somme des prix par nuit auxquels on a appliqué ce pourcentage (la somme est aussi un entier). La somme totale doit être la même que la somme de tous les prix par nuit (donc ne m'oubliez pas les minions). Comme précédemment, les pourcentages ne se cumulent pas si on appelle addDiscount plusieurs fois.
