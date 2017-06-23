package svn;

/**
 * SVN返回参数实体类
 *
 */
public class SVNReturnParam {

    /** 执行结果：成功或者失败 */
    private boolean exeResult;
    /** 返回信息 */
    private String returnInfo;

    public SVNReturnParam() {
        super();
    }

    public SVNReturnParam(boolean exeResult, String returnInfo) {
        super();
        this.exeResult = exeResult;
        this.returnInfo = returnInfo;
    }

    public boolean isExeResult() {
        return exeResult;
    }

    public void setExeResult(boolean exeResult) {
        this.exeResult = exeResult;
    }

    public String getReturnInfo() {
        return returnInfo;
    }

    public void setReturnInfo(String returnInfo) {
        this.returnInfo = returnInfo;
    }

    @Override
    public String toString() {
        return "SVNReturnParam [exeResult=" + exeResult + ", errInfo=" + returnInfo + "]";
    }

}