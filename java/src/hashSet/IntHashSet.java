package hashSet;

import java.util.Objects;
import java.util.function.IntConsumer;

public class IntHashSet {
	private static int SIZE =2;
	private final Entry[] array;
	private int size =0;
	public record Entry(int value, Entry next) {
		
	}
	
	public IntHashSet() {
		this.size=0;
		this.array = new Entry[SIZE];
	}
	
	public int hash(int value) {
		return (value) & (SIZE-1);
	}
	
	public void add(int value) {
		var hashvalue = hash(value);
		Entry element;
		for(element = array[hashvalue];element!=null; element =element.next()) {
			if(element.value() == value) {
				return;
			}
		}
		this.size++;
		array[hashvalue] = new Entry(value,array[hashvalue]);
	}
	
	public int size() {
		return size;
	}
	
	public void forEach(IntConsumer cons) {
		Objects.requireNonNull(cons);
		for(var i=0; i < SIZE;i++) {
			for(var element =array[i]; element !=null ;element=element.next()) {
				cons.accept(element.value);
			}
		}
	}
	
	public boolean contains(int value) {
		Objects.requireNonNull(value);
		for(var element =array[hash(value)]; element!=null; element = element.next) {
			if(element.value == value) {
				return true;
			}
		}
		return false;
	}
	
}
