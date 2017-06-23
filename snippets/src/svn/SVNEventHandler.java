package svn;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;
import org.tmatesoft.svn.core.wc.SVNStatusType;

public class SVNEventHandler implements ISVNEventHandler {

    @Override
    public void checkCancelled() throws SVNCancelException {

    }

    @Override
    public void handleEvent(SVNEvent event, double progress) throws SVNException {

        SVNEventAction action = event.getAction();
        // 更新
        if (action == SVNEventAction.UPDATE_STARTED) {
            System.out.println("Updating  " + event.getFile().getPath());
        } else if (action == SVNEventAction.UPDATE_COMPLETED) {
            System.out.println("Completed  At Revision:" + event.getRevision());
        } else if (action == SVNEventAction.UPDATE_ADD) {
            System.out.println("Added  " + event.getFile().getPath());
        } else if (action == SVNEventAction.UPDATE_DELETE) {
            System.out.println("Deleted  " + event.getFile().getPath());
        } else if (action == SVNEventAction.RESTORE) {
            System.out.println("Restored " + event.getFile().getPath());
            // if (event.getFile().exists()) { event.getFile().delete(); }
        } else if (action == SVNEventAction.UPDATE_UPDATE) {
            SVNNodeKind kind = event.getNodeKind();
            System.out.println("Updated  " + event.getFile().getPath());
            if (event.getContentsStatus() == SVNStatusType.CHANGED && kind == SVNNodeKind.FILE) {
                System.out.println("Modified " + event.getFile().getPath());
            } else if (event.getPropertiesStatus() == SVNStatusType.MERGED) {
                System.out.println("Merged " + event.getFile().getPath());
            } else if (event.getLockStatus() == SVNStatusType.LOCK_UNLOCKED) {
                System.out.println("modified " + event.getFile().getPath());
            }
        }

        // 提交
        if (action == SVNEventAction.COMMIT_COMPLETED) {
            long revision = event.getRevision();
            System.out.println("Completed  At Revision:" + revision);
        } else if (action == SVNEventAction.COMMIT_ADDED) {
            String mimeType = event.getMimeType();
            if (SVNProperty.isBinaryMimeType(mimeType)) {// 二进制文件
                System.out.println("Adding  (bin) " + event.getFile().getPath());
            } else {
                System.out.println("Adding  " + event.getFile().getPath());
            }
        } else if (action == SVNEventAction.COMMIT_DELETED) {
            System.out.println("Deleting  " + event.getFile().getPath());
        } else if (action == SVNEventAction.COMMIT_MODIFIED) {
            System.out.println("Modified  " + event.getFile().getPath());
        } else if (action == SVNEventAction.COMMIT_REPLACED) {
            System.out.println("Replaced  " + event.getFile().getPath());
        } else if (action == SVNEventAction.COMMIT_FINALIZING) {
            System.out.println("Commiting transcation...");
        } else if (action == SVNEventAction.COMMIT_DELTA_SENT) {
            System.out.println("Sending content  " + event.getFile().getPath());
        }

        if (action == SVNEventAction.ADD) {
            System.out.println("Add " + event.getFile().getPath());
        }
        if (action == SVNEventAction.DELETE) {
            System.out.println("Delete " + event.getFile().getPath());
        }
        if (action == SVNEventAction.RESOLVED) {
            System.out.println("Resolved " + event.getFile().getPath());
        }
        if (action == SVNEventAction.REVERT) {
            System.out.println("Revert " + event.getFile().getPath());
        }

        // 锁
        if (action == SVNEventAction.LOCKED) {
            System.out.println("Locked " + event.getFile().getPath());
        } else if (action == SVNEventAction.UNLOCKED) {
            System.out.println("Unlocked " + event.getFile().getPath());
        } else if (action == SVNEventAction.UNLOCK_FAILED) {
            System.out.println("Unlock Failed " + event.getFile().getPath());
        } else if (action == SVNEventAction.LOCK_FAILED) {
            System.out.println("Lock Failed " + event.getFile().getPath());
        }

        // SVNStatusType propertiesStatus = event.getPropertiesStatus();
        // SVNStatusType lockType = event.getLockStatus();
    }

}