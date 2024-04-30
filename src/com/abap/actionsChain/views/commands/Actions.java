package com.abap.actionsChain.views.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import java.awt.AWTException;
import java.awt.Robot;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import com.abap.actionsChain.views.*;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;
public class Actions extends AbstractHandler {

	boolean  debug = true;
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		runActionChain();
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}
	
	public static void runActionChain() {
		UIJob uiJob = new UIJob("actions chain uiJob") {
			boolean  debug	= true;
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				try {
					for(Button btn : View.ButtonsList) {
						if (!btn.getSelection()) {
							continue;
						}
						
						if("Use Abap Cleaner" == btn.getText()) {
							useAbapCleaner();
						}
						if ("Save current File" == btn.getText()) {
							savecurrentFile(View.getSourcPage(), monitor);
						}
						if ("Activate current File" == btn.getText()) {
							activatFile();
						}
					}

					
					
				} catch (Exception e) {

				}
				return Status.OK_STATUS;
			}
		};
		uiJob.setSystem(true);
		uiJob.schedule();
	}

	public static void useAbapCleaner() {
			System.out.println( "Start useAbabCleaner" );
		
		Robot robot;
		try {
			
			robot = new Robot();
			robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
		    robot.keyPress(java.awt.event.KeyEvent.VK_4);
		    robot.delay(100);
		    robot.keyRelease(java.awt.event.KeyEvent.VK_4);
		    robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
		    robot.delay(200);
		    
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void savecurrentFile(IAbapSourcePage sourcepage, IProgressMonitor monitor) {
			System.out.println( "Start savecurrentFile" );
			sourcepage.doSave(monitor);
	}
	
	public static void activatFile() {
			System.out.println( "Start activatFile" );
		
		Robot robot;
		try {
			
			robot = new Robot();
			robot.delay(100);
			robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
		    robot.keyPress(java.awt.event.KeyEvent.VK_F3);
		    robot.delay(100);
		    robot.keyRelease(java.awt.event.KeyEvent.VK_F3);
		    robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
		    
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
