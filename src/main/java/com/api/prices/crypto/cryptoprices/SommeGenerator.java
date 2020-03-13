package com.api.prices.crypto.cryptoprices;

import java.util.*;
import java.io.*;
    public class SommeGenerator
    {
        public static void main(String args[])
        {
            File file =new File("C:\\harba\\tracktor\\log\\report-stats.log");
            Scanner in = null;
            int i = 0 ;
            try {
                in = new Scanner(file);
                while(in.hasNext())
                {
                    String line=in.nextLine();
                    if(line.contains("Somme")){

                        System.out.println("["+i+", "+line);

                        i++;
                    }

                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }}