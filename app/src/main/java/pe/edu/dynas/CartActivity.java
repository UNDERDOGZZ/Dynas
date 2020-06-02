package pe.edu.dynas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import java.text.DecimalFormat;

import pe.edu.dynas.Model.Cart;
import pe.edu.dynas.Prevalent.Prevalent;
import pe.edu.dynas.ViewHolder.CartViewHolder;

public class CartActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private Button NextProcessBtn;
    private TextView txtTotalAmount, txtMsg1;

    private float overTotalPrice = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProcessBtn = (Button) findViewById(R.id.next_btn);
        txtTotalAmount = (TextView) findViewById(R.id.total_price);
        txtMsg1 = (TextView) findViewById(R.id.msg1);




        NextProcessBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {

            if (overTotalPrice != 0.0) {

                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(CartActivity.this, "No hay ningun producto en el carrito.", Toast.LENGTH_SHORT).show();

            }
            }
        });
    }

    protected void onStop() {
        super.onStop();
        overTotalPrice = 0;
    }


    @Override
    protected void onStart()
    {
        super.onStart();



        CheckOrderState();


        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("User View")
                                .child(Prevalent.currentOnlineUser.getEmail())
                                .child("Products"), Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
            {
                holder.txtProductQuantity.setText("Cantidad: " + model.getQuantity());
                holder.txtProductPrice.setText("Precio: S/. " + model.getPrice());
                holder.txtProductName.setText(model.getPname());

                DecimalFormat df = new DecimalFormat("#.##");

                float oneTyprProductTPrice = ((Float.parseFloat(model.getPrice()))) * Float.parseFloat(model.getQuantity());
                overTotalPrice = overTotalPrice + Float.valueOf(df.format(oneTyprProductTPrice));
                overTotalPrice= Float.valueOf(df.format(overTotalPrice));
                txtTotalAmount.setText("Precio total = S/. " + overTotalPrice);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Editar",
                                        "Eliminar"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Opciones del carrito");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if (i == 0)
                                {
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                if (i == 1)
                                {
                                    cartListRef.child("User View")
                                            .child(Prevalent.currentOnlineUser.getEmail())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if (task.isSuccessful())
                                                    {
                                                        Toast.makeText(CartActivity.this, "Producto removido con éxito.", Toast.LENGTH_SHORT).show();

                                                        //Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                        //startActivity(intent);
                                                    }
                                                }
                                            });
                                    cartListRef.child("Admin View")
                                            .child(Prevalent.currentOnlineUser.getEmail())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if (task.isSuccessful())
                                                    {
                                                        //Toast.makeText(CartActivity.this, "Producto removido con éxito.", Toast.LENGTH_SHORT).show();

                                                        Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }



    private void CheckOrderState()
    {
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getEmail());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if (shippingState.equals("shipped"))
                    {
                        txtTotalAmount.setText("Estimado " + userName + "\n su orden ha sido enviada exitosamente.");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Felicidades, su orden ha sido enviada. Muy pronto la recibirá en la puerta de su hogar.");
                        NextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "Podrá comprar más productos una vez haya recibido su orden.", Toast.LENGTH_SHORT).show();
                    }
                    else if(shippingState.equals("not shipped"))
                    {
                        txtTotalAmount.setText("Estado = No enviada");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "Podrá comprar más productos una vez haya recibido su orden.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}