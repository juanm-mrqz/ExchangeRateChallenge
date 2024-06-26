package com.alura.app.main;

import com.alura.app.servicios.ApiAccess;

public class Main {
    private static ApiAccess access = new ApiAccess();
    private static final String configPath = "config.properties";


    public static void main(String[] args) {
        AppManager appManager = new AppManager(access, configPath);

        appManager.showMenu();


    }
}
