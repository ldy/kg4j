package svn;

/**
 * 保存SVN信息实体类
 *
 */
public class SVNParam {

    /** svn路径 */
    private String svnUrl;

    /** svn登录名 */
    private String svnUserName;

    /** svn登录密码 */
    private String svnPassword;

    public SVNParam() {
        super();
    }

    public SVNParam(String svnUrl, String svnUserName, String svnPassword) {
        super();
        this.svnUrl = svnUrl;
        this.svnUserName = svnUserName;
        this.svnPassword = svnPassword;
    }

    public String getSvnUrl() {
        return svnUrl;
    }

    public void setSvnUrl(String svnUrl) {
        this.svnUrl = svnUrl;
    }

    public String getSvnUserName() {
        return svnUserName;
    }

    public void setSvnUserName(String svnUserName) {
        this.svnUserName = svnUserName;
    }

    public String getSvnPassword() {
        return svnPassword;
    }

    public void setSvnPassword(String svnPassword) {
        this.svnPassword = svnPassword;
    }

}