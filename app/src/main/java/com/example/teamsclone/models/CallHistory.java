package com.example.teamsclone.models;

public class CallHistory {

    public String callType,callerid,startdate,starttime;

    public CallHistory(){

    }

    public CallHistory(String callType, String callerid, String startdate, String starttime) {
        this.callType = callType;
        this.callerid = callerid;
        this.startdate = startdate;
        this.starttime = starttime;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallerid() {
        return callerid;
    }

    public void setCallerid(String callerid) {
        this.callerid = callerid;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }
}
