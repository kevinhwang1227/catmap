package com.example.user.catmap;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AddCat extends AppCompatActivity {
    ImageView mImageView;
    boolean mPermissionDenied= false;
    String mCurrentPhotoPath;
    ArrayList<HashMap<String,String>> catlist = new ArrayList<>();
    final HashMap<String,Bitmap> catimg = new HashMap<>();
    ArrayList<String> catpath = new ArrayList<>();
    Bitmap rotatedBitmap;
    Bitmap bitmap;
    ListView listView;
    Uri uri;
    Bitmap filemap;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cat);
        mImageView = (ImageView) findViewById(R.id.imageView);
        Button newbutton = (Button) findViewById(R.id.addnew);
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() !=null) {
            if (intent.getStringExtra("from").equals("gallery")) {
                try {
                    uri = Uri.parse(intent.getExtras().getString("img"));
                    setUriPic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                goToPhotos();
            }
        }

        assert intent != null;
        Bundle args = intent.getBundleExtra("bundle");
        catlist = (ArrayList<HashMap<String,String>>) args.getSerializable("cats");
        Bundle path = intent.getBundleExtra("path");
        catpath = (ArrayList<String>) path.getSerializable("path");
        listView = (ListView) findViewById(R.id.listView);

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

        listAdapter mlistAdapter = new listAdapter(catimg,catlist);

        listView.setAdapter(mlistAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                Intent intent = new Intent(AddCat.this, catInfo.class);
                intent.putExtra("name", name);
                intent.putExtra("info",info);
                //intent.putExtra("img", byteArray);
                intent.putExtra("from","AddCat");
                startActivityForResult(intent,Constants.SEND_ADD_CAT_RESULT);
            }
        });

        newbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(AddCat.this);
                alert.setTitle("What is its name?");

                final EditText input = new EditText(AddCat.this);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        name = input.getText().toString();
                        Intent backtomain = new Intent();
                        backtomain.putExtra("name", name);
                        setResult(RESULT_OK, backtomain);
                        finish();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();


            }
        });


    }

    private void setUriPic() throws IOException {

        mCurrentPhotoPath = getRealPathFromURI(uri);
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();
        int scaleFactor = 1;
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        if (targetW > 0 && targetH > 0) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        if (bitmap == null) {
            finish();
        }

        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
                break;
        }
        mImageView.setImageBitmap(rotatedBitmap);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void goToPhotos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Access to Camera", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            // Access to the location has been granted to the app.
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.user.catmap.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE) {
            try {
                setPic();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == Constants.SEND_ADD_CAT_RESULT) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra("result");
                catimg.put(name,rotatedBitmap);
                Intent backtomain = new Intent();
                backtomain.putExtra("name",name);
                setResult(RESULT_OK,backtomain);
                finish();
            }
        }
    }



    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goToPhotos();
                } else {
                    mPermissionDenied = true;
                }
            }
        }
    }

    private void setPic() throws IOException {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();
        int scaleFactor = 1;
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        if (targetW > 0 && targetH > 0) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        if (bitmap == null) {
            finish();
        }

        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
                break;
        }
        mImageView.setImageBitmap(rotatedBitmap);

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


}
