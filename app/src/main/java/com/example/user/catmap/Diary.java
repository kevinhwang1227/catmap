package com.example.user.catmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2018-01-10.
 */

public class Diary extends AppCompatActivity{

    ArrayList<String> catpath = new ArrayList<>();
    ArrayList<HashMap<String,String>> catlist = new ArrayList<>();
    final HashMap<String,Bitmap> catimg = new HashMap<>();
    Bitmap filemap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        ListView cardlist = (ListView) findViewById(R.id.cardlist);

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

        Diarycards mDiarycards = new Diarycards(catimg,catlist);
        cardlist.setAdapter(mDiarycards);
        setResult(RESULT_OK);
    }
}
