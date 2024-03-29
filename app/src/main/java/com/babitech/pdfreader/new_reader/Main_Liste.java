package com.babitech.pdfreader.new_reader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.babitech.pdfreader.DocumentActivity;
import com.babitech.pdfreader.R;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main_Liste extends AppCompatActivity {

    String root = (Environment.getExternalStorageDirectory().getPath());
   // String root = Environment.getExternalStorageDirectory().toString() + "/Download/";
    File directory = new File(root);
    String[] values = directory.list();
    File[] files = directory.listFiles();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        final ListView listView = findViewById(R.id.list);
        assert values != null;
        List l = Arrays.asList(values);
        ArrayAdapter<String> directoryList = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
        //List to sort A-Z
        Collections.sort(l);
        listView.setAdapter(directoryList);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String name = (String) listView.getItemAtPosition(position);

            File tt = new File(root + "/" + name);
            assert files != null;
            if (tt.isFile()) {
                /*
                  if (tt.getName().endsWith(".pdf")){
                    startActivity(new Intent(this, DocumentActivity.class)
                            .putExtra("path",tt.getAbsolutePath())
                    );
                }else {
                    Toast.makeText(getApplicationContext(), "chemin " + tt.getAbsolutePath() + " clicked", Toast.LENGTH_SHORT).show();
                }
                 */
                Toast.makeText(getApplicationContext(), "chemin " + name + " clicked", Toast.LENGTH_SHORT).show();
              //

            } else if (tt.isDirectory())
                Toast.makeText(getApplicationContext(), "Folder " + name + " clicked", Toast.LENGTH_SHORT).show();
        });
    }
}