package com.blue.findword;

public class Main {
	public static void main(String[] args) throws Exception {
		Scaner s = new Scaner();
		s.setProcessor(new IdRoleProcessor());
		s.scan();
	}
}
