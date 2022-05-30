/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2017年5月16日 下午3:34:27 
* 类说明 
*/ 

package org.jpf.svn;

/**
 * 
 */
public class SvnChangeInfo {

    /**
     * 
     */
    public SvnChangeInfo() {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @return the changeType
     */
    public String getChangeType() {
        return changeType;
    }
    /**
     * @param changeType the changeType to set
     */
    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }
    /**
     * @return the changeFileName
     */
    public String getChangeFileName() {
        return changeFileName;
    }
    /**
     * @param changeFileName the changeFileName to set
     */
    public void setChangeFileName(String changeFileName) {
        this.changeFileName = changeFileName;
    }

    private String changeType;
    private String changeFileName;
}
