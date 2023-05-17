package com.example.swiatzwierzat.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.swiatzwierzat.ProductDetailActivity;
import com.example.swiatzwierzat.R;
import com.example.swiatzwierzat.configuration.BackendConfig;
import com.example.swiatzwierzat.model.ItemCart;
import com.example.swiatzwierzat.model.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CartAdapter extends ArrayAdapter<ItemCart> {
    private Context context;
    private List<ItemCart> cartList;

    private FirebaseDatabase database;
    private DatabaseReference shoppingCart;
    private SharedPreferences sharedPreferences;

    public CartAdapter(Context context, List<ItemCart> cartList) {
        super(context, 0, cartList);
        this.context = context;
        this.cartList = cartList;

        sharedPreferences = context.getSharedPreferences(BackendConfig.getSharedPreferenceName(), Context.MODE_PRIVATE);

        String email = sharedPreferences.getString("loginEmail", "").replace(".", "_");

        database = FirebaseDatabase.getInstance();
        shoppingCart = database.getReference(email + "_shopping_cart");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        }

        ItemCart currentProduct = cartList.get(position);

        TextView nameTextView = (TextView) listItem.findViewById(R.id.tv_name_cart);
        nameTextView.setText(currentProduct.getName());

        TextView amountTextView = (TextView) listItem.findViewById(R.id.tv_cart_ammount);
        amountTextView.setText(currentProduct.getAmount().toString());

        Button minus = (Button) listItem.findViewById(R.id.bt_cart_minus);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemCart item = cartList.get(position);

                if(item.getAmount() >= 2) {
                    item.setAmount(item.getAmount()-1);
                }
                cartList.set(position, item);
                amountTextView.setText(item.getAmount().toString());

                updateFirebase();
            }
        });

        Button delete = (Button) listItem.findViewById(R.id.bt_cart_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartList.remove(position);

                updateFirebase();
            }
        });

        Button add = (Button) listItem.findViewById(R.id.bt_cart_plus);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemCart item = cartList.get(position);

                if(item.getAmount() < item.getAvailable()) {
                    item.setAmount(item.getAmount()+1);
                }
                cartList.set(position, item);
                amountTextView.setText(item.getAmount().toString());

                updateFirebase();
            }
        });

        return listItem;
    }

    private void updateFirebase() {
        JSONArray items = new JSONArray();

        for(ItemCart itemCart : cartList) {
            JSONObject itemJson = new JSONObject();
            try {
                itemJson.put("id", itemCart.getId());
                itemJson.put("name", itemCart.getName());
                itemJson.put("available", itemCart.getAvailable());
                itemJson.put("amount", itemCart.getAmount());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            items.put(itemJson);
        }

        shoppingCart.setValue(items.toString());
    }

}
