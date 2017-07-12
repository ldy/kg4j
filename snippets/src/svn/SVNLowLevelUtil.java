package svn;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNRevision;

public class SVNLowLevelUtil extends SVNUtil {

    /**
     * 创建本地SVN仓库（使用file协议）
     *
     * @param path
     * @return
     * @throws SVNException
     */
    public static SVNURL createRepository(String path) throws SVNException {

        SVNURL svnURL = SVNRepositoryFactory.createLocalRepository(new File(path), true, false);
        // 使用高级接口实现
        // SVNAdminClient adminClient = clientManager.getAdminClient();
        // adminClient.doCreateRepository(new File(path), null, true, false);
        return svnURL;
    }

    /**
     * 检查SVN仓库是否存在path指定的文件或目录
     *
     * @param path
     *            相对SVN地址路径
     * @return
     * @throws SVNException
     */
    public static boolean isExistedInSvn(String path) throws SVNException {

        // SVNNodeKind nodeKind = repository.checkPath(path, -1);
        SVNNodeKind nodeKind = repository.checkPath(path, SVNRevision.HEAD.getNumber());
        if (nodeKind == SVNNodeKind.NONE) {
            return false;
        } else if (nodeKind == SVNNodeKind.DIR) {
            return true;
        } else if (nodeKind == SVNNodeKind.FILE) {
            return true;
        }
        return true;
    }

    /**
     * 获取SVN仓库信息
     *
     * @return
     * @throws SVNException
     */
    public static SVNURL repositoryInfo() throws SVNException {

        // 获取最后版本号
        long latestRevision = repository.getLatestRevision();
        System.out.println("SVN仓库最新版本号：" + latestRevision);

        SVNURL svnURL = repository.getLocation();
        return svnURL;
    }

    /**
     * 获取文件信息
     *
     * @param path
     * @throws SVNException
     */
    public static void fileInfo(String path) throws SVNException {

        // 此变量用来存放要查看的文件的属性名/属性值列表
        SVNProperties fileProperties = new SVNProperties();
        // 此输出流用来存放要查看的文件的内容
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // SVNRevision.HEAD为最新版本，和-1作用一样
        long revision = repository.getFile(path, -1, fileProperties, baos);
        System.out.println("文件版本号为：" + revision);

        // 获取文件的mime-type
        String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);
        // 判断此文件是否是文本文件
        boolean isTextType = SVNProperty.isTextMimeType(mimeType);

        // 显示文件的所有属性
        Iterator<?> iterator = fileProperties.nameSet().iterator();
        while (iterator.hasNext()) {
            String propertyName = (String) iterator.next();
            String propertyValue = fileProperties.getStringValue(propertyName);
            System.out.println("文件的属性\t" + propertyName + "\t" + propertyValue);
        }

        // 如果文件是文本类型，则把文件的内容显示到控制台。
        if (isTextType) {
            System.out.println("该文本文件内容为：");
            try {
                baos.writeTo(System.out);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            System.out.println("改文件为二进制文件类型");
        }

    }

    /**
     * 展示SVN仓库目录树结构
     *
     * @param path
     * @throws SVNException
     */
    public static void repositoryTree(String path) throws SVNException {

        Collection<?> entries = repository.getDir(path, -1, null, (Collection<?>) null);
        Iterator<?> iterator = entries.iterator();
        while (iterator.hasNext()) {
            SVNDirEntry entry = (SVNDirEntry) iterator.next();
            // path=""或者path="一柜通"
            System.out.println((path.equals("") ? "" : path + "/") + entry.getName() + " ( author: '"
                    + entry.getAuthor() + "'; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")");
            if (entry.getKind() == SVNNodeKind.DIR) {
                repositoryTree((path.equals("")) ? entry.getName() : path + "/" + entry.getName());
            }
        }
    }

    /**
     * 获取SVN日志信息
     *
     * @param paths
     * @throws SVNException
     */
    public static void showLog(String[] paths) throws SVNException {

        // Collection<?> logEntries = repository.log(paths, null, 0, -1, true,
        // true);
        Collection<?> logEntries = repository.log(paths, null, 600, -1, true, true);

        for (Iterator<?> entries = logEntries.iterator(); entries.hasNext();) {
            SVNLogEntry logEntry = (SVNLogEntry) entries.next();

            System.out.println("---------------------------------------------");
            System.out.println("revision: " + logEntry.getRevision());
            System.out.println("author: " + logEntry.getAuthor());
            System.out.println("date: " + logEntry.getDate());
            System.out.println("log message: " + logEntry.getMessage());

            // 输出内容改变的文件路径
            if (logEntry.getChangedPaths().size() > 0) {
                System.out.println("\nchanged paths:");

                Set<?> changedPathsSet = logEntry.getChangedPaths().keySet();

                for (Iterator<?> changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
                    SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
                    System.out.println(entryPath.getType() + "\t" + entryPath.getPath()
                            + ((entryPath.getCopyPath() != null) ? " (from " + entryPath.getCopyPath() + " revision "
                            + entryPath.getCopyRevision() + ")" : ""));
                }
            }
        }
    }

    public static void commitBySVNEditor() throws SVNException {

        // Sample file contents.
        byte[] contents = "This is a new file".getBytes();
        byte[] modifiedContents = "This is the same file but modified a little.".getBytes();

        // "" is path relative to that URL, -1表示HEAD (latest) revision.
        SVNNodeKind nodeKind = repository.checkPath("", -1);

        // 检查SVN地址不为目录的情况
        if (nodeKind == SVNNodeKind.NONE) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "No entry at URL ''{0}''");
            throw new SVNException(err);
        } else if (nodeKind == SVNNodeKind.FILE) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN,
                    "Entry at URL ''{0}'' is a file while directory was expected");
            throw new SVNException(err);
        }

        // 打印最新版本号
        long latestRevision = repository.getLatestRevision();
        System.out.println("Repository latest revision (before committing): " + latestRevision);

        // 提交编辑器
        String logMessage = "directory and file added";
        ISVNEditor editor = repository.getCommitEditor(logMessage, null);

        // 包含revision number, author name, commit date and commit message
        SVNCommitInfo commitInfo = SVNLowLevelCommit.addDir(editor, "test", "test/file.txt", contents);
        System.out.println("The directory was added: " + commitInfo);

        // 提交修改
        editor = repository.getCommitEditor("file contents changed", null);
        commitInfo = SVNLowLevelCommit.modifyFile(editor, "test", "test/file.txt", contents, modifiedContents);
        System.out.println("The file was changed: " + commitInfo);

        // Copy用于创建tags or branches或者rename files or directories.
        String absoluteSrcPath = repository.getRepositoryPath("test");
        long srcRevision = repository.getLatestRevision();

        editor = repository.getCommitEditor("directory copied", null);

        commitInfo = SVNLowLevelCommit.copyDir(editor, absoluteSrcPath, "test2", srcRevision);
        System.out.println("The directory was copied: " + commitInfo);

        // 删除目录
        editor = repository.getCommitEditor("directory deleted", null);
        commitInfo = SVNLowLevelCommit.deleteDir(editor, "test");
        System.out.println("The directory was deleted: " + commitInfo);

        // 删除目录
        editor = repository.getCommitEditor("copied directory deleted", null);
        commitInfo = SVNLowLevelCommit.deleteDir(editor, "test2");
        System.out.println("The copied directory was deleted: " + commitInfo);

        // 打印最新版本号
        latestRevision = repository.getLatestRevision();
        System.out.println("Repository latest revision (after committing): " + latestRevision);
    }
}