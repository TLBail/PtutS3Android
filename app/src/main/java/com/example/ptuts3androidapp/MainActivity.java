package com.example.ptuts3androidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Rectangle;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.checkerframework.checker.units.qual.C;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;

public class MainActivity extends AppCompatActivity {

    private ImageView imgView;
    private ImageView imgGray;
    private TextView textView;

    private String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    private int permCode = 23;

    private TessOCR ocr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        imgGray = findViewById(R.id.imgGray);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},permCode);
        }

        ocr = null;
        try {
            ocr = new TessOCR(getAssets().open("fra.traineddata"));
    } catch (IOException e) {
            e.printStackTrace();
        }
        File dir = new File(Environment.getExternalStorageDirectory() + "/Citydex/images");
        if (!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Citydex/images/image_travers_crop.png");
        try {
            copy(getAssets().open("image_travers_crop.png") ,file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

        Bitmap bitmap_gray = toGrayscale(bitmap);

       // bitmap_gray = cropImage(bitmap_gray, new Rect(0,0,500,200));

        imgGray.setImageBitmap(bitmap_gray);

        textView.setText(ocr.getOCRResult(bitmap_gray));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void copy(InputStream in, File dst) throws IOException {
        try (OutputStream out = new FileOutputStream(dst)) {
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ocr.onDestroy();
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {

        //Permet d'enlever les pixels de "couleurs"
        bmpOriginal = bmpOriginal.copy(Bitmap.Config.ARGB_8888 , true);

        /*for (int i = 0; i < bmpOriginal.getWidth(); i++){
            for(int j = 0; j < bmpOriginal.getHeight(); j++){
                    int c = bmpOriginal.getPixel(i,j);
                    if(Color.red(c) > 100 || Color.blue(c) > 100 || Color.green(c) > 100){
                        bmpOriginal.setPixel(i,j,Color.rgb(255,255,255));
                    }
            }
        }*/

        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    private Bitmap cropImage(Bitmap src, Rect rect) {
        Bitmap dest = src.createBitmap(src, rect.left, rect.top, rect.width(), rect.height());
        return dest;
    }

}