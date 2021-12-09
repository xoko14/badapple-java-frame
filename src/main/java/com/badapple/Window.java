package com.badapple;

import com.badapple.audio.PlayAudio;
import com.badapple.utils.GZipBytes;

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

    private static final int WINDOW_WIDTH = WIDTH * 2;
    private static final int WINDOW_HEIGHT = HEIGHT * 2;

    private final Canvas canvas;
    private final BufferStrategy bufferStrategy;

    private final GZipBytes video;
    private boolean closed = false;

    public Window(){
        video = new GZipBytes(Window.class.getResourceAsStream("/frames.bin.gz"));
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

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        canvas.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        canvas.setIgnoreRepaint(true);
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
        bufferStrategy = canvas.getBufferStrategy();
    }

    @Override
    public void run(){
        this.setVisible(true);
        canvas.setVisible(true);

        new Thread(() -> {
            PlayAudio play = new PlayAudio();
            play.start();

            int totalFrames = video.getLength() / (WIDTH * HEIGHT);

            for (int i = 0; i < totalFrames || closed; i++) {
                long time1 = System.nanoTime();
                renderFrame(i, totalFrames);
                long time2 = System.nanoTime();
                LockSupport.parkNanos(1000000000 / 30 - (time2 - time1)); // fps (duration of frame in nanosec - time taken to render frame)
            }
        }).start();
        System.out.println("I reached here");
    }

    private void renderFrame(int frame, int totalFrames){
        Graphics graphics = bufferStrategy.getDrawGraphics();

        double rendererAspect = (double) WIDTH / (double) HEIGHT,
                windowAspect = (double)canvas.getWidth() / (double) canvas.getHeight();
        int scaleFactor = windowAspect > rendererAspect ?
                canvas.getHeight() / HEIGHT :
                canvas.getWidth() / WIDTH;

        int rw = (WIDTH * scaleFactor), rh = (HEIGHT * scaleFactor);

        int startbyte = frame * WIDTH * HEIGHT;

        // clear buffer
        graphics.setColor(Color.GRAY);
        graphics.fillRect(0,0, canvas.getWidth(), canvas.getHeight());

        // draw frame
        int y = (canvas.getHeight()-rh)/2;
        for (int i = 0; i < HEIGHT * WIDTH; i += WIDTH) {
            int x = (canvas.getWidth()-rw)/2;
            for (int j = startbyte + i; j < startbyte + WIDTH + i; j++) {
                if (video.getByte(j) == 0) graphics.setColor(Color.BLACK);
                else graphics.setColor(Color.WHITE);

                graphics.fillRect(x, y, scaleFactor, scaleFactor);

                x+=scaleFactor;
            }
            y+=scaleFactor;
        }
        //graphics.setColor(Color.BLUE); graphics.fillRect(0,0,WIDTH*scaleFactor, HEIGHT*scaleFactor);

        // draw frame number
        graphics.setColor(Color.green);
        Font font = new Font("Sans Serif", Font.BOLD, 12);
        graphics.setFont(font);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        graphics.drawString(String.format("frame %d/%d", frame, totalFrames), 2, fontMetrics.getAscent());

        // draw progressbar
        graphics.setColor(Color.RED);
        double barSize =  (double) frame/(double) totalFrames * canvas.getWidth();
        graphics.fillRect(0,canvas.getHeight()-4, (int) barSize, 4);

        graphics.dispose();

        // show buffer on canvas
        if (!bufferStrategy.contentsLost()) {
            bufferStrategy.show();
        }
    }
}
