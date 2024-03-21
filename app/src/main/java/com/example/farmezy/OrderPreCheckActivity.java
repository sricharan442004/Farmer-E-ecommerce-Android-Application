package com.example.farmezy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class OrderPreCheckActivity extends AppCompatActivity {

    private TextView orderprechecklable,quantitydisplay,pricedisplay;
    private String[] quantity=new String[]{"0.5","1","2","5","10"};
    private Button plus,minus,addtocart;
    private ImageView productimg;
    private int pos=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pre_check);

        orderprechecklable=findViewById(R.id.orderprechecklable);
        plus=findViewById(R.id.qplusButton);
        minus=findViewById(R.id.qminusButton);
        quantitydisplay=findViewById(R.id.quantityText);
        addtocart=findViewById(R.id.addtocart);
        pricedisplay=findViewById(R.id.orderprecheckprice);
        productimg=findViewById(R.id.productimg);

        quantitydisplay.setText(quantity[pos]);

        setdata();
        updatePrice(pos);

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos--;
                if(pos>=0){
                    double selectedQuantity = Double.parseDouble(quantity[pos]);
                    if (selectedQuantity >= 1.0) {
                        quantitydisplay.setText(quantity[pos] + " kg");
                    } else {
                        int grams = (int) (selectedQuantity * 1000);
                        quantitydisplay.setText(grams + " gm");
                    }
                }
                else
                    pos=0;
                updatePrice(pos);
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos++;
                if(pos<=quantity.length-1)
                    quantitydisplay.setText(quantity[pos]+" kg");
                else
                    pos=quantity.length-1;
                updatePrice(pos);
            }
        });
        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fire();
                finish();
            }
        });
    }
    private void setdata(){
        String name=getIntent().getStringExtra("name");
        orderprechecklable.setText(name);
        name=name.replace("/"," ");
        String check=getIntent().getStringExtra("check");

        MyUtility myUtility=new MyUtility();
        myUtility.extractimg(OrderPreCheckActivity.this,productimg,name,check);
    }
    private void fire(){
        String selectedProduct = orderprechecklable.getText().toString();
        String selectedQuantity=quantitydisplay.getText().toString();
        String selectedPrice=pricedisplay.getText().toString();

        if (selectedQuantity.contains("gm")) {
            selectedQuantity = selectedQuantity.replace(" gm", "");
        } else {
            selectedQuantity = selectedQuantity.replace(" kg", "");
        }

        // Add the selected product to the cart using SharedPreferences
        addProductToCart(selectedProduct, selectedQuantity, selectedPrice);

        Intent intent = new Intent(OrderPreCheckActivity.this, CartActivity.class);
        startActivity(intent);
        finish();
    }
    private void addProductToCart(String product,String quantity,String Price) {
        // Retrieve the existing cart items from SharedPreferences
        ArrayList<String> cartItems = getCartItems();

        // Add the selected product to the cart
        String cartItem = product + " - " + quantity + " - "+Price;
        cartItems.add(cartItem);

        // Save the updated cart items to SharedPreferences
        saveCartItems(cartItems);
    }
    private ArrayList<String> getCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        Set<String> cartItemsSet = sharedPreferences.getStringSet("cartItems", new HashSet<>());
        return new ArrayList<>(cartItemsSet);
    }
    private void saveCartItems(ArrayList<String> cartItems) {
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> cartItemsSet = new HashSet<>(cartItems);
        editor.putStringSet("cartItems", cartItemsSet);
        editor.apply();
    }
    private void updatePrice(int pos) {
        String name=orderprechecklable.getText().toString();
        MyUtility utility=new MyUtility();
        String check=getIntent().getStringExtra("check");
        utility.extractdata(OrderPreCheckActivity.this,check,name,quantity,pos,pricedisplay);
    }
}