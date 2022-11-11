package com.example.myapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.content.Context.MODE_PRIVATE;

public class FragmentProfile extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FragmentProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        EditText name = v.findViewById(R.id.profile_name_et);
        EditText email = v.findViewById(R.id.profile_email_et);
        EditText password = v.findViewById(R.id.profile_password_et);
        EditText passwordR = v.findViewById(R.id.profile_password_repeat_et);

        MaterialButton quit = v.findViewById(R.id.quit_button);
        quit.setOnClickListener(v1 -> {
            SharedPreferences sp =  requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
            sp.edit().putBoolean("hasVisited", false).apply();
            sp.edit().putString("userID", null).apply();
            Navigation.findNavController(v1).navigate(R.id.action_fragmentProfile_to_fragmentAuth);
        });
        return v;
    }
}