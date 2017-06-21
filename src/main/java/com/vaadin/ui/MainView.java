package com.vaadin.ui;

import com.vaadin.model.DbConnector;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinSession;


/**
 * Created by caspar on 08.06.17.
 */
public class MainView extends MainDesign implements View {
    String userID;

    public MainView() {
        this.userID = (String) VaadinSession.getCurrent().getAttribute("userID");   

        updateUserProfile();
    }

    public void updateUserProfile() {
        userLabel.setValue("User ID: " + userID);
        image.setSource(new ExternalResource(DbConnector.extractUserPicture(userID)));
        nameLabel.setValue(DbConnector.extractUserRealName(userID));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {


    }

}
