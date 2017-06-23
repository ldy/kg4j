package svn;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNConflictChoice;
import org.tmatesoft.svn.core.wc.SVNCopyClient;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNMoveClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * SVN高级API工具类
 *
 *
 */
public class SVNHighLevelUtil extends SVNUtil {

    private static SVNLogClient svnLogClient;
    private static SVNUpdateClient svnUpdateClient;
    private static SVNWCClient svnwcClient;
    private static SVNStatusClient svnStatusClient;
    private static SVNCommitClient svnCommitClient;
    private static SVNMoveClient svnMoveClient;
    private static SVNCopyClient svnCopyClient;
    private static SVNDiffClient svnDiffClient;

    static {
        svnLogClient = clientManager.getLogClient();
        svnLogClient.setEventHandler(new SVNEventHandler());
        svnUpdateClient = clientManager.getUpdateClient();
        svnUpdateClient.setEventHandler(new SVNEventHandler());
        svnwcClient = clientManager.getWCClient();
        svnwcClient.setEventHandler(new SVNEventHandler());
        svnStatusClient = clientManager.getStatusClient();
        svnStatusClient.setEventHandler(new SVNEventHandler());
        svnCommitClient = clientManager.getCommitClient();
        svnCommitClient.setEventHandler(new SVNEventHandler());
        svnMoveClient = clientManager.getMoveClient();
        svnMoveClient.setEventHandler(new SVNEventHandler());
        svnCopyClient = clientManager.getCopyClient();
        svnCopyClient.setEventHandler(new SVNEventHandler());
        svnDiffClient = clientManager.getDiffClient();
        svnDiffClient.setEventHandler(new SVNEventHandler());
    }

    /**
     * 检出SVN项目到本地
     *
     * @param url
     * @param destPath
     * @param depth
     * @return
     * @throws SVNException
     */
    public static long checkout(SVNURL url, File destPath, SVNDepth depth) throws SVNException {

        svnUpdateClient.setIgnoreExternals(false);
        return svnUpdateClient.doCheckout(url, destPath, SVNRevision.HEAD, SVNRevision.HEAD, depth, false);
    }

    /**
     * 更新工作副本
     *
     * @param file
     * @param depth
     * @return
     * @throws SVNException
     */
    public static long update(File file, SVNDepth depth) throws SVNException {

        // 更新，MISSING状态会恢复
        svnUpdateClient.setIgnoreExternals(false);
        return svnUpdateClient.doUpdate(file, SVNRevision.HEAD, depth, true, true);
    }

    /**
     * 添加文件和目录到版本控制
     *
     * @param file
     * @throws SVNException
     */
    public static void addEntry(File file) throws SVNException {

        svnwcClient.doAdd(file, true, false, false, SVNDepth.INFINITY, false, true);
        svnStatusClient.doStatus(file, SVNRevision.HEAD, SVNDepth.INFINITY, false, false, false, false,
                new ISVNStatusHandler() {
                    @Override
                    public void handleStatus(SVNStatus status) throws SVNException {
                        if (SVNStatusType.STATUS_ADDED.equals(status.getNodeStatus())) {
                            if (status.getFile().getName().endsWith(".cache")) {
                                svnwcClient.doRevert(new File[] { status.getFile() }, SVNDepth.EMPTY, null);
                            }
                        }
                    }
                }, null);
    }

    /**
     * SVN清除
     *
     * @param file
     * @throws SVNException
     */
    public static void cleanUp(File file) throws SVNException {
        if (SVNWCUtil.isVersionedDirectory(file)) {
            svnwcClient.doCleanup(file);
        }
    }

    /**
     * SVN锁定
     *
     * @param urls
     * @throws SVNException
     */
    public static void lock(String[] urls) throws SVNException {
        List<SVNURL> list = new ArrayList<>();
        for (String url : urls) {
            SVNURL svnurl = SVNURL.parseURIEncoded(url);
            list.add(svnurl);
        }
        SVNURL[] svnurls = list.toArray(new SVNURL[list.size()]);
        svnwcClient.doLock(svnurls, true, "lock");
    }

