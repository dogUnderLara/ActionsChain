package com.abap.actionsChain.utils;


import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.abap.actionsChain.views.LinkedObject;
import com.abap.actionsChain.views.View;
import com.sap.adt.destinations.logon.AdtLogonServiceFactory;
import com.sap.adt.destinations.model.IAuthenticationToken;
import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.destinations.model.internal.AuthenticationToken;
import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;
import com.sap.adt.project.AdtCoreProjectServiceFactory;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;
import com.sap.adt.sapgui.ui.editors.AdtSapGuiEditorUtilityFactory;
import com.sap.adt.tools.abapsource.ui.AbapSourceUi;
import com.sap.adt.tools.abapsource.ui.IAbapSourceUi;
import com.sap.adt.tools.abapsource.ui.sources.IAbapSourceScannerServices;
import com.sap.adt.tools.abapsource.ui.sources.IAbapSourceScannerServices.Token;
import com.sap.adt.tools.abapsource.ui.sources.editors.AbapSourcePage;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourceMultiPageEditor;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;
import com.sap.adt.tools.core.model.adtcore.IAdtObject;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.tools.core.project.IAbapProject;
import com.sap.adt.tools.core.ui.dialogs.AbapProjectSelectionDialog;
import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;
import com.sap.adt.tools.core.ui.internal.navigation.ITextNavigationSource;
import com.sap.adt.tools.core.ui.navigation.AdtNavigationServiceFactory;
import com.sap.adt.tools.core.ui.viewer.AdtObjectReferenceFinder;
import com.sap.adt.tools.core.wbtyperegistry.WorkbenchAction;
import com.sap.rnd.ui.*;

import com.sap.adt.tools.core.relations.IAtomLinkToolsCoreRelations;


import com.sap.adt.tools.core.AdtObjectReference;
import com.sap.adt.tools.abapsource.ui.sources.codeelementinformation.AbapSourceCodeElementInformationProvider;
import com.sap.adt.tools.abapsource.ui.internal.abapdoc.*;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.internal.dialogs.EventLoopProgressMonitor;

import com.sap.adt.activation.ActivationException;
import com.sap.adt.activation.internal.AdtActivationService;
import com.sap.adt.activation.internal.AdtActivationServiceDiscovery;
import com.sap.adt.activation.internal.AdtActivationServiceFactory;
import com.sap.adt.communication.message.IResponse;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.resources.IRestResourceFactory;
import com.sap.adt.communication.resources.ResourceNotFoundException;
import com.sap.adt.activation.AdtActivationBGRunsService;
import com.sap.adt.activation.IAdtActivationBGRunsStatusService;

@SuppressWarnings({ "restriction", "unused" })
public class ProjectUtility {
	static NullProgressMonitor pgmoni;
	public static IProject getActiveAdtProject() {
		try {
			IWorkbenchPage page 	= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IWorkbenchWindow window = page.getWorkbenchWindow();
			ISelection adtSelection = window.getSelectionService().getSelection();
			IProject project = ProjectUtil.getActiveAdtCoreProject(adtSelection, null, null, null);
			return project;
		} catch (Exception e) {
			if (true) {
				System.out.println("ProjectUtility " + e  );
			}
			return null;
		}
	}

	public static String getDestinationID(IProject project) {

		String destinationId = AdtCoreProjectServiceFactory.createCoreProjectService().getDestinationId(project);
		return destinationId;
	}

