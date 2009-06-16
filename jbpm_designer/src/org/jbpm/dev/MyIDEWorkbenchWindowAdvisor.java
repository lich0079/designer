package org.jbpm.dev;

import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.internal.ide.EditorAreaDropAdapter;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchAdvisor;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchWindowAdvisor;
import org.eclipse.ui.part.EditorInputTransfer;
import org.eclipse.ui.part.MarkerTransfer;
import org.eclipse.ui.part.ResourceTransfer;
import org.jbpm.dev.action.NewProcessAction;
import org.jbpm.dev.action.NewProjectAction;

public class MyIDEWorkbenchWindowAdvisor extends IDEWorkbenchWindowAdvisor {

    public MyIDEWorkbenchWindowAdvisor(IDEWorkbenchAdvisor wbAdvisor,
            IWorkbenchWindowConfigurer configurer) {
        super(wbAdvisor, configurer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#createActionBarAdvisor(org.eclipse.ui.application.IActionBarConfigurer)
     */
    public ActionBarAdvisor createActionBarAdvisor(
            IActionBarConfigurer configurer) {
        return new MyWorkbenchActionBuilder(configurer);
    }

    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

        // show the shortcut bar and progress indicator, which are hidden by
        // default
        configurer.setShowPerspectiveBar(false);
        configurer.setShowFastViewBars(true);
        configurer.setShowProgressIndicator(true);

        // add the drag and drop support for the editor area
        configurer.addEditorAreaTransfer(EditorInputTransfer.getInstance());
        configurer.addEditorAreaTransfer(ResourceTransfer.getInstance());
        configurer.addEditorAreaTransfer(FileTransfer.getInstance());
        configurer.addEditorAreaTransfer(MarkerTransfer.getInstance());
        configurer.configureEditorAreaDropListener(new EditorAreaDropAdapter(
                configurer.getWindow()));
        //�������������������titile ��д�Ļ� �漰̫��˽�з��� ��ע��
        // hookTitleUpdateListeners(configurer);
    }

    public void postWindowOpen() {
        super.postWindowOpen();
        //���²ο���runa��ui����
        //�õ�PackageExplorerPart
        final IViewPart[] vs = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage().getViews();
        PackageExplorerPart explorerPart = (PackageExplorerPart) vs[0];
        TreeViewer fViewer = explorerPart.getTreeViewer();
        //��Ӳ˵�
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        Menu fContextMenu = menuMgr.createContextMenu(fViewer.getControl());
        menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        menuMgr.setRemoveAllWhenShown(true);
//        final IAction action=ActionFactory.DELETE.create(getWindowConfigurer().getWindow());
//        getWindowConfigurer().getActionBarConfigurer().registerGlobalAction(action);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                manager.add(new NewProjectAction(null));
                manager.add(new NewProcessAction(null));
//                manager.add(action);
            }
        });
        
        //�����ԭ���Ĳ˵��滻����
        fViewer.getControl().setMenu(fContextMenu);
        //����Ĵ������ע�� ����ᵯ���������ע���action
        // Register viewer with site. This must be done before making the
        // actions.
//        IWorkbenchPartSite site = explorerPart.getSite();
//        site.registerContextMenu(menuMgr, fViewer);
//        site.setSelectionProvider(fViewer);
        
        //���´������������Ҽ�
        // PlatformUI.getWorkbench().getDisplay().addFilter(SWT.MouseUp, new
        // Listener() {
        // public void handleEvent(final Event event) {
        // // if(event.widget.get == vs[0] && event.button == 3) {
        // int hwndCursor = OS.GetCapture ();
        // OS.PostMessage(hwndCursor, OS.WM_LBUTTONDOWN, hwndCursor, OS.HTCLIENT
        // | (OS.WM_MOUSEMOVE << 16));
        // // }
        // }
        // });
    }
}
