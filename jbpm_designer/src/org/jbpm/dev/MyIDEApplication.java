package org.jbpm.dev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.ChooseWorkspaceData;
import org.eclipse.ui.internal.ide.ChooseWorkspaceDialog;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.ui.internal.ide.application.IDEApplication;

public class MyIDEApplication extends IDEApplication {
    
    /**
     * The name of the folder containing metadata information for the workspace.
     */
    public static final String METADATA_FOLDER = ".metadata";

    private static final String VERSION_FILENAME = "version.ini"; //$NON-NLS-1$

    private static final String WORKSPACE_VERSION_KEY = "org.eclipse.core.runtime"; //$NON-NLS-1$

    private static final String WORKSPACE_VERSION_VALUE = "1"; //$NON-NLS-1$

    private static final String PROP_EXIT_CODE = "eclipse.exitcode"; //$NON-NLS-1$

    /**
     * A special return code that will be recognized by the launcher and used to
     * restart the workbench.
     */
    private static final Integer EXIT_RELAUNCH = new Integer(24);

    
    
    
    /* (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext context)
     */
    public Object start(IApplicationContext appContext) throws Exception {
        Display display = createDisplay();

        try {
            Shell shell = new Shell(display, SWT.ON_TOP);

            try {
                if (!checkInstanceLocation(shell)) {
                    Platform.endSplash();
                    return EXIT_OK;
                }
            } finally {
                if (shell != null) {
                    shell.dispose();
                }
            }

            // create the workbench with this advisor and run it until it exits
            // N.B. createWorkbench remembers the advisor, and also registers
            // the workbench globally so that all UI plug-ins can find it using
            // PlatformUI.getWorkbench() or AbstractUIPlugin.getWorkbench()
            int returnCode = PlatformUI.createAndRunWorkbench(display,
                    new MyIDEWorkbenchAdvisor());

            // the workbench doesn't support relaunch yet (bug 61809) so
            // for now restart is used, and exit data properties are checked
            // here to substitute in the relaunch return code if needed
            if (returnCode != PlatformUI.RETURN_RESTART) {
                return EXIT_OK;
            }

            // if the exit code property has been set to the relaunch code, then
            // return that code now, otherwise this is a normal restart
            return EXIT_RELAUNCH.equals(Integer.getInteger(PROP_EXIT_CODE)) ? EXIT_RELAUNCH
                    : EXIT_RESTART;
        } finally {
            if (display != null) {
                display.dispose();
            }
        }
    }

    
    private boolean checkInstanceLocation(Shell shell) {
        // -data @none was specified but an ide requires workspace
        Location instanceLoc = Platform.getInstanceLocation();
        if (instanceLoc == null) {
            MessageDialog
                    .openError(
                            shell,
                            IDEWorkbenchMessages.IDEApplication_workspaceMandatoryTitle,
                            IDEWorkbenchMessages.IDEApplication_workspaceMandatoryMessage);
            return false;
        }

        // -data "/valid/path", workspace already set
        if (instanceLoc.isSet()) {
            // make sure the meta data version is compatible (or the user has
            // chosen to overwrite it).
            if (!checkValidWorkspace(shell, instanceLoc.getURL())) {
                return false;
            }

            // at this point its valid, so try to lock it and update the
            // metadata version information if successful
            try {
                if (instanceLoc.lock()) {
                    writeWorkspaceVersion();
                    return true;
                }
                
                // we failed to create the directory.  
                // Two possibilities:
                // 1. directory is already in use
                // 2. directory could not be created
                File workspaceDirectory = new File(instanceLoc.getURL().getFile());
                if (workspaceDirectory.exists()) {
                    MessageDialog.openError(
                            shell,
                            IDEWorkbenchMessages.IDEApplication_workspaceCannotLockTitle,
                            IDEWorkbenchMessages.IDEApplication_workspaceCannotLockMessage);
                } else {
                    MessageDialog.openError(
                            shell, 
                            IDEWorkbenchMessages.IDEApplication_workspaceCannotBeSetTitle,
                            IDEWorkbenchMessages.IDEApplication_workspaceCannotBeSetMessage);
                }
            } catch (IOException e) {
                IDEWorkbenchPlugin.log("Could not obtain lock for workspace location", //$NON-NLS-1$
                        e);             
                MessageDialog
                .openError(
                        shell,
                        IDEWorkbenchMessages.InternalError,
                        e.getMessage());                
            }            
            return false;
        }

        // -data @noDefault or -data not specified, prompt and set
        ChooseWorkspaceData launchData = new ChooseWorkspaceData(instanceLoc
                .getDefault());

        boolean force = false;
        while (true) {
            URL workspaceUrl = promptForWorkspace(shell, launchData, force);
            if (workspaceUrl == null) {
                return false;
            }

            // if there is an error with the first selection, then force the
            // dialog to open to give the user a chance to correct
            force = true;

            try {
                // the operation will fail if the url is not a valid
                // instance data area, so other checking is unneeded
                if (instanceLoc.setURL(workspaceUrl, true)) {
                    launchData.writePersistedData();
                    writeWorkspaceVersion();
                    return true;
                }
            } catch (IllegalStateException e) {
                MessageDialog
                        .openError(
                                shell,
                                IDEWorkbenchMessages.IDEApplication_workspaceCannotBeSetTitle,
                                IDEWorkbenchMessages.IDEApplication_workspaceCannotBeSetMessage);
                return false;
            }

            // by this point it has been determined that the workspace is
            // already in use -- force the user to choose again
            MessageDialog.openError(shell, IDEWorkbenchMessages.IDEApplication_workspaceInUseTitle, 
                    IDEWorkbenchMessages.IDEApplication_workspaceInUseMessage);
        }
    }

