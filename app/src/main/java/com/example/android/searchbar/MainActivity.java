package com.example.android.searchbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Toast;

import com.example.android.searchbar.Retrofit.ISuggestAPI;
import com.example.android.searchbar.Retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private MaterialSearchBar materialSearchBar;
    private ISuggestAPI myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private List<String> suggestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        materialSearchBar = (MaterialSearchBar) findViewById(R.id.search_bar);
        myAPI = RetrofitClient.getInstance().create(ISuggestAPI.class);

            materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    Toast.makeText(MainActivity.this, text.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });
            materialSearchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    getSuggestions(s.toString(), "chrome", "en", "yt");

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void getSuggestions(String query, String client, String language, String restrict) {
        if (!TextUtils.isEmpty(restrict)) {
            compositeDisposable.add(myAPI.getSuggestFromYoutube(query, client, language, restrict)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    if(suggestions.size()>0)
                        suggestions.clear();
                    JSONArray mainJSON=new JSONArray(s);
                    JSONArray suggestArray=new JSONArray(mainJSON.getString(1));
                    suggestions=new Gson().fromJson(mainJSON.getString(1),
                            new TypeToken<List<String>>(){}.getType());
                    materialSearchBar.updateLastSuggestions(suggestions);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Toast.makeText(MainActivity.this,""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    System.out.println(throwable.getMessage());
                }
            }));
        }
    }

}