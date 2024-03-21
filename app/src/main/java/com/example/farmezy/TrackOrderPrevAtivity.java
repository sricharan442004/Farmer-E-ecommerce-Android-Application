package com.example.farmezy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TrackOrderPrevAtivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayList<String> orders=new ArrayList<String>();
    private ArrayList<String> billnos=new ArrayList<String>();
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order_prev_ativity);

        listview=findViewById(R.id.trackactivitylistview);

        checkorders();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedBillId = billnos.get(position); // Retrieve the bill ID based on the selected position
                Intent intent = new Intent(TrackOrderPrevAtivity.this, TrackOrderActivity.class);
                intent.putExtra("id", selectedBillId); // Pass the selected bill ID to the TrackOrderActivity
                startActivity(intent);
            }
        });
    }
    private void checkorders(){
        SharedPreferences sharedPreferences=getSharedPreferences("MyPrefs",MODE_PRIVATE);
        String number=sharedPreferences.getString("MobileNumber","");
        db=FirebaseFirestore.getInstance();
        db.collection("Orders").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                orders.clear();
                billnos.clear();
                for(DocumentSnapshot snapshot:value)
                {
                    if(number.equals(snapshot.getString("contact"))){
                        orders.add("Id : #"+snapshot.getString("bill id")+"\nTotal Bill :"+snapshot.getString("total bill"));
                        billnos.add(snapshot.getString("bill id"));
                    }
                }
                ArrayAdapter<String> orderadapter=new ArrayAdapter<String>(TrackOrderPrevAtivity.this,R.layout.stock_custom_listview,orders);
                listview.setAdapter(orderadapter);
            }
        });

    }
}