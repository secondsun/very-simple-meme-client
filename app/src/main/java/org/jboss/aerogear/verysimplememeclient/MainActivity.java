package org.jboss.aerogear.verysimplememeclient;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;
import org.jboss.aerogear.verysimplememeclient.auth.KeycloakHelper;
import org.jboss.aerogear.verysimplememeclient.vo.Meme;

import java.io.ByteArrayOutputStream;

public class MainActivity extends Activity {

    private static final int REQ_CODE_PICK_IMAGE = 0x2109;

    private ImageView image;
    private String fileUrl;
    private EditText topComment;
    private EditText bottomComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        (image = (ImageView) findViewById(R.id.image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicker();
            }
        });

        bottomComment = (EditText) findViewById(R.id.bottomComment);
        topComment= (EditText) findViewById(R.id.topComment);

    }

    private void showImagePicker() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQ_CODE_PICK_IMAGE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!KeycloakHelper.isConnected()) {
            KeycloakHelper.connect(this, new Callback() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(MainActivity.this, "Login Worked", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("LOGIN", e.getMessage(), e);
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void submit() {
        Meme meme = new Meme();
        meme.setTopComment(topComment.getText().toString());
        meme.setBottomComment(bottomComment.getText().toString());



        image.setDrawingCacheEnabled(true);

        image.buildDrawingCache();

        Bitmap bm = image.getDrawingCache();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        meme.setImage(byteArray);
        PipeManager.getPipe("kc-upload").save(meme, new Callback() {
            @Override
            public void onSuccess(Object o) {
                Log.d("TAG", new Gson().toJson(o));
                Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("TAG", e.getMessage(), e);
                Toast.makeText(MainActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
            }
        });
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

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case REQ_CODE_PICK_IMAGE:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();

                    Picasso.with(this).load(selectedImage).into(image);

                }
        }
    }

}

