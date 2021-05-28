package com.example.myshoppingapp;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;


public class HomeFragment extends Fragment {

    private View view;
    private CardView cardView1,cardView2,cardView3,cardView4;
    private FragmentManager frag_man;
    private FragmentTransaction frag_tran;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_home, container, false);
        cardView1 = view.findViewById(R.id.card_view1);
        cardView2 = view.findViewById(R.id.card_view2);
        cardView3 = view.findViewById(R.id.card_view3);
        cardView4 = view.findViewById(R.id.card_view4);

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Mens Wear", Toast.LENGTH_LONG).show();
                loadFragment(new ProductViewFragment("Mens Wear","Mens Fashion"));
            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Womens Wear", Toast.LENGTH_LONG).show();
                loadFragment(new ProductViewFragment("Womens Wear","Womens Fashion"));
            }
        });
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Kids Wear", Toast.LENGTH_LONG).show();
                loadFragment(new ProductViewFragment("Kids Wear","Kids Fashion"));
            }
        });
        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Sports Wear", Toast.LENGTH_LONG).show();
                loadFragment(new ProductViewFragment("Sports Wear","Sports Fashion"));
            }
        });
        return view;
    }

    private void loadFragment(Fragment fragment) {
        frag_man = getFragmentManager();
        frag_tran = frag_man.beginTransaction();
        frag_tran.add(R.id.frag_container,fragment);
        frag_tran.commit();
    }
}