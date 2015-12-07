package com.egg.edb.hibernate.query;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Query {

	// operator : =, !=, >, >=, <, <=, like, between, in, not in, is null, is not null
	private static final int eq = 0;
	private static final int ne = 1;
	private static final int gt = 2;
	private static final int ge = 3;
	private static final int lt = 4;
	private static final int le = 5;
	private static final int like = 6;
	private static final int between = 7;
	private static final int in = 8;
	private static final int not_in = 9;
	private static final int is_null = 10;
	private static final int is_not_null = 11;
	
	private static final String left_join = " left join ";
	private static final String left_join_fetch = " left join fetch ";

	private static final String SQL_KEY = "_Param_Key_";
	private int paramIndex;

	private HQLQuery hql;
	private SQLQuery sql;

	private String table;
	private Map<String, String> intos = new LinkedHashMap<String, String>();
	private Map<String, Object> intoParams = new HashMap<String, Object>();
	private StringBuffer sets = new StringBuffer();
	private Map<String, Object> setParams = new HashMap<String, Object>();
	private Map<String, String> joins = new LinkedHashMap<String, String>();
	private StringBuffer wheres = new StringBuffer();
	private Map<String, Object> whereParams = new HashMap<String, Object>();
	private StringBuffer selects = new StringBuffer();
	private StringBuffer orders = new StringBuffer();

	public Query(String table) {
		this.table = table;
	}

	public static Query table(String table) {
		return new Query(table.trim());
	}

	public HQLQuery hql() {
		if (hql == null) {
			hql = new HQLQuery(this);
		}
		return hql;
	}

	public SQLQuery sql() {
		if (sql == null) {
			sql = new SQLQuery(this);
		}
		return sql;
	}
	
	// ------------
	// join
	// ------------
	/**
	 * 得到JOIN
	 * <pre>
	 * isRemoveFetch：是否移除fetch关键字
	 * 
	 * left join [fetch] tableName on|with condition
	 * </pre>
	 */
	public String getJoin(boolean isRemoveFetch) {
		if (joins == null || joins.isEmpty()) {
			return "";
		}
		
		StringBuffer sql = new StringBuffer();
		Set<String> keys = joins.keySet();
		if (isRemoveFetch) {
			for (String join : keys) {
				sql.append(left_join).append(join);
			}
		} else {
			for (String join : keys) {
				sql.append(joins.get(join)).append(join);
			}
		}
		return sql.toString();
	}
	
	public Query leftJoin(String table) {
		return leftJoin(table, null);
	}

	public Query leftJoin(String table, String on) {
		if (isNotBlank(table)) {
			String join = table;
			if (isNotBlank(on)) {
				join += " on " + on;
			}
			joins.put(join, left_join);
		}
		return this;
	}

	public Query leftJoinFetch(String table) {
		return leftJoinFetch(table, null);
	}

	public Query leftJoinFetch(String table, String with) {
		if (isNotBlank(table)) {
			String join = table;
			if (isNotBlank(with)) {
				join += " with " + with;
			}
			joins.put(join, left_join_fetch);
		}
		return this;
	}

	// ------------
	// where
	// ------------
	/**
	 * 得到条件语句（where）
	 * 
	 * <pre>
	 * condition1
	 * and condition2
	 * ...
	 * and conditionN
	 * 
	 * 1.返回的语句中没有[where]关键字
	 * 2.第一个条件语句没有[and]关键字
	 * </pre>
	 */
	public String getWhere() {
		return wheres.toString();
	}

	/**
	 * 得到where语句的参数
	 */
	public Map<String, Object> getWhereParams() {
		return whereParams;
	}

	/**
	 * 条件语句：and condition
	 */
	public Query and(String condition) {
		return and(condition, "", null);
	}

	/**
	 * 条件语句：and condition
	 */
	public Query and(String condition, String paramKey, Object val) {
		if (isBlank(condition)) {
			return this;
		}
		
		if (wheres.length() > 0) {
			wheres.append(" and ");
		}
		wheres.append(condition).append(" ");
		if (isNotBlank(paramKey) && val != null) {
			whereParams.put(paramKey, val);
		}
		return this;
	}

	/**
	 * 条件语句：and condition
	 */
	public Query and(String condition, String[] paramKeys, Object[] vals) {
		if (isBlank(condition)) {
			return this;
		}

		if (wheres.length() > 0) {
			wheres.append(" and ");
		}
		wheres.append(condition).append(" ");
		if (paramKeys != null && paramKeys.length > 0) {
			for (int i = 0, len = paramKeys.length; i < len; i++) {
				whereParams.put(paramKeys[i], vals[i]);
			}
		}
		return this;
	}

	/**
	 * 相等：and key = val
	 */
	public Query eq(String key, Object val) {
		addWhere(eq, key, val, null);
		return this;
	}

	/**
	 * 不等:and key != val
	 */
	public Query ne(String key, Object val) {
		addWhere(ne, key, val, null);
		return this;
	}

	/**
	 * 大于：and key > val
	 */
	public Query gt(String key, Object val) {
		addWhere(gt, key, val, null);
		return this;
	}

	/**
	 * 大于等于：and key >= val
	 */
	public Query ge(String key, Object val) {
		addWhere(ge, key, val, null);
		return this;
	}

	/**
	 * 小于：and key < val
	 */
	public Query lt(String key, Object val) {
		addWhere(lt, key, val, null);
		return this;
	}

	/**
	 * 小于等于：and key <= val
	 */
	public Query le(String key, Object val) {
		addWhere(le, key, val, null);
		return this;
	}

	/**
	 * like：and key like val
	 */
	public Query like(String key, String val) {
		addWhere(like, key, val, null);
		return this;
	}

	/**
	 * between：and key between val1 and val2
	 */
	public Query between(String key, Object val1, Object val2) {
		addWhere(between, key, val1, val2);
		return this;
	}

	/**
	 * in：and key in val
	 */
	public Query in(String key, Object val) {
		addWhere(in, key, val, null);
		return this;
	}

	/**
	 * not in：and key not in val
	 */
	public Query notIn(String key, Object val) {
		addWhere(not_in, key, val, null);
		return this;
	}

	/**
	 * is null
	 */
	public Query isNull(String key) {
		addWhere(is_null, key, null, null);
		return this;
	}

	/**
	 * is not null
	 */
	public Query isNotNull(String key) {
		addWhere(is_not_null, key, null, null);
		return this;
	}

	/**
	 * 创建where条件语句
	 */
	private void addWhere(int operator, String key, Object val1, Object val2) {
		if (isBlank(key) || val1 == null) {
			return;
		}

		if (wheres.length() > 0) {
			wheres.append(" and ");
		}

		String paramKey1 = buildParamKey();
		String paramKey2 = (val2 == null) ? null : buildParamKey();
		StringBuffer condition = new StringBuffer();

		switch (operator) {
		case eq:
			condition.append("= :").append(paramKey1);
			break;
		case ne:
			condition.append("!= :").append(paramKey1);
			break;
		case gt:
			condition.append("> :").append(paramKey1);
			break;
		case ge:
			condition.append(">= :").append(paramKey1);
			break;
		case lt:
			condition.append("< :").append(paramKey1);
			break;
		case le:
			condition.append("<= :").append(paramKey1);
			break;
		case is_null:
			condition.append("is null");
			break;
		case is_not_null:
			condition.append("is not null");
			break;
		case like:
			condition.append("like :").append(paramKey1);
			break;
		case in:
			condition.append("in (:").append(paramKey1).append(")");
			break;
		case not_in:
			condition.append("not in (:").append(paramKey1).append(")");
			break;
		case between:
			condition.append("between :").append(paramKey1);
			condition.append(" and :").append(paramKey2);
			break;
		default:
			return;
		}

		wheres.append(key).append(" ").append(condition).append(" ");
		if (!(operator == is_null || is_null == is_not_null)) {
			whereParams.put(paramKey1, val1);
		}
		if (val2 != null) {
			whereParams.put(paramKey2, val2);
		}
	}

	// ------------
	// insert
	// ------------
	/**
	 * 得到插入语句
	 */
	public String getInsert() {
		StringBuffer columns = new StringBuffer();
		StringBuffer paramKeys = new StringBuffer();
		Set<String> columnSet = intos.keySet();
		boolean isFirst = true;
		for (String column : columnSet) {
			if (isFirst) {
				isFirst = false;
			} else {
				columns.append(", ");
				paramKeys.append(", ");
			}

			columns.append(column);
			paramKeys.append(":").append(intos.get(column));
		}

		StringBuffer sql = new StringBuffer();
		sql.append("insert into ").append(table);
		sql.append("(").append(columns).append(")");
		sql.append(" values");
		sql.append("(").append(paramKeys).append(")");
		return sql.toString();
	}

	/**
	 * 得到插入语句的参数
	 */
	public Map<String, Object> getInsertParams() {
		return intoParams;
	}

	public Query into(String column, Object val) {
		if (isBlank(column)) {
			return this;
		}

		if (intos.containsKey(column)) {
			String key = intos.remove(column);
			intoParams.remove(key);
		}

		String paramKey = buildParamKey();
		intos.put(column, paramKey);
		intoParams.put(paramKey, val);
		return this;
	}

	// ------------
	// delete
	// ------------
	/**
	 * 得到删除语句
	 */
	public String getDelete() {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from ").append(table);
		if (isNotBlank(wheres)) {
			sql.append(" where ").append(wheres);
		}
		return sql.toString();
	}

	/**
	 * 得到删除语句的参数
	 */
	public Map<String, Object> getDeleteParams() {
		return whereParams;
	}

	// ------------
	// update
	// ------------
	/**
	 * 得到更新语句
	 */
	public String getUpdate() {
		StringBuffer sql = new StringBuffer();
		sql.append("update ").append(table);
		sql.append(" set ").append(sets);
		if (isNotBlank(wheres)) {
			sql.append(" where ").append(wheres);
		}
		return sql.toString();
	}

	/**
	 * 得到更新语句的参数
	 */
	public Map<String, Object> getUpdateParams() {
		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.putAll(setParams);
		updateParams.putAll(whereParams);
		return updateParams;
	}

	/**
	 * set：updateStr
	 */
	public Query set(String updateStr) {
		if (isBlank(updateStr)) {
			return this;
		}

		if (sets.length() > 0) {
			sets.append(", ");
		}
		sets.append(updateStr);

		return this;
	}

	/**
	 * set : updateStr
	 * 
	 * <pre>
	 * column = :paramKey
	 * </pre>
	 */
	public Query set(String updateStr, String paramKey, Object paramVal) {
		set(updateStr);
		if (isNotBlank(paramKey)) {
			setParams.put(paramKey, paramVal);
		}
		return this;
	}

	/**
	 * set：column = val
	 */
	public Query set(String column, Object val) {
		if (isBlank(column)) {
			return this;
		}

		if (sets.length() > 0) {
			sets.append(", ");
		}

		String paramKey = buildParamKey();
		sets.append(column).append(" = :").append(paramKey);
		setParams.put(paramKey, val);

		return this;
	}

	// ------------
	// select
	// ------------
	/**
	 * 得到条件语句和排序语句
	 * 
	 * <pre>
	 * from
	 * [tableName]
	 * where
	 * condition1
	 * and condition2
	 * ...
	 * order by
	 * key1 ASC|DESC,
	 * key2 ASC|DESC
	 * </pre>
	 */
	public String getFrom(boolean isRemoveFetch) {
		StringBuffer sql = new StringBuffer();
		sql.append(" from ").append(table);
		sql.append(getJoin(isRemoveFetch));
		if (isNotBlank(wheres)) {
			sql.append(" where ").append(wheres);
		}
		if (isNotBlank(orders)) {
			sql.append(" order by ").append(orders);
		}
		return sql.toString();
	}

	/**
	 * 得到查询语句
	 * 
	 * <pre>
	 * select
	 *   column1,
	 *   column
	 * from
	 *   tableName
	 * where
	 *     condition1
	 * and condition2
	 * ...
	 * and conditionN
	 * order by
	 *   key1 ASC|DESC,
	 *   key2 ASC|DESC
	 * </pre>
	 */
	public String getSelect() {
		StringBuffer sql = new StringBuffer();
		sql.append(getSelectColumn());
		sql.append(getFrom(false));
		
		return sql.toString();
	}

	/**
	 * 得到COUNT查询语句
	 * 
	 * <pre>
	 * select
	 *   count(*)
	 * from
	 *   tableName
	 * where
	 *     condition1
	 * and condition2
	 * ...
	 * and conditionN
	 * order by
	 *   key1 ASC|DESC,
	 *   key2 ASC|DESC
	 * </pre>
	 */
	public String getCount() {
		StringBuffer sql = new StringBuffer();
		sql.append("select count(*) ");
		sql.append(getFrom(true));

		return sql.toString();
	}

	/**
	 * 得到select列
	 * 
	 * <pre>
	 * select
	 *   column1,
	 *   column
	 * </pre>
	 */
	public String getSelectColumn() {
		StringBuffer sql = new StringBuffer();

		sql.append("select");
		if (isBlank(selects)) {
			sql.append(" * ");
		} else {
			sql.append(" ").append(selects).append(" ");
		}

		return sql.toString();
	}

	/**
	 * 得到查询语句的参数
	 */
	public Map<String, Object> getSelectParams() {
		return whereParams;
	}

	public Query select(String column) {
		if (isBlank(column)) {
			return this;
		}

		if (selects.length() > 0) {
			selects.append(", ");
		}
		selects.append(column);
		return this;
	}

	/**
	 * order by：key ASC/DESC
	 */
	public Query order(String key, boolean asc) {
		if (isBlank(key)) {
			return this;
		}

		if (orders.length() > 0) {
			orders.append(",");
		}
		orders.append(key);
		if (asc) {
			orders.append(" ASC");
		} else {
			orders.append(" DESC");
		}
		return this;
	}

	private boolean isBlank(CharSequence str) {
		if (str == null) {
			return true;
		}
		for (int i = 0, len = str.length(); i < len; i++) {
			if (str.charAt(i) > ' ') {
				return false;
			}
		}
		return true;
	}
	
	private boolean isNotBlank(CharSequence str) {
		return !isBlank(str);
	}

	private String buildParamKey() {
		return SQL_KEY + paramIndex++;
	}
}
