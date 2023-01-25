package com.example.myapp.view;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FragmentReg extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;

    private final String regex = "^(([a-z0-9_\"+-]+\\.?)*([a-z0-9_\"+-]+)){1,64}@((\\[?([0-9]{1,3}.){3}[0-9]{1,3}]?)|(([a-z0-9][a-z0-9_+-]+)(\\.[a-z]{2,})+))$";

    public FragmentReg() {
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
        View v = inflater.inflate(R.layout.fragment_reg, container, false);

        SharedPreferences sp =  this.requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Initializing View objects
        EditText nameEt = v.findViewById(R.id.reg_name_etext);
        EditText emailEt = v.findViewById(R.id.reg_email_etext);
        EditText passwordEt = v.findViewById(R.id.reg_password_etext);
        EditText password2Et = v.findViewById(R.id.reg_password_repeat_etext);

        Button signIn = v.findViewById(R.id.reg_signIn);
        signIn.setOnClickListener(v1 -> {
            try{
                // Variables with the meanings from ET
                String name = nameEt.getText().toString();
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                String password2 = password2Et.getText().toString();

                // Проверяем существует ли такой пользователь
                /*db.collection("users")
                        .whereEqualTo("email", email)
                        .limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(context, "Пользователь с данной электронной почтой уже существует", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Log.w("FIREBASE", "Error getting documents.", task.getException());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Неверно введен логин или пароль", Toast.LENGTH_SHORT).show();
                            }
                        });*/

                // Инициализируем HashMap для вставки информации о пользователе в документ
                HashMap<String, Object> user = new HashMap<>();
                user.put("name", name);
                user.put("email", email);
                user.put("password", password);

                // Создаем шаблон регулярного выражения для валидации email
                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher match = pattern.matcher(email);

                // Вставляем данные в FireStore
                if (password.equals(password2) & match.matches()){
                    db.collection("users")
                            .add(user)
                            .addOnSuccessListener(documentReference -> {
                                sp.edit().putString("userID", documentReference.getId()).apply();
                                Navigation.findNavController(v1).navigate(R.id.action_fragmentReg_to_fragmentHome);
                                Toast.makeText(context, "Вы вошли как "+ name, Toast.LENGTH_LONG).show();
                                Log.d("FIREBASE", "DocumentSnapshot added with ID: " + documentReference.getId());
                            })
                            .addOnFailureListener(e -> {
                                Log.w("FIREBASE", "Error adding document", e);
                                Toast.makeText(context, "Ошибка подключения к БД", Toast.LENGTH_SHORT).show();
                            });
                }
                else{
                    Toast.makeText(context, "Введите корректные данные", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e) {
                    Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }
}