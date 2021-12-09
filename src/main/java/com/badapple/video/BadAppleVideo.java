package com.badapple.video;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import com.badapple.utils.GZipBytes;

public class BadAppleVideo extends Video{
    public static final int WIDTH = 96;
    public static final int HEIGHT = 64;
    private static final int BPF = WIDTH*HEIGHT;
    public int LENGTH = 6000;

    private RandomAccessFile videoFile = null;

    public BadAppleVideo(){
        try {
            videoFile = new RandomAccessFile(Objects.requireNonNull(getClass().getResource("/frames.bin")).getFile(), "r");
            LENGTH = (int) new File(Objects.requireNonNull(getClass().getResource("/frames.bin")).getFile()).length();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getFrame(int frameNum){
        byte[] frame = new byte[BPF];
        int initPointer = frameNum*WIDTH*HEIGHT;
        try {
            videoFile.seek(initPointer);
            videoFile.readFully(frame, 0, BPF);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return frame;
    }
}
