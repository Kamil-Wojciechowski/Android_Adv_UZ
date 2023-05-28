package com.example.swiatzwierzat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swiatzwierzat.configuration.BackendConfig;
import com.example.swiatzwierzat.library.LibNotifications;
import com.example.swiatzwierzat.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductDetailActivity extends AppCompatActivity {

    private BackendConfig backendConfig;
    private SharedPreferences sharedPreferences;
    private FirebaseDatabase database;
    private DatabaseReference shoppingCart;
    private Product product;

    private ImageView productImage;
    private TextView productName;
    private TextView description;
    private TextView productTag;
    private TextView available;
    private TextView price;
    private String email;

    private Button buy;

    private JSONArray shoppingCartInternal = new JSONArray();

    /*
    Inicjalizuje cały obiekt.
    1. Pobiera wysłane elementy z Intenta na temat produktu
    2. Przypisuje elementy do zmiennych
    3. Przypisuje funkcjonalności do przycisków
    4. pobiera informacje na temat aktualnego koszyka
     */
    private void initializer() {
        setContentView(R.layout.activity_product_detail);

        product = new Product(
                getIntent().getIntExtra("id", 0),
                getIntent().getStringExtra("name"),
                getIntent().getStringExtra("description"),
                getIntent().getStringExtra("productTag"),
                getIntent().getIntExtra("available", 0),
                getIntent().getDoubleExtra("price", 0.0),
                getIntent().getStringExtra("image")
        );

        productImage = findViewById(R.id.iv_product_image);
        productName = findViewById(R.id.tv_product_detail_name);
        description = findViewById(R.id.tv_product_description);
        productTag = findViewById(R.id.tv_product_tag);
        available = findViewById(R.id.tv_product_available);
        price = findViewById(R.id.tv_product_price);
        buy = findViewById(R.id.bt_product_buy);

        buy.setOnClickListener(this::onBuy);

        Context context = this.getApplicationContext();
        sharedPreferences = context.getSharedPreferences(BackendConfig.getSharedPreferenceName(), Context.MODE_PRIVATE);

        email = sharedPreferences.getString("loginEmail", "").replace(".", "_");

        database = FirebaseDatabase.getInstance();
        shoppingCart = database.getReference(email + "_shopping_cart");

        shoppingCart.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value != null && !value.isBlank()) {
                    try {
                        shoppingCartInternal = new JSONArray(value);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("com.example.swiatzwierzat.firebase", "Failed to read value.", error.toException());
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again later!", Toast.LENGTH_LONG).show();
            }
        });

    }

    /*
    Wstawia wszystkie informacje do pól nt. produktu
     */
    private void putData() {
        productName.setText(product.getName());
        description.setText(product.getDescription());
        productTag.setText(product.getProductTag());
        available.setText(product.getAvailable().toString());
        price.setText(product.getPrice().toString());

        String cleanImage = product.getImage().replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,", "");
        byte[] decodedString = Base64.decode(cleanImage, Base64.DEFAULT);
        Bitmap decodedBytes = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        productImage.setImageBitmap(decodedBytes);
    }

    /*
    Logika odpowiadająca za  wstawienie dodatkowego produktu (lub pierwszego) do koszyka
     */
    private void onBuy(View v) {
        Boolean found = false;
        for (int i = 0; i < shoppingCartInternal.length(); i++) {
            try {
                JSONObject obj = shoppingCartInternal.getJSONObject(i);

                if (product.getId() == obj.getInt("id")) {
                    int toBuy = obj.getInt("amount");
                    if (product.getAvailable() >= toBuy) {
                        obj.put("amount", toBuy + 1);
                    }

                    found = true;
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            if (!found && product.getAvailable() > 0) {
                JSONObject object = new JSONObject();
                object.put("id", product.getId());
                object.put("name", product.getName());
                object.put("available", product.getAvailable());
                object.put("amount", 1);

                shoppingCartInternal.put(object);
            }

            LibNotifications.sendNotification(this, R.string.notification_cart_item_added_title, R.string.notification_cart_item_added_message);
        } catch (JSONException e) {
            LibNotifications.sendToast(this, R.string.notification_error_unknown);
            throw new RuntimeException(e);
        }

        shoppingCart.setValue(shoppingCartInternal.toString());
    }

    /*
    Zbudowanie aktyuwności
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer();

        putData();
    }
}