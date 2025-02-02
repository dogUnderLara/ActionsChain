package com.abap.actionsChain.views;

import java.util.List;

import org.eclipse.core.resources.IProject;

import com.sap.adt.activation.ui.IActivationSuccessListener;
import com.sap.adt.tools.core.IAdtObjectReference;

@SuppressWarnings("restriction")
public class ActivationSuccessListenerHandler implements IActivationSuccessListener {

	@Override
	public void onActivationSuccess(List<IAdtObjectReference> adtObject, IProject project) {
		if (View.view != null)
			View.view.reloadOutlineContent(true, true, false);

	}

}
