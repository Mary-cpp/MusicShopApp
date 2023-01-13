package com.example.myapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class FragmentShop extends Fragment {

    private static final String[] sort = {"Сначала дешевле","Сначала дороже", "По алфавиту"};

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

            // Выпадающий список
            Spinner spinner = v.findViewById(R.id.spinner);
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, sort);
            spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String item = (String)adapterView.getItemAtPosition(i);
                    switch(item){
                        case ("Сначала дешевле"):
                            eventChangeListener("price", 0);
                            break;
                        case("Сначала дороже"):
                            eventChangeListener("price");
                            break;
                        case("По алфавиту"):
                            eventChangeListener("name", 0);
                            break;
                        default:
                            eventChangeListener();
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    eventChangeListener();
                }
            });

            RecyclerView rv = v.findViewById(R.id.products_list);
            rv.setLayoutManager(new LinearLayoutManager(context));

            // Нажатие на элемент RecyclerView
            ProductAdapter.OnProductClickListener productClickListener = (position) ->
                    Navigation.findNavController(v).navigate(R.id.action_fragmentShop_to_fragmentProduct, productToBundle(mProducts.get(position)));

            // Кнопка "Купить"
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

    // Метод, обрабатывающий изменения в DataSet Recycler 'а
    private void eventChangeListener() {
        mProducts.clear();
        db.collection("products")
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

    private void eventChangeListener(String sort){
        mProducts.clear();
        db.collection("products")
                .orderBy(sort, Query.Direction.DESCENDING)
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

    private void eventChangeListener(String sort, int order){
        mProducts.clear();
        db.collection("products")
                .orderBy(sort, Query.Direction.ASCENDING)
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

    // преобразование экземпляра класса в Bundle для передачи в Intent
    private Bundle productToBundle(Product product){
        Bundle bundle = new Bundle();
        // Передаем экземпляр класса
        bundle.putParcelable("product", product);
        return bundle;
    }
}