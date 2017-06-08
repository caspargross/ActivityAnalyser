package com.vaadin.ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;

/**
 * Created by caspar on 08.06.17.
 */
public class MainView extends MainUI implements View {
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        userLabel.setValue((String) UI.getCurrent().getSession().getAttribute("userName"));
    }
}
