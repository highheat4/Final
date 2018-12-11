package com.example.ayanbhowmick.afinal;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Main screen for our API testing app.
 */
public final class MainActivity extends AppCompatActivity {

    /** Stores data from API call.**/
    private static String data = "";

    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "Lab12:Main";

    /** Request queue for our network requests. */
    private static RequestQueue requestQueue;

    /**
     * Run when our activity comes into view.
     *
     * @param savedInstanceState state that was saved by the activity last time it was paused
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up a queue for our Volley requests
        requestQueue = Volley.newRequestQueue(this);

        // Load the main layout for our activity
        setContentView(R.layout.activity_main);

        // Attach the handler to our UI button
        final Button startAPICall = findViewById(R.id.startAPICall);
        startAPICall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Start API button clicked");
                startAPICall();
            }
        });

    }

    /**
     * Make an API call.
     */
    public void startAPICall() {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://api.spacexdata.com/v3/launches/next",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.d(TAG, response.toString());
                            data = response.toString();
                            apiCallDone(response);
                        }
                        /**
                         * data stores string from api
                         */
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.w(TAG, error.toString());
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * this thing is just called and also sets a textview regarding when the next launch is.
     * @param response is the input from the api.
     */
    public void apiCallDone(final JSONObject response) {
        try {
            setContentView(R.layout.activity_main);
            TextView jsonResult = findViewById(R.id.jsonResult);
            TextView countdown = findViewById(R.id.countdown);
            String displayText = response.get("launch_date_local").toString();
            String[] temp = displayText.split("T");
            String date = temp[0];
            String time = temp[1];
            displayText = "DATE: " + date + "                       TIME: " + time;
            jsonResult.setText("Launch date is --- " + displayText);

            TextView description = findViewById(R.id.description);
            description.setText("Mission Details: " + response.get("details").toString());

            TextView flightnumber = findViewById(R.id.flightnumber);
            flightnumber.setText("Flight Number: " + response.get("flight_number").toString());

            TextView rocket = findViewById(R.id.rocket);
            rocket.setText("Rocket: " + response.get("rocket").toString().split("\"")[7]);

            TextView countdown2 = findViewById(R.id.countdown2);
            countdown2.setText("Time until the next launch is");


            //Get the current date and time
            Date computerDate = new Date();
            int myYear = computerDate.getYear() + 1900;
            int myMonth = computerDate.getMonth() + 1;
            int myDay = computerDate.getDate();
            int myHours = computerDate.getHours();
            int myMinutes = computerDate.getMinutes();
            int mySeconds = computerDate.getSeconds();
            int[] myTimes = {myYear, myMonth, myDay, myHours, myMinutes, mySeconds};
            //get the launch date and time
            int launchYear = Integer.parseInt(date.split("-")[0]);
            int launchMonth = Integer.parseInt(date.split("-")[1]);
            int launchDay = Integer.parseInt(date.split("-")[2]);
            int launchHours = Integer.parseInt(time.split(":")[0]);
            int launchMinutes = Integer.parseInt(time.split(":")[1]);
            int launchSeconds = 0;
            int[] launchTimes = {launchYear, launchMonth, launchDay, launchHours,
                    launchMinutes, launchSeconds};
            int[] array = timeUntil(myTimes, launchTimes);
            String displayTimeUntil = Integer.toString(array[0])
                    + "-" + Integer.toString(array[1])
                    + "-" + Integer.toString(array[2])
                    + " " + Integer.toString(array[3]) + ":" + Integer.toString(array[4]) + ":"
                    + Integer.toString(array[5]);
            countdown.setText(displayTimeUntil);
        } catch (JSONException ignored) { }
    }


    public int[] timeUntil(final int[] mine, final int[] launch) {
        int[] returnTimes = {launch[0] - mine[0], launch[1] - mine[1],
                launch[2] - mine[2], launch[3] - mine[3], launch[4] - mine[4], launch[5] - mine[5]};
        if (returnTimes[5] < 0) {
            returnTimes[5] += 60;
            returnTimes[4] -= 1;
        }
        if (returnTimes[4] < 0) {
            returnTimes[4] += 60;
            returnTimes[3] -= 1;
        }
        if (returnTimes[3] < 0) {
            returnTimes[3] += 24;
            returnTimes[2] -= 1;
        }
        if (returnTimes[2] < 0) {
            returnTimes[2] += 30;
            returnTimes[1] -= 1;
        }
        if (returnTimes[1] < 0) {
            returnTimes[1] += 12;
            returnTimes[0] -= 1;
        }
        return returnTimes;
    }
}
