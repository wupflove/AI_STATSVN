/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2018年1月11日 下午12:14:11 
* 类说明 
*/ 

package org.jpf.svn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.asiainfo.utils.xmls.AiXmlUtil;

/**
 * 
 */
public class SvnMain {
    private static final Logger logger = LogManager.getLogger();
    
    String SvnUrl="http://10.1.195.110:8080/svn/CB20_RES_CENTER/trunk";
    String SvnUsr="wupf";
    String SvnPwd="edc!@#456";
    /**
     * 
     */
    public SvnMain() {
        // TODO Auto-generated constructor stub
        long start = System.currentTimeMillis();
        try {
            //执行SVN LOG
            getSvnXmlLog_Win();

            //打开XML文件处理
            NodeList cNodeList=AiXmlUtil.getNodeList("logentry", "d:\\svn2.log");
            logger.info(cNodeList.getLength());
            for(int i=0;i<cNodeList.getLength();i++)
            {
                //cNodeList.item(i).getTextContent();
                Element in_el=(Element)cNodeList.item(i);
                logger.info(AiXmlUtil.getParStrValue(in_el, "author"));
                logger.info(AiXmlUtil.getParStrValue(in_el, "date"));
                logger.info(AiXmlUtil.getParStrValue(in_el, "msg"));
                logger.info(in_el.getAttribute("revision"));
                SvnChangeInf cSvnChangeInf=new SvnChangeInf();
                cSvnChangeInf.setSvnUrl(SvnUrl);
                cSvnChangeInf.setSvnPwd(SvnPwd);
                cSvnChangeInf.setSvnUsr(SvnUsr);
                cSvnChangeInf.setAuthor(AiXmlUtil.getParStrValue(in_el, "author"));
                cSvnChangeInf.setDate(AiXmlUtil.getParStrValue(in_el, "date"));
                cSvnChangeInf.setMsg(AiXmlUtil.getParStrValue(in_el, "msg"));
                cSvnChangeInf.setRevision(in_el.getAttribute("revision"));
                cSvnChangeInf.setAction(in_el.getAttribute("action"));
                
                NodeList  cNodeList2=in_el.getElementsByTagName("path");
                logger.info(cNodeList2.getLength());
                for(int j=0;j<cNodeList2.getLength();j++)
                {
                    Element in_el2=(Element)cNodeList2.item(j);
                    logger.info(in_el2.getAttribute("kind") );
                    logger.info(in_el2.getTextContent());
                    
                    SvnChangeFile cSvnChangeFile=new SvnChangeFile();
                    cSvnChangeFile.setFilename(in_el2.getTextContent());
                    cSvnChangeFile.setAction(in_el2.getAttribute("action") );
                    cSvnChangeInf.getvChangeFiles().add(cSvnChangeFile);
                }
                
                SvnDiffThread cSvnDiffThread=new SvnDiffThread(cSvnChangeInf);
                cSvnDiffThread.run();
            }
            
            
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }

        logger.info("ExcuteTime " + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 
     * @category @author 吴平福
     * @param strURL
     * @param strStartDateTime
     * @param strEndStartDateTime
     * @return update 2017年5月16日
     */
    public  void getSvnXmlLog_Win() {

        //svn log -v --xml -r {"2018-01-01"}:{"2018-01-12"} http://130.51.23.250/bss_gd/crm/tags/tst_20170601/j2ee/BSSWEB/acctmanm --username public --password 123456 >svn2.log     
        String strCmd = "svn log -v --xml -r {\"2017-01-01\"}:{\"2018-01-12\"} " + SvnUrl
                + " --username " + SvnUsr + " --password " + SvnPwd
                +" > d:\\svn2.log";
        logger.info(strCmd);
        try {
            
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec("cmd.exe /c "+strCmd);
            p.waitFor();
            int iRetValue = p.exitValue();
            logger.info("cmd return value:"+iRetValue);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    /**
     * @category 
     * @author 吴平福 
     * @param args
     * update 2018年1月11日
     */

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new SvnMain();
    }

}
