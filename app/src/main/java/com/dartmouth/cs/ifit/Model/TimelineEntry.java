package com.dartmouth.cs.ifit.Model;

import java.sql.Time;
import java.util.Calendar;

/**
 * Created by chris61015 on 5/14/17.
 */

public class TimelineEntry {
    private long id;
    private long groupId;
    private String collectionName;
    private Integer isRemind;
    private String remindText;
    private byte[] photo;
    private double weight;
    private double bodyFatRate;
    private Calendar dateTime;

    public TimelineEntry() {
        id = -1;
        groupId = -1;
        isRemind = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGroudId() {
        return groupId;
    }

    public void setGroudId(long groudId) {
        this.groupId = groudId;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public Integer getRemind() {
        return isRemind;
    }

    public void setRemind(Integer remind) {
        isRemind = remind;
    }

    public String getRemindText() {
        return remindText;
    }

    public void setRemindText(String remindText) {
        this.remindText = remindText;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo.clone();
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Double getBodyFatRate() {
        return bodyFatRate;
    }

    public void setBodyFatRate(double bodyFatRate) {
        this.bodyFatRate = bodyFatRate;
    }

    public Calendar getDateTime() {
        return dateTime;
    }

    public void setDateTime(Calendar dateTime) {
        this.dateTime = dateTime;
    }
}
