package svn;

import java.io.ByteArrayInputStream;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;

public class SVNLowLevelCommit {
    /**
     * commit an addition of a directory containing a file.
     */
    public static SVNCommitInfo addDir(ISVNEditor editor, String dirPath, String filePath, byte[] data)
            throws SVNException {
        // Opens the current root directory
        editor.openRoot(-1);
        // Adds a new directory
        editor.addDir(dirPath, null, -1);
        // Adds a new file to the just added directory
        editor.addFile(filePath, null, -1);
        // applying delta to the file
        editor.applyTextDelta(filePath, null);
        // Use delta generator utility class to generate and send delta
        SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
        String checksum = deltaGenerator.sendDelta(filePath, new ByteArrayInputStream(data), editor, true);

        // Closes the new added file.
        editor.closeFile(filePath, checksum);
        // Closes the new added directory.
        editor.closeDir();
        // Closes the root directory.
        editor.closeDir();
        // As a result the server sends the new commit information
        return editor.closeEdit();
    }

    /**
     * commit file modifications.
     */
    public static SVNCommitInfo modifyFile(ISVNEditor editor, String dirPath, String filePath, byte[] oldData,
                                           byte[] newData) throws SVNException {
        // Opens the current root directory
        editor.openRoot(-1);
        // Opens a next subdirectory
        editor.openDir(dirPath, -1);
        // Opens the file added in the previous commit
        editor.openFile(filePath, -1);
        // applying and writing the file delta.
        editor.applyTextDelta(filePath, null);

        // Use delta generator utility class to generate and send delta
        SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
        String checksum = deltaGenerator.sendDelta(filePath, new ByteArrayInputStream(oldData), 0,
                new ByteArrayInputStream(newData), editor, true);

        // Closes the file.
        editor.closeFile(filePath, checksum);
        // Closes the directory.
        editor.closeDir();
        // Closes the root directory.
        editor.closeDir();
        // As a result the server sends the new
        return editor.closeEdit();
    }

    /**
     * commit a deletion of a directory.
     */
    public static SVNCommitInfo deleteDir(ISVNEditor editor, String dirPath) throws SVNException {
        // Opens the current root directory
        editor.openRoot(-1);
        // Deletes the subdirectory with all its contents.
        editor.deleteEntry(dirPath, -1);
        // Closes the root directory.

        editor.closeDir();
        // As a result the server sends the new
        return editor.closeEdit();
    }

    /**
     * a directory in the repository be copied to branch
     */
    public static SVNCommitInfo copyDir(ISVNEditor editor, String srcDirPath, String dstDirPath, long revision)
            throws SVNException {
        // Opens the current root directory
        editor.openRoot(-1);
        // Adds a new directory that is a copy of the existing one
        editor.addDir(dstDirPath, srcDirPath, revision);
        // Closes the just added copy of the directory.
        editor.closeDir();
        // Closes the root directory.
        editor.closeDir();
        // As a result the server sends the new
        return editor.closeEdit();
    }

}