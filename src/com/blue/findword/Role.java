package com.blue.findword;

public class Role {
	String name;
	String clazz;

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name.replaceAll("<img.*?/>", "");
	}
	
	Long id;
	
	@Override
	public String toString() {
		return clazz + "_" + name;
	}
	public Long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
}
