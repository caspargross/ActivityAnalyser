package com.vaadin.model;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.fitness.Fitness;
import com.google.api.services.fitness.model.AggregateBy;
import com.google.api.services.fitness.model.AggregateRequest;
import com.google.api.services.fitness.model.AggregateResponse;
import com.google.api.services.fitness.model.BucketByTime;
import com.mongodb.util.JSON;
import com.vaadin.server.VaadinSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by caspar on 20.06.17.
 */
public class DataRequest extends AuthRequest{

    /* Google User ID for the current user*/
    public DataRequest() {
        myCredential = (Credential) VaadinSession.getCurrent().getAttribute("sessionCredential");

    }


    public String getFitData() throws IOException {

        Fitness fit = new Fitness.Builder(HTTP_TRANSPORT, JSON_FACTORY, myCredential)
                .setApplicationName("TrackFit").build();

        // Setting a start and end date using a range of 1 year before this moment.
        Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis();
        cal.setTimeInMillis(now - now  % (24 * 60 * 60 * 1000)); // Find the last full day
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -1);
        long startTime = cal.getTimeInMillis();

        AggregateRequest aggRequest = new AggregateRequest();

        // set start- and endtime
        aggRequest.setStartTimeMillis(startTime);
        aggRequest.setEndTimeMillis(endTime);

        BucketByTime bucketByTime = new BucketByTime();
        bucketByTime.setDurationMillis(86400000l); // 24h
        aggRequest.setBucketByTime(bucketByTime);

        AggregateBy aggregateBy1 = new AggregateBy();
        aggregateBy1.setDataTypeName("com.google.step_count.delta");

        ArrayList<AggregateBy> list = new ArrayList<>();
        list.add(aggregateBy1);
        aggRequest.setAggregateBy(list);

        AggregateResponse aggResponse = fit.users().dataset()
                .aggregate("me", aggRequest).execute();

        System.out.println(aggResponse.toPrettyString());

        return aggResponse.toString();
    }
}
