package com.dartmouth.cs.ifit.Model;

import java.util.Calendar;

/**
 * Created by chris61015 on 5/14/17.
 */

public class TimelineEntry {
    private long id;
    private String collectionName;
    private Integer isRemind;
    private String remindText;
    private byte[] photo;
    private Float weight;
    private Float bodyFatRate;
    private Calendar dateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        this.photo = photo;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getBodyFatRate() {
        return bodyFatRate;
    }

    public void setBodyFatRate(Float bodyFatRate) {
        this.bodyFatRate = bodyFatRate;
    }

    public Calendar getDateTime() {
        return dateTime;
    }

    public void setDateTime(Calendar dateTime) {
        this.dateTime = dateTime;
    }
}
