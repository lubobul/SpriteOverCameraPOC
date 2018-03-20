package threads;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.concurrent.ArrayBlockingQueue;

import lubobul.cameratest.MainActivity;

/**
 * Created by lubobul on 9/15/2015.
 */
public class BufferLoader extends Thread {

    private ArrayBlockingQueue<Bitmap> bitmaps = null;
    private MainActivity context = null;
    private int numberOfFrames = 0;

    int currFrame = 1;

    int timesLooped = 0;

    String fname = "";

    BitmapFactory.Options bitMapOptions;

    public BufferLoader(MainActivity context, int bufferCapacity, int numberOfFrames, int preloadFrames) {
        super();
        this.bitmaps = new ArrayBlockingQueue<>(bufferCapacity);
        this.context = context;
        this.numberOfFrames = numberOfFrames;
        bitMapOptions = new BitmapFactory.Options();
        bitMapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        bitMapOptions.inPreferQualityOverSpeed = false;

        timesLooped = preloadFrames;

        this.preloadNumberOfFrames(preloadFrames);
    }

    @Override
    public void run() {

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        while (timesLooped <= numberOfFrames) {

            fname = "frame" + currFrame;

            int id = context.getResources().getIdentifier(fname, "raw", context.getPackageName());

            //Log.d("File ID: ", ""+id);

            if (id != 0) {

                if (bitmaps.offer(BitmapFactory.decodeResource(context.getResources(), id, bitMapOptions))) {
                    currFrame++;
                    timesLooped++;
                    if (currFrame > 24)
                        currFrame = 1;
                }
            }
        }
    }

    private void preloadNumberOfFrames(int numberOfFrames){

        for(int i =0; i < numberOfFrames; i++) {
            fname = "frame" + currFrame;

            int id = context.getResources().getIdentifier(fname, "raw", context.getPackageName());

            if (bitmaps.offer(BitmapFactory.decodeResource(context.getResources(), id, bitMapOptions))) {
                currFrame++;
                if (currFrame > 24)
                    currFrame = 1;
            }
        }
    }

    public ArrayBlockingQueue<Bitmap> getBuffer() {

        return bitmaps;
    }

}
