# TP2 Sed, the stream editor
###### LY-IENG Steven

## Exercice 2

>#### Q1
> Dans un premier temps, on va créer une classe StreamEditor dans le package fr.uge.sed avec une méthode d'instance transform qui prend en paramètre un LineNumberReader et un Writer et écrit, ligne à ligne, le contenu du LineNumberReader dans le Writer.

- StreamEditor.java
```java
public class StreamEditor {
	
	public void transform(LineNumberReader lnr, Writer wr) throws IOException {
		Objects.requireNonNull(lnr);
		Objects.requireNonNull(wr);
		var line = "";
		
		while((line = lnr.readLine())!=null){
			wr.append(line).append("\n");
		}
	}

}
```

>#### Q2
> On veut maintenant pouvoir spécifier une commande à la création du StreamEditor pour transformer les lignes du fichier en entrée. Ici, lineDelete renvoie un record LineDeleteCommand qui indique la ligne à supprimer (la première ligne d'un fichier est 1, pas 0).
> L'exemple ci-dessous montre comment supprimer la ligne 2 d'un fichier.
> ```java
>        var command = StreamEditor.lineDelete(2);
>        var editor = new StreamEditor(command);
>        editor.transform(reader, writer);
>```
> ---

- LineDeleteCommand.java
```java
public record LineDeleteCommand(int lineNumber) {
	
	public LineDeleteCommand{
		if (lineNumber < 0) {
			throw new IllegalArgumentException("int < 0");
		}
	}
}
```

- StreamEditor.java
```java
public class StreamEditor {
	
	private final LineDeleteCommand command;
	
	public StreamEditor(LineDeleteCommand command) {
		Objects.requireNonNull(command);
		this.command = command;
	}
	
	public StreamEditor() {
		this.command = new LineDeleteCommand(0);
	}

	public void transform(LineNumberReader lnr, Writer wr) throws IOException {
		Objects.requireNonNull(lnr);
		Objects.requireNonNull(wr);
		String line;
		var count=0;
		while((line = lnr.readLine())!=null){
			count++;
			if(count != command.lineNumber()) {
				wr.append(line).append("\n");
			}
		}
	}
	
	public static LineDeleteCommand lineDelete(int number) {
		return new LineDeleteCommand(number);
	}

}
```
Pour pouvoir specifier j'ai crée un record "LineDeleteCommand" ayant pour unique champs un int et dans la fonction demandé j'ai juste renvoyé le nouveau record contenant la valeur prise en argument dans son unique champ

> #### Q3 On souhaite maintenant écrire un main qui prend en paramètre sur la ligne de commande un nom de fichier, supprime la ligne 2 de ce fichier et écrit le résultat sur la sortie standard.

- StreamEditor.java
```java
public class StreamEditor {
	
	private final LineDeleteCommand command;
	
	public StreamEditor(LineDeleteCommand command) {
		Objects.requireNonNull(command);
		this.command = command;
	}
	
	public StreamEditor() {
		this.command = new LineDeleteCommand(0);
	}

	public void transform(LineNumberReader lnr, Writer wr) throws IOException {
		Objects.requireNonNull(lnr);
		Objects.requireNonNull(wr);
		String line;
		while((line = lnr.readLine())!=null){
			if(lnr.getLineNumber() != command.lineNumber()) {
				wr.append(line).append("\n");
			}
		}
	}
	
	public static LineDeleteCommand lineDelete(int number) {
		return new LineDeleteCommand(number);
	}

	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.println("ERROR!!!!! No args inputed");
			System.exit(1);
		}
		var path = Path.of(args[0]);
		try (var reader = Files.newBufferedReader(path,StandardCharsets.UTF_8);
				var lineReader = new LineNumberReader(reader);
				var writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8)){
			
				new StreamEditor(lineDelete(2)).transform(lineReader,writer);
		} catch (IOException e) {
			System.err.println(e.getMessage()); 
			System.exit(2); 	
		}
	}
}

```
Dans le main j'utilise un try pour pouvoir ouvrir et ne pas oublier de fermer le document, on prend en paramètre le reader le numero de ligne et le writer,
puis on renvoye un StreamEditor qui supprime la ligne 2 du document pour l'afficher sur la ligne de commande. avec le try je fais un exit si il y a une erreur et aussi si il n'y a pas d'argument je fais un exit aussi avec une autre valeur.
Aussi au lieu du compteur on m'a donné l'information que je pouvais utiliser getLineNumber.
> #### Q4 On souhaite introduire une nouvelle commande qui permet de supprimer une ligne si elle contient une expression régulière. Avant de le coder, on va faire un peu de re-factoring pour préparer le fait que l'on puisse avoir plusieurs commandes.

- StreamEditor.java

```java
public class StreamEditor {
	
	private enum Action {
		DELETE,PRINT;
	}

	public record LineDeleteCommand(int lineNumber) {
		
		public LineDeleteCommand{
			if (lineNumber < 0) {
				throw new IllegalArgumentException("int < 0");
			}
		}
		public Action action(int currentLine) {
			if(currentLine==lineNumber) {
				return Action.DELETE;
			}
			return Action.PRINT;
		}

	}
	
	private final LineDeleteCommand command;
	
	
	public StreamEditor(LineDeleteCommand commands) {
		Objects.requireNonNull(commands);
		this.command = commands;
	}
	
	public StreamEditor() {
		this.command = new LineDeleteCommand(0);
	}
	
	public void transform(LineNumberReader lnr, Writer wr) throws IOException {
		Objects.requireNonNull(lnr);
		Objects.requireNonNull(wr);
		String line;
		while((line = lnr.readLine())!=null){
			switch(command.action(lnr.getLineNumber())) {
			case PRINT : wr.append(line).append("\n");
			case DELETE : ;
			}
		}
	}
	
	public static LineDeleteCommand lineDelete(int number) {
		return new LineDeleteCommand(number);
	}

	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.println("ERROR!!!!! No args inputed");
			System.exit(1);
		}
		var path = Path.of(args[0]);
		try (var reader = Files.newBufferedReader(path,StandardCharsets.UTF_8);
				var lineReader = new LineNumberReader(reader);
				var writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8)){
				new StreamEditor(lineDelete(2)).transform(lineReader,writer);
		} catch (IOException e) {
			System.err.println(e.getMessage()); 
			System.exit(2); 	
		}
	}
}

```
J'ai décidé d'ajouter une methode dans LineDeleteCommand qui nous permet de voir si on retrouve la valeur qu'elle retourne l'action a faire.
j'appel cette methode dans la methode transform a la place du "if", si on a un DELETE,on ne fait rien si c'est un PRINT on affiche la ligne.
Aussi le fait d'avoir un enum en private, m'a forcé a placer le record dans la classe StreamEditor pour pouvoir y acceder.
En faisant cette question, il y a un problème qui apparait dans la question 2, mais rien de grave, d'apres mes camarades c'est une erreur de Mr forax,
les tests demandent à tout les champs d'etre private final cependant en ajoutant les records cette contrainte est brisé.

> #### Q5 Maintenant que l'on a bien préparé le terrain, on peut ajouter une nouvelle commande renvoyée par la méthode findAndDelete qui prend en paramètre un java.util.regex.Pattern telle que le code suivant fonctionne
> ```java        
> var command = StreamEditor.findAndDelete(Pattern.compile("foo|bar"));
> var editor = new StreamEditor(command);
> editor.transform(reader, writer);
>```
>---
- StreamEditor.java
```java
public class StreamEditor {
	
	private enum Action {
		DELETE,PRINT;
	}

	public record LineDeleteCommand(int lineNumber) implements Command{
		
		public LineDeleteCommand{
			if (lineNumber < 0) {
				throw new IllegalArgumentException("int < 0");
			}
		}
		public Action action(int currentLine,String pat) {
			if(currentLine==lineNumber) {
				return Action.DELETE;
			}
			return Action.PRINT;
		}

	}
	
	public record FindAndDeleteCommand(Pattern pattern) implements Command{
		public FindAndDeleteCommand{
			Objects.requireNonNull(pattern);
		}
		
		public Action action(int currentLine,String pat) {
			if(pattern.matcher(pat).find()) {
				return Action.DELETE;
			}
			return Action.PRINT;
		}
		
	}
	
	public sealed interface Command permits FindAndDeleteCommand, LineDeleteCommand{
		Action action(int currentLine,String pat);
	}
	
	private final Command command;
	
	public StreamEditor(Command commands) {
		Objects.requireNonNull(commands);
		this.command = commands;
	}
	
	public StreamEditor() {
		this.command = new LineDeleteCommand(0);
	}

	public void transform(LineNumberReader lnr, Writer wr) throws IOException {
		Objects.requireNonNull(lnr);
		Objects.requireNonNull(wr);
		String line;
		while((line = lnr.readLine())!=null){
			switch(command.action(lnr.getLineNumber(), line)) {
					case PRINT : wr.append(line).append("\n");
					case DELETE : ;
			}
		}
	}
	
	public static LineDeleteCommand lineDelete(int number) {
		return new LineDeleteCommand(number);
	}

	public static FindAndDeleteCommand findAndDelete(Pattern pattern) {
		return new FindAndDeleteCommand(pattern);
	}
	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.println("ERROR!!!!! No args inputed");
			System.exit(1);
		}
		var path = Path.of(args[0]);
		try (var reader = Files.newBufferedReader(path,StandardCharsets.UTF_8);
				var lineReader = new LineNumberReader(reader);
				var writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8)){
				new StreamEditor(lineDelete(2)).transform(lineReader,writer);
		} catch (IOException e) {
			System.err.println(e.getMessage()); 
			System.exit(2); 	
		}
	}
}
```
Pour cette question j'ai décidé d'avoir 2 records qui implémentent une interface "Command",
que j'ai décidé de sceller (pour me permettre de faire un switch sans default).
Chaque record possede une méthode action qui selon le record agit differement. l'un utilise le pattern matcher et l'autre verifie par rapport a la ligne.
Dans la methode transform, j'utilise la methode action de l'interface et avec un polymorphisme, l'objet sait lui même quel methode utiliser.
En plus de cela j'ai ajouté la méthode findAndDelete qui fait la meme chose que lineDelete mais pour avec le deuxieme record pour le pattern matcher.

> #### Q6 En fait, cette implantation n'est pas satisfaisante, car les records LineDeleteCommand et FindAndDeleteCommand ont beaucoup de code qui ne sert à rien. Il serait plus simple de les transformer en lambdas, car la véritable information intéressante est comment effectuer la transformation d'une ligne.

- StreamEditor.java

```java
public class StreamEditor {
	
	private enum Action {
		DELETE,PRINT;
	}

	@FunctionalInterface
	public interface Command {
		Action apply(int currentLine,String pat);
	}
	
	private final Command command;
	
	public StreamEditor(Command commands) {
		Objects.requireNonNull(commands);
		this.command = commands;
	}
	
	public StreamEditor() {
		this.command = lineDelete(0);
	}

	public void transform(LineNumberReader lnr, Writer wr) throws IOException {
		Objects.requireNonNull(lnr);
		Objects.requireNonNull(wr);
		String line;
		while((line = lnr.readLine())!=null){
			switch(command.apply(lnr.getLineNumber(), line)) {
					case PRINT : wr.append(line).append("\n");
					case DELETE : ;
			}
		}
	}
	
	public static Command lineDelete(int number) {
		if(number < 0) {
			throw new IllegalArgumentException("number of the line < 0");
		}
		return (int lineNumber,String line) -> number == lineNumber ? Action.DELETE : Action.PRINT;
	}

	public static Command findAndDelete(Pattern pattern) {
		Objects.requireNonNull(pattern);
		return (int lineNumber,String line) -> pattern.matcher(line).find() ? Action.DELETE : Action.PRINT;
	}
	
	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.println("ERROR!!!!! No args inputed");
			System.exit(1);
		}
		var path = Path.of(args[0]);
		try (var reader = Files.newBufferedReader(path,StandardCharsets.UTF_8);
				var lineReader = new LineNumberReader(reader);
				var writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8)){
				new StreamEditor(lineDelete(2)).transform(lineReader,writer);
		} catch (IOException e) {
			System.err.println(e.getMessage()); 
			System.exit(2); 	
		}
	}
}
```
J'ai supprimé les deux records a la place j'ai une interface fonctionnelle Command qui a une action pour methode
Dans lineDelete et findAndDelete j'ai crée des lambdas qui renvoyent une Command avec l'utilisation de action implicitement

> #### Q7 On souhaite maintenant introduire une commande substitute(pattern, replacement) qui dans une ligne remplace toutes les occurrences du motif par une chaîne de caractère de remplacement. Malheureusement, notre enum Action n'est pas à même de gérer ce cas, car il faut que la commande puisse renvoyer PRINT mais avec une nouvelle ligne.
> On se propose pour cela de remplacer l'enum Action par une interface et DELETE et PRINT par des records implantant cette interface comme ceci
>```java
>       private interface Action {
>         record DeleteAction() implements Action {}
>         record PrintAction(String text) implements Action {}
>       }
>```
> ---
- StreamEditor.java
```java
public class StreamEditor {
	public record DeleteAction() implements Action {}
    public record PrintAction(String text) implements Action {
    	public PrintAction{
    		Objects.requireNonNull(text);
    	}
    }
	private sealed interface Action permits DeleteAction,PrintAction{	
        
      }

	@FunctionalInterface
	public interface Command {
		Action apply(int currentLine,String pat);
	}
	
	private final Command command;
	
	public StreamEditor(Command commands) {
		Objects.requireNonNull(commands);
		this.command = commands;
	}
	
	public StreamEditor() {
		this.command = lineDelete(0);
	}

	public void transform(LineNumberReader lnr, Writer wr) throws IOException {
		Objects.requireNonNull(lnr);
		Objects.requireNonNull(wr);
		var line = "";
		while((line = lnr.readLine())!=null){
			switch(command.apply(lnr.getLineNumber(), line)) {
					case PrintAction p -> wr.append(p.text).append("\n");
					case DeleteAction p -> { }
			}
		}
	}
	
	public static Command lineDelete(int number) {
		if(number < 0) {
			throw new IllegalArgumentException("number of the line < 0");
		}
		return (int lineNumber,String line) -> number == lineNumber ? new DeleteAction() : new PrintAction(line);
	}

	public static Command findAndDelete(Pattern pattern) {
		Objects.requireNonNull(pattern);
		return (int lineNumber,String line) -> pattern.matcher(line).find() ? new DeleteAction() : new PrintAction(line);
	}
	
	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.println("ERROR!!!!! No args inputed");
			System.exit(1);
		}
		var path = Path.of(args[0]);
		try (var reader = Files.newBufferedReader(path,StandardCharsets.UTF_8);
				var lineReader = new LineNumberReader(reader);
				var writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8)){
				new StreamEditor(lineDelete(2)).transform(lineReader,writer);
		} catch (IOException e) {
			System.err.println(e.getMessage()); 
			System.exit(2); 	
		}
	}
}
```
Tout d'abord j'ai sortis les records de l'interface pour faciliter la tache et j'ai sealed l'interface et permits.
verifier que text n'est pas null dans le record avec un Objects.requireNonNull(text) pour eviter des erreurs

> #### Q8 On peut enfin ajouter la commande substitute(pattern, replacement) telle que le code suivant fonctionne
>```java
>       var command = StreamEditor.substitute(Pattern.compile("foo|bar"), "hello");
>       var editor = new StreamEditor(command);
>       editor.transform(reader, writer);
>```
> ---
```java

public class StreamEditor {
	public record DeleteAction() implements Action {}
    public record PrintAction(String text) implements Action {
    	public PrintAction{
    		Objects.requireNonNull(text);
    	}
    }
	private sealed interface Action permits DeleteAction,PrintAction{	
        
      }

	@FunctionalInterface
	public interface Command {
		Action apply(int currentLine,String pat);
	}
	
	private final Command command;
	
	public StreamEditor(Command commands) {
		Objects.requireNonNull(commands);
		this.command = commands;
	}
	
	public StreamEditor() {
		this.command = lineDelete(0);
	}

	public void transform(LineNumberReader lnr, Writer wr) throws IOException {
		Objects.requireNonNull(lnr);
		Objects.requireNonNull(wr);
		String line;
		while((line = lnr.readLine())!=null){
			switch(command.apply(lnr.getLineNumber(), line)) {
					case PrintAction p -> wr.append(p.text).append("\n");
					case DeleteAction p -> { }
			}
		}
	}
	
	public static Command lineDelete(int number) {
		if(number < 0) {
			throw new IllegalArgumentException("number of the line < 0");
		}
		return (int lineNumber,String line) -> number == lineNumber ? new DeleteAction() : new PrintAction(line);
	}

	public static Command findAndDelete(Pattern pattern) {
		Objects.requireNonNull(pattern);
		return (int lineNumber,String line) -> pattern.matcher(line).find() ? new DeleteAction() : new PrintAction(line);
	}
	
	public static Command substitute(Pattern pat,String str) {
		Objects.requireNonNull(pat);
		Objects.requireNonNull(str);
		return (int lineNumber,String line) -> pat.matcher(line).find() ? new PrintAction(pat.matcher(line).replaceAll(str)) : new PrintAction(line);
	}
	
	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.println("ERROR!!!!! No args inputed");
			System.exit(1);
		}
		var path = Path.of(args[0]);
		try (var reader = Files.newBufferedReader(path,StandardCharsets.UTF_8);
				var lineReader = new LineNumberReader(reader);
				var writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8)){
				new StreamEditor(lineDelete(2)).transform(lineReader,writer);
		} catch (IOException e) {
			System.err.println(e.getMessage()); 
			System.exit(2); 	
		}
	}
}
```
D'abord  je verifie que les paramètres sont non null et je fais la meme chose que pour findAndDelete mais 
je remplace toutes les occurrences a l'aide d'un pattern matcher replaceAll si il ne trouve pas le pattern il affiche l'element qui y etait deja
> #### Q9 **Optionnellement**, créer une DeleteAction avec un new semble bizarre car une DeleteAction est un record qui n'a pas de composant donc on pourrait toujours utiliser la même instance.
> Comment faire tout en gardant le record DeleteAction pour éviter de faire un new à chaque fois que l'on veut avoir une instance de DeleteAction ?
> En fait, il y a une façon plus élégante de faire la même chose que la précédente en transformant DeleteAction en enum (chercher "enum singleton java" sur internet).
> Vérifier que les tests JUnit marqués "Q9" passent.

...

> #### Q10 Enfin, on peut vouloir combiner plusieurs commandes en ajoutant une méthode andThen à Command tel que le code suivant fonctionne.
>```java       
>		var command1 = StreamEditor.substitute(Pattern.compile("foo"), "hello");
>       var command2 = StreamEditor.findAndDelete(Pattern.compile("baz"));
>       var editor = new StreamEditor(command1.andThen(command2));
>       editor.transform(reader), writer);
>```
> ---

- StreamEditor.java
```java
public class StreamEditor {
	public record DeleteAction() implements Action {}
    public record PrintAction(String text) implements Action {
    	public PrintAction{
    		Objects.requireNonNull(text);
    	}
    }
	public sealed interface Action permits DeleteAction,PrintAction{	
        
      }

	@FunctionalInterface
	public interface Command {
		Action apply(int currentLine,String pat);
		
		public default Command andThen(Command comm) {
			Objects.requireNonNull(comm);
			return (int currentLine,String pat) -> 
				switch(apply(currentLine,pat)) {
				case  PrintAction a -> comm.apply(currentLine, a.text());
				case DeleteAction a -> a;
			};
		}
	}
	
	private final Command command;
	
	public StreamEditor(Command commands) {
		Objects.requireNonNull(commands);
		this.command = commands;
	}
	
	public StreamEditor() {
		this.command = lineDelete(0);
	}

	public void transform(LineNumberReader lnr, Writer wr) throws IOException {
		Objects.requireNonNull(lnr);
		Objects.requireNonNull(wr);
		String line;
		while((line = lnr.readLine())!=null){
			switch(command.apply(lnr.getLineNumber(), line)) {
					case PrintAction p -> wr.append(p.text).append("\n");
					case DeleteAction p -> { }
			}
		}
	}
	
	public static Command lineDelete(int number) {
		if(number < 0) {
			throw new IllegalArgumentException("number of the line < 0");
		}
		return (int lineNumber,String line) -> number == lineNumber ? new DeleteAction() : new PrintAction(line);
	}

	public static Command findAndDelete(Pattern pattern) {
		Objects.requireNonNull(pattern);
		return (int lineNumber,String line) -> pattern.matcher(line).find() ? new DeleteAction() : new PrintAction(line);
	}
	
	public static Command substitute(Pattern pat,String str) {
		Objects.requireNonNull(pat);
		Objects.requireNonNull(str);
		return (int lineNumber,String line) -> pat.matcher(line).find() ? new PrintAction(pat.matcher(line).replaceAll(str)) : new PrintAction(line);
	}
	
	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.println("ERROR!!!!! No args inputed");
			System.exit(1);
		}
		var path = Path.of(args[0]);
		try (var reader = Files.newBufferedReader(path,StandardCharsets.UTF_8);
				var lineReader = new LineNumberReader(reader);
				var writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8)){
				new StreamEditor(lineDelete(2)).transform(lineReader,writer);
		} catch (IOException e) {
			System.err.println(e.getMessage()); 
			System.exit(2); 	
		}
	}
}

```
Comme on me demande que se soit une methode d'instance de Command je vais la mettre en défault et 
je renvoye une lambda de l'interface command qui si on a un print action, je vais utiliser la commande passé en parametre pour lui fiare passer l'action a faire en plus


> #### Q11 En conclusion, dans quel cas, à votre avis, va-t-on utiliser des records pour implanter de différentes façons une interface et dans quel cas va-t-on utiliser des lambdas ?

A mon avis les cas dans lesquels on va utiliser une lambda, c'est lorsque le record est assez simple et ne nécésite pas qu'il y ai un nom sinon il est nécéssaire d'avoir un record