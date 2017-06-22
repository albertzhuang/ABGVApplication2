package edu.bluejack162.matchfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends Activity {

    Thread splashThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }catch (Exception e){}
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        splashThread.start();
    }
}