    /**
     * SVN解锁
     *
     * @param urls
     * @throws SVNException
     */
    public static void unLock(String[] urls) throws SVNException {
        List<SVNURL> list = new ArrayList<>();
        for (String url : urls) {
            SVNURL svnurl = SVNURL.parseURIEncoded(url);
            list.add(svnurl);
        }
        SVNURL[] svnurls = list.toArray(new SVNURL[list.size()]);
        svnwcClient.doUnlock(svnurls, true);
    }

    /**
     * 解决文件冲突（无法解决树冲突，树冲突需要使用revert）
     *
     * @param file
     * @param localIsRight
     * @throws SVNException
     */
    public static void resolve(File file, boolean localIsRight) throws SVNException {
        SVNConflictChoice conflictChoice = null;

        if (localIsRight) {
            conflictChoice = SVNConflictChoice.MINE_FULL;
        } else {
            conflictChoice = SVNConflictChoice.THEIRS_FULL;
        }

        svnwcClient.doResolve(file, SVNDepth.INFINITY, true, true, true, conflictChoice);
    }

    /**
     * 还原当前版本文件
     *
     * @param file
     * @param changeLists
     * @throws SVNException
     */
    public static void revert(File file, Collection<String> changeLists) throws SVNException {
        File[] path = { file };
        svnwcClient.doRevert(path, SVNDepth.INFINITY, changeLists);
    }

    /**
     * 工作副本状态信息
     *
     * @param file
     * @param remote
     * @return
     * @throws SVNException
     */
    public static SVNStatus showStatus(File file, boolean remote) throws SVNException {
        return svnStatusClient.doStatus(file, remote);
    }

    /**
     * 提交工作副本改变到SVN
     *
     * @param files
     * @param file
     * @param keepLocks
     * @param commitMessage
     * @return
     * @throws SVNException
     */
    public static SVNCommitInfo commit(File[] files, File file, boolean keepLocks, String commitMessage,
                                       SVNDepth svnDepth) throws SVNException {

        // 验证提交文件状态， 若为MISSING,执行delete操作
        svnStatusClient.doStatus(file, SVNRevision.HEAD, SVNDepth.INFINITY, false, false, false, false,
                new ISVNStatusHandler() {

                    @Override
                    public void handleStatus(SVNStatus status) throws SVNException {
                        if (SVNStatusType.STATUS_MISSING.equals(status.getNodeStatus())) {
                            svnwcClient.doDelete(status.getFile(), true, false, false);
                        }
                    }
                }, null);

        // 不会提交未执行add操作的文件，本地删除状态为Missing的文件
        return svnCommitClient.doCommit(files, keepLocks, commitMessage, null, null, true, false, svnDepth);
    }

    /**
     * 导入文件夹到SVN仓库
     *
     * @param localPath
     * @param svnUrl
     * @param isRecursive
     * @return
     * @throws SVNException
     */
    public static SVNCommitInfo importDirectory(File localPath, String svnUrl, boolean isRecursive)
            throws SVNException {
        SVNProperties revisionProperties = null;
        SVNURL dstURL = SVNURL.parseURIEncoded(svnUrl);
        return svnCommitClient.doImport(localPath, dstURL, "import directory to svn", revisionProperties, true, true,
                SVNDepth.fromRecurse(isRecursive), true);
    }

    /**
     * SVN仓库创建目录
     *
     * @param urls
     * @param commitMessage
     * @return
     * @throws SVNException
     */
    public static SVNCommitInfo makeDirectory(String[] urls, String commitMessage) throws SVNException {
        SVNProperties revisionProperties = null;
        List<SVNURL> list = new ArrayList<>();
        for (String url : urls) {
            SVNURL svnurl = SVNURL.parseURIEncoded(url);
            list.add(svnurl);
        }
        SVNURL[] svnurls = list.toArray(new SVNURL[list.size()]);
        return svnCommitClient.doMkDir(svnurls, commitMessage, revisionProperties, true);
    }

