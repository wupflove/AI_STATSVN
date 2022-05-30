/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2017年7月20日 下午10:08:22 类说明
 */

package org.jpf.svn;

import java.io.File;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;



public class SvnCheckOutUtil {
    // 声明SVN客户端管理类
    private static SVNClientManager ourClientManager;
    private static final Logger logger = LogManager.getLogger();
    
    public static boolean getSVNClientManager()
    {
        return ourClientManager!=null;
    }
    public static void main(String... args) {
        try {

        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
    }

    public static void CheckOut(String strSVNURL, String strUsr, String strPwd, String strWorkPath,
            String strDateTime) throws Exception {
        SVNURL repositoryURL = null;
        ISVNOptions options =null;
        SVNUpdateClient updateClient=null;

            // 初始化支持svn://协议的库。 必须先执行此操作。
            SVNRepositoryFactoryImpl.setup();
            // 相关变量赋值
            
            try {
                repositoryURL = SVNURL.parseURIEncoded(strSVNURL);
            } catch (SVNException e) {
                //
                System.out.println("无法连接");
            }
            options = SVNWCUtil.createDefaultOptions(true);
            // 实例化客户端管理类
            ourClientManager =
                    SVNClientManager.newInstance((DefaultSVNOptions) options, strUsr, strPwd);
            // 要把版本库的内容check out到的目录
            // FIle wcDir = new File("d:/test")
            File wcDir = new File(strWorkPath);
            // 通过客户端管理类获得updateClient类的实例。
            updateClient = ourClientManager.getUpdateClient();
            // sets externals not to be ignored during the checkout
            updateClient.setIgnoreExternals(false);
            Date dateSvn = AiDateTimeUtil.StrToDate(strDateTime);

            long anc = SVNUtil.getSvnRevisionByDate(strSVNURL, strUsr, strPwd, dateSvn);
            //System.out.println(anc);
            // 执行check out 操作，返回工作副本的版本号。
            long workingVersion = updateClient.doCheckout(repositoryURL, wcDir,
                    SVNRevision.parse(String.valueOf(anc)), SVNRevision.parse(String.valueOf(anc)),
                    SVNDepth.INFINITY, false);
            // long workingVersion = updateClient.doCheckout(repositoryURL, wcDir,
            // SVNRevision.HEAD,SVNRevision.HEAD, false);

            logger.info(strSVNURL+" :" + workingVersion + " check out 到目录：" + wcDir + "中。");



    }
}
