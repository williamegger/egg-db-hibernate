package com.egg.db.hibernate.template;

public interface Arrayer {

	public <T> T build(Object[] objs);

}
