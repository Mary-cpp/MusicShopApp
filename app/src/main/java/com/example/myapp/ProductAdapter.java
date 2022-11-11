package com.example.myapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private final Context context ;
    private final ArrayList<Product> mProducts;
    private final OnProductClickListener mOnProductClickListener;
    private final OnProductBuyListener mOnProductBuyListener;

    public interface OnProductClickListener{
        void onProductClick(int position);
    }

    public interface OnProductBuyListener{
        void onProductBuy(int position);
    }

    public ProductAdapter(Context context, ArrayList<Product> products, OnProductClickListener onProductClickListener, OnProductBuyListener onProductBuyListener) {
        this.context = context;
        mProducts = products;
        this.mOnProductClickListener = onProductClickListener;
        this.mOnProductBuyListener = onProductBuyListener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView productName, productPrice;
        private final ImageView productPic;
        private ImageButton like;
        private MaterialButton buy;

        public MyViewHolder(View itemView) {
            super(itemView);

            // Инициализация View
            productName = itemView.findViewById(R.id.product_name);
            productPic = itemView.findViewById(R.id.product_pic);
            productPrice = itemView.findViewById(R.id.product_price);
            like = itemView.findViewById(R.id.like_button);
            buy = itemView.findViewById(R.id.buy_button);
        }
    }

    @NonNull
    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View products = LayoutInflater.from(context).inflate(R.layout.product_card_element, parent, false);
        return new MyViewHolder(products);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Product product = mProducts.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText("" + product.getPrice());
        Picasso.with(holder.productPic.getContext())
                .load(product.getPic())
                .placeholder(R.drawable.photo)
                .error(R.drawable.photo)
                .into(holder.productPic);

        holder.itemView.setOnClickListener(view -> mOnProductClickListener.onProductClick(position));

        holder.buy.setOnClickListener(view -> mOnProductBuyListener.onProductBuy(position));

    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }


}
