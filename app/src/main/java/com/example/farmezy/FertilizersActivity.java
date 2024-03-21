package com.example.farmezy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class FertilizersActivity extends AppCompatActivity {

    private ListView listView;
    private TextView trackorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fertilizers);

        listView=findViewById(R.id.fertilizersactivitylistview);
        trackorder=findViewById(R.id.trackorder);

        MyUtility myUtility=new MyUtility();

        myUtility.showstock(FertilizersActivity.this,"FertilizersStock",listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name=(String)listView.getItemAtPosition(position);
                String check="FertilizersStock";
                Intent intent=new Intent(FertilizersActivity.this, OrderPreCheckActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("check",check);
                startActivity(intent);
            }
        });
        trackorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myUtility.trackorder(FertilizersActivity.this);
            }
        });
    }
}