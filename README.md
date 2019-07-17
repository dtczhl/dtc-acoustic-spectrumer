# dtc-acoustic-spectrumer
A simple android app to display the spectrum of received sounds

## Preparation

1.  download the source code of Apache Commons Math library for Java that includes Fast-Fourier-Transform (FFT). <http://commons.apache.org/proper/commons-math/download_math.cgi>

2.  extract the downloaded file. Goes to `commons-math3-***/src/main/java/org/apache/commons/math3`, remove the `geometry` folder which is not compatible with Android.

3.  goes to `commons-math3-***/src/main/java/` and copy the `org` folder to your Android project `app/src/main/`

4.  enable Android permission `<uses-permission android:name="android.permission.RECORD_AUDIO" />`

## Source Code
The interface to the spectrum analysis is the `AnalyzeFrequency.java` class. The spectrum range is [\0 Hz, 24 KHz\].

1.  start frequency analysis
  ```java
  AnalyzeFrequency mFftAnalysis = new AnalyzeFrequency(mHandler, mRun);
  mFftAnalysis.start();
  ```

2.  stop frequency analysis
  ```java
  if (mFftAnalysis != null) {
      mFftAnalysis.stop();
      mFftAnalysis = null;
  }
  ```

## Phones Tested
*   Huawei Mate 20
*   Google Pixel 2
