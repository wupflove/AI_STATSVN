/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2018年1月12日 上午11:24:41 
* 类说明 
*/ 

package org.jpf.svn;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 
 */
public class SvnDiffThread extends Thread {
    private static final Logger logger = LogManager.getLogger();
    SvnChangeInf cSvnChangeInf;
    /**
     * 
     */
    public SvnDiffThread(SvnChangeInf cSvnChangeInf) {
        // TODO Auto-generated constructor stub
        this.cSvnChangeInf=cSvnChangeInf;
    }

    public void run()
    {
        try {
            //svn diff -r 132353:132354 http://130.51.23.250/bss_gd/crm/tags/tst_20170601/j2ee/BSSWEB/acctmanm/src/com/linkage/acctx/bmo/amnote/impl/ChargeNotePrintBmo.java --username public --password 123456
            
            for (int i=0;i<cSvnChangeInf.getvChangeFiles().size();i++)
            {
                logger.info(cSvnChangeInf.getSvnUrl());
                logger.info(cSvnChangeInf.getvChangeFiles().get(i).getFilename());
                String strCmd = "svn diff -r " + (Long.parseLong(cSvnChangeInf.getRevision())-1)+ ":" +cSvnChangeInf.getRevision() 
                +" \"http://10.1.195.110:8080/svn/CB20_RES_CENTER"+cSvnChangeInf.getvChangeFiles().get(i).getFilename()+"\""
                + " --username " + cSvnChangeInf.getSvnUsr() + " --password " + cSvnChangeInf.getSvnPwd();
                logger.info(strCmd);
  
                    
                    Runtime rt = Runtime.getRuntime();
                    Process p = rt.exec("cmd.exe /c "+strCmd);
                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));  
                    String line = null;  
                    int iAddCount=0;
                    int iDelCount=0;
                    boolean isBin=false;
                    while ((line = br.readLine()) != null) {  
                        line=line.trim();
                        logger.info("line="+line);
                        if (line.startsWith("+ ") ) {
                            iAddCount++;
                        }
                        if (line.startsWith("- ")  && !isBlank(line.substring(1))) {
                            iDelCount++;
                        }
                        if (line.startsWith("svn:mime-type = application/octet-stream"))
                        {
                            logger.info("二进制类型");
                            isBin=true;
                            break;
                        }
                    }  
                    if (iAddCount>0)
                    {  
                        cSvnChangeInf.getvChangeFiles().get(i).setAddRowCount(iAddCount);
                    }
                    
                    if (iDelCount>0)
                    {
                        cSvnChangeInf.getvChangeFiles().get(i).setDelRowCount(iDelCount);
                    }
                    if (isBin)
                    {
                        cSvnChangeInf.getvChangeFiles().get(i).setFileType("bin");
                    }
                    logger.info("add row=" +cSvnChangeInf.getvChangeFiles().get(i).getAddRowCount() );
                    logger.info("del row=" +cSvnChangeInf.getvChangeFiles().get(i).getDelRowCount() );
            }
        } catch (Exception ex) {
            // TODO: handle exception
        }
    }
    private boolean isBlank(String strInput)
    {
        if (strInput==null || strInput.equalsIgnoreCase(" "))
        {
            return true;
        }
        return false;
    }
}
