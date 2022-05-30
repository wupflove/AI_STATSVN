/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2017年5月10日 下午4:29:13 类说明
 */

package org.jpf.svn;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;




/* 此类用来比较某个文件两个版本的差异 */
public class SvnDoDiff {
    // 声明SVN客户端管理类
    private static SVNClientManager ourClientManager;
    private static final Logger logger = LogManager.getLogger();

    String SvnUrl="http://130.51.23.250/";
    String SvnUsr="public";
    String SvnPwd="123456";
    public static void main(String[] args) throws Exception {
        new SvnDoDiff();
    }
    public SvnDoDiff()
    {
        long start = System.currentTimeMillis();
        try {
            Vector<SvnChangeInfo> vChangeInfos=new Vector<SvnChangeInfo>();
            //getSvnDiff(SvnUrl,"2018-01-01 00:00:00","2018-01-11 00:00:00",vChangeInfos,SvnUsr,SvnPwd);
            doSvnDiff();
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }

        logger.info("ExcuteTime " + (System.currentTimeMillis() - start) + "ms");
    }
    
    final static String regEx = "['   ']+";
    
    public  void getSvnDiff(String strURL, String strStartDateTime,
            String strEndStartDateTime, Vector<SvnChangeInfo> vChangeInfos, String UserName,
            String PassWord) 
    {
        if (AiOsUtil.isWindows())
        {
            getSvnDiff_Win(strURL, strStartDateTime, strEndStartDateTime, vChangeInfos);
        }else {
            getSvnDiff_Linux(strURL, strStartDateTime, strEndStartDateTime, vChangeInfos);
        }
    }
    /**
     * 
     * @category @author 吴平福
     * @param strURL
     * @param strStartDateTime
     * @param strEndStartDateTime
     * @return update 2017年5月16日
     */
    public  void getSvnDiff_Win(String strURL, String strStartDateTime,
            String strEndStartDateTime, Vector<SvnChangeInfo> vChangeInfos) {
        if (strURL == null || strURL.trim().length() == 0) {
            return;
        }
        if (strStartDateTime == null || strStartDateTime.trim().length() == 0) {
            return;
        }
        if (strEndStartDateTime == null || strEndStartDateTime.trim().length() == 0) {
            return;
        }

        String strCmd = "svn diff -r {\"" + strStartDateTime + "\"}:{\""+strEndStartDateTime+"\"} --summarize " + strURL
                + " --username " + SvnUsr + " --password " + SvnPwd;
        // svn diff -r {"2017-05-01 00:00:00"}:{"2017-05-16 00:00:00"} --summarize
        // http://10.3.3.233/svn/products/openboss/newcrm_jx_modules/newcrm_jx_ams
        logger.info(strCmd);
        try {
            
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec("cmd.exe /c "+strCmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));  
            String line = null;  
            StringBuilder sb = new StringBuilder();  
            while ((line = br.readLine()) != null) {  
                sb.append(line + "\n");  
                line = line.trim();
                if (line.endsWith(".java")) {
                    String[] mResult = line.split(regEx);
                    if (2 == mResult.length) {
                        SvnChangeInfo cSvnChangeInfo = new SvnChangeInfo();
                        cSvnChangeInfo.setChangeFileName(mResult[1]);
                        cSvnChangeInfo.setChangeType(mResult[0]);
                        vChangeInfos.add(cSvnChangeInfo);
                    }
                    System.out.println(line);
                }
            }  
            logger.info("gVector.size=" + vChangeInfos.size());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    /**
     * 
     * @category @author 吴平福
     * @param strURL
     * @param strStartDateTime
     * @param strEndStartDateTime
     * @return update 2017年5月16日
     */
    public void getSvnDiff_Linux(String strURL, String strStartDateTime,
            String strEndStartDateTime, Vector<SvnChangeInfo> vChangeInfos) {
        if (strURL == null || strURL.trim().length() == 0) {
            return;
        }
        if (strStartDateTime == null || strStartDateTime.trim().length() == 0) {
            return;
        }
        if (strEndStartDateTime == null || strEndStartDateTime.trim().length() == 0) {
            return;
        }

        String strCmd = "svn diff -r {\"" + strStartDateTime + "\"}:{\""+strEndStartDateTime+"\"} --summarize " + strURL
                + " --username " + SvnUsr + " --password " + SvnPwd;
        // svn diff -r {"2017-05-01 00:00:00"}:{"2017-05-16 00:00:00"} --summarize
        // http://10.3.3.233/svn/products/openboss/newcrm_jx_modules/newcrm_jx_ams
        logger.info(strCmd);
        try {
            String[] cmd = new String[] {"bash", "-c", strCmd};
            Process process = Runtime.getRuntime().exec(cmd);
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            String line;
            while ((line = input.readLine()) != null) {

                line = line.trim();
                if (line.endsWith(".java")) {
                    String[] mResult = line.split(regEx);
                    if (2 == mResult.length) {
                        SvnChangeInfo cSvnChangeInfo = new SvnChangeInfo();
                        cSvnChangeInfo.setChangeFileName(mResult[1]);
                        cSvnChangeInfo.setChangeType(mResult[0]);
                        vChangeInfos.add(cSvnChangeInfo);
                    }
                    System.out.println(line);
                }
            }
            process.waitFor();
            int iRetValue = process.exitValue();

            // System.out.println(sBuffer);
            logger.info("gVector.size=" + vChangeInfos.size());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 
     * @category @author 吴平福
     * @throws Exception update 2017年5月16日
     */
    public  void doSvnDiff() throws Exception {
        // 初始化支持svn://协议的库。 必须先执行此操作。
        SVNRepositoryFactoryImpl.setup();
        // 相关变量赋值
        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        // 实例化客户端管理类
        ourClientManager =
                SVNClientManager.newInstance((DefaultSVNOptions) options, SvnUsr, SvnPwd);
        // 要比较的文件
        File compFile =
                new File("F:\\svn\\svn130.51.23.250\\acctmanm\\src\\com\\linkage\\acctcore\\data\\SessionKey.java");
        // 获得SVNDiffClient类的实例。
        SVNDiffClient diff = ourClientManager.getDiffClient();
        // 保存比较结果的输出流
        BufferedOutputStream result =
                new BufferedOutputStream(new FileOutputStream("d:/result.txt"));
        // 比较compFile文件的SVNRevision.WORKING版本和 SVNRevision.HEAD版本的差异，结果保存在E:/result.txt文件中。
        // SVNRevision.WORKING版本指工作副本中当前内容的版本，SVNRevision.HEAD版本指的是版本库中最新的版本。
        diff.doDiff(compFile,null, SVNRevision.WORKING, SVNRevision.PREVIOUS,
                SVNDepth.INFINITY, true, result, null);
        //diff.doDiff(compFile, SVNRevision.HEAD, SVNRevision.WORKING, SVNRevision.HEAD,                SVNDepth.INFINITY, true, result, null);
        //diff.doDiff(SVNURL.parseURIDecoded(url), SVNRevision.create(oldVersion), SVNURL.parseURIDecoded(url), SVNRevision.HEAD, false, false, System.out);

         result.close();
        logger.info("比较的结果保存在d:/result.txt文件中！");

    }
    /*
svn log -v --xml -r {"2018-01-01"}:{"2018-01-12"} http://130.51.23.250/bss_gd/crm/tags/tst_20170601/j2ee/BSSWEB/acctmanm --username public --password 123456 >svn2.log     
svn diff -r 132353:132354 http://130.51.23.250/bss_gd/crm/tags/tst_20170601/j2ee/BSSWEB/acctmanm/src/com/linkage/acctx/bmo/amnote/impl/ChargeNotePrintBmo.java --username public --password 123456
     * */
}
