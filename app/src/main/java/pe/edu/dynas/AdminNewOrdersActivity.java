package pe.edu.dynas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import pe.edu.dynas.Model.AdminOrders;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;
    private DatabaseReference cartListRef;
    String saveCurrentDate, saveCurrentTime,userRandomKey, keyNPNU;
    ArrayList<String> listaIdsP = new ArrayList<String>();
    ArrayList<String> listaFechas = new ArrayList<String>();
    ArrayList<String> listaCantidades = new ArrayList<String>();
    ArrayList<String> listaNombres = new ArrayList<String>();
    ArrayList<String> listaPrecios = new ArrayList<String>();
    Integer cantProductos;
    String uID = "";
    String oID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);


        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");


        ordersList = findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));




    }

    @Override
    protected void onStart()
    {
        super.onStart();


        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef.orderByChild("uid").startAt("ns_").endAt("ns_"+"\uf8ff"), AdminOrders.class)
                        .build();

        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model)
                    {
                        holder.userName.setText("Nombre: " + model.getName());
                        holder.userPhoneNumber.setText("Telefono: " + model.getPhone());
                        holder.userTotalPrice.setText("Total =  S/." + model.getTotalAmount());
                        holder.userDateTime.setText("Fecha: " + model.getDate() + "  " + model.getTime());
                        holder.userShippingAddress.setText("Dirección: " + model.getAddress() + ", " + model.getCity());

                        holder.ShowOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                final String userID = model.getUser();

                                Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("uid", userID);
                                startActivity(intent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Sí",
                                                "No"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("¿Se enviaron los productos de esta orden?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        if (i == 0)
                                        {
                                            uID = model.getUser();

                                            Log.i("usuario", uID);

                                            LlenarListas(uID);


                                            //Creamos tabla para predecir

                                            oID = model.getOid();






                                            RemoverOrder(oID, uID);
                                            finish();

                                        }
                                        else
                                        {
                                            finish();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                        return new AdminOrdersViewHolder(view);
                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();
    }




    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder
    {
        public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress;
        public Button ShowOrdersBtn;


        public AdminOrdersViewHolder(View itemView)
        {
            super(itemView);


            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);
            ShowOrdersBtn = itemView.findViewById(R.id.show_all_products_btn);
        }
    }




    private void RemoverOrder(String oID, String uID)
    {

        ordersRef.child(oID).child("uid").setValue("s_"+uID);
        finish();

    }

    private void LlenarListas(final String uID)
    {
        cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List").child("Admin View").child(uID).child("Products");
        Log.i("aaaaa", cartListRef.toString());
        cartListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot datasnapshot2 : dataSnapshot.getChildren()) {
                        String key = datasnapshot2.child("pid").getValue().toString();
                        listaIdsP.add(key);

                    }

                    for (DataSnapshot datasnapshot2 : dataSnapshot.getChildren()) {
                        String key = datasnapshot2.child("date").getValue().toString();
                        listaFechas.add(key);

                    }

                    for (DataSnapshot datasnapshot2 : dataSnapshot.getChildren()) {
                        String key = datasnapshot2.child("quantity").getValue().toString();
                        listaCantidades.add(key);

                    }

                    for (DataSnapshot datasnapshot2 : dataSnapshot.getChildren()) {
                        String key = datasnapshot2.child("pname").getValue().toString();
                        listaNombres.add(key);

                    }
                    for (DataSnapshot datasnapshot2 : dataSnapshot.getChildren()) {
                        String key = datasnapshot2.child("price").getValue().toString();
                        listaPrecios.add(key);

                    }


                    //Log.i("test", listaIdsP.get(0));
                    //Log.i("test", listaFechas.get(0));
                    //Log.i("test", listaCantidades.get(0));
                    cantProductos = listaIdsP.size();
                    Log.i("bbb", String.valueOf(cantProductos));

                    final DatabaseReference RootRef;
                    RootRef = FirebaseDatabase.getInstance().getReference();

                    for (int ind = 0; ind < cantProductos; ind++) {
                        Calendar calendar = Calendar.getInstance();

                        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
                        saveCurrentDate = currentDate.format(calendar.getTime());

                        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                        saveCurrentTime = currentTime.format(calendar.getTime());

                        userRandomKey = saveCurrentDate + saveCurrentTime;

                        keyNPNU = listaIdsP.get(ind).toString() + uID.toString();


                        HashMap<String, Object> toPredictData = new HashMap<>();
                        toPredictData.put("quantity", listaCantidades.get(ind));
                        toPredictData.put("date", saveCurrentDate);
                        toPredictData.put("pid", listaIdsP.get(ind));
                        toPredictData.put("pname", listaNombres.get(ind));
                        toPredictData.put("price", listaPrecios.get(ind));

                        RootRef.child("ToPredict").child(uID).child(keyNPNU).child(userRandomKey).updateChildren(toPredictData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AdminNewOrdersActivity.this, "Se agrego a predicciones.", Toast.LENGTH_SHORT).show();
                                            FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(uID).removeValue();
                                            Intent intent = new Intent(AdminNewOrdersActivity.this, AdminCategoryActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();


                                        } else {

                                            Toast.makeText(AdminNewOrdersActivity.this, "Error de red. Por favor, vuelva a intentarlo.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}