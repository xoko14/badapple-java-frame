package com.badapple;

import com.badapple.audio.PlayAudio;
import com.badapple.utils.GZipBytes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.concurrent.locks.LockSupport;

public class Window {

    private static final int WIDTH = 96;
    private static final int HEIGHT = 64;

    private static final int WINDOW_WIDTH = WIDTH * 2;
    private static final int WINDOW_HEIGHT = HEIGHT * 2;

    private static JFrame frame;
    private static Canvas canvas;
    private static BufferStrategy bufferStrategy;

    private static GZipBytes video;
    private static boolean closed = false;
    private static boolean focused;

    public static void init(){
        Window.video = new GZipBytes(Window.class.getResourceAsStream("/frames.bin.gz"));

        Window.frame = new JFrame("Bad Apple");
        Window.frame.getContentPane().setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        Window.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Window.frame.setResizable(false);

        Window.canvas = new Canvas();
        Window.canvas.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        Window.canvas.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        Window.canvas.setIgnoreRepaint(true);
        Window.frame.add(canvas);
        Window.frame.pack();
        Window.frame.setLocationRelativeTo(null);
        Window.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                Window.closed = true;
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {
                super.windowGainedFocus(e);
                Window.focused = true;
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                super.windowLostFocus(e);
                Window.focused = false;
            }
        });

        Window.canvas.createBufferStrategy(2);
        Window.bufferStrategy = Window.canvas.getBufferStrategy();

        Window.frame.setVisible(true);
        Window.canvas.setVisible(true);

        PlayAudio play = new PlayAudio();
        play.start();

        int totalFrames = video.getLength() / (WIDTH * HEIGHT);

        for (int i = 0; i < totalFrames || Window.closed; i++) {
            long time1 = System.nanoTime();
            renderFrame(i, totalFrames);
            long time2 = System.nanoTime();
            LockSupport.parkNanos(1000000000 / 30 - (time2 - time1)); // fps (duration of frame in nanosec - time taken to render frame)
        }
    }

    private static void renderFrame(int frame, int totalFrames){
        Graphics graphics = bufferStrategy.getDrawGraphics();

        double rendererAspect = (double) WIDTH / (double) HEIGHT,
                windowAspect = (double)Window.canvas.getWidth() / (double) Window.canvas.getHeight();
        int scaleFactor = windowAspect > rendererAspect ?
                Window.canvas.getHeight() / HEIGHT :
                Window.canvas.getWidth() / WIDTH;

        int rw = (WIDTH * scaleFactor), rh = (HEIGHT * scaleFactor);

        int startbyte = frame * WIDTH * HEIGHT;

        // clear buffer
        graphics.setColor(Color.GRAY);
        graphics.fillRect(0,0, Window.frame.getWidth(), Window.frame.getHeight());

        // draw frame
        int y = (Window.canvas.getHeight()-rh)/2;
        for (int i = 0; i < HEIGHT * WIDTH; i += WIDTH) {
            int x = (Window.canvas.getWidth()-rw)/2;
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
        double barSize =  (double) frame/(double) totalFrames * Window.canvas.getWidth();
        graphics.fillRect(0,Window.canvas.getHeight()-4, (int) barSize, 4);

        graphics.dispose();

        // show buffer on canvas
        if (!Window.bufferStrategy.contentsLost()) {
            Window.bufferStrategy.show();
        }
    }
}
