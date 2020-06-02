package pe.edu.dynas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import pe.edu.dynas.Model.Products;
import pe.edu.dynas.ViewHolder.ProductViewHolder;

public class SearchProductsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private ImageView SearchBtn;
    private EditText inputText;
    private RecyclerView searchList;
    private String SearchInput;
    private Spinner spinnerCat;
    String categoria;
    String text;
    FirebaseRecyclerOptions<Products> options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);
        spinnerCat = findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(adapter);
        spinnerCat.setOnItemSelectedListener(this);

        inputText = findViewById(R.id.search_product_name);
        SearchBtn = findViewById(R.id.search_btn);
        searchList = findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(SearchProductsActivity.this));



        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchInput = inputText.getText().toString();

                buscar();
            }
        });
    }

    public void buscar()
    {
        if (text.equals("Aseo Personal")){
            categoria = "aseo_";
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(reference.orderByChild("search").startAt(categoria+SearchInput).endAt(categoria+SearchInput+"\uf8ff"), Products.class)
                            .build();
        }
        else if (text.equals("Alimentos")){
            categoria = "comida_";
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(reference.orderByChild("search").startAt(categoria+SearchInput).endAt(categoria+SearchInput+"\uf8ff"), Products.class)
                            .build();
        }
        else if (text.equals("Limpieza")){
            categoria = "limpieza_";
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(reference.orderByChild("search").startAt(categoria+SearchInput).endAt(categoria+SearchInput+"\uf8ff"), Products.class)
                            .build();
        }
        else if (text.equals("Todos")){

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(reference.orderByChild("pname").startAt(SearchInput).endAt(SearchInput+"\uf8ff"), Products.class)
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
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                Intent intent = new Intent(SearchProductsActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        text = parent.getItemAtPosition(position).toString();






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