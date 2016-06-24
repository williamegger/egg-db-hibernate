package com.egg.db.hibernate.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TemplateUtil {

	public static <T> List<T> build(List<Map> maps, Mapper mapper) {
		if (maps == null || maps.isEmpty()) {
			return null;
		}

		if (mapper == null) {
			throw new NullPointerException("mapper is null!!!");
		}

		List<T> list = new ArrayList<T>();
		Object obj = null;
		for (Map map : maps) {
			obj = mapper.build(map);
			if (obj != null) {
				list.add((T) obj);
			}
		}
		return list;
	}

	public static <T> List<T> build(List<Object[]> objsList, Arrayer arrayer) {
		if (objsList == null || objsList.isEmpty()) {
			return null;
		}

		if (arrayer == null) {
			throw new NullPointerException("arrayer is null.");
		}

		List<T> list = new ArrayList<T>();
		Object obj = null;
		for (Object[] objs : objsList) {
			obj = arrayer.build(objs);
			list.add((T) obj);
		}
		return list;
	}

}
