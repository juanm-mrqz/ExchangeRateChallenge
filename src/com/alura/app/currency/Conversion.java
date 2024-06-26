package com.alura.app.currency;

import com.alura.app.main.utility.UserData;

import java.time.LocalDateTime;

public class Conversion{
    public LocalDateTime date;
    private String baseCode;
    private String targetCode;
    private Double baseCurrency;
    private Double targetCurrency;
    private Double value;
    private Double result;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Conversion(UserData data) {
        this.date = LocalDateTime.now();
        this.baseCode = data.baseCode();
        this.targetCode = data.targetCode();
        this.value = data.value();

    }

    public Conversion(String baseCode, String targetCode, Double baseCurrency, Double targetCurrency, Double value, Double result) {
        this.date = LocalDateTime.now();
        this.baseCode = baseCode;
        this.targetCode = targetCode;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.value = value;
        this.result = result;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    public Double getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Double baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Double getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Double targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public Double getResult() {
        return this.result;
    }
    public void setResult(Double result) {
        this.result = result;
    }

    public void calculate() {
        setResult(value * targetCurrency / baseCurrency);
    }

    @Override
    public String toString() {
        return "Conversion{" +
                "date=" + date +
                ", baseCode='" + baseCode + '\'' +
                ", targetCode='" + targetCode + '\'' +
                ", baseCurrency=" + baseCurrency +
                ", targetCurrency=" + targetCurrency +
                ", value=" + value +
                ", result=" + result +
                '}';
    }
}
