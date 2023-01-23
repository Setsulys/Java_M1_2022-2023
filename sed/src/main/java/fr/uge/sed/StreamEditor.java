package fr.uge.sed;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Pattern;

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
