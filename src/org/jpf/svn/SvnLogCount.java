/** 
 * @author ��ƽ�� 
 * E-mail:wupf@asiainfo-linkage.com 
 * @version ����ʱ�䣺2013-8-14 ����5:03:26 
 * ��˵�� 
 */

package org.jpf.svn;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.asiainfo.utils.AiDateTimeUtil;
import com.asiainfo.utils.dbsql.AiDBUtil;

/**
 * 
 */
public class SvnLogCount
{
	private static final Logger LOGGER = LogManager.getLogger();
	private int iAction = 0;
	private String strProdName = "";
	private String strStartDate = "2013-06-01";
	private String strEndDate = AiDateTimeUtil.getToday();
	/**
	 * 
	 */
	public SvnLogCount()
	{
		// TODO Auto-generated constructor stub
		iAction = 1;
		runSvnLogCounts("http://10.10.10.141/svn/svnfiles/source/ob_dev/openbilling60/");
		runSvnLogCounts("http://10.10.10.141/svn/svnfiles/source/ob_dev/openbilling60_cmcc/");
		runSvnLogCounts("http://10.10.10.141/svn/svnfiles/source/ob_dev/openbilling60_ais/");
	}
	public SvnLogCount(String strSvnPath, String strInProdName,String  strInStartDate,String strInEndDate )
	{
		// TODO Auto-generated constructor stub
		if (!strSvnPath.endsWith("/"))
			strSvnPath += "/";
		if (!strSvnPath.startsWith("/"))
			strSvnPath = "/" + strSvnPath;
		strSvnPath = "http://10.10.10.141/svn/svnfiles" + strSvnPath;
		LOGGER.info(strSvnPath);
		this.strProdName = strInProdName;
		this.strStartDate=strInStartDate;
		this.strEndDate=strInEndDate;
		runSvnLogCounts(strSvnPath);
	}
	
	public SvnLogCount(String strSvnPath, String strInProdName)
	{
		// TODO Auto-generated constructor stub
		if (!strSvnPath.endsWith("/"))
			strSvnPath += "/";
		if (!strSvnPath.startsWith("/"))
			strSvnPath = "/" + strSvnPath;
		strSvnPath = "http://10.10.10.141/svn/svnfiles" + strSvnPath;
		LOGGER.info(strSvnPath);
		this.strProdName = strInProdName;
		runSvnLogCounts(strSvnPath);
	}

