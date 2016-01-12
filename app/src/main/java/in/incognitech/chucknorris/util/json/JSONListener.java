package in.incognitech.chucknorris.util.json;

import org.json.JSONObject;

/**
 * Created by udit on 10/01/16.
 */

public interface JSONListener {
    public void onJSONReceive(JSONObject jsonObject);
}