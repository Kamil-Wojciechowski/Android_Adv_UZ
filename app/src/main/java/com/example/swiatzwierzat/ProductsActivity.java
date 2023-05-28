package com.example.swiatzwierzat;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.swiatzwierzat.adapter.ProductAdapter;
import com.example.swiatzwierzat.configuration.BackendConfig;
import com.example.swiatzwierzat.model.Product;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    private ListView listView;
    private Button settings, cart, location;
    private FirebaseDatabase database;
    private DatabaseReference products;

    private BackendConfig backendConfig;
    private List<Product> productsArr;

    private SharedPreferences sharedPreferences;

    private void initializer(Bundle savedInstanceState) {
        sharedPreferences = getApplicationContext().getSharedPreferences(BackendConfig.getSharedPreferenceName(), Context.MODE_PRIVATE);
        if (sharedPreferences.getInt("user_address", 0) == 0) {
            Toast.makeText(getApplicationContext(), R.string.provide_address, Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }

        setContentView(R.layout.activity_products);

        listView = findViewById(R.id.lv_products);
        settings = findViewById(R.id.bt_products_settings);
        cart = findViewById(R.id.bt_products_cart);
        location = findViewById(R.id.bt_maps);

        settings.setOnClickListener(this::toSettings);
        cart.setOnClickListener(this::toCart);
        location.setOnClickListener(this::toMaps);

        database = FirebaseDatabase.getInstance();
        products = database.getReference("products");

        // Initialize the Mobile Ads SDK
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Toast.makeText(getApplicationContext(), " successful ", Toast.LENGTH_SHORT).show();
            }
        });

        AdView mAdView;
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer(savedInstanceState);

        getProducts();

        loadProductsToGrid();
    }

    private void getProducts() {
        AndroidNetworking.get(BackendConfig.getUrl() + "/products")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.w("backend.config.success", response.toString());

                        products.setValue(response.toString());
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.w("login.activity.error", "Error occured. Server is not accessible!");
                        Log.w("login.activity.error", anError.toString());
                    }
                });
    }

    private void loadProductsToGrid() {
        products.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                productsArr = new ArrayList<>();

                try {
                    JSONArray products = new JSONArray(value);
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject product = products.getJSONObject(i);

                        productsArr.add(
                                new Product(
                                        product.getInt("id"),
                                        product.getString("name"),
                                        product.getString("description"),
                                        product.getJSONObject("productTag").getString("name"),
                                        product.getInt("available"),
                                        product.getDouble("priceUnit"),
                                        product.getString("imageBase")
                                ));
                    }

                    ProductAdapter productAdapter = new ProductAdapter(getApplicationContext(), productsArr);
                    listView.setAdapter(productAdapter);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("com.example.swiatzwierzat.firebase", "Failed to read value.", error.toException());
                Toast.makeText(getApplicationContext(), R.string.global_something_went_wrong, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toSettings(View v) {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }

    private void toCart(View v) {
        startActivity(new Intent(getApplicationContext(), CartActivity.class));
    }

    private void toMaps(View v) {
        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
    }

    @Override
    public void onBackPressed() {
        // Zabezpieczenie przed powrotem na ekran logowania
    }
}