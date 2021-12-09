package com.badapple.graphics;


import java.awt.*;
import java.awt.image.BufferStrategy;

import com.badapple.video.BadAppleVideo;

public class VideoCanvas extends Canvas{
    private BufferStrategy bufferStrategy;
    public void setBufferStrategy(BufferStrategy bf){
        this.bufferStrategy = bf;
    }

    private BadAppleVideo video;
    
    public VideoCanvas(int width, int height){
        this.setPreferredSize(new Dimension(width, height));
        this.setSize(width, height);
        this.setIgnoreRepaint(true);
    }

    public void renderFrame(byte[] frame){
        Graphics graphics = bufferStrategy.getDrawGraphics();

        double rendererAspect = (double) BadAppleVideo.WIDTH / (double) BadAppleVideo.HEIGHT,
                windowAspect = (double) getWidth() / (double) getHeight();
        int scaleFactor = windowAspect > rendererAspect ? getHeight() / BadAppleVideo.HEIGHT : getWidth() / BadAppleVideo.WIDTH;

        int rw = (BadAppleVideo.WIDTH * scaleFactor), rh = (BadAppleVideo.HEIGHT * scaleFactor);

        // clear buffer
        graphics.setColor(Color.GRAY);
        graphics.fillRect(0,0, getWidth(), getHeight());

        // draw frame
        int y = (getHeight()-rh)/2;
        for (int i = 0; i < frame.length; i += BadAppleVideo.WIDTH) {
            int x = (getWidth()-rw)/2;
            for (int j = i; j < BadAppleVideo.WIDTH + i; j++) {
                if (frame[j] == 0) graphics.setColor(Color.BLACK);
                else graphics.setColor(Color.WHITE);

                graphics.fillRect(x, y, scaleFactor, scaleFactor);

                x+=scaleFactor;
            }
            y+=scaleFactor;
        }
        //graphics.setColor(Color.BLUE); graphics.fillRect(0,0,WIDTH*scaleFactor, HEIGHT*scaleFactor);

        // // draw frame number
        // graphics.setColor(Color.green);
        // Font font = new Font("Sans Serif", Font.BOLD, 12);
        // graphics.setFont(font);
        // FontMetrics fontMetrics = graphics.getFontMetrics();
        // graphics.drawString(String.format("frame %d/%d", frame, totalFrames), 2, fontMetrics.getAscent());

        // // draw progressbar
        // graphics.setColor(Color.RED);
        // double barSize =  (double) frame/(double) totalFrames * getWidth();
        // graphics.fillRect(0,getHeight()-4, (int) barSize, 4);

        graphics.dispose();

        // show buffer on canvas
        if (!bufferStrategy.contentsLost()) {
            bufferStrategy.show();
        }
    }
}
