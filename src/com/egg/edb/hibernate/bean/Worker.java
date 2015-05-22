package com.egg.edb.hibernate.bean;

import org.hibernate.Session;

import com.egg.edb.hibernate.exception.BLException;

/**
 * 
 * <pre>
 * 用户可以实现一个Worker&lt;Param, Result&gt;接口，
 * 并在execute方法中编写数据库的业务代码
 * 
 * 注：使用 DBHelper.doWorker() 方法操作该接口
 * 
 * DBHelper.doWorker() 方法中：
 * 1.自动得到Session
 * 2.执行 execute() 方法
 * 3.提交并返回结果
 * </pre>
 * @param <Param> execute方法中的参数类型
 * @param <Result> execute方法的返回值类型
 */
public interface Worker<Params, Result> {

	/**
	 * <pre>
	 * 使用 DBHelper.doWorker() 方法操作该接口
	 * </pre>
	 * @param session Hibernate的Session
	 * @param params 参数
	 * @return
	 * @throws Exception
	 */
	public Result execute(Session session, Params... params) throws BLException, Exception;

}
