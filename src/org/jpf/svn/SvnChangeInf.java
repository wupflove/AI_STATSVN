/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2018年1月12日 上午10:53:21 
* 类说明 
*/ 

package org.jpf.svn;

import java.util.Vector;

/**
 * 
 */
public class SvnChangeInf {

    private String author;
    private String date;
    private String msg;
    private String revision;
    private String SvnUrl;
    private String SvnUsr;
    private String SvnPwd;
    private String action;
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
     * @return the svnUsr
     */
    public String getSvnUsr() {
        return SvnUsr;
    }
    /**
     * @param svnUsr the svnUsr to set
     */
    public void setSvnUsr(String svnUsr) {
        SvnUsr = svnUsr;
    }
    /**
     * @return the svnPwd
     */
    public String getSvnPwd() {
        return SvnPwd;
    }
    /**
     * @param svnPwd the svnPwd to set
     */
    public void setSvnPwd(String svnPwd) {
        SvnPwd = svnPwd;
    }
    /**
     * @return the svnUrl
     */
    public String getSvnUrl() {
        return SvnUrl;
    }
    /**
     * @param svnUrl the svnUrl to set
     */
    public void setSvnUrl(String svnUrl) {
        SvnUrl = svnUrl;
    }
    /**
     * @return the revision
     */
    public String getRevision() {
        return revision;
    }
    /**
     * @param revision the revision to set
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }
    private Vector<SvnChangeFile> vChangeFiles=new Vector<SvnChangeFile>();
    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }
    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }
    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }
    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }
    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }
    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
    /**
     * @return the vChangeFiles
     */
    public Vector<SvnChangeFile> getvChangeFiles() {
        return vChangeFiles;
    }
    /**
     * @param vChangeFiles the vChangeFiles to set
     */
    public void setvChangeFiles(Vector<SvnChangeFile> vChangeFiles) {
        this.vChangeFiles = vChangeFiles;
    }
    /**
     * 
     */
    public SvnChangeInf() {
        // TODO Auto-generated constructor stub
    }

}
