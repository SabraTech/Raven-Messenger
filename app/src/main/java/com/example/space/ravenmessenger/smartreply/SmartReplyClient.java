package com.example.space.ravenmessenger.smartreply;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.support.annotation.Keep;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface to load TfLite model and provide predictions.
 **/
public class SmartReplyClient implements AutoCloseable {
    private static final String TAG = "SmartReply";
    private static final String MODEL_PATH = "smartreply.tflite";
    private static final String BACKOFF_PATH = "backoff_response.txt";
    private static final String JNI_LIB = "smartreply_jni";

    private final Context context;
    private long storage;
    private MappedByteBuffer model;

    private volatile boolean isLibraryLoaded;

    public SmartReplyClient(Context context) {
        this.context = context;
    }

    public boolean isLoaded() {
        return storage != 0;
    }

    @WorkerThread
    public synchronized void loadModel() {
        if (!isLibraryLoaded) {
            System.loadLibrary(JNI_LIB);
            isLibraryLoaded = true;
        }

        try {
            model = loadModelFile();
            String[] backoff = loadBackoffList();
            storage = loadJNI(model, backoff);
        } catch (IOException e) {
            Log.e(TAG, "Fail to load model", e);
            return;
        }
    }

    @WorkerThread
    public synchronized SmartReply[] predict(String[] input) {
        if (storage != 0) {
            return predictJNI(storage, input);
        } else {
            return new SmartReply[]{};
        }
    }

    @WorkerThread
    public synchronized void unloadModel() {
        close();
    }

    @Override
    public synchronized void close() {
        if (storage != 0) {
            unloadJNI(storage);
            storage = 0;
        }
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        try {
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } finally {
            inputStream.close();
        }
    }

    private String[] loadBackoffList() throws IOException {
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(context.getAssets().open(BACKOFF_PATH)));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.isEmpty()) {
                labelList.add(line);
            }
        }
        reader.close();
        String[] ans = new String[labelList.size()];
        labelList.toArray(ans);
        return ans;
    }

    @Keep
    private native long loadJNI(MappedByteBuffer buffer, String[] backoff);

    @Keep
    private native SmartReply[] predictJNI(long storage, String[] text);

    @Keep
    private native void unloadJNI(long storage);
}
