package com.example.myshoppingapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class ProductDetailsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TextView txtname,txtcate,txtscat,txtprice,txtdesc,txtavail;
    private ImageView prod_image,sharebtn;
    private Spinner sp_size;
    private EditText txtqty;
    private Button btnadd;
    private String pid;
    private DatabaseReference dbref;
    private final String TAG = "ProductDetailsFragment";
    private ArrayList<String> list_size;
    private ArrayAdapter<String> adapter;
    private FirebaseAuth auth;
    private String imageUri = "";


    public ProductDetailsFragment(String pid) {
        // Required empty public constructor
        this.pid = pid;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_product_details, container, false);
        txtname = view.findViewById(R.id.prod_details_name);
        txtcate = view.findViewById(R.id.prod_details_cate);
        txtscat = view.findViewById(R.id.prod_details_scate);
        txtprice = view.findViewById(R.id.prod_details_price);
        txtdesc = view.findViewById(R.id.prod_details_desc);
        txtavail = view.findViewById(R.id.prod_details_aqty);
        prod_image = view.findViewById(R.id.prod_details_image);
        sp_size = view.findViewById(R.id.prod_details_sp_size);
        txtqty = view.findViewById(R.id.prod_details_qty);
        btnadd = view.findViewById(R.id.prod_details_btn_add);
        sharebtn = view.findViewById(R.id.share_btn);
        dbref = FirebaseDatabase.getInstance().getReference();
        list_size = new ArrayList<String>();
        list_size.add(0,"--Select Size--");
        list_size.add(1,"Small");
        list_size.add(2,"Medium");
        list_size.add(3,"Large");
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,list_size);
        sp_size.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        sp_size.setOnItemSelectedListener(this);
        auth = FirebaseAuth.getInstance();
        fetchData();

        sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),ShareFriendsActivity.class));
            }
        });
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveToCart();
            }
        });
        return view;
    }

    private void saveToCart() {
        if(auth.getCurrentUser()==null){
            Toast.makeText(getContext(), "Please Login to Your account to add the products to the cart", Toast.LENGTH_SHORT).show();
        }else if(sp_size.getSelectedItemPosition()==0){
            Toast.makeText(getContext(), "Please Select the Size", Toast.LENGTH_SHORT).show();

        }
        else if (txtqty.getText().toString().equals("")){
            Toast.makeText(getContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();

        }else if (Integer.parseInt(txtqty.getText().toString())<1){
            Toast.makeText(getContext(), "quantity should be higher than 1", Toast.LENGTH_SHORT).show();

        }else if (Integer.parseInt(txtqty.getText().toString())>Integer.parseInt(txtavail.getText().toString())){
            Toast.makeText(getContext(), "Available Quantity is less than the Requested. Available"+txtavail.getText().toString(), Toast.LENGTH_SHORT).show();

        }else {
            String cid = auth.getCurrentUser().getUid();
            String cartid = dbref.child("FashionCart").child(cid).child("FashionItems").push().getKey();
            ShoppingCart sp = new ShoppingCart();
            sp.setProd_id(pid);
            sp.setCust_id(cid);
            sp.setProd_name(txtname.getText().toString());
            sp.setProd_price(Float.parseFloat(txtprice.getText().toString()));
            sp.setProd_qty(Integer.parseInt(txtqty.getText().toString()));
            sp.setProd_size(sp_size.getSelectedItem().toString().toLowerCase());
            Calendar calender = Calendar.getInstance();
            SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            sp.setDate_added(dataFormat.format(calender.getTime()));
            sp.setTime_added(timeFormat.format(calender.getTime()));
            sp.setProd_image(imageUri);
            sp.setCart_id(cartid);

            dbref.child("FashionCart").child(cid).child("FashionItems").child(cartid).setValue(sp).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "Successfully Addedd...!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "Try Again...!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.e(TAG,e.getMessage());
                }
            });

        }
    }

    private void fetchData() {
        dbref.child("FashionProducts").child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot!=null){
                    Product prod = snapshot.getValue(Product.class);
                    txtname.setText(prod.getProd_name());
                    Picasso.get().load(prod.getProd_image()).into(prod_image);
                    txtcate.setText(prod.getProd_cate());
                    txtscat.setText(prod.getProd_scat());
                    txtprice.setText(String.valueOf(prod.getProd_price()));
                    txtdesc.setText(prod.getProd_desc());
                    imageUri = prod.getProd_image();

                }else {
                    Toast.makeText(getContext(), "Product details loading failed...!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG,error.getMessage());
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getSelectedItemPosition() != 0) {
            String size = parent.getSelectedItem().toString();
            dbref.child("FashionStock").child(pid).child(size.toLowerCase()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        txtavail.setText(snapshot.getValue().toString());
                    } else {
                        txtavail.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}