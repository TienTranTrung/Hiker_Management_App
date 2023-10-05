package com.example.hikermanagementapp.Database;

public class Observation {
    private int id;
    private String observationName;
    private String time;
    private String comments;
    private int hikeId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getObservationName() {
        return observationName;
    }

    public void setObservationName(String observationName) {
        this.observationName = observationName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getHikeId() {
        return hikeId;
    }

    public void setHikeId(int hikeId) {
        this.hikeId = hikeId;
    }

    public Observation(int id, String observationName, String time, String comments, int hikeId) {
        this.id = id;
        this.observationName = observationName;
        this.time = time;
        this.comments = comments;
        this.hikeId = hikeId;
    }

    public Observation(String observationName, String time, String comments, int hikeId) {
        this.observationName = observationName;
        this.time = time;
        this.comments = comments;
        this.hikeId = hikeId;
    }

    public Observation() {

    }
}
