package com.dalle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String OPENAI_API_KEY = "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";
    private static final String API_ENDPOINT = "https://api.openai.com/v1/images/generations";
    private Button generateImages;
    private RecyclerView recyclerView;
    private EditText editText;

    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generateImages = findViewById(R.id.generateImages);
        recyclerView = findViewById(R.id.recyclerView);
        editText = findViewById(R.id.searchEdit);


        requestQueue = Volley.newRequestQueue(this);

        generateImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("Smiling Cats");
                String query = editText.getText().toString();
                if(query.equals("")){
                    Toast.makeText(MainActivity.this,"Query cannot be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                generateImages(query);
            }
        });


    }

    private void generateImages(String query) {
        // Create a JSON object with the request data
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("prompt", query);
            requestData.put("n", 1);
            requestData.put("size", "1024x1024");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                API_ENDPOINT,
                requestData,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray value = response.getJSONArray("data");
                    Log.d("TAG","" + value.getJSONObject(0).get("url"));
                    Log.d("TAG","" + value.getJSONObject(1).get("url"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG","Error " + error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> mapHeader = new HashMap<>();
                mapHeader.put("Authorization", "Bearer "+ OPENAI_API_KEY);
                mapHeader.put("Content-Type", "application/json");

                return mapHeader;
            }
        };

        int intTimeOutPeriod = 60000; // 60 seconds
        RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeOutPeriod,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
    }
}