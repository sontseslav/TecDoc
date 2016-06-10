/*
 * 
 */
package test;

/**
 *
 * @author user
 */
public class Main {
public static void main(String[] args) {
new B(10);
}

public static class A {
private int i = 20;

public A(int i) {
this.i = i;
initialize();
}

protected void initialize() {
System.out.println(i);
}
}

public static class B extends A {
protected int i = 5;

public B(int i) {
super(i);//goes to A but calls B's initialize wich refers to B's variable, wich 
//is not initialised (default value 0), 'cause we jet not returned from super.
this.i += i;
initialize();
}

protected void initialize() {
System.out.println(i);
}
}
}