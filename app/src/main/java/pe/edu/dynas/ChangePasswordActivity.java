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

public class ChangePasswordActivity extends AppCompatActivity {

    private Button ChangePassButton;
    private EditText InputEmail, InputNewPassword, InputPin;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ChangePassButton = (Button) findViewById(R.id.changepass_btn);
        InputNewPassword = (EditText) findViewById(R.id.changepass_newpassword_input);
        InputEmail = (EditText) findViewById(R.id.changepass_email_input);
        InputPin = (EditText) findViewById(R.id.changepass_pin_input);
        loadingBar = new ProgressDialog(this);

        ChangePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)

            {
                ChangePassword();
            }
        });
    }

    private void ChangePassword() {

        String email = InputEmail.getText().toString();
        String newPassword = InputNewPassword.getText().toString();
        String pin = InputPin.getText().toString();

         if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Por favor, escriba su email.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(newPassword))
        {
            Toast.makeText(this, "Por favor, escriba su nueva contraseña.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pin))
        {
            Toast.makeText(this, "Por favor, escriba su pin para cambiar contraseña.", Toast.LENGTH_SHORT).show();
        }
        else
         {
             loadingBar.setTitle("Cambiando contraseña");
             loadingBar.setMessage("Por favor, espere");
             loadingBar.setCanceledOnTouchOutside(false);
             loadingBar.show();

             ValidateEmail(email, newPassword, pin);
         }
    }
    private void ValidateEmail(final String email, final String newPassword, final String pin)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {



                if ((dataSnapshot.child("Users").child(email).exists()))
                {
                    if (dataSnapshot.child("Users").child(email).child("pin").getValue().toString().equals(pin)) {

                        RootRef.child("Users").child(email).child("password").setValue(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChangePasswordActivity.this, "Su contaseña fue restablecida con éxito.", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();

                                            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        } else {
                                            loadingBar.dismiss();
                                            Toast.makeText(ChangePasswordActivity.this, "Error de red. Por favor, vuelva a intentarlo.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else {
                        Toast.makeText(ChangePasswordActivity.this, "Comprobar si los datos ingresados son correctos.", Toast.LENGTH_SHORT).show();

                    }
                }
                else
                {
                    Toast.makeText(ChangePasswordActivity.this, "Comprobar si los datos ingresados son correctos.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(ChangePasswordActivity.this, "Comprobar si los datos ingresados son correctos.", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
