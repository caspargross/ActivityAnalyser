package com.vaadin.model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

/**
 * Created by caspar on 12.06.17.
 */
public class DbConnector extends MongoClient {

    MongoDatabase db;
    static MongoCollection<Document> userColl;

    Block<Document> printBlock = new Block<Document>() {
        @Override
        public void apply(final Document document) {
            System.out.println(document.toJson());
        }
    };

    public DbConnector() {

        // Accesses DB, creates an Instance if it does not exist yet.c
        this.db = getDatabase("trackFitDB");
        userColl = db.getCollection("users");
    }

    public boolean findUser (String userName) {
        //db.getCollection("users")

        return true;
    }

    public void createUser(String googleUserData, GoogleCredential credential) {


        Document userDoc = Document.parse(googleUserData);
        userDoc.append("refreshToken", credential.getRefreshToken());
        userDoc.append("serviceAccountUser", credential.getServiceAccountUser());
        userColl.insertOne(userDoc);
    }

    public static String getUserPicture(String userID) {
        return userColl.find(eq("id", userID))
                .projection(fields(include("name", "picture"))).first().getString("picture");


    }

    public static String getUserRealName(String userID) {
        return userColl.find(eq("id", userID))
                .projection(fields(include("name", "picture"))).first().getString("name");

    }



}