	public static LinkedObject getObjectFromEditor(IEditorPart editor) {
		if (editor instanceof IAdtFormEditor) {
			IAdtFormEditor formEditor = (IAdtFormEditor) editor;
			IAdtObject model = formEditor.getModel();
			
			if (model != null) {
				int count = 0;
				IProject project = getActiveAdtProject();
				
				while (View.linkedObjects.size() > count) {
					LinkedObject currentlyLinkedObject = View.linkedObjects.get(count);

					if (currentlyLinkedObject.equals(model, project)) {
						return currentlyLinkedObject;
					}
					count++;
				}
				IAbapSourcePage sourcePage = (IAbapSourcePage) editor.getAdapter(AbapSourcePage.class);
				LinkedObject linkedObject = new LinkedObject(formEditor, project, sourcePage);
				
				
				
				
				
				
				
				
				
				
				
//				IFile file = sourcePage.getFile();
//				URI uri = URI.create(file.getFullPath().toString());
//				
//				String dest = getDestinationID(project);
//				IAdtObjectReference ref = formEditor.getModel().getContainerRef();
//
//				IRestResourceFactory restResourceFactory = AdtRestResourceFactory.createRestResourceFactory();
//				IRestResource flightResource = restResourceFactory.createResourceWithStatelessSession(uri, dest);
//
//				
//				
//				try {
//					// Trigger GET request on resource data
//					IResponse response = flightResource.get(null,IResponse.class);
//					
//					// confirm that flight exists
//					System.out.println("Flight exists! HTTP-status: "+String.valueOf(response.getStatus()) );
//					} catch (ResourceNotFoundException e) {
//						System.out.println("No flight data found");
//					}      
				
				
				
				
				View.linkedObjects.add(linkedObject);
				return linkedObject;
			}
		}
		return null;
	}

	


	public static void ensureLoggedOn(IProject project) {
		try {
			IAbapProject abapProject = project.getAdapter(IAbapProject.class);
			AdtLogonServiceUIFactory.createLogonServiceUI().ensureLoggedOn(abapProject.getDestinationData(),
					PlatformUI.getWorkbench().getProgressService());
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}

	public static IEditorPart getActiveEditor() {
		try {
			
			IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
			System.out.println("  getActiveEditor in ProjectUtility.java : " + activeEditor );
			return activeEditor;
		} catch (Exception e) {
			return null;
		}
	}

	public static LinkedObject getObjectFromEditor() {
		return getObjectFromEditor(getActiveEditor());
	}

	public static IAbapSourcePage getAbapSourcePage(IEditorPart editor) {
		try {
			MultiPageEditorPart multiPartEditor = (MultiPageEditorPart) editor;
			IAbapSourcePage sourcePage = (IAbapSourcePage) multiPartEditor.getSelectedPage();
			return sourcePage;
		} catch (Exception e) {
			return null;
		}
	}


	public static IAdtObject getAdtObject(IEditorPart editor) {
		if (editor instanceof IAbapSourceMultiPageEditor) {
			IAbapSourcePage abapSourcePage = editor.getAdapter(IAbapSourcePage.class);
			if (abapSourcePage == null) {
				return null;
			}
			return abapSourcePage.getMultiPageEditor().getModel();
		}
		return null;
	}


	public static IProject getProjectByName(String projectName) {
		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			return project;
		} catch (Exception e) {
			return null;
		}
	}

	public static IProject getProjectFromProjectChooserDialog() {
		return AbapProjectSelectionDialog.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null);
	}

	public static void logonWithPassword(IProject project, String password) {
		IAdtCoreProject adtProject = project.getAdapter(IAdtCoreProject.class);
		IDestinationData destinationData = adtProject.getDestinationData();
		try {
			if (!password.isEmpty()) {
				IAuthenticationToken authenticationToken = new AuthenticationToken();

				authenticationToken.setPassword(password);
				pgmoni = new NullProgressMonitor(); 
				AdtLogonServiceFactory.createLogonService().ensureLoggedOn(destinationData, authenticationToken,
						 pgmoni);
				
				
			}
		} catch (Exception e) {
			ensureLoggedOn(project);
		}
	}
	

	public static void openObject(final IProject project, final IAdtObjectReference adtObjectRef) {
		AdtNavigationServiceFactory.createNavigationService().navigate(project, adtObjectRef, true);
	}


	public static void runObject(final IProject project, final IAdtObjectReference adtObjectRef) {

		AdtSapGuiEditorUtilityFactory.createSapGuiEditorUtility().openEditorForObject(project, adtObjectRef, true,
				WorkbenchAction.EXECUTE.toString(), null, Collections.<String, String>emptyMap());
	}

}
