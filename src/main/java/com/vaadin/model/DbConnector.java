package com.vaadin.model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

/**
 * Created by caspar on 12.06.17.
 */
public class DbConnector extends MongoClient {

    MongoDatabase db;
    static MongoCollection<Document> userColl;
    MongoCollection<Document> stepColl;
    MongoCollection<Document> daysColl;
    String sessionUserID;



    public DbConnector(String sessionUserID) {
        // Accesses DB, creates an Instance if it does not exist yet.c
        this.db = getDatabase("trackFit");
        this.sessionUserID = sessionUserID;
        userColl = db.getCollection("users");
        stepColl = db.getCollection("steps");
        daysColl = db.getCollection("days");
    }

    public boolean findUser (String userName) {
        //db.getCollection("users")

        return true;
    }

    public void storeUser(String googleUserData) {

        Document userDoc = Document.parse(googleUserData);
        userDoc.append("_id", userDoc.get("id"));
        userDoc.remove("id");
        //userColl.replaceOne(eq("_id" , sessionUserID),  userDoc );
        userColl.replaceOne(userDoc, userDoc, new UpdateOptions().upsert(true));
    }



    public void storeSteps(String stepData) {
        // Create new Document parsed from Google JSON data
        Document stepDoc = Document.parse(stepData);
        // Split Document into Array
        ArrayList<Document> stepList = (ArrayList<Document>)stepDoc.get("bucket");
        stepList.forEach(document -> {
            long startTime = Long.valueOf(document.get("endTimeMillis").toString());
            int steps;
            // Assign steps to currently logged in User
            document.append("user", sessionUserID);
            // Ugly loop through nested arrays to get the Step value. Maybe there is a better way.
            ArrayList<Document> datasetList  =  (ArrayList<Document>) document.get("dataset");
            ArrayList<Document> pointList  =  ((ArrayList<Document>) datasetList.get(0).get("point"));
            if (pointList.size() > 0 ) {
                ArrayList<Document> valueList = (ArrayList<Document>)(pointList.get(0).get("value"));
                steps =  valueList.get(0).getInteger("intVal");

            } else {
                // Set step value to 0 if no activity for this day
                steps = 0;
            }

            // Convert timeInMillis to long
            document.put("steps", steps);
            document.put("startMillis", startTime);
            document.append("date", new Date(startTime));
            document.put("endMillis", Long.valueOf(document.get("endTimeMillis").toString()));

            // Clean up
            document.remove("dataset");
            document.remove("startTimeMillis");
            document.remove("endTimeMillis");

            Bson myFilters = and(eq("user", sessionUserID), eq("startMillis", startTime));
            stepColl.replaceOne(myFilters, document, new UpdateOptions().upsert(true));

            // Update Days Collection
            storeDays(startTime, steps);
        });

    }

    public void storeDays(Long startTime, int steps) {

        Document newDayDoc = new Document("_id", startTime);
        // In the case, that this is the first entry on this day
        double newAverage = steps;
        int entries = 0;

        // Recalculate values when this day is already in the collection
        if (daysColl.count(eq("_id", startTime)) > 0) {
            double oldAverage = Double.valueOf(daysColl.find(eq("_id", startTime)).first().get("average").toString());
            entries = daysColl.find(eq("_id", startTime)).first().getInteger("entries");
            newAverage = ((oldAverage * entries) + steps) / (entries + 1);
        }

        newDayDoc.put("entries", entries+1 );
        newDayDoc.put("average", newAverage);
        newDayDoc.put("date", new Date(startTime));

        daysColl.replaceOne(eq("_id", startTime), newDayDoc, new UpdateOptions().upsert(true));

    }

    public static String extractUserPicture(String userID) {
        return userColl.find(eq("_id", userID)).first().getString("picture");
    }

    public static String extractUserRealName(String userID) {
        return userColl.find(eq("_id", userID)).first().getString("name");
    }



}
