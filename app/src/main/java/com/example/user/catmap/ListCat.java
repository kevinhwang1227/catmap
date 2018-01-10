package com.example.user.catmap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListCat extends AppCompatActivity {

    private ListView listView;
    private SearchView searchView;
    ArrayList<String> catpath = new ArrayList<>();
    ArrayList<HashMap<String,String>> catlist = new ArrayList<>();
    final HashMap<String,Bitmap> catimg = new HashMap<>();
    Bitmap filemap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cat);
        listView = (ListView) findViewById(R.id.catList);
        searchView = (SearchView) findViewById(R.id.searchView);

        Intent intent = getIntent();
        assert intent != null;
        Bundle args = intent.getBundleExtra("bundle");
        catlist = (ArrayList<HashMap<String,String>>) args.getSerializable("cats");
        Bundle path = intent.getBundleExtra("path");
        catpath = (ArrayList<String>) path.getSerializable("path");

        for (int i=0; i<catpath.size(); i++) {
            File file = new File(catpath.get(i));
            if(file.exists()){
                filemap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
            if (i == 0) {
                catimg.put("냥냥이",filemap);
            }
            if (i == 1) {
                catimg.put("아름이",filemap);
            }
            if (i == 2) {
                catimg.put("메롱이",filemap);
            }
            if (i == 3) {
                catimg.put("케빈",filemap);
            }
        }

        final listAdapter mlistAdapter = new listAdapter(catimg,catlist);

        listView.setAdapter(mlistAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mlistAdapter.getFilter().filter(s);
                return false;
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = catlist.get(i).get("name");
                String info = catlist.get(i).get("info");
                Bitmap img = catimg.get(name);
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                FileOutputStream fo = null;
                try {
                    fo = openFileOutput("cat", Context.MODE_PRIVATE);
                    fo.write(bStream.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(ListCat.this, catInfo.class);
                intent.putExtra("name", name);
                intent.putExtra("info",info);
                //intent.putExtra("img", byteArray);
                intent.putExtra("from","listView");
                startActivity(intent);

            }
        });
    }




}
