package com.example.myapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class FragmentHome extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;
    PostAdapter adapter;

    ArrayList<Post> mPosts;

    public FragmentHome() {
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
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        Chip chipShop = v.findViewById(R.id.chip_shop);
        chipShop.setOnClickListener(v12 -> Navigation
                .findNavController(v12)
                .navigate(R.id.action_fragmentHome_to_fragmentShop));

        Chip chipProfile = v.findViewById(R.id.chip_profile);
        chipProfile.setOnClickListener(v1 -> Navigation
                .findNavController(v1)
                .navigate(R.id.action_fragmentHome_to_fragmentProfile));

        Chip chipNewPost = v.findViewById(R.id.chip_new_post);
        chipNewPost.setOnClickListener(v13 -> Navigation
                .findNavController(v13)
                .navigate(R.id.action_fragmentHome_to_fragmentNewPost));

        Chip chipCart = v.findViewById(R.id.chip_cart);
        chipCart.setOnClickListener(v14 -> Navigation
                .findNavController(v14)
                .navigate(R.id.action_fragmentHome_to_fragmentCart));

        RecyclerView rv = v.findViewById(R.id.posts_list);

        PostAdapter.OnPostClickListener postClickListener = position -> {
            Navigation.findNavController(v).navigate(R.id.action_fragmentHome_to_fragmentPost, postToBundle(mPosts.get(position)));
        };

        mPosts = new ArrayList<>();

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostAdapter(context, mPosts, postClickListener);
        rv.setAdapter(adapter);

        eventChangeListener();

        return v;
    }

    private void eventChangeListener() {
        db.collection("posts")
                .orderBy("date", Query.Direction.DESCENDING)    // ???????????????????? ????????????????????
                .addSnapshotListener((value, error) -> {
            if(error!=null){
                Log.e("FIREBASE ERROR", error.getMessage());
            }
            assert value != null;
            for(DocumentChange dc : value.getDocumentChanges()){
                Log.d("FIREBASE ", dc.getDocument().getData().toString());
                mPosts.add(dc.getDocument().toObject(Post.class));
            }
            adapter.notifyDataSetChanged();
        });
    }

    private Bundle postToBundle(Post post){
        Bundle bundle = new Bundle();
        bundle.putParcelable("post", post);
        return bundle;
    }
}