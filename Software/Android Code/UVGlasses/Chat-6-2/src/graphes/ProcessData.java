package graphes;

import android.os.Handler;

import com.redbear.chat.Graph;

/**
 * Created by Andrew on 6/22/17.
 *
 * ProcessData
 * Gathers the data from the glove and sends it to the exercise activity to be graphed.
 */

public class ProcessData implements GraphHandler
{
    private static float currentval;
    //private static float thumbValue;
    private static Handler handler;

    public ProcessData(Handler handler){this.handler = handler;}

    public void setHandler(Handler handler) {this.handler = handler;}

    public static float returnCurrentVal()
    {
        return currentval;
    }
  //  public float returnThumb()
   // {
     //   return thumbValue;
    //}

    // Sends each value to be graphed
    public static void process(float value1)
    {
        currentval = value1;
       // thumbValue = (float) value2;

        if(handler != null)
            handler.sendEmptyMessageDelayed(Progress.READY, 100);
    }


}
