package com.alura.app.currency;

import com.google.gson.JsonObject;


public record CurrencyData(
        long time_last_update_unix,
        long time_next_update_unix,
        String base_code,
        JsonObject conversion_rates
) {
}