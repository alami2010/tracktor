package com.api.prices.crypto.cryptoprices.utils;

import com.api.prices.crypto.cryptoprices.client.pojo.Currency;
import com.api.prices.crypto.cryptoprices.entity.enums.AlphaVantageCurrency;
import com.api.prices.crypto.cryptoprices.entity.enums.BinanceCurrency;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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


    public static List<String> generateSomme(int index) {
        File file = new File("C:\\harba\\tracktor\\log\\report-stats.log");
        List<String> sommeLine = new ArrayList<>();
        Scanner in = null;
        int i = 0;
        try {
            in = new Scanner(file);
            while (in.hasNext()) {
                String line = in.nextLine();
                if (line.contains("Somme")) {

                    String stats = line.split("Somme")[1];
                    stats = stats.trim();
                    String percent = stats.split(",")[index];

                    sommeLine.add("[" + i + "," + percent + "]");

                    i++;
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return sommeLine;
    }

    public static boolean checkIfBinanceBrokerSupportCurrency(Currency currency) {
        return Arrays.stream(BinanceCurrency.values()).anyMatch((t) -> t.name().equals(currency.getSymbol()));
    }

    public static boolean checkIfAplhaBrokerSupportCurrency(Currency currency) {
        return Arrays.stream(AlphaVantageCurrency.values()).anyMatch((t) -> t.name().equals(currency.getSymbol()));
    }

}
