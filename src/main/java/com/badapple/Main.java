package com.badapple;


public class Main {
    public static void main(String[] args) {
        Thread mainWindow = new Thread(new Window());
        mainWindow.start();
    }
}
