package com.example.farmezy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyUtility {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference sr;
    private ArrayList<String> stock=new ArrayList<String>();

    public void trackorder(Context context){
        Intent intent=new Intent(context, TrackOrderPrevAtivity.class);
        context.startActivity(intent);
    }
    public void showstock(Context context, String collectionpath, ListView listView){
        db=FirebaseFirestore.getInstance();
        db.collection(collectionpath).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                stock.clear();
                for(DocumentSnapshot snapshot:value){
                    stock.add(snapshot.getString("name"));
                }
                if(stock.isEmpty()){
                    Toast.makeText(context, "Failed to Fetch", Toast.LENGTH_SHORT).show();
                }
                else{
                    ArrayAdapter<String> stockadapter=new ArrayAdapter<String>(context,R.layout.stock_custom_listview,stock);
                    listView.setAdapter(stockadapter);
                }
            }
        });
    }
    public void extractdata(Context context, String collectionpath, String name, String[] quantity, int pos, TextView pricedisplay){
        db=FirebaseFirestore.getInstance();
        db.collection(collectionpath).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                for(DocumentSnapshot snapshot:value){
                    if(snapshot.getString("name").equals(name)){
                        double p=Double.parseDouble((snapshot.getString("price")));
                        double qua=Double.parseDouble(quantity[pos]);
                        pricedisplay.setText("Price: "+p*qua+"/-");
                    }
                }
            }
        });
    }
    public void extractimg(Context context, ImageView productimg, String name, String collectionpath){
        storage=FirebaseStorage.getInstance();
        sr=storage.getReferenceFromUrl("gs://farmezy-f88eb.appspot.com/"+collectionpath).child(name+".jpg");
        try{
            final File files=File.createTempFile("img","jpg");
            sr.getFile(files).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap= BitmapFactory.decodeFile(files.getAbsolutePath());
                    productimg.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void placeorder(Context context,String consumername,String consumercontact,String consumerlocation,String consumerbill,ArrayList<String>cartItems){
        long timestamp = System.currentTimeMillis(); // Get the current timestamp
        int randomNumber = (int) (Math.random() * 10000); // Generate a random number between 0 and 9999
        String billId = timestamp + "_" + randomNumber;
        Map<String, Object> order = new HashMap<>();
        order.put("name", consumername);
        order.put("contact", consumercontact);
        order.put("location", consumerlocation);
        order.put("total bill",consumerbill);
        order.put("order placed","1");
        order.put("order packed","0");
        order.put("started","0");
        order.put("delivered","0");
        order.put("bill id",billId);
        for (int i = 0; i < cartItems.size(); i++) {
            String productKey = "Product " + (i + 1);
            String quantityKey = "Quantity " + (i + 1);

            String cartItem = cartItems.get(i);
            String[] parts = cartItem.split(" - ");
            String productName = parts[0];
            String productQuantity = parts[1];
            if(productQuantity.matches("500"))
                productQuantity=parts[1]+" gm";
            else
                productQuantity=parts[1]+" kg";
            order.put(productKey, productName);
            order.put(quantityKey, productQuantity);
        }
        FirebaseFirestore.getInstance().collection("Orders").add(order).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(context, "Order Placed", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }
}
