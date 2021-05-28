package com.example.myshoppingapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ShoppingcartFragment extends Fragment {

    private RecyclerView recyclerview;
    private static TextView grandtot;
    private Button btncount;
    private DatabaseReference dbref;
    private FirebaseAuth auth;
    private  CartAdapter cAdapter;
    private ArrayList<ShoppingCart> clist;
    public static ArrayList<ShoppingCart> selectedItems;
    private final String TAG = "ShoppingcartFragment";


    public ShoppingcartFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_shoppingcart, container, false);
        recyclerview = view.findViewById(R.id.rec_cart);
        grandtot = view.findViewById(R.id.cart_view_total);
        btncount = view.findViewById(R.id.cart_view_btn_cout);

        dbref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        clist = new ArrayList<>();
        cAdapter = new CartAdapter(clist,getContext());
        recyclerview.setAdapter(cAdapter);
        selectedItems = new ArrayList<>();

        populateAdapter(uid);

        btncount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItems.size()==0){
                    Toast.makeText(getContext(), "Please select an item to goto the checkout", Toast.LENGTH_SHORT).show();
                }else {
                    getFragmentManager().beginTransaction().replace(R.id.frag_container,new PaymentFragment(selectedItems)).commit();
                }


            }

        });


        return view;
    }

    public static void updateGrandTotal(String value){
        grandtot.setText(value);
    }

    private void populateAdapter(String uid) {

        dbref.child("FashionCart").child(uid).child("FashionItems").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clist.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ShoppingCart shoppingCart = dataSnapshot.getValue(ShoppingCart.class);
                    clist.add(shoppingCart);
                }
                cAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}