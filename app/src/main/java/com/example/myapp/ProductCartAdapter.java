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

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductCartAdapter extends RecyclerView.Adapter<ProductCartAdapter.MyViewHolder>{

    public interface OnDeleteProductListener{
        void onProductDelete(int position);
    }

    public interface OnProductAddListener{
        void onProductAdd(int position, int value);
    }

    private final OnProductAddListener mOnProductAddListener;
    private final OnDeleteProductListener mOnDeleteProductListener;
    private final Context context ;
    private final ArrayList<Product> mProducts;

    public ProductCartAdapter(Context context, ArrayList<Product> mProducts, OnProductAddListener onProductAddListener, OnDeleteProductListener onDeleteProductListener) {
        this.context = context;
        this.mProducts = mProducts;
        this.mOnProductAddListener = onProductAddListener;
        this.mOnDeleteProductListener = onDeleteProductListener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView productName, productPrice, quantity;
        private final ImageView productPic;
        private final ImageButton delete, minus, plus;

        public MyViewHolder(View itemView) {
            super(itemView);

            // Инициализация View
            productName = itemView.findViewById(R.id.product_cart_name);
            productPic = itemView.findViewById(R.id.product_cart_pic);
            productPrice = itemView.findViewById(R.id.product_cart_price);
            delete = itemView.findViewById(R.id.cart_delete_button);
            plus = itemView.findViewById(R.id.plus_button);
            minus = itemView.findViewById(R.id.minus_button);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }


    @NonNull
    @Override
    public ProductCartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View products = LayoutInflater.from(context).inflate(R.layout.product_card_in_cart, parent, false);
        return new MyViewHolder(products);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCartAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Product product = mProducts.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.valueOf(product.getPrice()));
        holder.quantity.setText(String.valueOf(product.getValue()));
        Picasso
                .with(holder.productPic.getContext())
                .load(product.getPic())
                .placeholder(R.drawable.photo)
                .error(R.drawable.photo)
                .into(holder.productPic);

        holder.plus.setOnClickListener(view -> mOnProductAddListener.onProductAdd(position, 1));

        holder.minus.setOnClickListener(view -> mOnProductAddListener.onProductAdd(position, -1));

        holder.delete.setOnClickListener(view -> mOnDeleteProductListener.onProductDelete(position));
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }
}
