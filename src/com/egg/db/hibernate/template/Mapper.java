package com.egg.db.hibernate.template;

import java.util.Map;

public interface Mapper<T> {

	public T build(Map<String, Object> map);

}
