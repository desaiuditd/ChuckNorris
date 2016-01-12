package in.incognitech.chucknorris.util.json;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import in.incognitech.chucknorris.MainActivity;

/**
 * Created by udit on 09/01/16.
 */

public class JSONParser extends AsyncTask<String, Void, JSONObject> {

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final String TAG = JSONParser.class.getSimpleName();
    private JSONObject jObj = null;
    private MainActivity mainActivity = null;

    private List _listeners = new ArrayList();

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public synchronized void addJSONListener(JSONListener l) {
        _listeners.add(l);
    }

    public synchronized void removeJSONListener(JSONListener l) {
        _listeners.remove(l);
    }

    private synchronized void _fireJSONEvent() {
        Iterator listeners = _listeners.iterator();
        while (listeners.hasNext()) {
            ((JSONListener) listeners.next()).onJSONReceive(this.jObj);
        }
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

        if (null == jsonObject) {
            Toast.makeText(this.getMainActivity().getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();
        } else {
            this._fireJSONEvent();
        }
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        //do your request in here so that you don't interrupt the UI thread
        try {
            return this.getJSONFromUrl(params[0]);
        } catch (IOException e) {
            Log.d(TAG, "Error in JSON");
        }
        return null;
    }

    public JSONObject getJSONFromUrl(String urlstr) throws IOException {

        // HTTP Request
        InputStream is = null;
        try {
            URL url = new URL(urlstr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            conn.setConnectTimeout(CONNECT_TIMEOUT /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();
            Log.d(TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = this.convertInputStreamToString(is);

            // try parse the string to a JSON object
            try {
                jObj = new JSONObject(contentAsString);
            } catch (JSONException e) {
                Log.d("JSON Parser", "Error parsing data " + e.toString());
            }

            // return JSON String
            return jObj;
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }

    public String convertInputStreamToString(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedInputStream bis = new BufferedInputStream(stream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            byte b = (byte) result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toString();
    }
}
