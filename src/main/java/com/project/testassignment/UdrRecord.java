package com.project.testassignment;

import lombok.Data;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * Класс для преставления объекта UDR
 * Содержит номер абонента, суммарное время входящих звонков и суммарное время исходящих звонков.
 */
@Data
public class UdrRecord {
    private String number;
    private Duration incomingCallTotalTime;
    private Duration outcomingCallTotalTime;
    /**
     * Переопределяет строковое представление на указанное в примере.
     */
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
