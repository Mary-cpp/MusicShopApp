package com.example.myapp.view;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Objects;

public class FragmentProfile extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText name, email, password, passwordR;

    private static final String TAG_UPDATE = "FIRESTORE UPDATES";
    private static final String TAG_READ = "FIRESTORE READS";
    private static final String TAG_WRITE = "FIRESTORE WRITES";
    private static final String TAG_ERROR = "FIRESTORE ERROR";

    private String names, emails, passwords;

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

        Context context = getContext();

        name = v.findViewById(R.id.profile_name_et);
        email = v.findViewById(R.id.profile_email_et);
        password = v.findViewById(R.id.profile_password_et);
        passwordR = v.findViewById(R.id.profile_password_repeat_et);

        SharedPreferences sp =  requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        DocumentReference dr = db.collection("users").document(sp.getString("userID", null));

        setProfileData(dr);

        MaterialButton confirm = v.findViewById(R.id.confirm_button_profile);
        confirm.setOnClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Objects.requireNonNull(context), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog);
            builder
                    .setIcon(R.drawable.warning)
                    .setMessage("Do you want to change profile data?")
                    .setPositiveButton("Accept", (dialogInterface, i) -> makeChanges(context, dr))
                    .setNegativeButton("Decline", (dialogInterface, i) -> setProfileData(dr))
                            .show();
        });

        MaterialButton quit = v.findViewById(R.id.quit_button);
        quit.setOnClickListener(v1 -> {
            sp.edit().putBoolean("hasVisited", false).apply();
            sp.edit().putString("userID", null).apply();
            Navigation.findNavController(v1).navigate(R.id.action_fragmentProfile_to_fragmentAuth);
        });
        return v;
    }

    private void makeChanges(Context context, DocumentReference dr){
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name.getText().toString());
        map.put("email", email.getText().toString());
        if(password.getText().toString().equals(passwordR.getText().toString())){
            map.put("password", passwordR.getText().toString());
            dr
                    .set(map, SetOptions.merge())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG_UPDATE, "Profile data has changed");
                        }
                        else {
                            Log.d(TAG_ERROR, "get failed with ", task.getException());
                        }
                    });
        }
        else{
            Toast.makeText(context, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
        }
    }

    private void setProfileData(DocumentReference dr){
        dr.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG_READ, "Ok");
                    Log.d(TAG_READ, document.getData().toString());
                    names = document.getString("name");
                    name.setText(names, TextView.BufferType.EDITABLE);
                    emails = document.getString("email");
                    email.setText(emails, TextView.BufferType.EDITABLE);
                    passwords = document.getString("password");
                    password.setText(passwords, TextView.BufferType.NORMAL);
                } else {
                    Log.w(TAG_ERROR, "No such document");
                }
            } else {
                Log.w(TAG_ERROR, "get failed with ", task.getException());
            }
        });
    }
}