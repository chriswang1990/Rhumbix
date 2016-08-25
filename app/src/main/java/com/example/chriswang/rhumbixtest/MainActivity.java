package com.example.chriswang.rhumbixtest;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public final String LOG_TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] fruits = {"apple", "banana", "orange", "grape", "peach", "avocado", "cherry"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, fruits);
        final Button button = (Button) findViewById(R.id.search_button);
        final AutoCompleteTextView text = (AutoCompleteTextView) findViewById(R.id.search_term);
        text.setAdapter(adapter);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String searchTerm = text.getText().toString();
                GetGif getGif = new GetGif();
                getGif.execute(searchTerm);
            }
        });
    }

    private class GetGif extends AsyncTask<String, Void, URL> {

        @Override
        protected URL doInBackground(String... searchTerm) {
            //http://api.giphy.com/v1/gifs/search?q=[KEYWORD_HERE]&api_key=dc6zaTOxFJmzC&limit=1
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                Uri.Builder giphyUriBuilder = new Uri.Builder();
                giphyUriBuilder.scheme("http").authority("api.giphy.com").appendPath("v1")
                        .appendPath("gifs").appendPath("search")
                        .appendQueryParameter("q", searchTerm[0]).appendQueryParameter("api_key",
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
                URL returnedURL = getGifFromJSON(jsonString);
                return returnedURL;

            } catch (IOException e) {
                Log.e("Created URL failed", e.getMessage(), e);
                e.printStackTrace();
            }catch (JSONException e) {
                Log.e("Parse JSON failed", e.getMessage(), e);
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

        @Override
        protected void onPostExecute(URL url) {
            if (url != null) {
                TextView textView = (TextView) findViewById(R.id.returned_url);
                textView.setText("Loading... " + url.toString());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
                startActivity(browserIntent);
            }
        }
    }

    public URL getGifFromJSON(String jsonString) throws JSONException, IOException{
        JSONObject gifJSON= new JSONObject(jsonString);
        String url = gifJSON.getJSONArray("data").getJSONObject(0).getJSONObject("images")
                .getJSONObject("fixed_height").getString("url");
        return new URL(url);
    }

}
