package com.blue.findword;

public class Role {
	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	Long id;
	
	@Override
	public String toString() {
		return name+"\t"+id;
	}
	public Long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
}
