/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2018年1月12日 上午10:54:49 
* 类说明 
*/ 

package org.jpf.svn;

/**
 * 
 */
public class SvnChangeFile {

    private String action;
    private String filename;
    private String fileType;
    private long addRowCount;
    private long delRowCount;
    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }
    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }
    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }
    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
    /**
     * @return the fileType
     */
    public String getFileType() {
        return fileType;
    }
    /**
     * @param fileType the fileType to set
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    /**
     * @return the addRowCount
     */
    public long getAddRowCount() {
        return addRowCount;
    }
    /**
     * @param addRowCount the addRowCount to set
     */
    public void setAddRowCount(long addRowCount) {
        this.addRowCount = addRowCount;
    }
    /**
     * @return the delRowCount
     */
    public long getDelRowCount() {
        return delRowCount;
    }
    /**
     * @param delRowCount the delRowCount to set
     */
    public void setDelRowCount(long delRowCount) {
        this.delRowCount = delRowCount;
    }
    /**
     * 
     */
    public SvnChangeFile() {
        // TODO Auto-generated constructor stub
    }

}