    private boolean checkValidWorkspace(Shell shell, URL url) {
        // a null url is not a valid workspace
        if (url == null) {
            return false;
        }

        String version = readWorkspaceVersion(url);

        // if the version could not be read, then there is not any existing
        // workspace data to trample, e.g., perhaps its a new directory that
        // is just starting to be used as a workspace
        if (version == null) {
            return true;
        }

        final int ide_version = Integer.parseInt(WORKSPACE_VERSION_VALUE);
        int workspace_version = Integer.parseInt(version);

        // equality test is required since any version difference (newer
        // or older) may result in data being trampled
        if (workspace_version == ide_version) {
            return true;
        }

        // At this point workspace has been detected to be from a version
        // other than the current ide version -- find out if the user wants
        // to use it anyhow.
        String title = IDEWorkbenchMessages.IDEApplication_versionTitle;
        String message = NLS.bind(IDEWorkbenchMessages.IDEApplication_versionMessage, url.getFile());

        MessageBox mbox = new MessageBox(shell, SWT.OK | SWT.CANCEL
                | SWT.ICON_WARNING | SWT.APPLICATION_MODAL);
        mbox.setText(title);
        mbox.setMessage(message);
        return mbox.open() == SWT.OK;
    }

    /**
     * Write the version of the metadata into a known file overwriting any
     * existing file contents. Writing the version file isn't really crucial,
     * so the function is silent about failure
     */
    private static void writeWorkspaceVersion() {
        Location instanceLoc = Platform.getInstanceLocation();
        if (instanceLoc == null || instanceLoc.isReadOnly()) {
            return;
        }

        File versionFile = getVersionFile(instanceLoc.getURL(), true);
        if (versionFile == null) {
            return;
        }

        OutputStream output = null;
        try {
            String versionLine = WORKSPACE_VERSION_KEY + '='
                    + WORKSPACE_VERSION_VALUE;

            output = new FileOutputStream(versionFile);
            output.write(versionLine.getBytes("UTF-8")); //$NON-NLS-1$
        } catch (IOException e) {
            IDEWorkbenchPlugin.log("Could not write version file", //$NON-NLS-1$
                    StatusUtil.newStatus(IStatus.ERROR, e.getMessage(), e));
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    /**
     * Open a workspace selection dialog on the argument shell, populating the
     * argument data with the user's selection. Perform first level validation
     * on the selection by comparing the version information. This method does
     * not examine the runtime state (e.g., is the workspace already locked?).
     * 
     * @param shell
     * @param launchData
     * @param force
     *            setting to true makes the dialog open regardless of the
     *            showDialog value
     * @return An URL storing the selected workspace or null if the user has
     *         canceled the launch operation.
     */
    private URL promptForWorkspace(Shell shell, ChooseWorkspaceData launchData,
            boolean force) {
        URL url = null;
        do {
            // don't use the parent shell to make the dialog a top-level
            // shell. See bug 84881.
            new ChooseWorkspaceDialog(null, launchData, false, true).prompt(force);
            String instancePath = launchData.getSelection();
            if (instancePath == null) {
                return null;
            }

            // the dialog is not forced on the first iteration, but is on every
            // subsequent one -- if there was an error then the user needs to be
            // allowed to fix it
            force = true;

            // 70576: don't accept empty input
            if (instancePath.length() <= 0) {
                MessageDialog
                .openError(
                        shell,
                        IDEWorkbenchMessages.IDEApplication_workspaceEmptyTitle,
                        IDEWorkbenchMessages.IDEApplication_workspaceEmptyMessage);
                continue;
            }

            // create the workspace if it does not already exist
            File workspace = new File(instancePath);
            if (!workspace.exists()) {
                workspace.mkdir();
            }

            try {
                // Don't use File.toURL() since it adds a leading slash that Platform does not
                // handle properly.  See bug 54081 for more details.  
                String path = workspace.getAbsolutePath().replace(
                        File.separatorChar, '/');
                url = new URL("file", null, path); //$NON-NLS-1$
            } catch (MalformedURLException e) {
                MessageDialog
                        .openError(
                                shell,
                                IDEWorkbenchMessages.IDEApplication_workspaceInvalidTitle,
                                IDEWorkbenchMessages.IDEApplication_workspaceInvalidMessage);
                continue;
            }
        } while (!checkValidWorkspace(shell, url));

        return url;
    }

    /**
     * Look at the argument URL for the workspace's version information. Return
     * that version if found and null otherwise.
     */
    private static String readWorkspaceVersion(URL workspace) {
        File versionFile = getVersionFile(workspace, false);
        if (versionFile == null || !versionFile.exists()) {
            return null;
        }

        try {
            // Although the version file is not spec'ed to be a Java properties
            // file, it happens to follow the same format currently, so using
            // Properties to read it is convenient.
            Properties props = new Properties();
            FileInputStream is = new FileInputStream(versionFile);
            try {
                props.load(is);
            } finally {
                is.close();
            }

            return props.getProperty(WORKSPACE_VERSION_KEY);
        } catch (IOException e) {
            IDEWorkbenchPlugin.log("Could not read version file", new Status( //$NON-NLS-1$
                    IStatus.ERROR, IDEWorkbenchPlugin.IDE_WORKBENCH,
                    IStatus.ERROR,
                    e.getMessage() == null ? "" : e.getMessage(), //$NON-NLS-1$, 
                    e));
            return null;
        }
    }

    /**
     * The version file is stored in the metadata area of the workspace. This
     * method returns an URL to the file or null if the directory or file does
     * not exist (and the create parameter is false).
     * 
     * @param create
     *            If the directory and file does not exist this parameter
     *            controls whether it will be created.
     * @return An url to the file or null if the version file does not exist or
     *         could not be created.
     */
    private static File getVersionFile(URL workspaceUrl, boolean create) {
        if (workspaceUrl == null) {
            return null;
        }

        try {
            // make sure the directory exists
            File metaDir = new File(workspaceUrl.getPath(), METADATA_FOLDER);
            if (!metaDir.exists() && (!create || !metaDir.mkdir())) {
                return null;
            }

            // make sure the file exists
            File versionFile = new File(metaDir, VERSION_FILENAME);
            if (!versionFile.exists()
                    && (!create || !versionFile.createNewFile())) {
                return null;
            }

            return versionFile;
        } catch (IOException e) {
            // cannot log because instance area has not been set
            return null;
        }
    }

}
