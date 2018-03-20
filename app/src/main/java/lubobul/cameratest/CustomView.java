package lubobul.cameratest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.concurrent.ArrayBlockingQueue;

import threads.BufferLoader;

public class CustomView extends ImageView {

    private Bitmap currBitmap = null;
    private boolean canDraw = false;
    private long timePerFrame = 0;

    ArrayBlockingQueue<Bitmap> buffer;

    BufferLoader bufferLoader = null;

    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrst) {
        super(context, attrst);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onDraw(Canvas canvas) {

        if(bufferLoader != null && bufferLoader.isAlive()) {

            long startTime = SystemClock.uptimeMillis();

            if(!buffer.isEmpty())
                currBitmap = buffer.poll(); //animation.getCurrentFrame();

            if(currBitmap != null) {
                canvas.drawColor(Color.TRANSPARENT);
                canvas.drawBitmap(currBitmap, new Rect(0, 0,2000 , 2000), new Rect(0, 0, 240, 135), null);
            }else
                canvas.drawColor(Color.TRANSPARENT);

            currBitmap = null;

            long stopTime = SystemClock.uptimeMillis();

            long howLongItTakesForUsToDoOurWork = stopTime - startTime;
            super.onDraw(canvas);

            long timeToWait = timePerFrame - howLongItTakesForUsToDoOurWork;
            if (timeToWait < 2)
                timeToWait = 2;

            try {

                Thread.sleep(timeToWait);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
            //super.setImageBitmap(animation.getCurrentFrame());
            this.postInvalidate();
            //animation.update();

    }

    @Override
    public synchronized void setImageBitmap(Bitmap bm){

        super.setImageBitmap(bm);
    }

    public void setCanDraw(boolean canDraw){
        this.canDraw = canDraw;
    }

    public void setFPS(int fps)
    {
        this.timePerFrame = 1000/fps;
    }

    public void setBufferLoader (BufferLoader bufferLoader)
    {
        this.buffer = bufferLoader.getBuffer();
        this.bufferLoader = bufferLoader;
    }

}