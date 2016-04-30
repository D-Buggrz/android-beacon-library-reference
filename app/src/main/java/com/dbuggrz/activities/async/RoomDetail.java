package com.dbuggrz.activities.async;

/**
 * Created by Mike on 4/30/2016.
 */
public class RoomDetail extends LocationDetail {

    private String meetingAgenda;

    private String videoConferenceEnabled;

    private String howManyPeople;

    public String getMeetingAgenda() {
        return meetingAgenda;
    }

    public void setMeetingAgenda(String meetingAgenda) {
        this.meetingAgenda = meetingAgenda;
    }

    public String getVideoConferenceEnabled() {
        return videoConferenceEnabled;
    }

    public void setVideoConferenceEnabled(String videoConferenceEnabled) {
        this.videoConferenceEnabled = videoConferenceEnabled;
    }

    public String getHowManyPeople() {
        return howManyPeople;
    }

    public void setHowManyPeople(String howManyPeople) {
        this.howManyPeople = howManyPeople;
    }
}
