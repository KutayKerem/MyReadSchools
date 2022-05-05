package com.kutaykerem.myreadschools;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kutaykerem.myreadschools.databinding.ActivityDetailsBinding;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {




 ActivityResultLauncher<Intent> activityResultLauncher;
 ActivityResultLauncher<String> activitypermissiontLauncher;
 Bitmap selectedimage;
 SQLiteDatabase database;



    private ActivityDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);

        registerLauncher();


        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if(info.matches("new")){
            binding.name.setText("");
            binding.note.setText("");
            binding.location.setText("");
            binding.year.setText("");
            binding.save.setVisibility(View.VISIBLE);

            Bitmap selectImage = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.image);
            binding.imageView.setImageBitmap(selectImage);
        }else{
            int artId = intent.getIntExtra("artId",1);
            binding.save.setVisibility(View.INVISIBLE);

            try{

                Cursor cursor = database.rawQuery("SELECT * FROM arts WHERE id = ? ",new String[] {String.valueOf(artId)});

                int nameIx = cursor.getColumnIndex("name");
                int noteIx = cursor.getColumnIndex("note");
                int locationIx = cursor.getColumnIndex("location");
                int yearIx = cursor.getColumnIndex("year");
                int imageIx = cursor.getColumnIndex("image");

                while(cursor.moveToNext()){

                    binding.name.setText(cursor.getString(nameIx));
                    binding.note.setText(cursor.getString(noteIx));
                    binding.location.setText(cursor.getString(locationIx));
                    binding.year.setText(cursor.getString(yearIx));

                    byte[] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView.setImageBitmap(bitmap);


                }

                cursor.close();

            } catch (Exception e){
                e.printStackTrace();
            }


        }





  }



  public void save(View view){

        String name = binding.name.getText().toString();
        String year = binding.year.getText().toString();
        String location = binding.location.getText().toString();
        String note = binding.note.getText().toString();

        Bitmap fotoboyutu = fotoboyutu(selectedimage,300);

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      fotoboyutu.compress(Bitmap.CompressFormat.PNG,50,outputStream);
      byte[] byteArray = outputStream.toByteArray();

      try{

          database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
          database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY, name VARCHAR,year VARCHAR,location VARCHAR, note VARCHAR,image BLOB)");

          String sqlString = "INSERT INTO arts (name,year,location,note,image) VALUES(?,?,?,?,?)";

          SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
          sqLiteStatement.bindString(1,name);
          sqLiteStatement.bindString(2,year);
          sqLiteStatement.bindString(3,location);
          sqLiteStatement.bindString(4,note);
          sqLiteStatement.bindBlob(5,byteArray);
          sqLiteStatement.execute();



      }catch (Exception e ){
          e.printStackTrace();
      }


      Intent intent = new Intent(DetailsActivity.this,MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);


  }



  public Bitmap fotoboyutu(Bitmap image, int maximumSize){


          int width = image.getWidth();
          int height = image.getHeight();

          float bitmapRatio = (float) width / (float) height;

          if (bitmapRatio > 1) {
              width = maximumSize;
              height = (int) (width / bitmapRatio);
          } else {
              height = maximumSize;
              width = (int) (height * bitmapRatio);
          }

          return Bitmap.createScaledBitmap(image,width,height,true);
      }


public void imageview(View view){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                Snackbar.make(view,"Please allow to choose photos",Snackbar.LENGTH_INDEFINITE).setAction("Allow", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activitypermissiontLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();


            }else{
                activitypermissiontLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);


            }

        } else{
            Intent intentTogalery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentTogalery);
        }

}





public void registerLauncher(){



        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent cevaptangelen = result.getData();
                    if ( cevaptangelen != null){
                        Uri imagedata = cevaptangelen.getData();

                        try{

                            if(Build.VERSION.SDK_INT >= 28){
                                ImageDecoder.Source sourcee = ImageDecoder.createSource(getContentResolver(),imagedata);
                                selectedimage = ImageDecoder.decodeBitmap(sourcee);
                                binding.imageView.setImageBitmap(selectedimage);
                            } else{

                                selectedimage = MediaStore.Images.Media.getBitmap(getContentResolver(),imagedata);
                                binding.imageView.setImageBitmap(selectedimage);

                            }

                     } catch (Exception e){
                            e.printStackTrace();
                        }


                    }

                }

            }
        });




        activitypermissiontLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intentTogalery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentTogalery);

                    // İzin verildi
                }else{
                    Toast.makeText(DetailsActivity.this,"İzin Verilmiyor...",Toast.LENGTH_LONG).show();


                    // İzin verilmedi
                }
            }
        });






}

}