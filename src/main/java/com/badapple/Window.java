package com.badapple;

import com.badapple.audio.PlayAudio;
import com.badapple.graphics.VideoCanvas;
import com.badapple.utils.GZipBytes;
import com.badapple.video.BadAppleVideo;
import com.badapple.video.Video;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.concurrent.locks.LockSupport;

public class Window extends JFrame implements Runnable {

    private static final int WIDTH = 96;
    private static final int HEIGHT = 64;

    public static final int WINDOW_WIDTH = WIDTH * 2;
    public static final int WINDOW_HEIGHT = HEIGHT * 2;

    private final VideoCanvas canvas;

    private final BadAppleVideo video;
    private boolean closed = false;

    public Window(){
        video = new BadAppleVideo();
        this.setTitle("Bad Apple");
        this.getContentPane().setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_Q) {
                    System.exit(0);
                } else {
                    System.out.println("Key not mapped");
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("Key pressed "+ e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println("Key released"+ e.getKeyCode());
            }
        });
        this.setFocusable(true);

        canvas = new VideoCanvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.add(canvas);
        this.pack();
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                closed = true;
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {
                super.windowGainedFocus(e);
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                super.windowLostFocus(e);
            }
        });

        canvas.createBufferStrategy(2);
        canvas.setBufferStrategy(canvas.getBufferStrategy());
    }

    @Override
    public void run(){
        this.setVisible(true);
        canvas.setVisible(true);

        new Thread(() -> {
            PlayAudio play = new PlayAudio();
            play.start();;

            for (int i = 0; i < video.LENGTH || closed; i++) {
                long time1 = System.nanoTime();
                canvas.renderFrame(video.getFrame(i));
                long time2 = System.nanoTime();
                LockSupport.parkNanos(1000000000 / 30 - (time2 - time1)); // fps (duration of frame in nanosec - time taken to render frame)
            }
        }).start();
        System.out.println("I reached here");
    }
}
