package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.CustomAnimatorListener;

public class FullEmojiDisappearView extends FrameLayout {
    LinearLayout v;
    BackupImageView imageView;
    float statusBarHeight;
    FullEmojiDisappearViewDelegate delegate;

    public FullEmojiDisappearView(@NonNull Context context) {
        super(context);
        createImageView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        v = null;
        imageView = null;
        delegate = null;
    }

    public void setDelegate(FullEmojiDisappearViewDelegate delegate) {
        this.delegate = delegate;
    }

    private void createImageView() {
        v = new LinearLayout(getContext());
        addView(v);

        imageView = new BackupImageView(getContext());
        imageView.setAspectFit(true);
        imageView.setLayerNum(2);
        v.addView(imageView);
        v.setAlpha(0);
    }

    public void configure(TLRPC.Document document, float statusBarHeight) {
        this.statusBarHeight = statusBarHeight;
        imageView.setImage(ImageLocation.getForDocument(document), null, "webp", null, this);
    }

    float animatorFunction1BeforeFiftyPercent;
    public void show(int[] coords, boolean isShownInRight) {
        float baseTranslationX = coords[2];
        float baseTranslationY = coords[3] - statusBarHeight;
        v.setLayoutParams(LayoutHelper.createFrameWithoutDp(coords[0], coords[1], Gravity.TOP | Gravity.LEFT, 0, 0, 0, 0));
        v.setTranslationX(baseTranslationX);
        v.setTranslationY(baseTranslationY);
        v.setAlpha(1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        final float direction = isShownInRight ? 1 : -1;
        final float TRANSLATION = AndroidUtilities.dpf2(109f);
        ValueAnimator animatorTranslation = ValueAnimator.ofFloat(0, 1);
        animatorTranslation.addUpdateListener(valueAnimator -> {
            float progress = (float)valueAnimator.getAnimatedValue();
            float trX = TRANSLATION * progress;
            float trY;
            float function1 = (-2 * progress * progress + 2.5f * progress);
            float function2 = (-2 * progress * progress + 2 * progress + 0.25f);
            if (progress < 0.5) {
                trY = function1 * TRANSLATION;
            } else {
                trY = function2 * TRANSLATION;
            }
            float translationX = baseTranslationX - trX * direction;
            float translationY = baseTranslationY - trY;
            v.setTranslationX(translationX);
            v.setTranslationY(translationY);
        });
        ValueAnimator animatorScale = ValueAnimator.ofFloat(0, 1);
        animatorScale.addUpdateListener(valueAnimator -> {
            float progress = (float)valueAnimator.getAnimatedValue();
            float scale;
            float function1 = (-2 * progress * progress + 2.5f * progress);
            float function3 = (-7 * progress * progress + 7 * progress);
            if (progress < 0.5) {
                scale = function1 + 1;
            } else {
                scale = function3;
            }
            v.setScaleX(scale);
            v.setScaleY(scale);
        });
        ValueAnimator animatorRotation = ValueAnimator.ofFloat(0, 1);
        animatorRotation.addUpdateListener(valueAnimator -> {
            float progress = (float)valueAnimator.getAnimatedValue();
            float function1 = (-2 * progress * progress + 2.5f * progress);
            animatorFunction1BeforeFiftyPercent = function1;
            float rotation = 45 * animatorFunction1BeforeFiftyPercent * direction;
            v.setRotation(-rotation);
        });

        animatorSet.playTogether(animatorTranslation, animatorScale, animatorRotation);
        animatorSet.setDuration(300);
        float b = 0;
//        animator.addUpdateListener(valueAnimator -> {
//            float progress = (float)valueAnimator.getAnimatedValue();
//            final float TRANSLATION = 300;
//            float trX = TRANSLATION * progress;
//            float trY;
//            float scale;
//            float function1 = (-2 * progress * progress + 2.5f * progress);
//            float function2 = (-2 * progress * progress + 2 * progress + 0.25f);
//            float function3 = (-7 * progress * progress + 7 * progress);
//            float direction = isShownInRight ? 1 : -1;
//            if (progress < 0.5) {
//                trY = function1 * TRANSLATION;
//                scale = function1 + 1;
//                animatorFunction1BeforeFiftyPercent = function1;
//            } else {
//                trY = function2 * TRANSLATION;
//                scale = function3;
//            }
//            float rotation = 45 * animatorFunction1BeforeFiftyPercent * direction;
//            float translationX = baseTranslationX - trX * direction;
//            float translationY = baseTranslationY - trY;
//            v.setTranslationX(translationX);
//            v.setTranslationY(translationY);
//            v.setRotation(-rotation);
//            v.setScaleX(scale);
//            v.setScaleY(scale);
//        });
        animatorSet.addListener(new CustomAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (delegate != null) {
                    delegate.onEnd();
                }
            }
        });
        AndroidUtilities.runOnUIThread(() -> {
            animatorSet.start();
            delegate.dimOriginal();
            delegate.shouldRemoveChosenReaction();
        }, 20);
    }

    public interface FullEmojiDisappearViewDelegate {
        void shouldRemoveChosenReaction();
        void onEnd();
        void dimOriginal();
    }
}
