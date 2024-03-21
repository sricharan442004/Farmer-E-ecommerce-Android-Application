package com.example.farmezy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class CartAdapter extends ArrayAdapter<String> {
    private Context context;
    public ArrayList<String> cartitems;
    private TextView totalprice;
    private CartActivity cartActivity;
    public CartAdapter(Context context, ArrayList<String> cartitems, TextView totalprice, CartActivity cartActivity) {
        super(context, R.layout.cart_custom_listview, cartitems);
        this.context = context;
        this.cartitems = cartitems;
        this.totalprice = totalprice; // Assign the TextView for total price
        this.cartActivity=cartActivity;
    }
    @Override
    public View getView(final int position, View converView, ViewGroup parent){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View itemView=inflater.inflate(R.layout.cart_custom_listview,parent,false);

        TextView productdisplay=itemView.findViewById(R.id.cart_product_display);
        Button deletebtn=itemView.findViewById(R.id.cartdelete);

        final String product= cartitems.get(position);
        productdisplay.setText(product);

        String[] itemParts = product.split(" - ");
        if (itemParts.length >= 2) {
            String quantity = itemParts[1];
            String price=itemParts[2];
            String unit = "kg";
            if(quantity.matches("500"))
                unit="gm";
            productdisplay.setText(itemParts[0] + " - " +quantity+" " +unit + " - " + price);
        }

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartitems.remove(position);
                notifyDataSetChanged();
                removefromcart(product);
                double totalPrice = cartActivity.calculateTotalPrice(cartitems); // Update the total price
                totalprice.setText("Total Price: " + totalPrice + "/-");
            }
        });
        return itemView;
    }
    private void removefromcart(String product) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Cart", Context.MODE_PRIVATE);
        Set<String> cartitemsset = sharedPreferences.getStringSet("cartitems", new HashSet<>());
        cartitemsset.remove(product);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("cartitems", cartitemsset);
        editor.apply();

        cartitems.remove(product); // Remove the item from the list
    }
}

public class CartActivity extends AppCompatActivity {

    private ListView cartItemsListView;
    private ArrayAdapter<String> cartItemsAdapter;
    private Button placeorder;
    private FirebaseFirestore db;
    private TextView totalprice,trackorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartItemsListView=findViewById(R.id.cartactivitylistview);
        placeorder=findViewById(R.id.placeorder);
        totalprice=findViewById(R.id.carttotalprice);
        trackorder=findViewById(R.id.trackorder);

        ArrayList<String> cartItems = getCartItems();

        cartItemsAdapter = new CartAdapter(this, cartItems, totalprice,this);
        cartItemsListView.setAdapter(cartItemsAdapter);

        db=FirebaseFirestore.getInstance();

        if (cartItems.isEmpty()) {
            totalprice.setVisibility(View.INVISIBLE);
        } else {
            double totalPrice = calculateTotalPrice(cartItems);
            totalprice.setText("Total Price: " + totalPrice + "/-");
        }

        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cartItems.isEmpty())
                    Toast.makeText(CartActivity.this, "Cart IS Empty", Toast.LENGTH_SHORT).show();
                else {
                    String tot=totalprice.getText().toString();
                    Intent intent = new Intent(CartActivity.this, FinalPlaceOrderActivity.class);
                    intent.putStringArrayListExtra("cartItems", cartItems);
                    intent.putExtra("totalprice",tot);
                    startActivity(intent);
                    clearCart();
                    finish();
                }
            }
        });
        trackorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtility myUtility=new MyUtility();
                myUtility.trackorder(CartActivity.this);
            }
        });
    }
    private ArrayList<String> getCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        Set<String> cartItemsSet = sharedPreferences.getStringSet("cartItems", new HashSet<>());
        return new ArrayList<>(cartItemsSet);
    }
    private void clearCart() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        cartItemsAdapter.clear();
    }
    public double calculateTotalPrice(ArrayList<String> cartItems) {
        double totalPrice = 0.0;
        for (String cartItem : cartItems) {
            String[] itemParts = cartItem.split(" - ");
            if (itemParts.length >= 3) {
                String priceStr = itemParts[2].replace("Price: ", "").replace("/-", "");
                double price = Double.parseDouble(priceStr);
                totalPrice += price;
            }
        }
        return totalPrice;
    }
}