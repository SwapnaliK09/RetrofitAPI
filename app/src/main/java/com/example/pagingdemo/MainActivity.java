package com.example.pagingdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.jar.JarException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;
    ArrayList<MainData> dataArrayList = new ArrayList<>();
    MainAdapter adapter;
    int page = 1, limit = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nestedScrollView = findViewById(R.id.scroll_view);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        adapter = new MainAdapter(MainActivity.this,dataArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        getData(page,limit);


        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){

                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    getData(page,limit);
                }
                System.out.println("<<<<<<<<<<<  "+page);
            }
        });
    }

    private void getData(int page , int limit) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://picsum.photos/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        MainInterface mainInterface = retrofit.create(MainInterface.class);
        Call<String> call = mainInterface.STRING_CALL(page,limit);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()&& response.body()!= null){
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray jsonArray = new JSONArray(response.body());
                        parseResult(jsonArray);


                    }
                    catch ( JSONException e ){
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }



    private void parseResult(JSONArray jsonArray) {

        for (int i = 0;i<jsonArray.length();i++){
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                MainData data = new MainData();
                data.setImage(object.getString("download_url"));
                data.setName(object.getString("author"));
                dataArrayList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new MainAdapter(MainActivity.this,dataArrayList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}