package pe.edu.dynas.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import pe.edu.dynas.Interface.ItemClickListner;
import pe.edu.dynas.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView txtProductName, txtProductDescription, txtProductPrice, txtTipo;
    public ImageView imageView;
    public ItemClickListner listner;

    public ProductViewHolder(View itemView){
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_price);
        txtTipo = (TextView) itemView.findViewById(R.id.product_type);
    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }
    @Override
    public void onClick(View view){

    }
}
