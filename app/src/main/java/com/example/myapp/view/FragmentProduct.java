package com.example.myapp.view;

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

import com.example.myapp.R;
import com.example.myapp.model.Product;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class FragmentProduct extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences sp =  this.requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userId = sp.getString("userID", null);

        View v = inflater.inflate(R.layout.fragment_product, container, false);

        Product product = getArguments().getParcelable("product");

        showProduct(v, product);

        MaterialButton buy = v.findViewById(R.id.product_page_buy);

        buy.setOnClickListener(view -> {
            buyProduct(product, userId);
        });
        return v;
    }

    // Загружаем информацию во View
    private void showProduct(View v, Product product){
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
    }

    // Добавить товар в корзину
    private void buyProduct(Product product, String userDocument){
        CollectionReference userRef = db.collection("users").document(userDocument).collection("cart");
        userRef
                .add(product.makeMap())
                .addOnSuccessListener(documentReference -> Toast.makeText(getContext(), "Product was added to the cart!", Toast.LENGTH_SHORT).show());
    }
}
