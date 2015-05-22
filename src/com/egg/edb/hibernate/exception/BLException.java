package com.egg.edb.hibernate.exception;

import com.alibaba.fastjson.JSONObject;

public class BLException extends Throwable {

	private int err;
	private String msg;

	public BLException() {
	}

	public BLException(int err, String msg) {
		this.err = err;
		this.msg = msg;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		if (msg == null) {
			msg = "";
		}
		json.put("err", err);
		json.put("errmsg", msg);
		return json;
	}

	public int getErr() {
		return err;
	}

	public void setErr(int err) {
		this.err = err;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
