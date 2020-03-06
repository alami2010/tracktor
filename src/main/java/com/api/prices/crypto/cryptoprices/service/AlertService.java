package com.api.prices.crypto.cryptoprices.service;

import com.api.prices.crypto.cryptoprices.client.pojo.Currency;
import com.api.prices.crypto.cryptoprices.entity.CurrencyToTrack;
import com.api.prices.crypto.cryptoprices.entity.Decision;
import com.api.prices.crypto.cryptoprices.entity.TableHtml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AlertService {


    @Autowired
    private SendMailService sendMail;
    @Autowired
    private HTMLGeneratorService HTMLGeneratorService;
    final String SUBJECT_TO_SEND = "%3s  %1s";
    final String MESSAGE_TO_SEND = "%3s  %1s  price %2s </br>\n %s ";

    public void alert(double alertPrice, CurrencyToTrack newCurrencyToTrack, CurrencyToTrack oldCurrencyToTrack, Decision decision) {


        String subject = String.format(SUBJECT_TO_SEND, decision.toString(),oldCurrencyToTrack.getName());
        String message = String.format(MESSAGE_TO_SEND, decision.toString(), oldCurrencyToTrack.toString(), String.valueOf(alertPrice),newCurrencyToTrack);

        sendMail.sendMail(subject, message, false);
    }




    public void alert(TableHtml ...tableHtmlss) {

        String title = tableHtmlss.length > 1  ? "All Analyse" :  tableHtmlss[0].getTitle() ;
        StringBuffer sb = HTMLGeneratorService.generateHtmlMessage(Arrays.asList(tableHtmlss));
        sendMail.sendMail(title, sb.toString(), true);



    }
}
