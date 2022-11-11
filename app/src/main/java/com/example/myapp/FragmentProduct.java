package com.example.myapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class FragmentProduct extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences sp =  this.requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);

        View v = inflater.inflate(R.layout.fragment_product, container, false);
        Product product = getArguments().getParcelable("product");
        ImageView pic = v.findViewById(R.id.product_page_pic);
        Picasso.with(pic.getContext())
                .load(product.getPic())
                .error(R.drawable.photo)
                .into(pic);
        TextView name = v.findViewById(R.id.product_page_name);
        name.setText(product.getName());
        TextView desc = v.findViewById(R.id.product_desc_text);
        desc.setText(product.getDescription());
        TextView price = v.findViewById(R.id.product_page_price);
        price.setText(String.valueOf(product.getPrice()));
        MaterialButton buy = v.findViewById(R.id.product_page_buy);

        buy.setOnClickListener(view -> {
            String userId = sp.getString("userID", null);
            CollectionReference userRef = db.collection("users").document(userId).collection("cart");
            userRef
                    .add(product.makeMap())
                    .addOnSuccessListener(documentReference -> Toast.makeText(getContext(), "Product was added to the cart!", Toast.LENGTH_SHORT).show());
        });
        return v;
    }
}
