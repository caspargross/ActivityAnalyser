package com.vaadin.ui;

import javax.servlet.annotation.WebServlet;

import com.mongodb.MongoClient;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.model.MyAuthentification;
import com.vaadin.model.ReturnCodeHandler;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */

@Theme("mytheme")
@PreserveOnRefresh
public class MyUI extends UI {


    LoginView loginView;
    MainView mainView;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // Set this UI
        UI.setCurrent(this);
        loginView = new LoginView();
        setContent(loginView);
        loginView.loginSubmit.addClickListener(e -> {
               loginUser();
                    //startMainView();

        });

    }

    private void loginUser() {
        // Else: Start Authentification Process
        MyAuthentification auth = new MyAuthentification();
        getPage().setLocation(auth.getAuthURI());

        //setContent(new GoogleAuthFrame(auth.getAuthURI()));
        ReturnCodeHandler returnCodeHandler = new ReturnCodeHandler();
        returnCodeHandler.setMyAuthentification(auth);
        VaadinSession.getCurrent().addRequestHandler(returnCodeHandler);
    }


    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
        /* Vaadin Session parameters
        * This one is accessbile from all Classes */
        String userID;
    }
}
