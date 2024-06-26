package com.alura.app.currency;

public record QuotaData (
        int plan_quota,
        int requests_remaining,
        int refresh_day_of_month
)
{ }
