package com.vaadin.model;

import com.google.api.client.auth.oauth2.Credential;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;

/**
 * Created by caspar on 12.06.17.
 */
public class DbConnector extends MongoClient {

    MongoDatabase db;

    public DbConnector() {

        // Accesses DB, creates an Instance if it does not exist yet.
        this.db = getDatabase("trackFitDB");
    }

    public boolean findUser (String userName) {
        //db.getCollection("users")

        return true;
    }

    public void createUser(String userName, String googleUserData) {

        MongoCollection<Document> userColl = db.getCollection("users");
        Document userDoc = Document.parse(googleUserData);
        userDoc.put("userName", userName);
        userDoc.put("googleUserInfo", userName);
        userColl.insertOne(userDoc);
    }

    public String getUserImgURI(String userName) {
        return "TODO";

    }

    public String getUserRealName(String userName) {
        return "TODO";

    }



}
