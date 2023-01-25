package com.example.myapp.view;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.controller.OrdersAdapter;
import com.example.myapp.model.Order;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FragmentOrders extends Fragment {

    private ArrayList<Order> mOrders;

    public CollectionReference cr;

    private OrdersAdapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FragmentOrders() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order, container, false);

        SharedPreferences sp =  this.requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);

        String userId = sp.getString("userID", null);

        cr =  db.collection("users").document(userId).collection("orders");

        RecyclerView rv = v.findViewById(R.id.orders_recycler);

        mOrders = new ArrayList<>();

        adapter = new OrdersAdapter(getContext(), mOrders);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        eventChangeListener();

        return v;
    }

    private void eventChangeListener() {
        cr
                .addSnapshotListener((value, error) -> {
                    mOrders.clear();
                    if(error!=null){
                        Log.e("FIREBASE ERROR", error.getMessage());
                    }
                    assert value!=null;
                    for (QueryDocumentSnapshot doc : value){
                        mOrders.add(doc.toObject(Order.class));
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
