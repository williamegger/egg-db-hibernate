package com.egg.edb.hibernate.query;

public class SqlEntity {

	private String name;
	private Class<?> entityClass;

	public SqlEntity() {
	}

	public SqlEntity(String name, Class<?> entityClass) {
		this.name = name;
		this.entityClass = entityClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

}
