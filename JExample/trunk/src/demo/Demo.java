package demo;

public class Demo implements Cloneable {

	private String name;

	public Demo(String name){
		this.name = name;
	}
	
	public boolean equals(Object obj){
		return this.name.equals(((Demo)obj).name);
	}
	
	public Object clone(){
		return new Demo(this.name);
	}
}
