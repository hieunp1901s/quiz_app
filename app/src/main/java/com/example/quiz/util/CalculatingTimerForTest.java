package com.example.quiz.util;

import com.example.quiz.R;
import com.example.quiz.models.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalculatingTimerForTest {
    Test test;
    long diff;
    public CalculatingTimerForTest(Test test) {
        this.test = test;
        diff = 0;
    }

    public long getResult() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date start = null;

        try {
            start = simpleDateFormat.parse(test.getDate() + " " + test.getStartTime() + ":00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(Calendar.MINUTE, Integer.parseInt(test.getDuration()));
        Date end = calendar.getTime();
        if (now.before(end) && start.before(now)) {
            diff = end.getTime() - now.getTime();
        }
        return  diff;
    }
}
