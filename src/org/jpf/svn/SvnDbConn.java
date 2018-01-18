/** 
 * @author 吴平福 
 * E-mail:wupf@asiainfo-linkage.com 
 * @version 创建时间：2014年12月25日 下午1:31:23 
 * 类说明 
 */

package org.jpf.svn;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 */
public class SvnDbConn
{
	private static final Logger logger = LogManager.getLogger();
	public static SvnDbConn cSvnDbConn = new SvnDbConn();

	public static SvnDbConn getInstance()
	{
		return cSvnDbConn;
	}

	public Connection getConn()
	{
		Connection conn = null;

		try
		{

			String driver = "com.mysql.jdbc.Driver";
			//String driver="com.p6spy.engine.spy.P6SpyDriver";
			String URL = "jdbc:mysql://10.1.228.11:4306/visualwall";
			//String URL = "jdbc:mysql://10.21.38.145/tvwall";
			String dbuser = "sonar";
			String dbpass = "wupflove";
			logger.info("DB URL:" + URL);
			Class.forName(driver).newInstance();

			conn = DriverManager.getConnection(URL, dbuser, dbpass);

		} catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		return conn;
	}

}