    /**
     * 比较文件在两个版本之间的区别
     *
     * @param file
     * @throws SVNException
     * @throws IOException
     */
    public static String diff(File file) throws SVNException, IOException {

        OutputStream os = new ByteArrayOutputStream();

        // 比较file文件的SVNRevision.WORKING版本和SVNRevision.HEAD版本的差异
        // SVNRevision.WORKING版本指工作副本中当前内容的版本，SVNRevision.HEAD版本指的是版本库中最新的版本。

        svnDiffClient.doDiff(file, SVNRevision.HEAD, SVNRevision.WORKING, SVNRevision.HEAD, SVNDepth.INFINITY, true, os,
                null);

        // svnDiffClient.doDiffStatus(file, SVNRevision.WORKING,
        // SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY,
        // true, null);
        return os.toString();
    }

    /**
     * svn merge 不可用
     *
     * @param file
     * @throws SVNException
     */
    public static void merge(File file) throws SVNException {
        // SVNDiffClient diffClient = clientManager.getDiffClient();
        SVNStatus status = showStatus(file, false);
        if (status.getContentsStatus() == SVNStatusType.STATUS_MODIFIED) {
            // Collection<String> changeLists = null;
            // OutputStream result = null;
            // try {
            // result = new FileOutputStream("D:/test/mm.xml");
            // } catch (FileNotFoundException e1) {
            // Log.error(null, e1);
            // }
			/*
			 * try { // diffClient.doDiff(file, null, SVNRevision.WORKING, //
			 * SVNRevision.HEAD, SVNDepth.FILES, true, result, changeLists);
			 *
			 * diffClient.setIgnoreExternals(false); SVNURL svnUrl =
			 * SVNURL.parseURIEncoded(
			 * "svn://192.168.10.167/kbdpRepo/record/tree.xml");
			 * ISVNDiffStatusHandler hander = new SVNDiffCommand() {
			 *
			 * @Override public void handleDiffStatus(SVNDiffStatus status)
			 * throws SVNException {
			 * System.out.println(status.getModificationType().toString()); if
			 * (status.getModificationType() == SVNStatusType.STATUS_ADDED) { }
			 * if (status.getModificationType() == SVNStatusType.STATUS_DELETED)
			 * { } System.out.println(status.getFile().getName()); } };
			 * diffClient.doDiffStatus(file, SVNRevision.WORKING, svnUrl,
			 * SVNRevision.HEAD, SVNDepth.FILES, true, hander); // File file1 =
			 * new //
			 * File("D:/Svn/src/KBDP/runtime-EclipseApplication/tree.xml"); //
			 * diffClient.doMerge(file, SVNRevision.HEAD, file1, //
			 * SVNRevision.HEAD, file, SVNDepth.EMPTY, true, false, false, //
			 * false); } catch (SVNException e) { // Log.error(null,e); }
			 */
        }
    }

    /**
     * 设置过滤
     *
     * @throws SVNException
     */
    public static void setIgnoreFile() throws SVNException {

        // 工作空间全局设置过滤文件类型
        DefaultSVNOptions svnOptions = (DefaultSVNOptions) clientManager.getOptions();
        // svnOptions.addIgnorePattern("*.cache *.cpp *.h");
        svnOptions.addIgnorePattern("*.cache");
        svnOptions.addIgnorePattern("*.h");

        // 设置当前文件父级文件的过滤类型（file为工程）
        // SVNPropertyValue propValue = SVNPropertyValue.create("*.cache\n*.h");
        // svnwcClient.doSetProperty(file, SVNProperty.IGNORE, propValue, false,
        // SVNDepth.INFINITY, null, null);

        String[] ignorePatterns = clientManager.getOptions().getIgnorePatterns();
        for (String a : ignorePatterns) {
            System.out.print(a + "\t");
        }
    }

}