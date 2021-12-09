package com.badapple.video;

import java.nio.ByteBuffer;

import com.badapple.utils.GZipBytes;

public class BadAppleVideo extends Video{
    public static final int WIDTH = 96;
    public static final int HEIGHT = 64;
    private static final int BPF = WIDTH*HEIGHT;
    public final int LENGTH;

    private final byte[] video;
    private final ByteBuffer videoBuffer;

    public BadAppleVideo(){
        video = GZipBytes.getBytes(getClass().getResourceAsStream("/frames.bin.gz"));
        System.out.print(video.length);
        videoBuffer = ByteBuffer.wrap(video);
        LENGTH = video.length / (WIDTH * HEIGHT);
    }

    public byte[] getFrame(int frameNum){
        byte[] frame = new byte[BPF];
        videoBuffer.get(frame, 0, BPF);
        return frame;
    }
}
