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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FragmentShop extends Fragment {

    private static final String[] sort = {"Сортировать по:","Сначала дешевле","Сначала дороже", "По алфавиту"};

    private static final String TAG_UPDATE = "FIRESTORE UPDATES";
    private static final String TAG_READ = "FIRESTORE READS";
    private static final String TAG_WRITE = "FIRESTORE WRITES";
    private static final String TAG_ERROR = "FIRESTORE ERROR";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Context context;
    ArrayList<Product> mProducts;
    ArrayList<String> mIndexes;
    ArrayList<Product> likedProducts;
    ArrayList<Product> dislikedProducts;
    ProductAdapter adapter;
    String userId;

    public FragmentShop() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public void onPause() {
        super.onPause();
        likedProducts.removeAll(dislikedProducts);
        CollectionReference cr = db.collection("users").document(userId).collection("liked");
        for (Product product :
                likedProducts)  {
            cr.add(product.makeMap())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG_WRITE, "Products were added to favourites");
                        } else {
                            Log.w(TAG_ERROR, "get failed with ", task.getException());
                        }
                    });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_shop, container, false);

            SharedPreferences sp =  this.requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
            userId = sp.getString("userID", null);

            mProducts = new ArrayList<>();
            mIndexes = new ArrayList<>();
            likedProducts = new ArrayList<>();
            dislikedProducts = new ArrayList<>();

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

            // Кнопка "Понравилось"
            ProductAdapter.OnProductLikeListener productLikeListener = (position, likeTag) -> {
                HashMap<String, Object> map = new HashMap<>();
                DocumentReference dr = db.collection("products").document(mIndexes.get(position));
                if(likeTag.equals("fill")){
                    likeOrDislike(1, dr, map);
                    likedProducts.add(mProducts.get(position));
                }
                else{
                    likeOrDislike(0, dr, map);
                    dislikedProducts.add(mProducts.get(position));
                }
                eventChangeListener();
            };

            // Нажатие на элемент RecyclerView
            ProductAdapter.OnProductClickListener productClickListener = (position) ->
                    Navigation.findNavController(v).navigate(R.id.action_fragmentShop_to_fragmentProduct, productToBundle(mProducts.get(position)));

            // Кнопка "Купить"
            ProductAdapter.OnProductBuyListener productBuyListener = position -> {
                CollectionReference userRef = db.collection("users").document(userId).collection("cart");
                userRef
                        .add(mProducts.get(position).makeMap())
                        .addOnSuccessListener(documentReference -> Toast.makeText(getContext(), "Product was added to the cart!", Toast.LENGTH_SHORT).show());
            };

            // Поиск по товарам
            EditText searchEt = v.findViewById(R.id.search_product_et);
            searchEt.setText("");
            ImageButton search = v.findViewById(R.id.search_button);
            search.setOnClickListener(view -> searchProduct(searchEt.getText().toString()));

            adapter = new ProductAdapter(context, mProducts, productClickListener, productBuyListener, productLikeListener);
            rv.setAdapter(adapter);

            eventChangeListener();

        return v;
    }

    // Метод, обрабатывающий изменения в DataSet Recycler 'а
    private void eventChangeListener() {
        db.collection("products")
                .addSnapshotListener((value, error) -> {
                    mProducts.clear();
                    mIndexes.clear();
                    if(error!=null){

                        Log.e("FIREBASE ERROR", error.getMessage());
                    }
                    assert value != null;
                    for (QueryDocumentSnapshot doc : value){
                        mProducts.add(doc.toObject(Product.class));
                        mIndexes.add(doc.getId());
                    }
                    adapter.notifyDataSetChanged();
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

    private void likeOrDislike(int flag, DocumentReference document, HashMap<String, Object> map){
        map.put("like", flag);
        document
                .set(map, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG_UPDATE, "Product data has changed");
                    }
                    else {
                        Log.d(TAG_ERROR, "get failed with ", task.getException());
                    }
                });
    }

    private void searchProduct(String searchWord){
        CollectionReference crp = db.collection("products");
        if(!searchWord.equals("")){
            mProducts.clear();
            mIndexes.clear();
            crp
                    .whereEqualTo("name", searchWord)
                    .addSnapshotListener((value, error) -> {

                        if(error!=null){
                            Log.e("FIREBASE ERROR", error.getMessage());
                        }
                        assert value != null;
                        for (QueryDocumentSnapshot doc : value){
                            mProducts.add(doc.toObject(Product.class));
                            mIndexes.add(doc.getId());
                        }
                        adapter.notifyDataSetChanged();
                    });
        }
        else{
            eventChangeListener();
        }
    }
}