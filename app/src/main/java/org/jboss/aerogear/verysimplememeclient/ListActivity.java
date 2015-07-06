package org.jboss.aerogear.verysimplememeclient;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.PipeManager;

import java.util.List;

public class ListActivity extends Activity {

    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        recycler = (RecyclerView) findViewById(R.id.recycler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recycler.setLayoutManager(new LinearLayoutManager( this ));
        PostAdapter adapter = new PostAdapter(this);
        recycler.setAdapter(adapter);
        loadImages(adapter);
    }

    private void loadImages(final PostAdapter adapter) {
        PipeManager.getPipe("kc-post", this).read(new Callback<List>() {
            @Override
            public void onSuccess(List list) {
                adapter.setImages(list);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                Toast.makeText(ListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
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
}
