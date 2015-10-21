/*
*  Copyright (C) 2015 TzuTaLin
*/

package com.tzutalin.dlibtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.provider.BigImageCardProvider;
import com.dexafree.materialList.view.MaterialListView;
import com.tzutalin.dlib.PeopleDet;
import com.tzutalin.dlib.VisionDetRet;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMG = 1;
    private static final String TAG = "MainActivity";
    // UI
    MaterialListView mListView;

    private String mTestImgPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (MaterialListView) findViewById(R.id.material_listview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Pick one image", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        if (mTestImgPath != null) {
            DetTask task = new DetTask();
            task.execute(mTestImgPath);
        } else {
            // Create intent to Open Image applications like Gallery, Google Photos
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
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


    // ==========================================================
    // Tasks inner class
    // ==========================================================
    private class DetTask extends AsyncTask<String, Void, List<Card>> {
        private ProgressDialog mmDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mmDialog = ProgressDialog.show(MainActivity.this, "Wait", "Person and face detection", true);
        }

        @Override
        protected List<Card> doInBackground(String... strings) {
            String path = strings[0];
            Log.d(TAG, "Image path: " + path);
            List<Card> cardrets = new ArrayList<>();
            PeopleDet peopleDet = new PeopleDet();
            List<VisionDetRet> personList = peopleDet.detPerson(path);
            if (personList.size() > 0) {
                Card card = new Card.Builder(MainActivity.this)
                        .withProvider(BigImageCardProvider.class)
                        .setDrawable(drawRect(path, personList, Color.BLUE))
                        .setTitle("Person det")
                        .endConfig()
                        .build();
                cardrets.add(card);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "No person", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (path.endsWith("bmp") == true) {
                List<VisionDetRet> faceList = peopleDet.detFace(path);
                if (faceList.size() > 0) {
                    Card card = new Card.Builder(MainActivity.this)
                            .withProvider(BigImageCardProvider.class)
                            .setDrawable(drawRect(path, faceList, Color.GREEN))
                            .setTitle("Face det")
                            .endConfig()
                            .build();
                    cardrets.add(card);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No face", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Only support BMP now", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return cardrets;
        }

        @Override
        protected void onPostExecute(List<Card> rets) {
            super.onPostExecute(rets);
            if (mmDialog != null) {
                mmDialog.dismiss();
            }
            for (Card each : rets) {
                mListView.add(each);
            }
        }
    }

    @DebugLog
    private BitmapDrawable drawRect(String path, List<VisionDetRet> retlist, int color) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        android.graphics.Bitmap.Config bitmapConfig = bm.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bm = bm.copy(bitmapConfig, true);


        int width = bm.getWidth();
        int height = bm.getHeight();
        // By ratio scale
        float aspectRatio = bm.getWidth() /(float) bm.getHeight();

        final int MAX_SIZE = 512;
        int newWidth = MAX_SIZE;
        int newHeight = MAX_SIZE;
        float resizeRatio = 1;
        newHeight = Math.round(newWidth / aspectRatio);
        if (bm.getWidth() > MAX_SIZE && bm.getHeight() > MAX_SIZE) {
            Log.d(TAG, "Resize Bitmap");
            bm = getResizedBitmap(bm, newWidth, newHeight);
            resizeRatio = (float)bm.getWidth() / (float)width;
            Log.d(TAG, "resizeRatio " + resizeRatio);
        }

        // Create canvas to draw
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        // Loop result list
        for (VisionDetRet ret : retlist) {
            Rect bounds = new Rect();
            bounds.left = (int) (ret.getLeft() * resizeRatio);
            bounds.top = (int) (ret.getTop() * resizeRatio);
            bounds.right = (int) (ret.getRight() * resizeRatio);
            bounds.bottom = (int) (ret.getBottom() * resizeRatio);
            canvas.drawRect(bounds, paint);
        }

        return new BitmapDrawable(getResources(), bm);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mTestImgPath = cursor.getString(columnIndex);
                cursor.close();
                if (mTestImgPath != null) {
                    DetTask task = new DetTask();
                    task.execute(mTestImgPath);
                    Toast.makeText(this, "Img Path:" + mTestImgPath, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    @DebugLog
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return resizedBitmap;
    }
}
