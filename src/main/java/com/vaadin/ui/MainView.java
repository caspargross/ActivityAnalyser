package com.vaadin.ui;

import com.google.api.client.auth.oauth2.Credential;
import com.mongodb.util.JSON;
import com.vaadin.model.DbConnector;
import com.vaadin.model.DataRequest;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinSession;

import java.io.IOException;


/**
 * Created by caspar on 08.06.17.
 */
public class MainView extends MainDesign implements View {
    String userID;
    Credential myCredential;

    public MainView() {
        this.userID = (String) VaadinSession.getCurrent().getAttribute("userID");

        updateUserProfile();
        downloadData();
    }

    public void updateUserProfile() {
        userLabel.setValue("User ID: " + userID);
        image.setSource(new ExternalResource(DbConnector.extractUserPicture(userID)));
        nameLabel.setValue(DbConnector.extractUserRealName(userID));
    }

    public void downloadData() {
        DataRequest dataRequest = new DataRequest();
        DbConnector dbConnector = new DbConnector();
        try {
            dbConnector.storeSteps(dataRequest.getFitData());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while downloading Fit Data");
        }
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {


    }

}
