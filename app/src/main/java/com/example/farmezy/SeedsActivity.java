package com.example.farmezy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class SeedsActivity extends AppCompatActivity {

    private ListView listView;
    private TextView trackorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeds);

        listView=findViewById(R.id.seedsactivitylistview);
        trackorder=findViewById(R.id.trackorder);
        MyUtility myUtility=new MyUtility();

        myUtility.showstock(SeedsActivity.this,"SeedsStock",listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name=(String)listView.getItemAtPosition(position);
                String check="SeedsStock";
                Intent intent=new Intent(SeedsActivity.this, OrderPreCheckActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("check",check);
                startActivity(intent);

            }
        });
        trackorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myUtility.trackorder(SeedsActivity.this);
            }
        });
    }
}