package edu.cs4730.textureviewdemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.TextureView;
import android.widget.FrameLayout;


/*
 * original example from  here: http://pastebin.com/J4uDgrZ8  with much thanks.
 *
 *  Everything for the texture in in the MainActivity.  And a generic textureView is used
 *
 *
 */

public class AllinOneActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {


    private TextureView mTextureView1;
    private RenderingThread mThread1;

    private TextureView mTextureView2;
    private RenderingThread mThread2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //get a layout, which we will use later.
        FrameLayout content = new FrameLayout(this);

        //get a TextureView and set it up with all code below
        mTextureView1 = new TextureView(this);
        mTextureView1.setSurfaceTextureListener(this);
        //mTextureView1.setOpaque(false);  //normally, yes, but we need to see the block here.

        //add the TextureView to our layout from above.
        content.addView(mTextureView1, new FrameLayout.LayoutParams(500, 500, Gravity.LEFT));


        mTextureView2 = new TextureView(this);
        mTextureView2.setSurfaceTextureListener(this);
        content.addView(mTextureView2, new FrameLayout.LayoutParams(500, 500, Gravity.RIGHT));


        setContentView(content);

    }

    /*
     * TextureView.SurfaceTextureListener overrides below, that start up the drawing thread.
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mThread1 = new RenderingThread(mTextureView1);
        mThread1.start();

        mThread2 = new RenderingThread(mTextureView2);
        mThread2.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mThread1 != null) mThread1.stopRendering();
        if (mThread2 != null) mThread2.stopRendering();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Ignored
    }

    /*
     * Thread to draw a green square moving around the textureView.
     */
    private static class RenderingThread extends Thread {
        private final TextureView mSurface;
        private volatile boolean mRunning = true;

        public RenderingThread(TextureView surface) {
            mSurface = surface;
        }

        @Override
        public void run() {
            float x = 0.0f;
            float y = 0.0f;
            float speedX = 5.0f;
            float speedY = 3.0f;

            float squareSize = 20.0f;

            Paint paint = new Paint();
            paint.setColor(0xff00ff00);

            while (mRunning && !Thread.interrupted()) {
                    final Canvas canvas = mSurface.lockCanvas(null);
                try {
                    if(canvas != null) {
                        //canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
                        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
                        canvas.drawRect(x, y, x + squareSize, y + squareSize, paint);
                    }
                } finally {
                    if(canvas != null) {
                        mSurface.unlockCanvasAndPost(canvas);
                    }

                }

                if (x + 20.0f + speedX >= mSurface.getWidth() || x + speedX <= 0.0f) {
                    speedX = -speedX;
                }
                if (y + 20.0f + speedY >= mSurface.getHeight() || y + speedY <= 0.0f) {
                    speedY = -speedY;
                }

                x += speedX;
                y += speedY;

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    // Interrupted
                }
            }
        }

        void stopRendering() {
            interrupt();
            mRunning = false;
        }
    }
}
