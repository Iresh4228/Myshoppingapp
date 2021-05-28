package com.example.myshoppingapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ProductViewFragment extends Fragment {
    private View view;
    private RecyclerView recyclerview;
    private ProductAdapter pAdapter;
    private ArrayList<Product> pList;
    private DatabaseReference dbref;
    private final String TAG = "ProductViewFragment";
    private String title,type;
    private TextView txttitle,txtback;
    private Fragment cfrag;

    public ProductViewFragment(String title,String type) {
        this.title = title;
        this.type = type;
        this.cfrag = this;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_product_view, container, false);
        recyclerview = view.findViewById(R.id.rv_products);
        txttitle = view.findViewById(R.id.prod_view_title);
        txttitle.setText(this.title);
        txtback = view.findViewById(R.id.prod_view_back);
        pList = new ArrayList<>();
        pAdapter = new ProductAdapter(pList,getContext());
        recyclerview.setAdapter(pAdapter);
        dbref = FirebaseDatabase.getInstance().getReference().child("FashionProducts");

        dbref.orderByChild("prod_cate").equalTo(this.type).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    Product prod = ds.getValue(Product.class);
                    pList.add(prod);

                }
                pAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG,error.getMessage());
            }
        });

        txtback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(cfrag).commit();
            }
        });

        return view;
    }
}