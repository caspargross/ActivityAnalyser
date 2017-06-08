package com.vaadin.ui;

import javax.servlet.annotation.WebServlet;

import com.vaadin.model.MyAuthentification;
import com.vaadin.model.ReturnCodeHandler;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
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
public class StartUI extends UI {


    /* Vaadin Session parameters */
    String userName;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        LoginView loginView = new LoginView();
        setContent(loginView);
        loginView.loginSubmit.addClickListener(e -> {
            if (!loginView.loginUserID.equals("")){
                loginUser(loginView.loginUserID.getValue());
               userName = loginView.loginUserID.getValue();

            }
        });

    }

    private void loginUser(String userName){

        // Else: Start Authentification Process
        MyAuthentification auth = new MyAuthentification(userName);
        Page.getCurrent().open(auth.getAuthURI(), "_self");

        ReturnCodeHandler returnCodeHandler = new ReturnCodeHandler();
        returnCodeHandler.setMyAuthentification(auth);
        VaadinSession.getCurrent().addRequestHandler(returnCodeHandler);

    }

    private void startMainView() {

    }


    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = StartUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
