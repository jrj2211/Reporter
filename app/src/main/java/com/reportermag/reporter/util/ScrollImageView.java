package com.reportermag.reporter.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ScrollImageView extends ImageView {

    private int width;
    private int height;
    private String URL;
    private DownloadImageTask task;

    public ScrollImageView(Context context) {
        super(context);
    }

    public ScrollImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImage(Bitmap bm, String URL) {
        if (this.URL.equals(URL)) {
            this.setImageBitmap(bm);
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        double ratio = (double) this.getMeasuredWidth() / (double) width;
        Double newHeight = Math.ceil((double) height * ratio);

        setMeasuredDimension(this.getMeasuredWidth(), newHeight.intValue());
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void downloadImage(String URL) {
        task = new DownloadImageTask(this);
        task.execute(URL);
        this.URL = URL;
    }
}
