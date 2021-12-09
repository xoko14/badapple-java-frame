package com.badapple.audio;

import java.io.InputStream;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class PlayAudio extends Thread{
    public void run(){
        try {
            InputStream file = getClass().getResourceAsStream("/badapple.mp3");
            assert file != null;
            AdvancedPlayer player = new AdvancedPlayer(file);
            player.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
