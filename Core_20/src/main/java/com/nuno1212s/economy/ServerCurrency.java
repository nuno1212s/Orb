package com.nuno1212s.economy;

public interface ServerCurrency {

    long getCurrency();

    void addCurrency(long currency);

    void setCurrency(long currency);

    boolean removeCurrency(long currency);

}
