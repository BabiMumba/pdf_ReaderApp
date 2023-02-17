package com.babitech.pdfreader.new_reader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.babitech.pdfreader.R;

import java.io.File;
import java.io.IOException;

public final class MainActivity extends AppCompatActivity {

    Button createFile, createFolder;
    String theDir = Environment.getExternalStorageDirectory().toString() + "/Download/";
    private EditText one, two;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        one = findViewById(R.id.fileName);
        two = findViewById(R.id.folderName);
        createFile = findViewById(R.id.createFile);
        createFolder = findViewById(R.id.createFolder);
        createFile.setOnClickListener(v -> {
            if (one.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter a file name", Toast.LENGTH_SHORT).show();
            } else
                _createFile(new File(theDir + one.getText().toString()));
        });

        createFolder.setOnClickListener(v -> {
            if (two.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter a folder name", Toast.LENGTH_SHORT).show();
            } else
                _createFolder(new File(theDir + two.getText().toString()));
        });

        //Check if app has permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestWrite();
        }

    }

    //Request permission
    public void requestWrite() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    public void _createFile(File file) {
        if (!file.exists()) {
            try {
                boolean succ = file.createNewFile();
                if (succ) {
                    Toast.makeText(getApplicationContext(), "File Created", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Error creating file", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (file.exists()) {
            Toast.makeText(getApplicationContext(), "File already exists!", Toast.LENGTH_SHORT).show();
        }
    }

    public void _createFolder(File file) {
        if (!file.exists()) {
            boolean succ = file.mkdir();
            if (succ) {
                Toast.makeText(getApplicationContext(), "Folder Created", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "Error creating folder", Toast.LENGTH_SHORT).show();
        } else if (file.exists()) {
            Toast.makeText(getApplicationContext(), "Folder already exists!", Toast.LENGTH_SHORT).show();
        }
    }
}
