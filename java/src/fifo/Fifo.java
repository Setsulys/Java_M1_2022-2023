package fifo;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

public class Fifo<E> implements Iterable<E>{
	
	private int size = 0;
	private E[] queue;
	private int head;
	private int tail;
	
	public Fifo(int nb) {
		if(nb <=0 ) {
			throw new IllegalArgumentException();
		}
		@SuppressWarnings("unchecked")
		E[] queueb = (E[]) new Object[nb];
		this.queue = queueb;
		this.head=0;
		this.tail=0;
	}
	
	public void offer(E element) {
		Objects.requireNonNull(element);
		if(size == queue.length) {
			throw new IllegalStateException();
		}
		queue[tail]=element;
		tail= (tail+1)% queue.length;
		size++;
	}
	
	public E poll() {
		if(size <=0) {
			throw new IllegalStateException();
		}
		var r= queue[head];
		head = (head+1)% queue.length;
		size--;
		return r;
	}
	
	@Override
	public String toString() {
		int elem = head;
		var joiner = new StringJoiner(", ","[","]");
		for(var i=0; i < size ; i ++) {
			joiner.add(String.valueOf(queue[elem]));
			elem = (elem +1)%queue.length;
		}
		return joiner.toString();
	}
	
	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		return size ==0;
	}
	
	public Iterator<E> iterator(){
		return new Iterator<E>() {
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
}