	private void runSvnLogCount(String strCmd)
	{
		StringBuffer sbBuffer = new StringBuffer();
		try
		{
			String[] cmd = new String[] { "/bin/sh", "-c", strCmd };
			Process process = Runtime.getRuntime().exec(cmd);
			InputStreamReader ir = new InputStreamReader(process.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			String line;

			while ((line = input.readLine()) != null)
			{
				// System.out.println(line);
				// MakeSql(line);
				sbBuffer.append(line).append("\n");
				/*
				 * line=line.trim(); if(line.equals(
				 * "------------------------------------------------------------------------"
				 * )) { isNew=true; }else { isNew=false; }
				 * 
				 * if(!isNew) { if(line.startsWith("r")) {
				 * strUser=line.split("\\|")[1]; }
				 * 
				 * }
				 */
			}
			process.waitFor();
			int iRetValue = process.exitValue();

			makeMailTxt(sbBuffer);
			// SonarMail.sendMail("", makeMailTxt(sbBuffer), "GBK",
			// "SONAR SVN LOGͳ��(�Զ�����)");
		} catch (Exception e)
		{
			e.printStackTrace();

		}
	}

	class DevotionInfo
	{
		String strUsr = "";
		String strActionType = "";
		Double lTimes = 0.0;
		String strNumber = "";
		String dSvnDate = "";

		@Override
		public String toString()
		{
			return dSvnDate + " " + strNumber + " " + strUsr + " " + strActionType + " " + lTimes;
			// return strUsr + " " + strActionType+ " " + lTimes;
		}
	}

	private Vector<DevotionInfo> cDevotionInfos = new Vector<DevotionInfo>();

	private void getKeyString(String strNumber, String strUsrName, String strSvnDate, String strInput, String regEx)
	{

		Pattern pat = Pattern.compile(regEx);
		Matcher mat = pat.matcher(strInput);
		strUsrName = strUsrName.trim();
		if (mat.find())
		{
			int j = mat.group(0).lastIndexOf("###") + 3;
			/*
			 * System.out.println(strUsrName + " " + mat.group(0).substring(j -
			 * 5, j - 3).toUpperCase() + " " + mat.group(0).substring(j,
			 * mat.group(0).length() - 3));
			 */
			// return
			// strUsrName+" "+mat.group(0).substring(j,mat.group(0).length()-3);
			if (1 == iAction)
			{
				// ֻ��ʾʹ��
				boolean isFind = false;
				for (int i = 0; i < cDevotionInfos.size(); i++)
				{
					DevotionInfo cDevotionInfo = (DevotionInfo) cDevotionInfos.get(i);
					if (cDevotionInfo.strUsr.equalsIgnoreCase(strUsrName)
							&& cDevotionInfo.strActionType.equalsIgnoreCase(mat.group(0).substring(j - 5, j - 3)
									.toUpperCase()))
					{
						cDevotionInfo.lTimes += Double
								.parseDouble(mat.group(0).substring(j, mat.group(0).length() - 3));
						isFind = true;
					}
				}

				if (!isFind)
				{
					DevotionInfo cDevotionInfo = new DevotionInfo();
					cDevotionInfo.strUsr = strUsrName;
					cDevotionInfo.lTimes = Double.parseDouble(mat.group(0).substring(j, mat.group(0).length() - 3));
					cDevotionInfo.strActionType = mat.group(0).substring(j - 5, j - 3).toUpperCase();
					cDevotionInfo.dSvnDate = strSvnDate.trim().substring(0, 10);
					cDevotionInfos.add(cDevotionInfo);
				}

			} else
			{
				// �������
				DevotionInfo cDevotionInfo = new DevotionInfo();
				cDevotionInfo.strUsr = strUsrName;
				cDevotionInfo.lTimes = Double.parseDouble(mat.group(0).substring(j, mat.group(0).length() - 3));
				cDevotionInfo.strNumber = strNumber;
				cDevotionInfo.strActionType = mat.group(0).substring(j - 5, j - 3).toUpperCase();
				cDevotionInfo.dSvnDate = strSvnDate.trim().substring(0, 19);
				cDevotionInfos.add(cDevotionInfo);
			}

		}

	}

	private String makeMailTxt(StringBuffer sbBuffer)
	{
		// System.out.println(sbBuffer.toString());
		StringBuffer sbOutBuffer = new StringBuffer();

		String[] strResults = sbBuffer.toString().split(
				"------------------------------------------------------------------------");
		for (int i = 0; i < strResults.length; i++)
		{
			// System.out.println(strResults[i]);
			String[] tmpStrings = strResults[i].split("\\|");
			if (tmpStrings.length > 3)
			{
				// System.out.println(tmpStrings[1]);
				// strShowString=tmpStrings[1];
				String tmpStr = tmpStrings[3];
				if (tmpStr.indexOf("\n\n") > 0)
				{
					// System.out.println(tmpStr.substring(tmpStr.indexOf(strSourceString)));
					tmpStr = tmpStr.substring(tmpStr.indexOf("\n\n")).trim();

					if (tmpStr.length() > 0)
					{

						String regEx = "sonar###vi###[0-9]+min"; // ��ʾa��F
						getKeyString(tmpStrings[0], tmpStrings[1], tmpStrings[2], tmpStr.toLowerCase(), regEx);
						regEx = "sonar###ut###[0-9]+min";
						getKeyString(tmpStrings[0], tmpStrings[1], tmpStrings[2], tmpStr.toLowerCase(), regEx);
						regEx = "sonar###cc###[0-9]+min";
						getKeyString(tmpStrings[0], tmpStrings[1], tmpStrings[2], tmpStr.toLowerCase(), regEx);

					}
					// strShowString+=":"+tmpStr.substring(tmpStr.indexOf("\n\n"));
				}
			}

		}
		sbBuffer.setLength(0);

		return sbOutBuffer.toString();
	}

	/**
	 * 
	 * @param strSvnPath
	 *            ������������TODO �����Խӿ���:TODO ���Գ�����TODO ǰ�ò�����TODO ��Σ� У��ֵ�� ���Ա�ע��
	 *            update 2013-8-16
	 */
	private void runSvnLogCounts(final String strInSvnPath)
	{
		LOGGER.info(" StartDate:=" + strStartDate);
		LOGGER.info(" EndDate:=" + strEndDate);
		String strCmd = "svn log --username build --password 123 -v -r {" + strStartDate + "}:{" + strEndDate
				+ "} " + strInSvnPath;
		LOGGER.info(strCmd);

		runSvnLogCount(strCmd);

		/*
		 * strCmd = "svn log --username build --password 123 -v -r {" +
		 * strStartDate + "}:{" + strEndDate +
		 * "} http://10.10.10.141/svn/svnfiles/source/ob_dev/openbilling60_ais/infosystem/"
		 * ; logger.info(strCmd); // test(); runSvnLogCount(strCmd); strCmd =
		 * "svn log --username build --password 123 -v -r {" + strStartDate +
		 * "}:{" + strEndDate +
		 * "} http://10.10.10.141/svn/svnfiles/source/ob_dev/openbilling60_cmcc/"
		 * ; logger.info(strCmd); // test(); runSvnLogCount(strCmd);
		 */

		System.out.println("total result");

		// ɨ�豾��Ŀ¼�������ݿ��л�ȡ��¼
		if (0 == iAction)
		{
			Connection conn = null;
			try
			{
				conn = SvnDbConn.getInstance().getConn();
				Statement stmt = conn.createStatement();
				conn.setAutoCommit(false);

				for (int i = 0; i < cDevotionInfos.size(); i++)
				{
					DevotionInfo cDevotionInfo = (DevotionInfo) cDevotionInfos.get(i);
					System.out.println(cDevotionInfo.toString());
					cDevotionInfo.strNumber = cDevotionInfo.strNumber.replace("r", "");
					String strSqlString = "delete from hz_usr_svn where action_type='" + cDevotionInfo.strActionType
							+ "' and revision=" + cDevotionInfo.strNumber;
					System.out.println(strSqlString);
					stmt.addBatch(strSqlString);
					strSqlString = "insert into hz_usr_svn(usr_name,action_type,mins,revision,prj_name,svndate) values('"
							+ cDevotionInfo.strUsr
							+ "','" + cDevotionInfo.strActionType
							+ "'," + cDevotionInfo.lTimes
							+ "," + cDevotionInfo.strNumber
							+ ",'" + strProdName + "'"
							+ ",DATE_FORMAT('"+cDevotionInfo.dSvnDate+"' ,'%Y-%m-%d %H:%i:%S'))";

					System.out.println(strSqlString);
					stmt.addBatch(strSqlString);
				}
				stmt.executeBatch();

				// stmt.executeUpdate(sBuffer.toString());
				conn.commit();
				LOGGER.info("commit sql count=" + cDevotionInfos.size());

			} catch (Exception ex)
			{
				ex.printStackTrace();
			} finally
			{
			    AiDBUtil.doClear(conn);
			}
		}else {
			for (int i = 0; i < cDevotionInfos.size(); i++)
			{
				DevotionInfo cDevotionInfo = (DevotionInfo) cDevotionInfos.get(i);
				System.out.println(cDevotionInfo.toString());
			}
		}
		cDevotionInfos.clear();
		LOGGER.info("game over");
	}

	/**
	 * @param args
	 *            ������������TODO �����Խӿ���:TODO ���Գ�����TODO ǰ�ò�����TODO ��Σ� У��ֵ�� ���Ա�ע��
	 *            update 2013-8-14
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		if (2 == args.length)
		{
			SvnLogCount cSvnLogCount = new SvnLogCount(args[0], args[1]);
		}
		if (0 == args.length)
		{
			SvnLogCount cSvnLogCount = new SvnLogCount();

		}
		if (4 == args.length)
		{
			SvnLogCount cSvnLogCount = new SvnLogCount(args[0],args[1],args[2],args[3]);
		}
		/*
		 * System.out.println(Double.parseDouble("1.5")); Double aDouble=1.5;
		 * Double bDouble=2.4; aDouble+=bDouble; System.out.println(aDouble);
		 */
	}

}
