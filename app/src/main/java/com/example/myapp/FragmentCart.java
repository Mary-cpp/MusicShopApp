package com.example.myapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentCart extends Fragment {

    ArrayList<Product> mProducts;
    ArrayList<String> mIndexes;
    ProductCartAdapter adapter;

    public CollectionReference cr;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FragmentCart() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cart, container, false);
        try {
            SharedPreferences sp =  this.requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);

            String userId = sp.getString("userID", null);

            cr = db.collection("users").document(userId).collection("cart");

            RecyclerView rv = v.findViewById(R.id.cart_list);

            mProducts = new ArrayList<>();
            mIndexes = new ArrayList<>();

            // Слушатель нажатия на кнопку "Удалить"
            ProductCartAdapter.OnDeleteProductListener deleteProductListener = position
                    -> cr.document(mIndexes.get(position))
                    .delete()
                    .addOnCompleteListener(task -> {
                        eventChangeListener();
                        Toast.makeText(getContext(), "Product deleted!", Toast.LENGTH_SHORT).show();
                    });

            // Слушатель нажатия на кнопки "+" и "-"
            ProductCartAdapter.OnProductAddListener listener = (position, value) -> {
                HashMap <String, Long> data = new HashMap<>();
                data.put("value", mProducts.get(position).getValue()+value);
                cr
                        .document(mIndexes.get(position))
                        .set(data, SetOptions.merge())
                        .addOnCompleteListener(task -> eventChangeListener());
            };

            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ProductCartAdapter(getContext(), mProducts, listener, deleteProductListener);
            rv.setAdapter(adapter);
            eventChangeListener();
            }
            catch (NullPointerException e){
                Toast.makeText(getContext(), "Error occurred. Please, try te re-login or open this page again", Toast.LENGTH_SHORT).show();
            }
        return v;
    }

    private void eventChangeListener() {
       cr
                .addSnapshotListener((value, error) -> {
                    mProducts.clear();
                    mIndexes.clear();
                    if(error!=null){
                        Log.e("FIREBASE ERROR", error.getMessage());
                    }
                    assert value!=null;
                    for (QueryDocumentSnapshot doc : value){
                        mProducts.add(doc.toObject(Product.class));
                        mIndexes.add(doc.getId());
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}