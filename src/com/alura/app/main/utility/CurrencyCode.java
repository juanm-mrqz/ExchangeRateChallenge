package com.alura.app.main.utility;

import java.util.*;

public enum CurrencyCode {
    ARS ("Pesos argentinos"),
    BOB ("Boliviano boliviano"),
    BRL ("Real brasileño"),
    CLP ("Peso chileno"),
    COP ("Peso colombiano"),
    USD ("Dólar estadounidense");

    private String label;

    CurrencyCode(String label) {
        this.label = label;
    }



    public static Map<CurrencyCode, String> getAllCurrencies() {
        Map<CurrencyCode, String> currencies = new HashMap<>();
        for(var code : CurrencyCode.values())
        {
            currencies.put(code, code.label);
        }
        return currencies;
    }
}


