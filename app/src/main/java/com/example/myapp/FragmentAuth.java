package com.example.myapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class FragmentAuth extends Fragment {

    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FragmentAuth() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        SharedPreferences prefs = this.requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        if(prefs.getBoolean("hasVisited",false)){
            Navigation.findNavController(requireActivity(), R.id.fragment_container1).navigate(R.id.action_fragmentAuth_to_fragmentHome);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_auth, container, false);

        SharedPreferences sp =  this.requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);

        CheckBox cb = v.findViewById(R.id.remember_me_cb);
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(buttonView.isChecked()){

                sp.edit().putBoolean("hasVisited", true).apply();
            } else if(!buttonView.isChecked()){
                sp.edit().putBoolean("hasVisited", false).apply();
            }
        });

        EditText mail = v.findViewById(R.id.auth_email_et);
        mail.setOnClickListener(view -> mail.setText(""));
        EditText password = v.findViewById(R.id.auth_password_et) ;
        password.setOnClickListener(view -> password.setText(""));

        // Обработка и проверка
        Button signIn = v.findViewById(R.id.auth_signIn);
            signIn.setOnClickListener(v1 -> Navigation
                    .findNavController(v1)
                    .navigate(R.id.action_fragmentAuth_to_fragmentReg));

            Button logIn = v.findViewById(R.id.auth_login);
            logIn.setOnClickListener(v12 -> {

                // Запрашиваем из Firebase пользователя с нужным паролем и почтой

                db.collection("users")
                        .whereEqualTo("email", mail.getText().toString())
                        .whereEqualTo("password", password.getText().toString())
                        .limit(1)
                        .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()){
                                    Log.d("FIREBASE", document.getId() + "=>" + document.getData());
                                    sp.edit().putString("userID", document.getId()).apply();
                                    Navigation.findNavController(v12).navigate(R.id.action_fragmentAuth_to_fragmentHome);
                                    Toast.makeText(context, "Вы вошли как "+ document.getString("name"), Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                Toast.makeText(getContext(), "Неверно введен логин или пароль", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> Log.w("FIREBASE", "Error getting documents."));

            });
        return v;
    }
}