package com.project.testassignment;

import lombok.Data;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@Data
public class UdrRecord {
    private String number;
    private Duration incomingCallTotalTime;
    private Duration outcomingCallTotalTime;
    @Override
    public String toString(){
        return "{\"msisidn\": \"" + number
                + "\",\"incomingCall\": {\"totalTime\": \"" + getFormattedDuration(incomingCallTotalTime)
                + "\"},\"outcomingCall\": {\"totalTime\": \"" + getFormattedDuration(outcomingCallTotalTime)
                + "\"}}";
    }
    public String getFormattedDuration(Duration duration){
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.toSeconds() % 60;
        return String.format("%d:%d:%d", hours, minutes, seconds);
    }
}
