package com.example.myapp.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.model.Order;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder>{

    private final Context context ;
    private final ArrayList<Order> mOrders;

    public OrdersAdapter(Context context, ArrayList<Order> mOrders) {
        this.context = context;
        this.mOrders = mOrders;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView orderNumber, orderDetails, orderPrice;

        public MyViewHolder(View itemView) {
            super(itemView);

            // Инициализация View
            orderNumber = itemView.findViewById(R.id.order_number_text);
            orderDetails = itemView.findViewById(R.id.order_details_text);
            orderPrice = itemView.findViewById(R.id.order_price_text);
        }
    }

    @NonNull
    @Override
    public OrdersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View orders = LayoutInflater.from(context).inflate(R.layout.order_card_element, parent, false);
        return new MyViewHolder(orders);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.MyViewHolder holder, int position) {
        Order order = mOrders.get(position);
        holder.orderNumber.setText(order.getNumber());
        holder.orderDetails.setText(order.getDetails());
        holder.orderPrice.setText(String.valueOf(order.getPrice()));
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }
}
