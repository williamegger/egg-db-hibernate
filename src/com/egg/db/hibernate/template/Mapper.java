package com.egg.db.hibernate.template;

import java.util.Map;

public interface Mapper {

	public <T> T build(Map map);

}
