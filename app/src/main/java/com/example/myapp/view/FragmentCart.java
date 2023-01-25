package com.example.myapp.view;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.controller.ProductCartAdapter;
import com.example.myapp.model.Order;
import com.example.myapp.model.Product;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentCart extends Fragment {

    private static final String TAG_UPDATE = "FIRESTORE UPDATES";
    private static final String TAG_READ = "FIRESTORE READS";
    private static final String TAG_WRITE = "FIRESTORE WRITES";
    private static final String TAG_ERROR = "FIRESTORE ERROR";

    ArrayList<Product> mProducts;
    ArrayList<String> mIndexes;
    ProductCartAdapter adapter;

    ArrayList<Order> mOrders;

    public CollectionReference cr;
    long total;
    String details;
    String userId;

    public CollectionReference cr2;

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

            userId = sp.getString("userID", null);

            cr = db.collection("users").document(userId).collection("cart");
            cr2 = db.collection("users").document(userId).collection("orders");

            RecyclerView rv = v.findViewById(R.id.cart_list);

            mProducts = new ArrayList<>();
            mIndexes = new ArrayList<>();
            mOrders = new ArrayList<>();

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
                if (mProducts.get(position).getValue() < 1){       // Удаляем товар, если количество меньше одного
                    cr.document(mIndexes.get(position))
                            .delete()
                            .addOnCompleteListener(task -> {
                                eventChangeListener();
                                Toast.makeText(getContext(), "Product deleted!", Toast.LENGTH_SHORT).show();
                            });
                }
                else{
                    HashMap <String, Long> data = new HashMap<>();
                    data.put("value", mProducts.get(position).getValue()+value);
                    cr
                            .document(mIndexes.get(position))
                            .set(data, SetOptions.merge())
                            .addOnCompleteListener(task -> eventChangeListener());
                }
            };

            // Слушатель кнопки "Купить"
            Button shop = v.findViewById(R.id.shop_button_cart);
            shop.setOnClickListener(view -> {
                getAndConvert(v);
                deleteCartProducts();
            });

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

    // Конвертация информации из Корзины в экземпляр класса "Заказ"
    public void getAndConvert(View v){
        details = "";
        total = 0L;
        db.collection("users").document(userId).collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mOrders.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        Log.d(TAG_READ, doc.getId() + "=>" + doc.getData());
                        details += doc.getString("name") + ";\n";
                        total += doc.getLong("price")*doc.getLong("value");
                    }
                    Log.d("FIRESTORE READ 2", details + total);
                    mOrders.add(new Order(randString(), details, total));
                    Log.d("FIRESTORE READ 3", "Repeat: " + mOrders.get(0).getDetails() + mOrders.get(0).getNumber() + mOrders.get(0).getPrice());

                    try{
                        cr2.add(mOrders.get(0).makeMap())
                                .addOnSuccessListener(documentReference -> Toast.makeText(getContext(), "Order is finished", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Log.e(TAG_ERROR, e.getMessage()));
                        Navigation.findNavController(v).navigate(R.id.action_fragmentCart_to_fragmentOrders);
                    }
                    catch (IndexOutOfBoundsException e){
                        Log.e(TAG_ERROR, e.getMessage());
                    }
                })
                .addOnFailureListener(e -> Log.e("FIREBASE ERROR", e.getMessage()));
    }

    public void deleteCartProducts(){
        cr.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        cr.document(doc.getId()).delete().addOnCompleteListener(task -> Log.d(TAG_UPDATE, "Product cart is emptied"));
                    }
                });
    }

    // Генерация случайной строки для номера заказа
    public String randString() {
        String result = String.valueOf(Math.random()*9000000+1000000);
        return result + " ";
    }
}