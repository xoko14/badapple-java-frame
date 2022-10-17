package com.badapple.audio;

import java.io.InputStream;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class PlayAudio extends Thread{
    private AdvancedPlayer player;

    public PlayAudio(){
        InputStream file = getClass().getResourceAsStream("/badapple.mp3");
        assert file != null;
        try {
            player = new AdvancedPlayer(file);
        } catch (Exception e) {
            System.out.println("Error loading audio!");
        }
    }
    public void run(){
        try {
            player.play();
        } catch (Exception e) {
            System.out.println("Error playing audio!");
        }
    }
}
