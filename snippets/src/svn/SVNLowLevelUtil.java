package svn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

public class SVNLowLevelUtil extends SVNUtil {

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

    public static void listEntries(String path) throws SVNException {
        Collection<?> entries = repository.getDir(path, -1, null, (Collection<?>) null);
        Iterator<?> iterator = entries.iterator();
        while (iterator.hasNext()) {
            SVNDirEntry entry = (SVNDirEntry) iterator.next();
            System.out.println("/" + (path.equals("") ? "" : path + "/") + entry.getName() + " ( author: '"
                    + entry.getAuthor() + "'; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")");
            if (entry.getKind() == SVNNodeKind.DIR) {
                listEntries((path.equals("")) ? entry.getName() : path + "/" + entry.getName());
            }
        }
    }

    public static void repositoryInfo() throws SVNException {
        SVNURL location = repository.getLocation();
        System.out.println("getPath:" + location.getPath());
        System.out.println("getPath:" + location.getUserInfo());
        // System.out.println("getPath:" +
        // location.getDefaultPortNumber("svn"));
        System.out.println("root:" + repository.getRepositoryRoot(true));
        System.out.println("uuid:" + repository.getRepositoryUUID(true));
        // 检查文件
        String path = "b.txt";
        // String path="a";
        // String path="a/c.txt";
        // String path="c.txt";
        // String path="";
        // 检查path目录类型
        SVNNodeKind nodeKind = repository.checkPath(path, -1);
        if (nodeKind == SVNNodeKind.NONE) {
            System.err.println("There is no entry at '" + "'.");
            // System.exit(1);
        } else if (nodeKind == SVNNodeKind.DIR) {
            System.err.println("The entry at '" + "' is a directory.");
            // System.exit(1);
        } else if (nodeKind == SVNNodeKind.FILE) {
            System.err.println("The entry at '" + "' is a file while a directory was expected.");
            // System.exit(1);
        }

        // 获取最后版本号
        long latestRevision = repository.getLatestRevision();
        System.out.println(latestRevision);

        // 此变量用来存放要查看的文件的属性名/属性值列表。
        SVNProperties fileProperties = new SVNProperties();
        // 此输出流用来存放要查看的文件的内容。
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        repository.getFile(path, -1, fileProperties, baos);

        // 获取文件的mime-type
        String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);
        // 判断此文件是否是文本文件
        boolean isTextType = SVNProperty.isTextMimeType(mimeType);
		/*
		 * 显示文件的所有属性
		 */
        Iterator<?> iterator = fileProperties.nameSet().iterator();
        while (iterator.hasNext()) {
            String propertyName = (String) iterator.next();
            String propertyValue = fileProperties.getStringValue(propertyName);
            System.out.println("文件的属性: " + propertyName + "=" + propertyValue);
        }
		/*
		 * 如果文件是文本类型，则把文件的内容显示到控制台。
		 */
        if (isTextType) {
            System.out.println("File contents:");
            try {
                baos.writeTo(System.out);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            System.out.println("因为文件不是文本文件，无法显示！");
        }
    }

    public static void showLog() throws SVNException {
        Collection<?> logEntries = repository.log(new String[] { "" }, null, 0, -1, true, true);
        for (Iterator<?> entries = logEntries.iterator(); entries.hasNext();) {
            SVNLogEntry logEntry = (SVNLogEntry) entries.next();
            System.out.println("---------------------------------------------");
            System.out.println("revision: " + logEntry.getRevision());
            System.out.println("author: " + logEntry.getAuthor());
            System.out.println("date: " + logEntry.getDate());
            System.out.println("log message: " + logEntry.getMessage());

            if (logEntry.getChangedPaths().size() > 0) {
                System.out.println();
                System.out.println("changed paths:");
                Set<?> changedPathsSet = logEntry.getChangedPaths().keySet();

                for (Iterator<?> changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
                    SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
                    System.out.println(" " + entryPath.getType() + " " + entryPath.getPath()
                            + ((entryPath.getCopyPath() != null) ? " (from " + entryPath.getCopyPath() + " revision "
                            + entryPath.getCopyRevision() + ")" : ""));
                }
            }
        }
    }
}