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

public class FullEmojiDisappearView extends FrameLayout {
    LinearLayout v;
    BackupImageView imageView;
    float statusBarHeight;
    FullEmojiDisappearViewDelegate delegate;

    public FullEmojiDisappearView(@NonNull Context context) {
        super(context);
        createImageView();
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

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(3000);
        float b = 0;
        animator.addUpdateListener(valueAnimator -> {
            float progress = (float)valueAnimator.getAnimatedValue();
            final float TRANSLATION = 300;
            float trX = TRANSLATION * progress;
            float trY;
            float scale;
            float function1 = (-2 * progress * progress + 2.5f * progress);
            float function2 = (-2 * progress * progress + 2 * progress + 0.25f);
            float function3 = (-7 * progress * progress + 7 * progress);
            float direction = isShownInRight ? 1 : -1;
            if (progress < 0.5) {
                trY = function1 * TRANSLATION;
                scale = function1 + 1;
                animatorFunction1BeforeFiftyPercent = function1;
            } else {
                trY = function2 * TRANSLATION;
                scale = function3;
            }
            float rotation = 45 * animatorFunction1BeforeFiftyPercent * direction;
            float translationX = baseTranslationX - trX * direction;
            float translationY = baseTranslationY - trY;
            v.setTranslationX(translationX);
            v.setTranslationY(translationY);
            v.setRotation(-rotation);
            v.setScaleX(scale);
            v.setScaleY(scale);
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                if (delegate != null) {
                    delegate.onEnd();
                }
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        AndroidUtilities.runOnUIThread(() -> {
            animator.start();
            delegate.dimOriginal();
            delegate.shouldRemoveChosenReaction();
        }, 50);

//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.setDuration(500);
//        ValueAnimator animator = new ValueAnimator()
//        animatorSet.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//            }
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                if (delegate != null) {
//                    delegate.shouldRemoveChosenReaction();
//                }
//            }
//            @Override
//            public void onAnimationCancel(Animator animation) {
//            }
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//            }
//        });
//        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
//        animatorSet.playTogether(ObjectAnimator.ofFloat());
    }

    public interface FullEmojiDisappearViewDelegate {
        void shouldRemoveChosenReaction();
        void onEnd();
        void dimOriginal();
    }
}
