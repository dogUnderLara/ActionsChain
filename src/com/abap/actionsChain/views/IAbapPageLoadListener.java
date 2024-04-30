package com.abap.actionsChain.views;

import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;

@SuppressWarnings("restriction")
public interface IAbapPageLoadListener {
	void pageLoaded(IAbapSourcePage sourcePage);

	String getDestinationId();

}
