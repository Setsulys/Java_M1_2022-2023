package fr.uge.ymca;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

public final class House {

	
	private final ArrayList<People> house;
	private final HashSet<Kind> discount;
	public House() {
		house = new ArrayList<People>();
		discount = new HashSet<Kind>();
	}
	
	public void add(People people) {
		Objects.requireNonNull(people);
		house.add(people);
	}
	
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
	
	public void removeDiscount(Kind k) {
		Objects.requireNonNull(k);
		if(!discount.contains(k)) {
			throw new IllegalStateException();
		}
		discount.removeIf(e -> (e.equals(k)));
	}
	
	@Override
	public String toString() {
		if (!house.isEmpty()) {
			return "House with " + house.stream().map(People::name).sorted().collect(Collectors.joining(", ")).toString();
		}
		return "Empty House";
	}
}
