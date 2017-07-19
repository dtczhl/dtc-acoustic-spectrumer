package com.huanlezhang.responserecorder;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.Manifest;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    final int PERMISSIONS_RECORD_AUDIO = 1;

    private Handler mHandler = new Handler();
    private Runnable mRun = new DisplayFFT();

    private ImageView mFFTView;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private int mScreenWidth;
    private int mScreenHeight;

    private Button mStartBtn;

    private FFTAnalysis mFftAnalysis;
    private final int N_FFT_DOT = 4096;
    private float[] mMaxArray = new float[N_FFT_DOT/2-1];
    private float[] mCurArray = new float[N_FFT_DOT/2-1];
    private double mScreenWidthRatio;

    private CheckBox mMaxCheck;
    private CheckBox mCurCheck;
    private boolean mIsMaxCheck = true;
    private boolean mIsCurCheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_RECORD_AUDIO);
        }




        mFFTView = (ImageView) findViewById(R.id.imgView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels - (int)(getResources().getDisplayMetrics().density*2.0*16.0+0.5f);
        mScreenHeight = (int)(mScreenWidth/1.3);
        mBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.LTGRAY);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(3);
        mFFTView.setImageBitmap(mBitmap);
        mFFTView.invalidate();

        mStartBtn = (Button) findViewById(R.id.startBtn);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                if (btn.getText().toString().trim().toLowerCase().equals("start")) {
                    mFftAnalysis = new FFTAnalysis(mHandler, mRun);
                    mFftAnalysis.start();
                    btn.setText("STOP");
                } else {
                    if (mFftAnalysis != null) {
                        mFftAnalysis.stop();
                        mFftAnalysis = null;
                    }
                    btn.setText("START");
                    Arrays.fill(mCurArray, (float) 0.0);
                    Arrays.fill(mMaxArray, (float) 0.0);
                }
            }
        });

        Arrays.fill(mCurArray, (float) 0.0);
        Arrays.fill(mMaxArray, (float)0.0);

        mScreenWidthRatio = mCurArray.length * 1.0 / mScreenWidth;

        mMaxCheck = (CheckBox) findViewById(R.id.maxCheck);
        mMaxCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsMaxCheck = isChecked;
            }
        });
        mCurCheck = (CheckBox) findViewById(R.id.meanCheck);
        mCurCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsCurCheck = isChecked;
            }
        });

    }


    public void aboutMe(View view){
        AboutMe.showDialog(this);
    }


    public class DisplayFFT implements Runnable{


        @Override
        public void run() {
            if (mFftAnalysis != null){
                for (int i = 0; i < mMaxArray.length; i++){

                    mMaxArray[i] = mMaxArray[i] > mFftAnalysis.mMagnitude[i] ?
                            mMaxArray[i] : (float) mFftAnalysis.mMagnitude[i];

                    mCurArray[i] =  (float) mFftAnalysis.mMagnitude[i];
                }

                mCanvas.drawColor(Color.LTGRAY);
                if (mIsCurCheck || mIsMaxCheck){

                    for (int i = 0; i < mScreenWidth-1; i++){
                        if (mIsMaxCheck) {
                            mCanvas.drawLine(i,   (float)(mScreenHeight - mScreenHeight/40 * 10*Math.log10(mScreenHeight * mMaxArray[(int)mScreenWidthRatio*i])),
                                    i+1,   (float)(mScreenHeight - mScreenHeight/40 * 10*Math.log10(mScreenHeight * mMaxArray[(int)mScreenWidthRatio*(i+1)])), mPaint);
                        }
                        if (mIsCurCheck){
                            mCanvas.drawLine(i,   (float)(mScreenHeight - mScreenHeight/40 * 10*Math.log10(mScreenHeight * mCurArray[(int)mScreenWidthRatio*i])),
                                    i+1,   (float)(mScreenHeight - mScreenHeight/40 * 10*Math.log10(mScreenHeight * mCurArray[(int)mScreenWidthRatio*(i+1)])), mPaint);
                        }
                    }
                    mFFTView.invalidate();
                }


            }
        }
    }
}
