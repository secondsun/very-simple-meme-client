package org.jboss.aerogear.verysimplememeclient;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.pipe.module.ModuleFields;
import org.jboss.aerogear.verysimplememeclient.vo.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by summers on 7/6/15.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Holder> {


    private final Context context;

    private final List<Post> posts = new ArrayList<>();
    private final Picasso picasso;

    public PostAdapter(ListActivity listActivity) {
        context = listActivity.getApplicationContext();

        OkHttpClient picassoClient = new OkHttpClient();

        picassoClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                ModuleFields fields = AuthorizationManager.getModule("KeyCloakAuthz").loadModule(null, null, null);
                Pair<String, String> header = fields.getHeaders().get(0);
                Request newRequest = chain.request().newBuilder()
                        .addHeader(header.first, header.second)
                        .build();
                return chain.proceed(newRequest);
            }
        });

        picasso = new Picasso.Builder(context).downloader(new OkHttpDownloader(picassoClient)).build();

        picasso.setIndicatorsEnabled(true);
        picasso.setLoggingEnabled(true);


    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int i) {
        Post post = posts.get(i);
        picasso.load(post.getFileUrl()).error(R.drawable.select_image).into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        ImageView image;

        public Holder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public void setImages(List<Post> newPosts) {
        posts.clear();
        posts.addAll(newPosts);
        this.notifyDataSetChanged();
    }

}
