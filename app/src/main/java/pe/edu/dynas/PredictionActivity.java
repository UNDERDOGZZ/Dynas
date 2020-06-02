package pe.edu.dynas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import pe.edu.dynas.Prevalent.Prevalent;

public class PredictionActivity extends AppCompatActivity {

    private DatabaseReference ordersRef;
    private DatabaseReference toPredictRef, toPredictRef1;
    private String userID = "";
    ArrayList<String> listaIdsP = new ArrayList<String>();
    ArrayList<String> listaIDS = new ArrayList<String>();
    ArrayList<String> listaNombres = new ArrayList<String>();
    ArrayList<String> listaPrecios = new ArrayList<String>();
    ArrayList<Integer> distxcompraCant = new ArrayList<Integer>();
    ArrayList<Integer> distxcompra = new ArrayList<Integer>();
    ArrayList<String> listaCantidades = new ArrayList<String>();
    ArrayList<String> listaFechas = new ArrayList<String>();
    ArrayList<Double> distxcompraSuav = new ArrayList<Double>();
    Integer promedioDias = 0;
    Integer promedioCantidades = 0;
    Integer cantidadFinal;
    Date fechaFinal;
    public String resultado = "";
    ArrayList<Date> fechasPrediccion = new ArrayList<Date>();



    private Button ShopProcessBtn;
    private TextView  txtMsg1, txtMsg2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);



        ShopProcessBtn = (Button) findViewById(R.id.comprar_btn);
        txtMsg1 = (TextView) findViewById(R.id.mensaje);
        txtMsg2 = (TextView) findViewById(R.id.productos);

        userID = getIntent().getStringExtra("uid");

        toPredictRef = FirebaseDatabase.getInstance().getReference()
                .child("ToPredict").child(Prevalent.currentOnlineUser.getEmail());

        ShopProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PredictionActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        toPredictRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot datasnapshot: dataSnapshot.getChildren()){
                    String key = datasnapshot.getKey().toString();
                    listaIdsP.add(key);
                }

                /*
                for (int i =0; i < listaIdsP.size(); i++){
                    for(DataSnapshot datasnapshot: dataSnapshot.getChildren()){
                        String key = datasnapshot.child(listaIdsP.get(i)).getKey().toString();
                        listaOrden.add(key);

                        for(int f = 0; f <listaOrden.size(); f++) {
                            String fecha = datasnapshot.child(listaOrden.get(f)).child("date").getValue().toString();
                            listaFechas.add(fecha);
                            if (f < listaOrden.size() - 1) {
                                DateFormat fechaFormat = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    Date aux1 = fechaFormat.parse(listaFechas.get(f));
                                    Date aux2 = fechaFormat.parse(listaFechas.get(listaFechas.size() - 1));
                                    Integer restaFechas = (int) (aux2.getTime() - aux1.getTime());
                                    if (restaFechas < 30) {
                                        distxcompra.add(restaFechas.toString());
                                        promedioDias = promedioDias + restaFechas;
                                        cantidades.add(datasnapshot.child(listaIdsP.get(i)).child(listaOrden.get(f)).child("quantity").getValue().toString());
                                        promedioCantidades = promedioCantidades +  Integer.parseInt(datasnapshot.child(listaIdsP.get(i)).child(listaOrden.get(f)).child("quantity").getValue().toString());
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.setTime(aux2);
                                        calendar.add(Calendar.DAY_OF_YEAR, promedioDias/distxcompra.size());
                                        cantidadFinal =  (promedioCantidades/cantidades.size());
                                        fechaFinal = calendar.getTime();

                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                break;
                            }


                        }

                        Log.i("info", cantidadFinal.toString());
                        Log.i("info", fechaFinal.toString());

                    }

                }
                */

                //Log.i("test1", listaIdsP.get(2));

                for (int i = 0; i<listaIdsP.size();i++) {

                    for (DataSnapshot datasnapshot : dataSnapshot.child(listaIdsP.get(i)).getChildren()) {
                        String key = datasnapshot.getKey().toString();
                        //i("test3", key);
                        String fecha = dataSnapshot.child(listaIdsP.get(i)).child(key).child("date").getValue().toString();
                        listaFechas.add(fecha);
                        String cantidad = dataSnapshot.child(listaIdsP.get(i)).child(key).child("quantity").getValue().toString();
                        listaCantidades.add(cantidad);
                        String nombre = dataSnapshot.child(listaIdsP.get(i)).child(key).child("pname").getValue().toString();
                        listaNombres.add(nombre);
                        String precio = dataSnapshot.child(listaIdsP.get(i)).child(key).child("price").getValue().toString();
                        listaPrecios.add(precio);
                        String ids = dataSnapshot.child(listaIdsP.get(i)).child(key).child("pid").getValue().toString();
                        listaIDS.add(ids);

                    }
                    //Log.i("test4", listaFechas.get(0));


                    for (int j = 0; j < listaFechas.size(); j++) {
                        String fechaMayor = listaFechas.get(listaFechas.size() - 1);
                        String fechaARestar = listaFechas.get(j);
                        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                        Date fechaMayorConv = null;
                        Date fechaARestarConv = null;
                        try {
                            fechaMayorConv = formato.parse(fechaMayor);

                        } catch (ParseException e) {
                            e.printStackTrace();
                            //Toast.makeText(PredictionActivity.this, "entro al catch.", Toast.LENGTH_SHORT).show();
                        }
                        try {
                            fechaARestarConv = formato.parse(fechaARestar);

                        } catch (ParseException e) {
                            e.printStackTrace();
                            //Toast.makeText(PredictionActivity.this, "entro al catch.", Toast.LENGTH_SHORT).show();
                        }

                        //int diasDif = (int) ((fechaMayorConv.getTime() - fechaARestarConv.getTime()) / 86400000);
                        long fechaMayorConvMS = fechaMayorConv.getTime();
                        long fechaARestarConvMS = fechaARestarConv.getTime();
                        long diferencia = fechaMayorConvMS - fechaARestarConvMS;
                        double dias = Math.floor(diferencia / (1000 * 60 * 60 * 24));
                        int diasDif = (int) dias;
                        if (diasDif > 0 && diasDif <= 31) {
                            distxcompra.add(diasDif);
                            int cantAGuardar = Integer.parseInt(listaCantidades.get(j));
                            distxcompraCant.add(cantAGuardar);
                            //Log.i("test5", distxcompra.get(0).toString());
                            //Toast.makeText(PredictionActivity.this, "Se calculo dist.", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(PredictionActivity.this, "No se puede es 0.", Toast.LENGTH_SHORT).show();
                            //Log.i("test5", "es 0");

                        }

                    }
                    if (distxcompra.size() > 1) {
                        //Empieza predccion
                        DescriptiveStatistics stats = new DescriptiveStatistics();
                        // Add the data from the array
                        for (int k = 0; k < distxcompra.size(); k++) {
                            stats.addValue(distxcompra.get(k));
                        }
                        double median = stats.getPercentile(50);
                        double percentil25 = stats.getPercentile(25);
                        double percentil75 = stats.getPercentile(75);
                        //Log.i("media", String.valueOf(median));
                        for (int l = 0; l < distxcompra.size(); l++) {
                            if (distxcompra.get(l) < percentil25 || distxcompra.get(l) > percentil75) {
                                distxcompraSuav.add(median);
                            } else {
                                distxcompraSuav.add(distxcompra.get(l) * 1.0);
                            }
                        }
                        double promFechas = 0.0;
                        double promCantidades = 0.0;
                        for (int m = 0; m < distxcompra.size(); m++) {
                            promFechas = promFechas + distxcompraSuav.get(m);
                            promCantidades = promCantidades + distxcompraCant.get(m);

                        }
                        promFechas = promFechas / distxcompra.size();
                        promCantidades = promCantidades / distxcompra.size();
                        int parte1 = (int) (promFechas / promCantidades * (distxcompraCant.get(distxcompraCant.size()-1)));
                        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                        String fechaMayor = listaFechas.get(listaFechas.size() - 1);
                        Date fechaMayorConv = null;
                        try {
                            fechaMayorConv = formato.parse(fechaMayor);

                        } catch (ParseException e) {
                            e.printStackTrace();
                            //Toast.makeText(PredictionActivity.this, "entro al catch.", Toast.LENGTH_SHORT).show();
                        }

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(fechaMayorConv);
                        calendar.add(Calendar.DAY_OF_YEAR, parte1 - 1);
                        Date fechaFinal = calendar.getTime();

                        Log.i("fechafinal", String.valueOf(fechaFinal));
                        fechasPrediccion.add(fechaFinal);

                        Calendar calendar1 = Calendar.getInstance();
                        Date fechaActual = calendar1.getTime();

                        long fechaFinalMS = fechaFinal.getTime();
                        long fechaActualMS = fechaActual.getTime();

                        long diferenciaF = fechaFinalMS - fechaActualMS;
                        double dias = Math.floor(diferenciaF / (1000 * 60 * 60 * 24));
                        int diasDif = (int) dias;
                        if (diasDif <=1 && diasDif>=0){
                            resultado += "Producto: "+listaNombres.get(0) +"\nCantidad: " + Math.round(promCantidades) + "\n\n";
                            //i("resultado", resultado);


                            //Agregar al carrito

                            String saveCurrentTime, saveCurrentDate;

                            Calendar calForDate = Calendar.getInstance();
                            SimpleDateFormat currentDate = new SimpleDateFormat("MMM-dd-yyyy");
                            saveCurrentDate = currentDate.format(calForDate.getTime());

                            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                            saveCurrentTime = currentTime.format(calForDate.getTime());

                            final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

                            final HashMap<String, Object> cartMap = new HashMap<>();
                            cartMap.put("pid", listaIDS.get(0));
                            cartMap.put("pname", listaNombres.get(0));
                            cartMap.put("price", listaPrecios.get(0));
                            cartMap.put("date", saveCurrentDate);
                            cartMap.put("time", saveCurrentTime);
                            cartMap.put("quantity", listaCantidades.get(0));
                            cartMap.put("discount", "");

                            final int ifinal = i;
                            cartListRef.child("User View").child(Prevalent.currentOnlineUser.getEmail())
                                    .child("Products").child(listaIdsP.get(ifinal))
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getEmail())
                                                        .child("Products").child(listaIdsP.get(ifinal))
                                                        .updateChildren(cartMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    Toast.makeText(PredictionActivity.this, "Añadido al carrito.", Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });

                        }




                        listaFechas.clear();
                        listaCantidades.clear();
                        listaNombres.clear();
                        distxcompra.clear();
                        distxcompraCant.clear();
                        distxcompraSuav.clear();

                    }
                    else{
                        //Toast.makeText(PredictionActivity.this, "No cumple.", Toast.LENGTH_SHORT).show();
                        listaFechas.clear();
                        listaCantidades.clear();
                        distxcompra.clear();
                        distxcompraCant.clear();
                        distxcompraSuav.clear();
                    }
                }
                if (!resultado.equals("")) {
                    txtMsg2.setText(resultado);
                    txtMsg2.setVisibility(View.VISIBLE);
                    ShopProcessBtn.setVisibility(View.VISIBLE);
                    resultado="";

                }



            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Log.i("resultado1", resultado);
















    }



}
