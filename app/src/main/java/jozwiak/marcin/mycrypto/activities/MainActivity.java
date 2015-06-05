package jozwiak.marcin.mycrypto.activities;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import jozwiak.marcin.mycrypto.R;
import jozwiak.marcin.mycrypto.fragments.AES256_CBC;


public class MainActivity extends ActionBarActivity {

    private KeyGenerator kgen = null;
    private SecretKey skey = null;
    private byte []key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*System.setProperty("javax.net.ssl.keyStore", "C:/Program Files/Java/jdk1.8.0_25/bin/clientKeyStore.key");
        System.setProperty("javax.net.ssl.keyStorePassword","qwerty");
        System.setProperty("javax.net.ssl.trustStore", "C:/Program Files/Java/jdk1.8.0_25/bin/clientTrustStore.key");
        System.setProperty("javax.net.ssl.trustStorePassword","qwerty");*/

        setKey();
        File sdcard = Environment.getExternalStorageDirectory();
        try {
            new AES256_CBC().encrypt( new File(sdcard,"secure.txt"), new File(sdcard,"secure_encrypted.txt"), key);
            new ConnectToServer().execute(new File(sdcard,"secure.txt"));
            new AES256_CBC().decrypt(new File(sdcard,"secure_encrypted.txt"), new File(sdcard,"secure_decrypted.txt"), key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setKey(){
        try {
            kgen = KeyGenerator.getInstance("AES");
            kgen.init(256);
            skey = kgen.generateKey();
            key = skey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ConnectToServer extends AsyncTask<File,Void,Void>{

        @Override
        protected Void doInBackground(File... params) {
            try {
                AES256_CBC.decryptOnServer(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
