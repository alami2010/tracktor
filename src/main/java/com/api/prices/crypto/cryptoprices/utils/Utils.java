package com.api.prices.crypto.cryptoprices.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static boolean isSwitchDay() {
        try {
            String string1 = "00:00:00";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            Date time1 = simpleDateFormat.parse(string1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);
            calendar1.add(Calendar.DATE, 1);


            String string2 = "12:00:00";
            Date time2 = simpleDateFormat.parse(string2);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 1);

            String currentDate = simpleDateFormat.format(Calendar.getInstance().getTime());
            Date d = simpleDateFormat.parse(currentDate);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
            calendar3.add(Calendar.DATE, 1);

            Date x = calendar3.getTime();
            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {

                System.out.println(true);
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

}
