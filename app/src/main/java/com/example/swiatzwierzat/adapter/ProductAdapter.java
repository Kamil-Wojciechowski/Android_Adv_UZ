package com.example.swiatzwierzat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.swiatzwierzat.ProductDetailActivity;
import com.example.swiatzwierzat.R;
import com.example.swiatzwierzat.model.Product;

import java.util.List;

/*
Adaptery mają za zadanie wyświetlić odpowiednio produkty w ListView.

 */
public class ProductAdapter extends ArrayAdapter<Product> {
    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        super(context, 0, productList);
        this.context = context;
        this.productList = productList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        Product currentProduct = productList.get(position);

        TextView nameTextView = (TextView) listItem.findViewById(R.id.nameTextView);
        nameTextView.setText(currentProduct.getName());

        TextView priceTextView = (TextView) listItem.findViewById(R.id.priceTextView);
        priceTextView.setText(String.format("%.2f zł", currentProduct.getPrice()));

        TextView descTextView = (TextView) listItem.findViewById(R.id.descTextView);
        descTextView.setText(currentProduct.getDescription());

        Product product = productList.get(position);

        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tworzenie nowej aktywności i przekazanie danych o wybranym produkcie
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("id", product.getId());
                intent.putExtra("name", product.getName());
                intent.putExtra("productTag", product.getProductTag());
                intent.putExtra("description", product.getDescription());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("available", product.getAvailable());
                intent.putExtra("image", product.getImage());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return listItem;
    }
}
