package com.jatin.externalstoragewithencryptdecrypt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.scottyab.aescrypt.AESCrypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

/*
DATA PERSISTENCE:
    - SHARED PREFERENCES
    - INTERNAL STORAGE
    - EXTERNAL STORAGE:
        - It is a shared memory area of a device.
        - External Storage is generally a removable storage of the device, such as SD Card.
        - Data is accessible by any app or any user.
        - Since, your data is shared, anyone can read, write and remove it.
        - Before reading or writing into the external storage it is always recommended to
          check if the external storage is available or not.
        - Also, you need permissions to read and write with external storage.
            Examples:
                For write:  WRITE_EXTERNAL_STORAGE
                For read:   READ_EXTERNAL_STORAGE
        - Methods:
            - getExternalFilesDir():


 */

public class MainActivity extends AppCompatActivity {

    private EditText etText;
    private Button btnSave, btnRead;
    private TextView txtMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etText = findViewById(R.id.etText);
        btnSave = findViewById(R.id.btnSave);
        btnRead = findViewById(R.id.btnRead);
        txtMsg = findViewById(R.id.txtMsg);

        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            btnSave.setEnabled(false);
            btnRead.setEnabled(false);
        }
        
        btnSave.setOnClickListener(view -> {
            File f = new File(getExternalFilesDir("myFolder"),"jfile.txt");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                String data = etText.getText().toString();
                String eData = AESCrypt.encrypt("jv",data);
                fos.write(eData.getBytes());
                Toast.makeText(this, "File Created at "+ f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                txtMsg.setText("File Created at "+ f.getAbsolutePath());
                etText.setText("");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(fos!=null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        btnRead.setOnClickListener(view -> {

            File f = new File(getExternalFilesDir("myFolder"),"jfile.txt");
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                String line = br.readLine();
                StringBuilder sb = new StringBuilder();
                while(line!=null){
                    sb.append(line);
                    line = br.readLine();
                }
                String plainData = AESCrypt.decrypt("jv",sb.toString());
                etText.setText(plainData);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } finally {
                if(fis!=null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

    }
}