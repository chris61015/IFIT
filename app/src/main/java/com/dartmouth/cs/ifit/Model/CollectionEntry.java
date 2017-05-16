package com.dartmouth.cs.ifit.Model;

/**
 * Created by chris61015 on 5/14/17.
 */

public class CollectionEntry {
    private long id;
    private String collectionName;
    private byte[] icon;

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon.clone();
    }

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
}
