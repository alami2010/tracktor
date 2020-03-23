package com.api.prices.crypto.cryptoprices.restcontroller;

import com.api.prices.crypto.cryptoprices.utils.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class StatsController {


    @RequestMapping(value = "greeting", method = RequestMethod.GET)
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {


        String data1H = Utils.generateSomme(0).stream().collect(Collectors.joining(","));
        String data24h = Utils.generateSomme(1).stream().collect(Collectors.joining(","));
        String data7d = Utils.generateSomme(2).stream().collect(Collectors.joining(","));

        model.addAttribute("name", name);
        model.addAttribute("data1H", data1H);
        model.addAttribute("data24h", data24h);
        model.addAttribute("data7d", data7d);

        return "greeting";
    }

}
