package in.incognitech.chucknorris;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import in.incognitech.chucknorris.util.image.ImageListener;
import in.incognitech.chucknorris.util.image.ImageParser;
import in.incognitech.chucknorris.util.json.JSONListener;
import in.incognitech.chucknorris.util.json.JSONParser;

public class MainActivity extends AppCompatActivity implements JSONListener, ImageListener {

    private static final String TAG = JSONParser.class.getSimpleName();
    private static String JOKE_API_URL = "http://api.icndb.com/jokes/random";
    private static String IMAGE_API_URL = "http://loremflickr.com/1280/960/chuck";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // This is the only thing is required for Hello World !!
        /**
         *
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * Imp Part Over
         */

        /**
         * Change Values at your own will
         */
        TextView greet = (TextView) findViewById(R.id.greet);
        greet.setText("Hello, " + getResources().getString(R.string.my_name) + "!");

        JSONParser jsonParser = new JSONParser();
        jsonParser.setMainActivity(this);
        jsonParser.addJSONListener(this);
        jsonParser.execute(JOKE_API_URL);

        ImageParser imageParser = new ImageParser();
        imageParser.setMainActivity(this);
        imageParser.addImageListener(this);
        imageParser.execute(IMAGE_API_URL);

        final MainActivity that = this;

        /**
         * This is extra
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView quote = (TextView) findViewById(R.id.quote);
                quote.setText("Please Wait ... Loading");

                ImageView banner = (ImageView) findViewById(R.id.banner);
                banner.setVisibility(ImageView.INVISIBLE);

                Toast.makeText(that.getApplicationContext(), "Fetching new data", Toast.LENGTH_SHORT).show();

                JSONParser jsonParser = new JSONParser();
                jsonParser.setMainActivity(that);
                jsonParser.addJSONListener(that);
                jsonParser.execute(JOKE_API_URL);

                ImageParser imageParser = new ImageParser();
                imageParser.setMainActivity(that);
                imageParser.addImageListener(that);
                imageParser.execute(IMAGE_API_URL);
            }
        });
    }

    @Override
    public void onImageReceive(Bitmap imageObject) {
        ImageView banner = (ImageView) findViewById(R.id.banner);
        banner.setImageBitmap(imageObject);
        banner.setVisibility(ImageView.VISIBLE);
    }

    @Override
    public void onJSONReceive(JSONObject jsonObject) {
        TextView quote = (TextView) findViewById(R.id.quote);
        try {
            JSONObject value = jsonObject.getJSONObject("value");
            quote.setText(StringEscapeUtils.unescapeHtml4(value.getString("joke")));
        } catch (JSONException e) {
            Log.d(TAG, "Error in JSON");
        }
    }
}
