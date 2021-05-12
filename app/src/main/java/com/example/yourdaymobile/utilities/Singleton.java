package com.example.yourdaymobile.utilities;

public class Singleton {
    private static Singleton instance = null;

    public static String authToken;

    //signleton pozwala na przechowywanie zmiennej authTokena przez całość działania aplikacji
    public Singleton() {
    }
    public static Singleton getInstance() {
        if(instance == null) {
            instance = new Singleton();
            authToken = new String();

        }
        return instance;
    }

}
