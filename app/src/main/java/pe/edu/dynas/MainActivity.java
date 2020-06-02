package pe.edu.dynas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;
import pe.edu.dynas.Model.Users;
import pe.edu.dynas.Prevalent.Prevalent;

public class MainActivity extends AppCompatActivity {

    private Button joinNowButton, logInButton;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNowButton = (Button) findViewById(R.id.main_join_us_btn);
        logInButton = (Button) findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);


        Paper.init(this);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        String UserEmailKey = Paper.book().read(Prevalent.UserEmailKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (UserEmailKey != "" && UserPasswordKey != "")
        {
            if (!TextUtils.isEmpty(UserEmailKey)  &&  !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserEmailKey, UserPasswordKey);

                loadingBar.setTitle("Comprobando");
                loadingBar.setMessage("Espere un momento....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }

    }
    private void AllowAccess(final String email, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("Users").child(email).exists())
                {
                    Users usersData = dataSnapshot.child("Users").child(email).getValue(Users.class);

                    if (usersData.getEmail().equals(email) && usersData.getEmail().equals("admin")==false)
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this, "Cargando...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = usersData;
                            startActivity(intent);
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Contraseña incorrecta.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        loadingBar.dismiss();
                        Toast.makeText(MainActivity.this, "Admin, ingrese nuevamente sus credenciales.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "No existe una cuenta asociada a ese email.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
