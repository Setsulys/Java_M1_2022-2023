package fr.uge.ymca;

import java.util.Objects;

public record Minion(String name) implements People{

	public Minion{
		Objects.requireNonNull(name);
	}

//	public int Price() {
//		return 1;
//	}
	
	@Override
	public String toString() {
		return name + " (MINION)";
	}
}
