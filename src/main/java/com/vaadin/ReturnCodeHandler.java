package com.vaadin;

import com.vaadin.server.*;

import java.io.IOException;

/**
 * Created by caspar on 06.06.17.
 */
public class ReturnCodeHandler implements RequestHandler{

    MyAuthentification myAuthentification = null;

    @Override
    public boolean handleRequest(VaadinSession vaadinSession, VaadinRequest vaadinRequest, VaadinResponse vaadinResponse) throws IOException {
        System.out.print("Request: ");
        System.out.println(vaadinRequest);

        String request = ((VaadinServletRequest) vaadinRequest).getQueryString();
        if (request.startsWith("code")) {
            String code = request.split("=")[1];
            myAuthentification.setCode(code);
        } else if (request.contains("Error")) {
            System.out.println("Google Authentification returned error");
        } else {
            System.out.println("Request not identified: ");
            System.out.println("request");
        }

        System.out.printf("Response: ");
        System.out.println(vaadinResponse);
        return true;
    }

    public void setMyAuthentification(MyAuthentification myAuthentification) {
        this.myAuthentification = myAuthentification;
    }
}
