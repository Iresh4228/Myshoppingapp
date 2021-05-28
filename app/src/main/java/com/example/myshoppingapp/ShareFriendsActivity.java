package com.example.myshoppingapp;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.util.Properties;


public class ShareFriendsActivity extends AppCompatActivity {

    private TextView fashionshare, txtprice;
    private EditText fashionemail;
    private Button fashionsend;
    private RelativeLayout emaillay;
    private FirebaseAuth authenti;
    private String imageUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_friends);

        fashionshare = findViewById(R.id.fashion_product_share);
//        txtname = findViewById(R.id.email_details_product);
        txtprice = findViewById(R.id.prod_details_price);
        fashionemail = findViewById(R.id.email_details_product);
        fashionsend = findViewById(R.id.email_detail_btn_send);
        emaillay = findViewById(R.id.fashion_rela);

        fashionshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emaillay.getVisibility() == View.VISIBLE) {
                    emaillay.setVisibility(View.GONE);
                } else {
                    emaillay.setVisibility(View.VISIBLE);
                }
            }
        });

        fashionsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = fashionemail.getText().toString();
//                sendEmail(email);
                Toast.makeText(ShareFriendsActivity.this, authenti.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
            }
        });
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        return;


    }

    private void sendEmail(String email) {
        final String username = "sureshudayanga20@gmail.com";
        final String password = "Suresh4228";
        Properties propert = new Properties();
        propert.put("mail.smtp.auth", "true");
        propert.put("mail.smtp.starttls.enable", "true");
        propert.put("mail.smtp.host", "smtp.gmail.com");
        propert.put("mail.smtp.port", "587");

        Session session = Session.getInstance(propert,
                new javax.mail.Authenticator() {
                    @Override
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("LookGood Online Shopping : " + fashionemail.getText().toString());
            Multipart multipart = new MimeMultipart("related");
            BodyPart bodyPart = new MimeBodyPart();
            String htmlText = "Hello,<br/>Following message has been shared by " + authenti.getCurrentUser().getEmail();
            htmlText += "<p><b>Name: </b>" + fashionemail.getText().toString() + "</p>";
            htmlText += "<p><b>Price: Rs. </b>" + String.format("%.02f", Float.parseFloat(txtprice.getText().toString())) + "</p>";
            htmlText += "<img src='" + imageUri + "'/>";
            htmlText += "<p>Thank you,<br />Team - LookGood Online Shopping</p>";
            bodyPart.setContent(htmlText, "text/html");
            multipart.addBodyPart(bodyPart);
            message.setContent(multipart);
            Transport.send(message);
            Toast.makeText(getApplicationContext(), "Email Sent", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }


    }
}
