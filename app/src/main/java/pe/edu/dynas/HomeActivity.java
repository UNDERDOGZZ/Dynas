package pe.edu.dynas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import pe.edu.dynas.Model.Products;
import pe.edu.dynas.Prevalent.Prevalent;
import pe.edu.dynas.ViewHolder.ProductViewHolder;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener
{
    private DatabaseReference ProductsRef;
    private RecyclerView searchList;
    RecyclerView.LayoutManager layoutManager;
    private String type = "";
    private ImageView SearchBtn;
    private EditText inputText;
    private String SearchInput;
    private Spinner spinnerCat;
    String categoria;
    String texto;
    FirebaseRecyclerOptions<Products> options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);






        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            type = "Admin";
        }


        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");


        Paper.init(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Inicio");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.black));
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!type.equals("Admin")) {
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);
        if (!type.equals("Admin")){
            userNameTextView.setText(Prevalent.currentOnlineUser.getName());
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);
             }


        spinnerCat = findViewById(R.id.category_spinnerh);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(adapter);
        spinnerCat.setOnItemSelectedListener(this);

        inputText = findViewById(R.id.search_product_nameh);
        SearchBtn = findViewById(R.id.search_btnh);
        searchList = findViewById(R.id.recycler_menu);
        searchList.setLayoutManager(new LinearLayoutManager(HomeActivity.this));



        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchInput = inputText.getText().toString();

                buscar();
            }
        });




        //recyclerView = findViewById(R.id.recycler_menu);
        //recyclerView.setHasFixedSize(true);
        //layoutManager = new LinearLayoutManager(this);
        //recyclerView.setLayoutManager(layoutManager);
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef, Products.class)
                        .build();


        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model)
                    {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Precio = S/. " + model.getPrice());
                        holder.txtTipo.setText(model.getTipo());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {

                                if (type.equals("Admin"))
                                {
                                    Intent intent = new Intent(HomeActivity.this, AdminMaintainProductsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                else {

                                    Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        searchList.setAdapter(adapter);
        adapter.startListening();
    }

    public void buscar()
    {
        if (texto.equals("Aseo Personal")){
            categoria = "aseo_";
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(reference.orderByChild("search").startAt(categoria+SearchInput).endAt(categoria+SearchInput+"\uf8ff"), Products.class)
                            .build();
        }
        else if (texto.equals("Alimentos")){
            categoria = "comida_";
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(reference.orderByChild("search").startAt(categoria+SearchInput).endAt(categoria+SearchInput+"\uf8ff"), Products.class)
                            .build();
        }
        else if (texto.equals("Limpieza")){
            categoria = "limpieza_";
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(reference.orderByChild("search").startAt(categoria+SearchInput).endAt(categoria+SearchInput+"\uf8ff"), Products.class)
                            .build();
        }
        else if (texto.equals("Todos")){

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(reference.orderByChild("pname").startAt(SearchInput).endAt(SearchInput+"\uf8ff"), Products.class)
                            .build();
        }

        else if (texto.equals("")){

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(reference, Products.class)
                            .build();

        }








        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model)
                    {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Precio = S/. " + model.getPrice());
                        holder.txtTipo.setText(model.getTipo());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {

                                if (type.equals("Admin"))
                                {
                                    Intent intent = new Intent(HomeActivity.this, AdminMaintainProductsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                else {

                                    Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        searchList.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

//        if (id == R.id.action_settings)
//        {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart)
        {
            if (type.equals("Admin")){
                Toast.makeText(HomeActivity.this, "Debe ingresar como cliente.", Toast.LENGTH_SHORT).show();

            }
            else {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }

        }
        else if (id == R.id.nav_orders)
        {

            if (type.equals("Admin")){
                Toast.makeText(HomeActivity.this, "Debe ingresar como cliente.", Toast.LENGTH_SHORT).show();

            }
            else {
                Intent intent = new Intent(HomeActivity.this, PredictionActivity.class);
                startActivity(intent);
            }

        }
        else if (id == R.id.nav_misordenes)
        {

            if (type.equals("Admin")){
                Toast.makeText(HomeActivity.this, "Debe ingresar como cliente.", Toast.LENGTH_SHORT).show();

            }
            else {
                Intent intent = new Intent(HomeActivity.this, MyOrdersActivity.class);
                startActivity(intent);
            }

        }


        else if (id == R.id.nav_settings)
        {
            if (type.equals("Admin")){
                Toast.makeText(HomeActivity.this, "Debe ingresar como cliente.", Toast.LENGTH_SHORT).show();

            }
            else {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        }
        else if (id == R.id.nav_logout)
        {
            Paper.book().destroy();

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        texto = parent.getItemAtPosition(position).toString();

        if (texto.equals("Aseo Personal")){
            categoria = "aseo_";
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(reference.orderByChild("search").startAt(categoria).endAt(categoria+"\uf8ff"), Products.class)
                            .build();
        }
        else if (texto.equals("Alimentos")){
            categoria = "comida_";
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(reference.orderByChild("search").startAt(categoria).endAt(categoria+"\uf8ff"), Products.class)
                            .build();
        }
        else if (texto.equals("Limpieza")){
            categoria = "limpieza_";
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(reference.orderByChild("search").startAt(categoria).endAt(categoria+"\uf8ff"), Products.class)
                            .build();
        }
        else if (texto.equals("Todos")){

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(reference.orderByChild("pname").startAt(SearchInput).endAt(SearchInput+"\uf8ff"), Products.class)
                            .build();
        }

        else if (texto.equals("")){

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(reference, Products.class)
                            .build();

        }








        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model)
                    {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Precio = S/. " + model.getPrice());
                        holder.txtTipo.setText(model.getTipo());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {

                                if (type.equals("Admin"))
                                {
                                    Intent intent = new Intent(HomeActivity.this, AdminMaintainProductsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                else {

                                    Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        searchList.setAdapter(adapter);
        adapter.startListening();



    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
        options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(reference.orderByChild("pname").startAt(SearchInput).endAt(SearchInput+"\uf8ff"), Products.class)
                        .build();


    }
}