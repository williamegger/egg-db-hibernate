package com.egg.edb.hibernate.query;

import org.hibernate.type.Type;

public class SqlScalar {

	private String name;
	private Type type;

	public SqlScalar() {
	}

	public SqlScalar(String name, Type type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
