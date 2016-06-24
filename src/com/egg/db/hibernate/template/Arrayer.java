package com.egg.db.hibernate.template;

public interface Arrayer<T> {

	public T build(Object[] objs);

}
