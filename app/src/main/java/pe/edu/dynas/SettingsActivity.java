package pe.edu.dynas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import pe.edu.dynas.Prevalent.Prevalent;

public class SettingsActivity extends AppCompatActivity
{
    private CircleImageView profileImageView;
    private EditText addressEditText, phoneEditText, distritoEditText;
    private TextView profileChangeTextBtn;
    private Button closeTextBtn, saveTextButton;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        addressEditText = (EditText) findViewById(R.id.settings_address);
        profileChangeTextBtn = (TextView) findViewById(R.id.profile_change);
        closeTextBtn = (Button) findViewById(R.id.close_settings_btn);
        saveTextButton = (Button) findViewById(R.id.update_account_settings_btn);
        phoneEditText = (EditText) findViewById(R.id.settings_phone);
        distritoEditText = (EditText) findViewById(R.id.settings_distrito);


        userInfoDisplay(profileImageView, addressEditText);


        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });


        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });


        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });
    }



    private void updateOnlyUserInfo()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");


        if (!addressEditText.getText().toString().equals("") && !distritoEditText.getText().toString().equals("") && !phoneEditText.getText().toString().equals(""))  {

            HashMap<String, Object> userMap = new HashMap<>();

            userMap.put("address", addressEditText.getText().toString());
            userMap.put("district", distritoEditText.getText().toString());
            userMap.put("phone", phoneEditText.getText().toString());

            ref.child(Prevalent.currentOnlineUser.getEmail()).updateChildren(userMap);

            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
            Toast.makeText(SettingsActivity.this, "Actualización exitosa.", Toast.LENGTH_SHORT).show();
            finish();

        }
        else {
            Toast.makeText(this, "Debe ingresar todos los campos.", Toast.LENGTH_SHORT).show();

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Ocurrió un error, vuelva a intentarlo.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }




    private void userInfoSaved()
    {

        if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Debe ingresar su dirección.", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(distritoEditText.getText().toString()))
        {
            Toast.makeText(this, "Debe ingresar su distrito.", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(phoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Debe ingresar su teléfono.", Toast.LENGTH_SHORT).show();
        }


        else if(checker.equals("clicked"))
        {
            uploadImage();
        }
    }



    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Actualizando perfil");
        progressDialog.setMessage("Por favor espere, estamos actualizando su información");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(Prevalent.currentOnlineUser.getEmail() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            if (task.isSuccessful())
                            {
                                Uri downloadUrl = task.getResult();
                                myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap. put("address", addressEditText.getText().toString());
                                userMap. put("district", distritoEditText.getText().toString());
                                userMap. put("phone", phoneEditText.getText().toString());

                                userMap. put("image", myUrl);
                                ref.child(Prevalent.currentOnlineUser.getEmail()).updateChildren(userMap);

                                progressDialog.dismiss();

                                startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                                Toast.makeText(SettingsActivity.this, "Información actualizada correctamente.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(SettingsActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "Imagen no seleccionada", Toast.LENGTH_SHORT).show();
        }
    }


    private void userInfoDisplay(final CircleImageView profileImageView,  final EditText addressEditText)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getEmail());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();

                        String address = dataSnapshot.child("address").getValue().toString();

                        String distrito = dataSnapshot.child("district").getValue().toString();

                        String phone = dataSnapshot.child("phone").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);

                        addressEditText.setText(address);
                        distritoEditText.setText(distrito);
                        phoneEditText.setText(phone);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
