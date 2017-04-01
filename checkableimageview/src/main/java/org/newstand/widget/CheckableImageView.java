package org.newstand.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

public class CheckableImageView extends ImageView implements Checkable {
    private boolean mChecked = false;
    private int mCheckMarkBackgroundColor;
    private CheckableFlipDrawable mDrawable;

    public CheckableImageView(Context context) {
        super(context);
        init(context, null);
    }

    public CheckableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CheckableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckableImageView(Context context, AttributeSet attrs,
                              int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setCheckMarkBackgroundColor(context.getResources().getColor(R.color.def_checked_mark_background_color));
    }

    public void setCheckMarkBackgroundColor(int color) {
        mCheckMarkBackgroundColor = color;
        if (mDrawable != null) {
            mDrawable.setCheckMarkBackgroundColor(color);
        }
    }

    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        setChecked(checked, true);
    }

    public void setChecked(boolean checked, boolean animate) {
        if (mChecked == checked) {
            return;
        }

        mChecked = checked;
        applyCheckState(animate);
    }

    @Override
    public void setImageDrawable(Drawable d) {
        if (d != null) {
            if (mDrawable == null) {
                mDrawable = new CheckableFlipDrawable(d, getResources(),
                        mCheckMarkBackgroundColor, 150);
                applyCheckState(false);
            } else {
                mDrawable.setFront(d);
            }
            d = mDrawable;
        }
        super.setImageDrawable(d);
    }

    private void applyCheckState(boolean animate) {
        mDrawable.flipTo(!mChecked);
        if (!animate) {
            mDrawable.reset();
        }
    }
}