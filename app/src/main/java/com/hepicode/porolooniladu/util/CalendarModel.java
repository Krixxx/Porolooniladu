package com.hepicode.porolooniladu.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarModel {

    private Date calendar;
    private String myFormat, incomingDate, newFormat;
    private SimpleDateFormat format;
    private String calendarText;

    /*
    for using this class:
        CalendarModel cal = new CalendarModel();
        String currentDate = cal.getCalendarText();
     */

    public CalendarModel() {
    }

    public CalendarModel(String calendarText) {
        this.calendarText = calendarText;
    }

    public String getCalendarText() {
        return currentDate();
    }

    public void setCalendarText(String calendarText) {
        this.calendarText = calendarText;
    }

    private String currentDate (){

        calendar = Calendar.getInstance().getTime();
        myFormat = "dd MMMM yyyy";

        format = new SimpleDateFormat(myFormat);

        try{

            calendarText = format.format(calendar);

        } catch (Exception e){
        }

        return calendarText;

    }

    public String makeDateCorrect(String date){

        incomingDate = "dd.MM.yyyy";
        myFormat = "";


        return newFormat;
    }
}
