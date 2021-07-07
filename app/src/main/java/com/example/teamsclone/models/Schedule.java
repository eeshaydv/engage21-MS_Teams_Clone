package com.example.teamsclone.models;

public class Schedule {

    public String fromUid, toUid, description, status;
    public int year, month, day, hour, min;

    public Schedule() {

    }

    public Schedule(String fromUid, String toUid, String description, String status, int year, int month, int day, int hour, int min) {
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.description = description;
        this.status = status;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
