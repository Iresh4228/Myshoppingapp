package com.example.myshoppingapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    private View view;
    private Button btnsignout,btnedit;
    private EditText txtname,txtemail,txtphone,txtaddress;
    private FirebaseAuth authentication;
    private FirebaseUser user;
    private DatabaseReference dbRef;
    private final String TAG = "ProfileFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_profile, container, false);
        btnsignout = view.findViewById(R.id.btnsignout);
        txtname = view.findViewById(R.id.pro_name);
        txtemail = view.findViewById(R.id.pro_email);
        txtphone = view.findViewById(R.id.pro_phone);
        txtaddress = view.findViewById(R.id.pro_address);
        btnedit = view.findViewById(R.id.pro_btnedit);
        authentication = FirebaseAuth.getInstance();
        user = authentication.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();
        loadProfile(user);

        btnsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                getFragmentManager().beginTransaction().replace(R.id.frag_container,new HomeFragment()).commit();

            }
        });

        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String label = btnedit.getText().toString();
                if (label.equals("Edit Profile")) {
                    CTRL_MGT(true);
                    btnedit.setText("SAVE PROFILE");
                } else if (label.equals("SAVE PROFILE")) {
                    saveProfile(user);
                }
            }
        });

        return view;
    }

    private void loadProfile(FirebaseUser user) {
        String uid = user.getUid();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Customer cust = snapshot.child("FashionCustomer").child(uid).getValue(Customer.class);
                txtname.setText(cust.getCust_name());
                txtemail.setText(cust.getCust_email());
                txtphone.setText(cust.getCust_phone());
                txtaddress.setText(cust.getCust_address());

                txtname.setEnabled(false);
                txtemail.setEnabled(false);
                txtphone.setEnabled(false);
                txtaddress.setEnabled(false);

                CTRL_MGT(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CTRL_MGT(boolean status) {
        txtname.setEnabled(status);
        txtemail.setEnabled(status);
        txtphone.setEnabled(status);
        txtaddress.setEnabled(status);
    }


    private void saveProfile(FirebaseUser user) {
        String name = txtname.getText().toString();
        String email = txtemail.getText().toString();
        String phone = txtphone.getText().toString();
        String address = txtaddress.getText().toString();
        String uid = user.getUid();

        if ((name.equals("") || email.equals("")||phone.equals("")||address.equals(""))){
            Toast.makeText(getContext(), "Please Fill ALL THE FIELDS...!!", Toast.LENGTH_LONG).show();


        }else {

            Customer cust = new Customer();
            cust.setCust_id(uid);
            cust.setCust_name(name);
            cust.setCust_email(email);
            cust.setCust_phone(phone);
            cust.setCust_address(address);

            dbRef.child("FashionCustomer").child(uid).setValue(cust, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                    if (error==null){
                        CTRL_MGT(false);
                        btnedit.setText("Edit Profile");
                        Toast.makeText(getContext(), "Successfully Saved...!!", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(getContext(), "Error, Try Again...!!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG,"Record Update Error...!!:" +error.getMessage());

                    }
                }
            });

        }
    }
}