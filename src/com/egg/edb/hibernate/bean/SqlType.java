package com.egg.edb.hibernate.bean;

import org.hibernate.type.Type;

public class SqlType {

	private String name;
	private Type scalar;
	private Class<?> entityClass;

	public SqlType(String name, Type scalar) {
		this.name = name;
		this.scalar = scalar;
	}

	public SqlType(String name, Class<?> entityClass) {
		this.name = name;
		this.entityClass = entityClass;
	}

	public String getName() {
		return name;
	}

	public Type getScalar() {
		return scalar;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

}
