package dev.array21.classvalidator;

/**
 * Simple class for returning a tuple
 * 
 * @author Tobias de Bruijn
 * @since 1.0.0
 *
 * @param <A> Type of A
 * @param <B> Type of B
 */
public class Pair<A, B> {
	private A a;
	private B b;
	
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	public A getA() {
		return this.a;
	}
	
	public B getB() {
		return this.b;
	}
}
