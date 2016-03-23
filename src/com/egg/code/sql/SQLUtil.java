package com.egg.code.sql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLUtil {

	protected static final String url = "jdbc:mysql://192.168.0.163:3306/information_schema";
	protected static final String username = "root";
	protected static final String password = "banbantong";
	protected static final String database = "iteacher";

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(url, username, password);
	}

	public List<Map<String, Object>> query(CharSequence sql, Object... params) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(true);

			ps = conn.prepareStatement(sql.toString());
			if (!isBlank(params)) {
				for (int i = 0, len = params.length; i < len; i++) {
					ps.setObject(i + 1, params[i]);
				}
			}

			rs = ps.executeQuery();
			ResultSetMetaData md = rs.getMetaData();
			if (md == null) {
				return null;
			}

			List<String> keys = new ArrayList<String>();
			String label;
			for (int i = 1, len = md.getColumnCount(); i <= len; i++) {
				label = md.getColumnLabel(i);
				if (label != null && !label.isEmpty()) {
					keys.add(label);
				} else {
					keys.add(md.getColumnName(i));
				}
			}

			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Map<String, Object> map;
			while (rs.next()) {
				map = new HashMap<String, Object>();
				Object obj;
				Object val;
				for (int i = 0, len = keys.size(); i < len; i++) {
					obj = rs.getObject(i + 1);
					val = obj;
					if (obj instanceof BigInteger) {
						val = ((BigInteger) val).intValue();
					} else if (obj instanceof BigDecimal) {
						val = ((BigDecimal) val).doubleValue();
					}
					map.put(keys.get(i), val);
				}
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeQuickly(rs);
			closeQuickly(ps);
			closeQuickly(conn);
		}
	}

	public void closeQuickly(AutoCloseable obj) {
		if (obj != null) {
			try {
				obj.close();
			} catch (Exception e) {
				log(".closeQuickly()");
			}
		}
	}

	protected boolean isBlank(Object obj) {
		if (obj == null) {
			return true;
		}

		if (obj instanceof CharSequence) {
			return obj.toString().trim().isEmpty();
		} else if (obj instanceof Collection<?>) {
			return ((Collection<?>) obj).isEmpty();
		} else if (obj instanceof Map<?, ?>) {
			return ((Map<?, ?>) obj).isEmpty();
		} else if (obj instanceof Object[]) {
			return ((Object[]) obj).length == 0;
		}
		return false;
	}

	protected void log(Object obj) {
		System.out.println(obj);
	}

}
