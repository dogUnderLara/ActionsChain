package com.abap.actionsChain.views;

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import com.sap.adt.tools.abapsource.ui.sources.editors.AbapSourcePage;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;
import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;
import com.sap.ui.controls.swt.composites.panels.Panel;
//need to 

import com.sap.adt.tools.abapsource.ui.sources.prettyprinter.*;
import com.sap.adt.tools.core.actions.*;
import com.sap.adt.tools.core.relations.IAtomLinkToolsCoreRelations;
import com.sap.adt.tools.core.ui.viewer.AdtObjectReferenceFinder;  

import com.abap.actionsChain.utils.ProjectUtility;

import java.awt.AWTException;
import java.awt.Robot;


@SuppressWarnings({ "restriction", "unused" })
public class View extends ViewPart implements ILinkedWithEditorView {
	private Composite parent;
	
//	private static FilteredTree currentTree;

	public static List<LinkedObject> linkedObjects = new ArrayList<>();
	protected IPartListener2 linkWithEditorPartListener = new LinkWithEditorPartListener(this);
	public static LinkedObject linkedObject = new LinkedObject(null, null, null);
	private static Composite container;
	private static GridLayout layout;
	public static View view;
	private SaveCommandListener saveCommandListener = new SaveCommandListener();
	private IEditorPart editor;

	public static ArrayList<Button> ButtonsList = new ArrayList<Button>();

	static boolean debug = true;
	
	ArrayList<String> btn_str_list = new ArrayList<String>() {
		private static final long serialVersionUID = 12L;
		{
			add("Use Abap Cleaner");
			add("Save current File");
			add("Activate current File");
		}
	};
	
	public void createPartControl(Composite parent) {
		addSaveCommandListener();
		this.parent = parent;
		view = this;

		if (container == null) {
			container = new Composite(parent, SWT.NONE);

			layout = new GridLayout();
			layout.numColumns = 1;
			container.setLayout(layout);
			
			Label label = new Label(container, SWT.None);
			label.setText("Disable or Enable your Chain Actions");
			

			  // Create  checkboxes
			createButtons(container);
			
			container.addMouseListener(new MouseAdapter() {

              
				@Override
                public void mouseUp(MouseEvent e) {
            		if (debug) {
            			System.out.println( "In MouseAdapter Buttons2 : " + container );
            			for(Button btn : ButtonsList) {
            					System.out.println( btn );
            			}
            		}
                }
            });
		   
		}
		
		if (debug) {
			System.out.println( "In create PartControl Buttons2 : " + container );
			for(Button btn : ButtonsList) {
					System.out.println( btn );
			}
		}
		
		setLinkingWithEditor();
		linkedObject = ProjectUtility.getObjectFromEditor();
		if (linkedObject != null)
			reloadOutlineContent(false, false, true);
		
		
	}

private void addSaveCommandListener() {
	ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
	commandService.addExecutionListener(saveCommandListener);
}
	

public static IAbapSourcePage getSourcPage() {
	IAdtFormEditor edit = (IAdtFormEditor) linkedObject.getLinkedEditor(); 
	IAbapSourcePage sourcePage = (IAbapSourcePage) edit.getAdapter(AbapSourcePage.class);
	return sourcePage;
}

public static IAdtFormEditor getEditor() {
	IAdtFormEditor edit = (IAdtFormEditor) linkedObject.getLinkedEditor(); 
	return edit;
}

private void getViewerForLinkedObject(Composite parent, LinkedObject linkedObject, boolean refresh) {

	    if (debug) {
	    	for(Button btn : ButtonsList) {
	    		System.out.println( " getViewerForLinkedObject Button:" + btn.getEnabled() + btn.getText() );
	    	

	    	}
		}
		parent.layout();
	}



public void createButtons(Composite container) {
	    	for (String text : btn_str_list ) {
					Button mybtn = new Button(container, SWT.CHECK);
					 mybtn.setText(text);
					 mybtn.setLocation(20, 0);
					 mybtn.addSelectionListener(new SelectionAdapter() {

					        @Override
					        public void widgetSelected(SelectionEvent event) {
					            Button btn = (Button) event.getSource();
					            System.out.println("Listener: "+btn+btn.getSelection());
					        }
					    });
					 ButtonsList.add(mybtn);
	 		    }	
}







	@Override
	public void setFocus() {
		parent.setFocus();
		refreshObject();

	}

	private void refreshObject() {
		if ((linkedObject != null && linkedObject.isEmpty() && (isLinkingActive()))) {
			IProject LinkedProject = ProjectUtility.getActiveAdtProject();
			
			linkedObject = ProjectUtility.getObjectFromEditor();
			if (linkedObject == null || linkedObject.isEmpty()) {
				return;
			}
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					getViewerForLinkedObject(container, linkedObject, false);
				}
			});
		}
	}

	@Override
	public void editorActivated(IEditorPart activeEditor) {
		if (isLinkingActive()) {
			IProject LinkedProject = ProjectUtility.getActiveAdtProject();
			if (LinkedProject == null) {
				getViewerForNonManangedEditors();
				return;
			}
			linkedObject = ProjectUtility.getObjectFromEditor(activeEditor);
			if (linkedObject == null || linkedObject.isEmpty()) {
				// if (activeEditor.)
				getViewerForNonManangedEditors();
				return;
			}
			
		}
	}

	private void getViewerForNonManangedEditors() {
		getViewerForLinkedObject(container, null, false);
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(this.linkWithEditorPartListener);
		removeSaveCommandListener();
		for (Control ctrl : container.getChildren()) {
			System.out.println( "Dispose" ); 
			ctrl.dispose();
		}
		container = null;
		super.dispose();
	}

	private void removeSaveCommandListener() {
		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		commandService.removeExecutionListener(saveCommandListener);
	}

	public boolean isLinkingActive() {
		return true;
	}

	public void callEditorActivationWhenNeeded(final boolean linkingActive) {
		if (linkingActive)
			editorActivated(getSite().getPage().getActiveEditor());
	}

	private void setLinkingWithEditor() {
		final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		workbenchWindow.getPartService().addPartListener(this.linkWithEditorPartListener);
	}

	public void reloadOutlineContent(boolean refresh, boolean async, boolean direct) {
		if (async == true) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					
					linkedObject = ProjectUtility.getObjectFromEditor();
					getViewerForLinkedObject(container, linkedObject, refresh);
				}

			});
		} else if (direct == true) {
			linkedObject = ProjectUtility.getObjectFromEditor();
			getViewerForLinkedObject(container, linkedObject, refresh);
		} else {
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					linkedObject = ProjectUtility.getObjectFromEditor();
					getViewerForLinkedObject(container, linkedObject, refresh);
				}

			});
		}
	}

	public static void destroyLinkedObject(IAdtFormEditor formEditor) {
		if (formEditor.getModel() != null) {
			IProject project = ProjectUtility.getActiveAdtProject();
		}

	}

	


}
