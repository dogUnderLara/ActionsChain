package com.abap.actionsChain.views;

import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;

@SuppressWarnings("restriction")
public class AbapPageLoadListenerHandler implements IAbapPageLoadListener {

	String destinationId = "";

	public AbapPageLoadListenerHandler(String destinationId) {
		this.destinationId = destinationId;
	}

	@Override
	public void pageLoaded(IAbapSourcePage sourcePage) {

		if (sourcePage.getFile().getProject().getName().equals(getDestinationId())) {
			if (View.view != null) {
				
			}
		}
	}

	@Override
	public String getDestinationId() {
		return destinationId;
	}

}
