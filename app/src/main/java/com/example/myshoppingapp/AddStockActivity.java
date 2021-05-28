package com.example.myshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddStockActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spi_cate,spi_prod,spi_size;
    private EditText txtavail,txtqty;
    private Button btnadd,btncancel;
    private ArrayList<String>list_cate, list_prod, list_size,list_productsID;
    private ArrayAdapter<String> adap_cate,adap_prod,adap_size;
    private DatabaseReference dbref;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);
        spi_cate = findViewById(R.id.stock_category);
        spi_prod = findViewById(R.id.stock_product);
        spi_size = findViewById(R.id.stock_size);
        txtavail = findViewById(R.id.txt_available);
        txtqty = findViewById(R.id.txt_quantity);
        btnadd = findViewById(R.id.add);
        btncancel = findViewById(R.id.cancel);

        dbref = FirebaseDatabase.getInstance().getReference();
        pd = new ProgressDialog(this);

        list_cate = new ArrayList<>();
        adap_cate = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,list_cate);
        spi_cate.setAdapter(adap_cate);
        spi_cate.setOnItemSelectedListener(this);

        list_prod = new ArrayList<>();
        list_productsID = new ArrayList<String>();
        list_prod.add(0,"Select Product");
        adap_prod = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,list_prod);
        spi_prod.setAdapter(adap_prod);
        spi_prod.setOnItemSelectedListener(this);

        list_size = new ArrayList<>();
        list_size.add(0,"Select Size");
        list_size.add(1,"Small");
        list_size.add(2,"Medium");
        list_size.add(3,"Large");
        adap_size = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,list_size);
        spi_size.setAdapter(adap_size);
        spi_size.setOnItemSelectedListener(this);

        populateCategory();

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStock();

            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void addStock() {
        if (spi_cate.getSelectedItemPosition()==0 || spi_prod.getSelectedItemPosition()==0 || spi_size.getSelectedItemPosition()==0 || txtqty.getText().toString().equals("")){
            Toast.makeText(this, "Fill All The Field", Toast.LENGTH_SHORT).show();
            return;
        }
        pd.setMessage("Saving Data....");
        pd.show();
        String pid = list_productsID.get(spi_prod.getSelectedItemPosition()-1);
        String size = spi_size.getSelectedItem().toString();
        int avail = Integer.parseInt(txtavail.getText().toString());
        int qty = Integer.parseInt(txtqty.getText().toString());
        int newqty = avail+qty;
        dbref.child("FashionStock").child(pid).child(size.toLowerCase()).setValue(newqty);
        Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show();
        CTRL_MGT();
        pd.dismiss();


    }

    private void CTRL_MGT() {
        spi_size.setSelection(0);
        txtavail.setText("");
        txtqty.setText("");
    }

    private void populateCategory() {
        pd.setMessage("Loading Data.....");
        pd.show();
        list_cate.clear();
        dbref.child("FashionCategory").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data:snapshot.getChildren()){
                    list_cate.add(data.getValue().toString());
                }
                list_cate.add(0,"Select Category");
                adap_cate.notifyDataSetChanged();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int item=parent.getId();
        if(item==R.id.stock_category){
            if (spi_cate.getSelectedItemPosition()!=0){
                pd.setMessage("Loading Data.....");
                pd.show();
                list_prod.clear();
                list_productsID.clear();
                String cate = spi_cate.getSelectedItem().toString();
                dbref.child("FashionProducts").orderByChild("prod_cate").equalTo(cate).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data:snapshot.getChildren()){
                            Product prod = data.getValue(Product.class);
                            list_prod.add(prod.getProd_name());
                            list_productsID.add(prod.getProd_id());
                        }
                        list_prod.add(0,"Select Product");
                        adap_prod.notifyDataSetChanged();
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            else {
                list_prod.clear();
                list_productsID.clear();
                list_prod.add(0,"Select Product");
                adap_prod.notifyDataSetChanged();
            }

        }
        else if (item==R.id.stock_size){
            if (spi_size.getSelectedItemPosition()!=0){
                String size = spi_size.getSelectedItem().toString();
                int pos = spi_prod.getSelectedItemPosition();
                if (pos==0){
                    Toast.makeText(this, "select a product", Toast.LENGTH_SHORT).show();
                    spi_size.setSelection(0);
                    return;
                }
                pd.setMessage("Loading data....");
                pd.show();
                String pid = list_productsID.get(pos-1);
                dbref.child("FashionStock").child(pid).child(size.toLowerCase()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue()==null){
                            txtavail.setText("0");
                        }
                        else {
                            txtavail.setText(snapshot.getValue().toString());
                        }
                        pd.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else {
                txtavail.setText("");
                txtqty.setText("");
            }

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}