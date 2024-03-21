package com.example.farmezy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private CardView seeds,cart,fertilizers;
    private TextView trackorder;
    private ImageView mainlogoanim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seeds=findViewById(R.id.mainseeds);
        cart=findViewById(R.id.maincart);
        trackorder=findViewById(R.id.trackorder);
        fertilizers=findViewById(R.id.mainfertilizers);
        mainlogoanim=findViewById(R.id.mainlogoanim);
        MyUtility myUtility=new MyUtility();

        seeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, SeedsActivity.class);
                startActivity(intent);
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        fertilizers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, FertilizersActivity.class);
                startActivity(intent);
            }
        });
        trackorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myUtility.trackorder(MainActivity.this);
            }
        });

        Animation alpha= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        mainlogoanim.startAnimation(alpha);
    }
}