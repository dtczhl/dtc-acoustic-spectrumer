package com.huanlezhang.responserecorder;


import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.widget.Toast;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.*;

import java.util.Arrays;


/**
 * You can reuse my codes freely. I would appreciate if you could keep my name and website address.
 * Name: Huanle Zhang
 * Personal website: www.huanlezhang.com
 */
public class FFTAnalysis {

    private Handler mHandler;
    private Runnable mRun;

    private AudioRecord mAudioRecord;
    private final int mSampleRate = 48000;
    private boolean mIsRecording = false;

    private FastFourierTransformer mFFT;
    private final int N_FFT_DOT = 4096;
    private double[] mFftBuffer = new double[N_FFT_DOT];
    private short[] mAudioBuffer = new short[N_FFT_DOT];
    private Complex[] mResultFFT = new Complex[N_FFT_DOT];

    public double[] mMagnitude = new double[N_FFT_DOT/2 - 1];

    FFTAnalysis( Handler handler, Runnable runnable){
        mHandler = handler;
        mRun = runnable;

        int bufferSize = AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                mSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        Arrays.fill(mFftBuffer, 0.0);

        mFFT = new FastFourierTransformer(DftNormalization.UNITARY);
    }

    public void start(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mAudioRecord.startRecording();
                mIsRecording = true;

                int audioReadSize;
                int remainBufSize;

                while (mIsRecording){

                    remainBufSize = N_FFT_DOT;
                    while (remainBufSize != 0 && mIsRecording){
                        audioReadSize = mAudioRecord.read(mAudioBuffer, 0, remainBufSize);
                        for (int i = 0; i < audioReadSize; i++){
                            mFftBuffer[N_FFT_DOT-remainBufSize] = (double)mAudioBuffer[i] / 32768.0;
                            remainBufSize--;
                        }
                    }

                    if (!mIsRecording) break;
                    mResultFFT = mFFT.transform(mFftBuffer, TransformType.FORWARD);
                    for (int i = 0; i < mMagnitude.length; i++){
                        mMagnitude[i] = mResultFFT[i].abs();
                    }

                    mHandler.post(mRun);
                }
                mAudioRecord.stop();
                mAudioRecord.release();
            }
        });
        thread.start();
    }

    public void stop(){
        mIsRecording = false;
    }

}
