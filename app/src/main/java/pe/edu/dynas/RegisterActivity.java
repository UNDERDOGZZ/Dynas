package pe.edu.dynas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText InputName, InputEmail, InputPassword, InputPin;
    private ProgressDialog loadingBar;
    private String saveCurrentDate, saveCurrentTime,userRandomKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        CreateAccountButton = (Button) findViewById(R.id.register_btn);
        InputName = (EditText) findViewById(R.id.register_username_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);
        InputEmail = (EditText) findViewById(R.id.register_email_input);
        InputPin = (EditText) findViewById(R.id.register_pin_input);
        loadingBar = new ProgressDialog(this);


        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)

            {
                CreateAccount();
            }
        });
    }

    private void CreateAccount()
    {
        String name = InputName.getText().toString();
        String email = InputEmail.getText().toString();
        String password = InputPassword.getText().toString();
        String pin = InputPin.getText().toString();

        if (TextUtils.isEmpty(name))

        {
            Toast.makeText(this, "Por favor, escriba su nombre.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Por favor, escriba su usuario.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Por favor, escriba su contraseña.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pin))
        {
            Toast.makeText(this, "Por favor, escriba su pin para cambiar contraseña.", Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle("Creando cuenta");
            loadingBar.setMessage("Por favor, espere");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateEmail(name, email, password, pin);
        }
    }

    private void ValidateEmail(final String name, final String email, final String password, final String pin)
    {

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calendar.getTime());

        userRandomKey = saveCurrentDate + saveCurrentTime;
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("Users").child(email).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("email", email);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);
                    userdataMap.put("pin", pin);

                    RootRef.child("Users").child(email).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this, "Felicitaciones, su cuenta ha sido creada.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Error de red. Por favor, vuelva a intentarlo.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Ya existe una cuenta con ese nombre de usuario.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Por favor, vuelva a intentarlo con otro usuario.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
