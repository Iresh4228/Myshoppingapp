package com.example.myshoppingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class AddProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageView imageview;
    private Spinner spinner;
    private EditText txtsubcat,txtname,txtdesc,txtprice;
    private Button btnadd,btncancel;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> list;
    private DatabaseReference dbref;
    private String category;
    private final String TAG = "AddProductActivity";
    //private ProgressDialog pd;
    private Uri imageUri;
    private static final int PIC = 1;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        imageview = findViewById(R.id.fashion_product_image);
        spinner = findViewById(R.id.fashion_product_category);
        txtsubcat = findViewById(R.id.fashion_product_sub_category);
        txtname = findViewById(R.id.fashion_product_name);
        txtdesc = findViewById(R.id.fashion_product_description);
        txtprice = findViewById(R.id.fashion_product_price);
        btnadd = findViewById(R.id.fashion_product_add);
        btncancel = findViewById(R.id.fashion_product_cancel);
        dbref = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList<String>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        //pd = new ProgressDialog(this);
        category = "";
        storageRef = FirebaseStorage.getInstance().getReference().child("Product_images");



        fillSpinner();

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name,subcat,desc,price;

                name = txtname.getText().toString();
                subcat = txtsubcat.getText().toString();
                desc = txtdesc.getText().toString();
                price = txtprice.getText().toString();

                if (category.equals("") || name.equals("") || subcat.equals("") || desc.equals("") || price.equals("") ||  imageUri==null){
                    Toast.makeText(AddProductActivity.this, "Please Fill All The Fields...!", Toast.LENGTH_LONG).show();

                }else {

                    saveProduct(name,subcat,desc,price);
                }
            }
        });
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveProduct(String name, String subcat, String desc, String price) {
        String id = dbref.child("FashionProducts").push().getKey();
        String path = imageUri.getLastPathSegment().toString();
        String ext = path.substring(path.lastIndexOf("."));
        StorageReference filepath = storageRef.child(name+"_"+id+ext);
        final UploadTask uploadTask = filepath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
                Toast.makeText(AddProductActivity.this, "Upload Failure,Try Again....!!", Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            String url = task.getResult().toString();
                            Product pro = new Product();
                            pro.setProd_id(id);
                            pro.setProd_name(name);
                            pro.setProd_cate(category);
                            pro.setProd_scat(subcat);
                            pro.setProd_desc(desc);
                            pro.setProd_price(Float.parseFloat(price));
                            pro.setProd_image(url);


                            dbref.child("FashionProducts").child(id).setValue(pro);
                            Toast.makeText(AddProductActivity.this, "Successfully Added...!!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });


    }

    private void loadImage() {
        Intent int_image = new Intent();
        int_image.setAction(Intent.ACTION_GET_CONTENT);
        int_image.setType("image/*");
        startActivityForResult(int_image,PIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PIC && resultCode==RESULT_OK && data!=null);
        imageUri = data.getData();
        imageview.setImageURI(imageUri);
    }

    private void fillSpinner() {
        //pd.setMessage("Loading...");
        //pd.show();
        dbref.child("FashionCategory").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    list.add(data.getValue().toString());

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG,error.getMessage());
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}