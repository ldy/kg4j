package svn;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNUtil {

    protected static SVNRepository repository = null;

    protected static SVNClientManager clientManager = null;

    /**
     * 通过不同的协议初始化版本库
     */
    private static void setupLibrary() {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
    }

    /**
     * SVN登录验证
     *
     * @param svnParam
     * @throws SVNException
     *             捕获url格式不正确异常、仓库不存在、用户名或密码不正确异常、IP或者端口不正确异常（连接超时时间比较长）
     */
    public static void SVNAuth(SVNParam svnParam) throws SVNException {

        // 初始化版本库（不需要）
        setupLibrary();

        // 创建库连接
        repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnParam.getSvnUrl()));

        // 身份验证
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(svnParam.getSvnUserName(),
                svnParam.getSvnPassword().toCharArray());
        repository.setAuthenticationManager(authManager);

        // 创建身份验证管理器
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        clientManager = SVNClientManager.newInstance(options, authManager);

        // 获取SVN仓库根URL
        repository.getRepositoryRoot(true);

        // SVN全局设置过滤文件类型
        DefaultSVNOptions svnOptions = (DefaultSVNOptions) clientManager.getOptions();
        svnOptions.addIgnorePattern("*.cache *.cpp *.h SVNConfigure.xml");
    }
}