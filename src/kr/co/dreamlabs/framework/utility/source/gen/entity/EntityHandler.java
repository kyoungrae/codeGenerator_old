/**
 * ++ dreamlabs Product ++
 */
package kr.co.dreamlabs.framework.utility.source.gen.entity;

import java.util.List;

import kr.co.dreamlabs.framework.common.database.handle.DatabaseHandleException;
import kr.co.dreamlabs.framework.common.database.handle.DatabaseHandler;
import kr.co.dreamlabs.framework.common.database.mapper.SqlMapClientWrapper;
import kr.co.dreamlabs.framework.common.env.ConfigManager;

/**
 * <pre>
 * EntityHandler
 * </pre>
 *
 * @since 2012. 11. 6.
 * @version 1.0
 *
 *
 */
public class EntityHandler extends DatabaseHandler {

	/**
	 * <pre>
	 * SearchHandler constructor
	 * </pre>
	 */
	public EntityHandler() {}

	/**
	 * <pre>
	 * SearchHandler constructor
	 * </pre>
	 */
	public EntityHandler(SqlMapClientWrapper s) {
		super(s);
	}

	/**
	 * <pre>
	 * SearchHandler constructor
	 * </pre>
	 */
	public EntityHandler(String u, String p, SqlMapClientWrapper s) {
		super(u, p, s);
	}

	/**
	 * <pre>
	 * </pre>
	 *
	 * @param att_list
	 * @return
	 * @throws DatabaseHandleException
	 * @return List
	 */
	public List select() throws DatabaseHandleException {
		return super.select("SELECT.ENTITY", ConfigManager.getProperty("kr.co.dreamlabs.Framework.Automation.Gen.Product.Code"));
		// for Oracle
		//return super.select("SELECT.ENTITY", ConfigManager.getProperty("kr.co.dreamlabs.Framework.Automation.Gen.Product.Code"));
		// for Mysql
		//return super.select("SELECT.MYSQL.ENTITY", ConfigManager.getProperty("kr.co.dreamlabs.Framework.Automation.Gen.Product.Code"));
	}
}
