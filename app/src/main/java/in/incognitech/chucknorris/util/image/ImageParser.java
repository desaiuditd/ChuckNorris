package in.incognitech.chucknorris.util.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import in.incognitech.chucknorris.MainActivity;
import in.incognitech.chucknorris.util.json.JSONListener;
import in.incognitech.chucknorris.util.json.JSONParser;

/**
 * Created by udit on 11/01/16.
 */
public class ImageParser extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = JSONParser.class.getSimpleName();
    private Bitmap imageObj = null;
    private MainActivity mainActivity = null;

    private List _listeners = new ArrayList();

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public synchronized void addImageListener(JSONListener l) {
        _listeners.add(l);
    }

    public synchronized void removeImageListener(JSONListener l) {
        _listeners.remove(l);
    }

    private synchronized void _fireImageEvent() {
        Iterator listeners = _listeners.iterator();
        while (listeners.hasNext()) {
            ((ImageListener) listeners.next()).onImageReceive(this.imageObj);
        }
    }

    @Override
    protected void onPostExecute(Bitmap image) {

        if (null == image) {
            Toast.makeText(this.getMainActivity().getApplicationContext(), "Unable to fetch image from server", Toast.LENGTH_LONG).show();
        } else {
            this._fireImageEvent();
        }
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        //do your request in here so that you don't interrupt the UI thread
        imageObj = this.getImageFromUrl(params[0]);
        return imageObj;
    }

    public Bitmap getImageFromUrl(String urlstr) {
        Bitmap image = null;
        try {
            InputStream in = new java.net.URL(urlstr).openStream();
            image = BitmapFactory.decodeStream(in);
            in.close();
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
        return image;
    }
}
