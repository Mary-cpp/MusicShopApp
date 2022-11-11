package com.example.myapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FragmentShop extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Context context;
    ArrayList<Product> mProducts;
    ProductAdapter adapter;

    public FragmentShop() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_shop, container, false);

            SharedPreferences sp =  this.requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);

            mProducts = new ArrayList<>();

            RecyclerView rv = v.findViewById(R.id.products_list);
            rv.setLayoutManager(new LinearLayoutManager(context));

            ProductAdapter.OnProductClickListener productClickListener = (position) ->
                    Navigation.findNavController(v).navigate(R.id.action_fragmentShop_to_fragmentProduct, productToBundle(mProducts.get(position)));

            ProductAdapter.OnProductBuyListener productBuyListener = position -> {
                String userId = sp.getString("userID", null);
                CollectionReference userRef = db.collection("users").document(userId).collection("cart");
                userRef
                        .add(mProducts.get(position).makeMap())
                        .addOnSuccessListener(documentReference -> Toast.makeText(getContext(), "Product was added to the cart!", Toast.LENGTH_SHORT).show());
            };

            adapter = new ProductAdapter(context, mProducts, productClickListener, productBuyListener);
            rv.setAdapter(adapter);

            eventChangeListener();

        return v;
    }

    private void eventChangeListener() {
        db.collection("products") // Добавить сортировку
                .addSnapshotListener((value, error) -> {
                    if(error!=null){
                        Log.e("FIREBASE ERROR", error.getMessage());
                    }
                    assert value != null;
                    for(DocumentChange dc : value.getDocumentChanges()){
                        mProducts.add(dc.getDocument().toObject(Product.class));
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private Bundle productToBundle(Product product){
        Bundle bundle = new Bundle();
        // Передаем экземпляр класса
        bundle.putParcelable("product", product);
        return bundle;
    }
}