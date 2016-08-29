package com.example.chriswang.rhumbixtest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] fruits = {"apple", "banana", "orange", "grape", "peach", "avocado", "cherry"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, fruits);
        final Button button = (Button) findViewById(R.id.button);
        final AutoCompleteTextView text = (AutoCompleteTextView) findViewById(R.id.search_terms);
        text.setAdapter(adapter);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String searchTerm = text.getText().toString();
                if (searchTerm.length() != 0) {
                    String apiKey = "dc6zaTOxFJmzC";
                    int limit = 1;
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(GiphyAPI.ENDPOINT)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    GiphyAPI giphyAPI = retrofit.create(GiphyAPI.class);
                    Call<GiphyResponse> call = giphyAPI.getResponse(searchTerm, apiKey, limit);
                    call.enqueue(new Callback<GiphyResponse>() {
                        @Override
                        public void onResponse(Call<GiphyResponse> call, Response<GiphyResponse> response) {
                            String url = response.body().getData().get(0).getImages().getFixed_height().getUrl();
                            TextView textView = (TextView) findViewById(R.id.textView);
                            textView.setText("Loading... " + url);
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(browserIntent);
                        }

                        @Override
                        public void onFailure(Call<GiphyResponse> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Failed to retrive request GIF!", Toast.LENGTH_SHORT);
                        }
                    });
                }
            }
        });
    }
}
