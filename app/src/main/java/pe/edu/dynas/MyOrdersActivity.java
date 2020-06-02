package pe.edu.dynas;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pe.edu.dynas.Model.AdminOrders;
import pe.edu.dynas.Prevalent.Prevalent;

public class MyOrdersActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private RecyclerView myordersList;
    private DatabaseReference ordersRef;
    String uID = "";
    String oID = "";
    private Spinner spinnerOrd;
    private Button filter;
    String tipo;
    String texto;
    FirebaseRecyclerOptions<AdminOrders> options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");





        spinnerOrd = findViewById(R.id.category_spinnerm);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.orders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrd.setAdapter(adapter);
        spinnerOrd.setOnItemSelectedListener(this);


        filter = findViewById(R.id.search_btnm);
        myordersList = findViewById(R.id.myorders_list);
        myordersList.setLayoutManager(new LinearLayoutManager(MyOrdersActivity.this));



        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filtrar();
            }
        });
    }
    @Override
    protected void onStart()
    {
        super.onStart();


        options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef.orderByChild("uid").equalTo("ns_"+Prevalent.currentOnlineUser.getEmail()), AdminOrders.class)
                        .build();

        FirebaseRecyclerAdapter<AdminOrders, MyOrdersActivity.MyOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, MyOrdersActivity.MyOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MyOrdersActivity.MyOrdersViewHolder holder, final int position, @NonNull final AdminOrders model)
                    {

                        holder.userTotalPrice.setText("Total =  S/." + model.getTotalAmount());
                        holder.userDateTime.setText("Fecha: " + model.getDate() + "  " + model.getTime());
                        holder.userShippingAddress.setText("Dirección: " + model.getAddress() + ", " + model.getCity());

                        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view)
                            {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Sí",
                                                "No"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(MyOrdersActivity.this);
                                builder.setTitle("¿Está seguro que desea cancelar esta orden?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        if (i == 0)
                                        {
                                            uID = Prevalent.currentOnlineUser.getEmail();

                                            oID = model.getOid();





                                            RemoverOrder(oID);
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
                    public MyOrdersActivity.MyOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorders_layout, parent, false);
                        return new MyOrdersActivity.MyOrdersViewHolder(view);
                    }
                };

        myordersList.setAdapter(adapter);
        adapter.startListening();
    }

    public void filtrar()
    {
        if (texto.equals("Orden activa")){

            options =
                    new FirebaseRecyclerOptions.Builder<AdminOrders>()
                            .setQuery(ordersRef.orderByChild("uid").equalTo("ns_"+Prevalent.currentOnlineUser.getEmail()), AdminOrders.class)
                            .build();

            FirebaseRecyclerAdapter<AdminOrders, MyOrdersActivity.MyOrdersViewHolder> adapter =
                    new FirebaseRecyclerAdapter<AdminOrders, MyOrdersActivity.MyOrdersViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull MyOrdersActivity.MyOrdersViewHolder holder, final int position, @NonNull final AdminOrders model)
                        {

                            holder.userTotalPrice.setText("Total =  S/." + model.getTotalAmount());
                            holder.userDateTime.setText("Fecha: " + model.getDate() + "  " + model.getTime());
                            holder.userShippingAddress.setText("Dirección: " + model.getAddress() + ", " + model.getCity());

                            holder.cancelBtn.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view)
                                {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Sí",
                                                    "No"
                                            };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(MyOrdersActivity.this);
                                    builder.setTitle("¿Está seguro que desea cancelar esta orden?");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            if (i == 0)
                                            {
                                                uID = Prevalent.currentOnlineUser.getEmail();
                                                oID = model.getOid();

                                                RemoverOrder(oID);
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
                        public MyOrdersActivity.MyOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                        {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorders_layout, parent, false);
                            return new MyOrdersActivity.MyOrdersViewHolder(view);
                        }
                    };

            myordersList.setAdapter(adapter);
            adapter.startListening();
        }


        if (texto.equals("Órdenes anteriores")){

            options =
                    new FirebaseRecyclerOptions.Builder<AdminOrders>()
                            .setQuery(ordersRef.orderByChild("uid").equalTo("s_"+Prevalent.currentOnlineUser.getEmail()), AdminOrders.class)
                            .build();

            FirebaseRecyclerAdapter<AdminOrders, MyOrdersActivity.MyOrdersViewHolder> adapter =
                    new FirebaseRecyclerAdapter<AdminOrders, MyOrdersActivity.MyOrdersViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull MyOrdersActivity.MyOrdersViewHolder holder, final int position, @NonNull final AdminOrders model)
                        {

                            holder.userTotalPrice.setText("Total =  S/." + model.getTotalAmount());
                            holder.userDateTime.setText("Fecha: " + model.getDate() + "  " + model.getTime());
                            holder.userShippingAddress.setText("Dirección: " + model.getAddress() + ", " + model.getCity());
                            holder.cancelBtn.setVisibility(View.INVISIBLE);

                        }

                        @NonNull
                        @Override
                        public MyOrdersActivity.MyOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                        {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorders_layout, parent, false);
                            return new MyOrdersActivity.MyOrdersViewHolder(view);
                        }
                    };

            myordersList.setAdapter(adapter);
            adapter.startListening();
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        texto = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static class MyOrdersViewHolder extends RecyclerView.ViewHolder
    {
        public TextView userTotalPrice, userDateTime, userShippingAddress;
        public Button cancelBtn;


        public MyOrdersViewHolder(View itemView)
        {
            super(itemView);



            userTotalPrice = itemView.findViewById(R.id.myorder_total_price);
            userDateTime = itemView.findViewById(R.id.myorder_date_time);
            userShippingAddress = itemView.findViewById(R.id.myorder_address_city);
            cancelBtn = itemView.findViewById(R.id.cancel_btn);
        }
    }
    private void RemoverOrder(String oID)
    {

        ordersRef.child(oID).removeValue();
        finish();

    }
}
