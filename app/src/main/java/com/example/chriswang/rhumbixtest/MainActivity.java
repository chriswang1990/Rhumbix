package com.example.chriswang.rhumbixtest;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public final String LOG_TAG = MainActivity.class.getSimpleName();
    String searchTerm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = (Button) findViewById(R.id.search_button);
        final EditText text = (EditText) findViewById(R.id.search_term);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchTerm = text.getText().toString();
                GetGif getGif = new GetGif();
                getGif.execute();
            }
        });
    }

    private class GetGif extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //http://api.giphy.com/v1/gifs/search?q=[KEYWORD_HERE]&api_key=dc6zaTOxFJmzC&limit=1
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                Uri.Builder giphyUriBuilder = new Uri.Builder();
                giphyUriBuilder.scheme("http").authority("api.giphy.com").appendPath("v1app")
                        .appendPath("gifs").appendPath("search")
                        .appendQueryParameter("q", searchTerm).appendQueryParameter("api_key",
                        "dc6zaTOxFJmzC").appendQueryParameter("limit", "1").build();
                URL giphyUri = new URL(giphyUriBuilder.toString());
                Log.d(LOG_TAG, giphyUri.toString());
                urlConnection = (HttpURLConnection) giphyUri.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    line += "\n";
                    stringBuilder.append(line);
                }
                String jsonString = stringBuilder.toString();
                return null;

            } catch (IOException e) {
                Log.e("Created URL failed", e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream");
                    }
                }
            }
            return null;
        }



    }

    public URL getGifFromJSON(String jsonString) {
        return null;
    }
}