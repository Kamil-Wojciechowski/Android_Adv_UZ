package com.example.swiatzwierzat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.swiatzwierzat.adapter.CartAdapter;
import com.example.swiatzwierzat.configuration.BackendConfig;
import com.example.swiatzwierzat.model.ItemCart;
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

public class CartActivity extends AppCompatActivity {

    private ListView listView;

    private Button buy;
    private List<ItemCart> itemCarts;

    private FirebaseDatabase database;
    private DatabaseReference shoppingCart;
    private SharedPreferences sharedPreferences;

    private BackendConfig backendConfig;

    private void initializer() {
        setContentView(R.layout.activity_cart);

        listView = findViewById(R.id.lv_cart_items);
        buy = findViewById(R.id.bt_cart_buy);
        buy.setOnClickListener(this::onBuy);

        sharedPreferences = getApplicationContext().getSharedPreferences(BackendConfig.getSharedPreferenceName(), Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("loginEmail", "").replace(".", "_");
        database = FirebaseDatabase.getInstance();
        shoppingCart = database.getReference(email + "_shopping_cart");

        shoppingCart.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if(value != null && !value.isBlank()) {
                    try {
                        itemCarts = new ArrayList<>();
                        JSONArray items = new JSONArray(value);

                        for(int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            itemCarts.add(
                                    new ItemCart(
                                            item.getInt("id"),
                                            item.getString("name"),
                                            item.getInt("amount"),
                                            item.getInt("available")
                                    )
                            );
                        }

                        CartAdapter cartAdapter = new CartAdapter(getApplicationContext(), itemCarts);
                        listView.setAdapter(cartAdapter);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("com.example.swiatzwierzat.firebase", "Failed to read value.", error.toException());
                Toast.makeText(getApplicationContext() ,R.string.global_something_went_wrong, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onBuy(View view) {
        Integer addressId = sharedPreferences.getInt("user_address", 0);
        if(addressId == 0) {
            Toast.makeText(getApplicationContext(), R.string.provide_address, Toast.LENGTH_LONG);
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }

        try {
            JSONObject item = new JSONObject()
                    .put("status", "new")
                    .put("address", addressId);

            AndroidNetworking.post(backendConfig.getUrl() + "/orders")
                    .addHeaders("Authorization", backendConfig.getToken())
                    .addJSONObjectBody(item)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Integer orderId = response.getInt("id");

                                addItemsToOrder(orderId);

                                Toast.makeText(getApplicationContext(), R.string.order_is_placed, Toast.LENGTH_LONG).show();

                                JSONArray emptyArray = new JSONArray();
                                shoppingCart.setValue(emptyArray.toString());

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(getApplicationContext(), R.string.global_something_went_wrong, Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void addItemsToOrder(Integer orderId) throws JSONException {
        for(ItemCart item : itemCarts) {

            JSONObject preparedItem = new JSONObject();

            preparedItem
                    .put("productId", item.getId())
                    .put("amount", item.getAmount());

            AndroidNetworking.post(backendConfig.getUrl() + "/orders/" + orderId + "/units")
                    .addHeaders("Authorization", backendConfig.getToken())
                    .addJSONObjectBody(preparedItem)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.w("cart.activity", "Item was added to order!");
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(getApplicationContext(), R.string.global_something_went_wrong, Toast.LENGTH_LONG).show();
                        }
                    });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializer();
    }
}