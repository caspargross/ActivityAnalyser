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
import java.util.Calendar;
import java.util.Date;


/**
 * Created by caspar on 08.06.17.
 */
public class MainView extends MainDesign implements View {
    String userID;
    Credential myCredential;
    DbConnector dbConnector;
    Calendar cal;

    public MainView() {
        this.userID = (String) VaadinSession.getCurrent().getAttribute("userID");
        dbConnector = new DbConnector(userID);
        updateUserProfile();
        downloadData();
        cal = Calendar.getInstance();

        LineChart lineChart = new LineChart();

        System.out.println(lastMonth() + "   " + getNow());
        System.out.println(dbConnector.extractData(lastMonth(), getNow()));
        lineChart.setData(dbConnector.extractData(lastMonth(), getNow()));
        contentArea.addComponent(lineChart);
    }

    public void updateUserProfile() {
        userLabel.setValue("User ID: " + userID);
        image.setSource(new ExternalResource(DbConnector.extractUserPicture(userID)));
        nameLabel.setValue(DbConnector.extractUserRealName(userID));
    }

    public void downloadData() {
        DataRequest dataRequest = new DataRequest();

        try {
            dbConnector.storeSteps(dataRequest.getFitData());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while downloading Fit Data");
        }
    }

    public long getNow () {

        return cal.getTimeInMillis();
    }

    public long lastMonth() {
        Calendar lm = (Calendar)cal.clone();
        lm.add(Calendar.MONTH, -1);
        return lm.getTimeInMillis();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }

}
