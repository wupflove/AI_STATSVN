/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2018年1月11日 下午2:05:14 
* 类说明 
*/ 

package org.jpf.svn;

/**
 * 
 */
public class ChangeFile {

    private String filePath;
    private String fileType;
     
    /**A表示增加文件，M表示修改文件，D表示删除文件，U表示末知
     * 
     */
    private Character changeType;
    private String fileContent;
     
     
     
     
    public ChangeFile() {
    }
    public ChangeFile(String filePath) {
        this.filePath = filePath;
        this.fileType = getFileTypeFromPath(filePath);
    }
    public ChangeFile(String filePath, Character changeType, String fileContent) {
        this.filePath = filePath;
        this.changeType = changeType;
        this.fileContent = fileContent;
        this.fileType = getFileTypeFromPath(filePath);
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public Character getChangeType() {
        return changeType;
    }
    public void setChangeType(Character changeType) {
        this.changeType = changeType;
    }
    public String getFileContent() {
        return fileContent;
    }
    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
     
    private static String getFileTypeFromPath(String path) {
        String FileType = "";
        int idx = path.lastIndexOf(".");
        if (idx > -1) {
            FileType = path.substring(idx + 1).trim().toLowerCase();
        }
        return FileType;
    }

}
