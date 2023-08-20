package com.pilling.app;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;


public class TakePicActivity extends AppCompatActivity {
    ImageView imageView;
    File image;
    File imageDir;
    Bitmap bitmap;
    File file;
    String KakaoId;
    Uri photoURI;
    Intent takePictureIntent;
    String currentPhotoPath;
    String imageName;
    private static final String TAG = "TakePicActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.imageView = findViewById(R.id.image);
        Intent intent = getIntent();
        this.KakaoId = intent.getStringExtra("kakaoId");

        dispatchTakePictureIntent();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        this.imageName = "JPEG_" + timeStamp + ".jpg";
        image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                getCacheDir()      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.pilling.app.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                rotatedImage(bitmap);
                imageView.setImageBitmap(bitmap);
                saveImg();
                uploadImage(lobbyActivity.IMAGE_URL);
                finish();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private Bitmap rotatedImage(Bitmap bitmap) throws IOException {

        ExifInterface exif = new ExifInterface(currentPhotoPath);

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Bitmap rotateBitmap = null;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            rotateBitmap = rotateImage(bitmap, 90);
            System.out.println("90도 돌음*********");
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotateBitmap = rotateImage(bitmap, 180);
            System.out.println("180도 돌음*********");
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotateBitmap = rotateImage(bitmap, 270);
            System.out.println("270도 돌음*********");
        }
        this.bitmap = rotateBitmap;

        return null;

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void saveImg() {
        try {
            File storageDir = new File(getFilesDir() + "/capture");
            if (!storageDir.exists()) //폴더가 없으면 생성.
                storageDir.mkdirs();
            File file = image;
            System.out.println("file : " + file);
            boolean deleted = file.delete();

            FileOutputStream output = null;
            imageDir = new File(storageDir, imageName);
            try {
                output = new FileOutputStream(imageDir);
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                System.out.println("imageDri : " + imageDir);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output); //해상도에 맞추어 Compress
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert output != null;
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(this, "Capture Saved ", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();

        }
    }

    private void uploadImage(String url) {     // https://opheliesaysone.tistory.com/30
        Log.d(TAG,"kakaoID : " + KakaoId);
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"),KakaoId);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageDir);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("image", imageName, requestBody);
//        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), getFilesDir() + "/capture"+imageName);
//        MultipartBody.Part parts = MultipartBody.Part.createFormData("image", imageName,requestBody);
        System.out.println("업로드이미지 들어옴");
        // Retrofit 객체를 생성하고 이 객체를 이용해서, API service 를 create 해준다.
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        MyApi myAPI = retrofit.create(MyApi.class);
        System.out.println("title : + "+title+"requestBody  : "+requestBody);
        // post 한다는 request를 보내는 부분.
        Call<ResponseBody> call = myAPI.post_posts(title, parts);
        // 만약 서버로 부터 response를 받는다면.
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d(TAG,"등록 완료");
                }else {
                    Log.d(TAG,"Post Status Code : " + response.code());
                    Log.d(TAG,response.errorBody().toString());
                    Log.d(TAG,call.request().body().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"Fail msg : " + t.getMessage());

            }
        });
    }


}