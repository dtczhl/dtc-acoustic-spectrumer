package com.huanlezhang.dtcacousticspectrumer;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ToggleButton;

import java.util.Arrays;

/**
 * Drawing spectrum of sounds
 *
 * @author  Huanle Zhang, University of California, Davis
 *          www.huanlezhang.com
 * @version 0.2
 * @since   2019-07-16
 */

public class MainActivity extends Activity {

    private static final String[] PermissionStrings = {
            Manifest.permission.RECORD_AUDIO
    };
    private static final int Permission_ID = 1;

    private Handler mHandler = new Handler();
    private Runnable mRun = new DrawFFT();

    private ToggleButton mMainToggleBtn;

    private ImageView mImageView;
    private int mImageViewWidth;
    private int mImageViewHeight;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;


    private AnalyzeFrequency mFftAnalysis;
    private final int N_FFT_DOT = 4096;
    private double[] mCurArray = new double[N_FFT_DOT/2-1];
    private double mScreenWidthRatio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, PermissionStrings, Permission_ID);

        // toggle button
        mMainToggleBtn = findViewById(R.id.mainBtn);
        mMainToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton toggleButton = (ToggleButton) v;
                if (toggleButton.isChecked()) {
                    startMain();
                } else {
                    stopMain();
                }
            }
        });

        // ImageView for Spectrum
        mImageView = findViewById(R.id.mainImageView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mImageViewWidth = displayMetrics.widthPixels - (int)(getResources().getDisplayMetrics().density*4.0+0.5);
        mImageViewHeight = mImageViewWidth; // a square view
        mBitmap = Bitmap.createBitmap(mImageViewWidth, mImageViewHeight, Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.LTGRAY);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(3);
        mImageView.setImageBitmap(mBitmap);
        mImageView.invalidate();

        Arrays.fill(mCurArray, (float) 0.0);
        mScreenWidthRatio = mCurArray.length * 1.0 / mImageViewWidth;
    }

    private void startMain() {
        mFftAnalysis = new AnalyzeFrequency(mHandler, mRun);
        mFftAnalysis.start();
    }

    private void stopMain() {
        if (mFftAnalysis != null) {
            mFftAnalysis.stop();
            mFftAnalysis = null;
        }
        Arrays.fill(mCurArray, (float) 0.0);
    }

    public class DrawFFT implements Runnable {
        @Override
        public void run() {
            if (mFftAnalysis != null) {
                mCanvas.drawColor(Color.LTGRAY);

                int minI = 0, maxI = 0; // range

                for (int i = 0; i < mCurArray.length; i++) {
                    mCurArray[i] = mFftAnalysis.mMagnitude[i];
                    mCurArray[i] = 10 * Math.log10(mCurArray[i]); // dB

                    if (mCurArray[i] > mCurArray[maxI]) {
                        maxI = i;
                    }
                    if (mCurArray[i] < mCurArray[minI]) {
                        minI = i;
                    }
                }

                for (int i = 0; i < mImageViewWidth - 1; i++) {
                    mCanvas.drawLine(i, (float) (20+(mImageViewHeight-20) * (mCurArray[maxI] - mCurArray[(int) (mScreenWidthRatio * i)]) / (mCurArray[maxI] - mCurArray[minI])),
                            i + 1, (float) (20+(mImageViewHeight-20) * (mCurArray[maxI] - mCurArray[(int) (mScreenWidthRatio * (i+1))]) / (mCurArray[maxI] - mCurArray[minI])), mPaint);
                }

                mImageView.invalidate();
            }
        }
    }
}
