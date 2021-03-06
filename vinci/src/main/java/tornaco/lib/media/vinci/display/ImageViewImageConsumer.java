package tornaco.lib.media.vinci.display;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import tornaco.lib.media.vinci.effect.Animator;

/**
 * Created by Nick on 2017/5/5 12:39
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class ImageViewImageConsumer extends ImageConsumer {

    private ImageView imageView;

    public ImageViewImageConsumer(@NonNull ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void accept(final Bitmap image) {
        if (image != null) {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(null);
                    imageView.setImageResource(0);
                    imageView.setImageDrawable(new BitmapDrawable(imageView.getResources(), image));
                }
            });
        }
    }

    @Override
    public void applyAnimator(final Animator animator) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.startAnimation(animator.getAnimation());
            }
        });
    }

    @Override
    public String identify() {
        return "IMAGE_VIEW@" + imageView.hashCode();
    }
}
