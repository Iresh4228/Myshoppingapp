package com.example.myshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView nav;
    private FragmentManager frag_man;
    private FragmentTransaction frag_tran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nav = findViewById(R.id.navigation);

        loadFragment(new HomeFragment());

        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.nav_home:
                        loadFragment(new HomeFragment());
                        break;
                    case R.id.nav_search:
                        loadFragment(new SearchFragment());
                        break;
                    case R.id.nav_cart:
                        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }else {
                            loadFragment(new ShoppingcartFragment());
                        }
                        break;
                    case R.id.nav_profile:
                        if(FirebaseAuth.getInstance().getCurrentUser() == null){
                            startActivity(new Intent(MainActivity.this,LoginActivity.class));
                            finish();
                        }else {
                            loadFragment(new ProfileFragment());

                        }
                        break;
                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        frag_man  = getSupportFragmentManager();
        frag_tran = frag_man.beginTransaction();
        frag_tran.replace(R.id.frag_container,fragment);
        frag_tran.commit();
    }
}