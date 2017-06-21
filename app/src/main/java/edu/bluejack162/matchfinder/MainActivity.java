package edu.bluejack162.matchfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.myButton);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == button)
        {
            /*DatabaseReference databaseReference;
            databaseReference = FirebaseDatabase.getInstance().getReference("users");
            String id = databaseReference.push().getKey();
            Users user = new Users("Albert","Albert123");
            databaseReference.child(id).setValue(user);
            Toast.makeText(this, "Success Insert", Toast.LENGTH_SHORT).show();*/
        }
    }
}
