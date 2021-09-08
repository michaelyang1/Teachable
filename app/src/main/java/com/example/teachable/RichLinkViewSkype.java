package com.example.teachable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.teachable.R;
import com.squareup.picasso.Picasso;

import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichLinkListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

/**
 * Created by ponna on 16-01-2018.
 */

public class RichLinkViewSkype extends RelativeLayout {

    private View view;
    Context context;
    private MetaData meta;
    private Boolean onError = false;

    RelativeLayout relativeLayout;
    ImageView imageView;
    ImageView imageViewFavIcon;
    TextView textViewTitle;
    TextView textViewDesp;
    TextView textViewUrl;

    private String main_url;

    private boolean isDefaultClick = true;

    private RichLinkListener richLinkListener;


    public RichLinkViewSkype(Context context) {
        super(context);
        this.context = context;
    }

    public RichLinkViewSkype(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public RichLinkViewSkype(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RichLinkViewSkype(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    public void initView() {
        if(findRelativeLayoutChild() != null) {
            this.view = findRelativeLayoutChild();
        } else  {
            this.view = this;
            inflate(context, R.layout.skype_link_layout,this);
        }

        relativeLayout = (RelativeLayout) findViewById(R.id.rich_link_card);
        imageView = (ImageView) findViewById(R.id.rich_link_image);
        imageViewFavIcon = (ImageView) findViewById(R.id.rich_link_favicon);
        textViewTitle = (TextView) findViewById(R.id.rich_link_title);
        textViewDesp = (TextView) findViewById(R.id.rich_link_desp);
        textViewUrl = (TextView) findViewById(R.id.rich_link_url);
    }

    private void setData() {
        Log.e("RichLinkSkype", "Title: " + meta.getTitle());
        Log.e("RichLinkSkype", "Description: " + meta.getDescription());
        Log.e("RichLinkSkype", "Favicon: " + meta.getFavicon());
        Log.e("RichLinkSkype", "Image URL: " + meta.getImageurl());
        Log.e("RichLinkSkype", "Sitename: " + meta.getSitename());
        Log.e("RichLinkSkype", "URL: " + meta.getUrl());
        if (((Activity) context).isFinishing()) { // fix this later
            Log.e("Debug", "context is null");
        }
        if(meta.getImageurl().equals("") || meta.getImageurl().isEmpty()) {
            imageView.setVisibility(GONE);
        } else {
            Log.e("RichLinkSkype", "VISIBLE");
            imageView.setVisibility(VISIBLE);
            Glide.with(this).load(meta.getImageurl()).into(imageView);
//            Picasso.get()
//                    .load(meta.getImageurl())
//                    .into(imageView);
        }

        if(meta.getFavicon().equals("") || meta.getFavicon().isEmpty()) {
            imageViewFavIcon.setVisibility(GONE);
        } else {
            imageViewFavIcon.setVisibility(VISIBLE);
//            Picasso.get()
//                    .load(meta.getFavicon())
//                    .into(imageViewFavIcon);
            Glide.with(this).load(meta.getFavicon()).into(imageViewFavIcon);
        }

        if(meta.getTitle().isEmpty() || meta.getTitle().equals("")) {
            textViewTitle.setVisibility(GONE);
        } else {
            textViewTitle.setVisibility(VISIBLE);
            textViewTitle.setText(meta.getTitle());
        }
        if(meta.getUrl().isEmpty() || meta.getUrl().equals("")) {
            textViewUrl.setVisibility(GONE);
        } else {
            textViewUrl.setVisibility(VISIBLE);
            textViewUrl.setText(meta.getUrl());
        }
        if(meta.getDescription().isEmpty() || meta.getDescription().equals("")) {
            textViewDesp.setVisibility(GONE);
        } else {
            textViewDesp.setVisibility(VISIBLE);
            textViewDesp.setText(meta.getDescription());
        }


        relativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("RichLinkSkype", meta.getUrl());
                if(isDefaultClick) {
                    richLinkClicked();
                } else {
                    if(richLinkListener != null) {
                        richLinkListener.onClicked(view, meta);
                    } else {
                        richLinkClicked();
                    }
                }
            }
        });
    }

    private void richLinkClicked() {
        Log.e("richLinkClickked", meta.getUrl());
        if (meta.getUrl().isEmpty()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(meta.getUrl()));
        context.startActivity(intent);
    }

    protected RelativeLayout findRelativeLayoutChild() {
        if (getChildCount() > 0 && getChildAt(0) instanceof LinearLayout) {
            return (RelativeLayout) getChildAt(0);
        }
        return null;
    }

    public void setLinkFromMeta(MetaData metaData) {
        if (imageView == null) {
            Log.e("RichLinkTypeDebug", "imageview is null");
            initView();
        } else {
            Log.e("RichLinkTypeDebug", "imageview is not null");
            createBlankView();
        }
        meta = metaData;
//        initView();
        setData();
    }

    public MetaData getMetaData() {
        return meta;
    }

    public boolean getOnError() { return onError;}


    public void setDefaultClickListener(boolean isDefault) {
        isDefaultClick = isDefault;
    }

    public void setClickListener(RichLinkListener richLinkListener1) {
        richLinkListener = richLinkListener1;
    }

    private void createBlankView() {
//        relativeLayout.setVisibility(GONE);
        imageView.setVisibility(GONE);
        imageViewFavIcon.setVisibility(GONE);
        textViewTitle.setVisibility(GONE);
        textViewUrl.setVisibility(GONE);
        textViewDesp.setVisibility(GONE);
    }


    public void setLink(String url, final ViewListener viewListener) {
        Log.e("Special", "setLinkCalled");
        if (imageView == null) {
            Log.e("Special", "imageview is null");
            initView();
        } else {
            createBlankView();
            Log.e("Special", "imageview is not null");
        }
        main_url = url;
        RichPreview richPreview = new RichPreview(new ResponseListener() {
            @Override
            public void onData(MetaData metaData) {
                meta = metaData;

//                if(meta.getTitle().isEmpty() || meta.getTitle().equals("")) {
//                    viewListener.onSuccess(true);
//                }
                if(!meta.getTitle().isEmpty()) {
                    viewListener.onSuccess(true);
                }

                setData();
            }

            @Override
            public void onError(Exception e) {
                viewListener.onError(e);
            }
        });
        Log.e("RichLinkSkype", "WILL THIS WORK");
        richPreview.getPreview(url);
    }



    public void setLinkTest(final String url, final ViewListener viewListener) {
        com.example.teachable.RichPreview richPreview = new com.example.teachable.RichPreview(new ResponseListener() {
            @Override
            public void onData(MetaData metaData) {
                meta = metaData;
                meta.setUrl(url);
                Log.e("zillow", metaData.getImageurl());
                Log.e("zillow", metaData.getTitle());
                Log.e("zillow", metaData.getUrl());
                if(!(meta.getTitle().isEmpty()) || !(meta.getImageurl().isEmpty())) {
                    viewListener.onSuccess(true);
                }
            }

            @Override
            public void onError(Exception e) {
                viewListener.onError(e);
            }
        });
        richPreview.getPreview(url);
    }

}