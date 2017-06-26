package svn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import static com.sun.xml.internal.ws.dump.LoggingDumpTube.Position.Before;

public class SVNUtilTest {

    /** SVN参数信息 */
    private SVNParam svnParam;
    /** 工作副本路径 */
    private String path;

    {
        svnParam = new SVNParam("svn://10.201.64.21/kbdp", "ldy", "123");
        path = "C:\\Program Files\\eclipse\\runtime-EclipseApplication\\kbdp";
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        testAuthSvn();
    }

    @After
    public void tearDown() throws Exception {
        // testCleanUp();
    }

    @Test
    public void testAuthSvn() {
        try {
            SVNHighLevelUtil.SVNAuth(svnParam);
            System.out.println("执行testAuthSvn......");
        } catch (SVNException e) {
            e.printStackTrace();
        }
        // fail("Not yet implemented");
    }

    @Test
    public void testCheckout() {
        try {
            File file = new File(path);// 存放工作副本目录
            SVNURL svnurl = SVNURL.parseURIEncoded(svnParam.getSvnUrl());// SVN仓库地址
            // 只生成.svn文件夹，不下载文件
            // SVNHighLevelUtil.checkout(svnurl, file, SVNDepth.EMPTY);
            // 本地目录已经是本地副本，则更新，本地文件改变再执行checkout有可能产生冲突
            SVNHighLevelUtil.checkout(svnurl, file, SVNDepth.INFINITY);
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdate() {
        try {
            // path += File.separator + "folder1";
            // path += File.separator + "folder1/file.txt";
            File file = new File(path);
            SVNHighLevelUtil.update(file, SVNDepth.INFINITY);
            // SVNUtil.resolveConflict(file);
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddEntry() {
        try {
            path += "/folder1/folder3/folder4";
            File file = new File(path);
            SVNHighLevelUtil.addEntry(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCleanUp() {
        try {
            File file = new File(path);
            SVNHighLevelUtil.cleanUp(file);
            System.out.println("执行testCleanUp......");
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLock() {
        try {
            String svnUrl = svnParam.getSvnUrl();
            String[] url = new String[] { svnUrl + "/folder1/file.txt" };
            SVNHighLevelUtil.lock(url);
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUnLock() {
        try {
            String svnUrl = svnParam.getSvnUrl();
            String[] url = new String[] { svnUrl /* + "/folder1/file.txt" */ };
            SVNHighLevelUtil.unLock(url);
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testResolve() {
        try {
            path += "/folder1/file.txt";
            File file = new File(path);
            SVNHighLevelUtil.resolve(file, true);
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRevert() {
        try {
            File file = new File(path);
            SVNHighLevelUtil.revert(file, null);
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testShowStatus() {
        try {
            // path += "/folder1/folder3/a - Copy.h";
            // path += "/folder1/folder3/folder4";
            // path += "/folder1/folder2";
            // path += ".svn";
            // String path = "C:\\Program
            // Files\\eclipse\\runtime-EclipseApplication\\测试工程\\逻辑业务.logicservice\\分组.lsg\\cddd_r.lbs\\服务.sv\\(2342)dwef(KS_wer).xml";
            path += "/一柜通/configure.xml";
            // SVNHighLevelUtil.setIgnoreFile();
            System.out.println("path:" + path);
            File file = new File(path);
            SVNStatus svnStatus = SVNHighLevelUtil.showStatus(file, false);
            System.out.println("svnStatus:" + svnStatus);
            if (svnStatus != null) {
                System.out.println(svnStatus.getContentsStatus());
                System.out.println(svnStatus.getNodeStatus());
                System.out.println(svnStatus.getPropertiesStatus());
                System.out.println(svnStatus.getRemoteContentsStatus());
                System.out.println(svnStatus.getRemoteNodeStatus());
                System.out.println(svnStatus.getRemotePropertiesStatus());
                SVNNodeKind kind = svnStatus.getKind();
                System.out.println(kind.getID());
                System.out.println(svnStatus.isConflicted());
                long localRevision = svnStatus.getCommittedRevision().getNumber();
                long remoteRevision = svnStatus.getRemoteRevision().getNumber();
                System.out.println(localRevision + "---" + remoteRevision);
            }
            System.out.println();

            svnStatus = SVNHighLevelUtil.showStatus(file, true);
            System.out.println("svnStatus:" + svnStatus);
            if (svnStatus != null) {
                System.out.println(svnStatus.getContentsStatus());
                System.out.println(svnStatus.getNodeStatus());
                System.out.println(svnStatus.getPropertiesStatus());
                System.out.println(svnStatus.getRemoteContentsStatus());
                System.out.println(svnStatus.getRemoteNodeStatus());
                System.out.println(svnStatus.getRemotePropertiesStatus());
                SVNNodeKind kind = svnStatus.getKind();
                System.out.println(kind.getID());
                System.out.println(svnStatus.isConflicted());
                long localRevision = svnStatus.getCommittedRevision().getNumber();
                long remoteRevision = svnStatus.getRemoteRevision().getNumber();
                System.out.println(localRevision + "---" + remoteRevision);
            }
        } catch (SVNException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCommit() {
        try {
            // File file = new File(path + "/folder1");
            File file = new File(path + "/folder1/folder3/folder4");
            SVNHighLevelUtil.addEntry(file);

            File tempFile = new File(file.getAbsolutePath());
            List<File> list = new ArrayList<>();
            while (!SVNWCUtil.isWorkingCopyRoot(tempFile)) {
                list.add(tempFile);
                tempFile = tempFile.getParentFile();
            }

            // list.add(file);

            // String commitMessage = "Commit to svn";
            for (File f : list.toArray(new File[list.size()]))
                System.out.println(f.getAbsolutePath());
            SVNHighLevelUtil.commit(list.toArray(new File[list.size()]), file, false, null, SVNDepth.INFINITY);

            // System.out.println(SVNWCUtil.isVersionedDirectory(file));
            // System.out.println(SVNWCUtil.isWorkingCopyRoot(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testImportDirectory() {
        try {
            File localPath = new File(path + "/bin");
            String svnUrl = svnParam.getSvnUrl() + "/folder1/folder2/";
            SVNHighLevelUtil.importDirectory(localPath, svnUrl, true);
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMakeDirectory() {
        try {
            String svnUrl = svnParam.getSvnUrl();
            String[] url = new String[] { svnUrl + "/folder1/folder2", svnUrl + "/folder3/folder4" };
            if (!SVNLowLevelUtil.isExistedInSvn("folder3/folder4")
                    && !SVNLowLevelUtil.isExistedInSvn("folder1/folder2")) {
                SVNHighLevelUtil.makeDirectory(url, "在SVN上创建目录");
            }
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDiff() {
        try {
            File file = new File(path + "/一柜通/configure.xml");
            // File file = new File(path + "/一柜通/configure.xml.mine");
            // File file = new File(path + "/一柜通/configure.xml.r558");
            String diff = SVNHighLevelUtil.diff(file);
            System.out.println(diff);
            System.out.println("");
        } catch (SVNException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMerge() {
        try {
            String path = "C:\\Program Files\\eclipse\\runtime-EclipseApplication\\kbdp";
            File file = new File(path);
            SVNHighLevelUtil.merge(file);
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSetIgnoreFile() {
        try {
            SVNHighLevelUtil.setIgnoreFile();

            // path += "/folder1/folder3/folder4";
            // File file = new File(path);
            // SVNHighLevelUtil.addEntry(file);

        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    /**
     * 华丽丽的分割线------HighLevl API和LowLevel API
     */
    @Test
    public void testIsExistedInSvn() {
        try {
            boolean existedInSvn = SVNLowLevelUtil.isExistedInSvn("");
            // boolean existedInSvn = SVNUtil.isExistedInSvn(".project");
            System.out.println(existedInSvn);
            SVNLowLevelUtil.repositoryInfo();
        } catch (SVNException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}