package com.example.myshoppingapp;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private EditText txtfashionsearch;
    private Button fashionsearchbutton;
    private RecyclerView rv_search;
    private ProductAdapter pAdapter;
    private ArrayList<Product> pList;
    private ArrayList<Product> fashionallProdlist;
    private DatabaseReference dbRef;
    private FirebaseAuth auth;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        txtfashionsearch = view.findViewById(R.id.fashion_search);
        fashionsearchbutton = view.findViewById(R.id.fashion_search_btn);
        rv_search = view.findViewById(R.id.products_search);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1);
        rv_search.setLayoutManager(gridLayoutManager);
        fashionallProdlist = new ArrayList<>();
        pList = new ArrayList<>();
        pAdapter = new ProductAdapter(pList,getContext());
        rv_search.setAdapter(pAdapter);
        dbRef = FirebaseDatabase.getInstance().getReference().child("FashionProducts");
        auth = FirebaseAuth.getInstance();


        //To fill all the products to the fashionallProducts
        getAllProducts();

        fashionsearchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtfashionsearch.getText().toString().equals(""))
                    searchProducts(txtfashionsearch.getText().toString());
                else
                    Toast.makeText(getContext(), "Please enter the search text", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void getAllProducts() {

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Product prod = ds.getValue(Product.class);
                    fashionallProdlist.add(prod);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchProducts(String name) {

        pList.clear();
        boolean result = false;
        for (Product prod : fashionallProdlist){
            if (prod.getProd_name().toLowerCase().contains(name.toLowerCase())){
                pList.add(prod);
                result = true;
            }
            pAdapter.notifyDataSetChanged();

        }
        if(result==false){
            Toast.makeText(getContext(), "No results found for :"+name, Toast.LENGTH_SHORT).show();
            pList.clear();
        }
    }
}