package com.example.farmezy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TrackOrderActivity extends AppCompatActivity {

    private ImageView orderplaced,orderpacked,started,delivered;
    private ListView listview;
    private FirebaseFirestore db;
    private String orderplacedc,orderpackedc,startedc,deliveredc="0";
    private ArrayList<String> orderedProducts = new ArrayList<>();
    private ArrayAdapter<String> orderedProductsAdapter;
    private TextView billidisplay,trackorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        orderplaced = findViewById(R.id.orderplacedlogo);
        orderpacked = findViewById(R.id.orderpackedlogo);
        started = findViewById(R.id.startedlogo);
        delivered = findViewById(R.id.deliveredlogo);
        listview=findViewById(R.id.orderedproducts);
        billidisplay=findViewById(R.id.billiddisplay);
        trackorder=findViewById(R.id.trackorder);

        db = FirebaseFirestore.getInstance();

        SharedPreferences receive = getSharedPreferences("finalorder", MODE_PRIVATE);
        String cname = receive.getString("username", "");
        boolean orderPlaced = receive.getBoolean("order_placed", false);
        boolean orderPacked=receive.getBoolean("order_packed",false);
        boolean Started=receive.getBoolean("Started",false);
        boolean Delivered=receive.getBoolean("Delivered",false);

        String bill=getIntent().getStringExtra("id");
        billidisplay.setText(bill);

        if (orderPlaced) {
            orderplaced.setImageResource(R.drawable.track_order_logo_2); // Change color if order is placed
        }
        if(orderPacked){
            orderpacked.setImageResource(R.drawable.track_order_logo_2);
        }
        if(Started){
            started.setImageResource(R.drawable.track_order_logo_2);
        }
        if(Delivered){
            delivered.setImageResource(R.drawable.track_order_logo_2);
        }

        orderedProductsAdapter = new ArrayAdapter<>(this, R.layout.stock_custom_listview, orderedProducts);
        listview.setAdapter(orderedProductsAdapter);

        db.collection("Orders")
                .whereEqualTo("bill id",bill) // Query for the specific order based on username
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                        if (value != null && !value.isEmpty()) {
                            DocumentSnapshot snapshot = value.getDocuments().get(0); // Retrieve the first document
                            orderplacedc = snapshot.getString("order placed");
                            orderpackedc=snapshot.getString("order packed");
                            startedc=snapshot.getString("started");
                            deliveredc=snapshot.getString("delivered");
                            if (orderplacedc != null && orderplacedc.equals("1")) {
                                orderplaced.setImageResource(R.drawable.track_order_logo_2); // Change color if order is placed
                            }
                            if(orderpackedc!=null && orderpackedc.equals("1")){
                                orderpacked.setImageResource(R.drawable.track_order_logo_2);
                            }
                            if(startedc!=null && startedc.equals("1")){
                                started.setImageResource(R.drawable.track_order_logo_2);
                            }
                            if(deliveredc!=null && deliveredc.equals("1")){
                                delivered.setImageResource(R.drawable.track_order_logo_2);
                            }
                        }
                    }
                });
        trackorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtility myUtility=new MyUtility();
                myUtility.trackorder(TrackOrderActivity.this);
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
    }
}