package com.egg.code.vm;

public final class Tlp {

	private static final String BASE = Tlp.class.getClassLoader().getResource("").getPath();
	public static final String PATH = BASE + Tlp.class.getPackage().getName().replaceAll("[.]", "/");

}
