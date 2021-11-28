package org.telegram.ui.Components;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class FullEmojiView extends FrameLayout {

    FrameLayout bgView;
    BackupImageView imageView;
    BackupImageView effectsImageView;
    LinearLayout v;
    LinearLayout v2;


    public FullEmojiView(@NonNull Context context) {
        super(context);

        createBgView();
        createImageView();
    }

    private void createBgView() {
        bgView = new FrameLayout(getContext());
        bgView.setBackgroundColor(0x00000000);
        addView(bgView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    private void createImageView() {
        v = new LinearLayout(getContext());
        v.setLayoutParams(LayoutHelper.createFrame(200, 200, Gravity.CENTER));
        addView(v);

        v2 = new LinearLayout(getContext());
        addView(v2);

        imageView = new BackupImageView(getContext());
        imageView.setAspectFit(true);
        imageView.setLayerNum(2);
        imageView.imageReceiver.setAllowStartLottieAnimation(true);

        effectsImageView = new BackupImageView(getContext());
        effectsImageView.setAspectFit(true);
        effectsImageView.setLayerNum(2);
        effectsImageView.imageReceiver.setAllowStartLottieAnimation(true);
    }

    public void configure(EmojisScrollComponent.EmojisCell emojiView, FrameLayout emojiScroll, float statusBarHeight) {
        int[] loc = new int[2];
        emojiView.getLocationOnScreen(loc);

        TLRPC.Document select_animation = emojiView.reaction.activate_animation;
        SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(select_animation, Theme.key_windowBackgroundGray, 1.0f);
        effectsImageView.setImage(ImageLocation.getForDocument(emojiView.reaction.effect_animation), null, "webp", null, this);
        imageView.setImage(ImageLocation.getForDocument(select_animation), null, "webp", svgThumb, this);
        v.addView(imageView);
        float startWidth = emojiView.getWidth() * 1.02f;
        int startX = loc[0];
        int startY = (int)(loc[1] - statusBarHeight);
        v.setLayoutParams(LayoutHelper.createFrameWithoutDp((int)(startWidth), (int)(emojiView.getHeight() * 1.02f), Gravity.LEFT | Gravity.TOP, startX, startY, 0, 0));
        imageView.setAlpha(1);
        imageView.setVisibility(VISIBLE);

        float endSize = 0.48f * AndroidUtilities.getRealScreenSize().x;
        float endX = AndroidUtilities.getRealScreenSize().x * 0.5f - endSize * 0.5f;
        float endY = AndroidUtilities.getRealScreenSize().y * 0.5f - endSize * 0.5f;
        float endScale = endSize / (startWidth);
        float translationX = endX - startX;
        float translationY = endY - startY;

        float effectsImageWidth = 0.68f * AndroidUtilities.getRealScreenSize().x;
        float effectImageTopSpace = endY - (Math.abs(effectsImageWidth - endSize));

        //effectsImageView.setImage(ImageLocation.getForDocument(emojiView.reaction.effect_animation), null, "webp", svgThumb, this);
        v2.addView(effectsImageView);
        float v2LeftMargin = -effectsImageWidth * 0.1f;
        v2.setLayoutParams(LayoutHelper.createFrameWithoutDp((int)effectsImageWidth, (int)effectsImageWidth, Gravity.LEFT | Gravity.TOP, (int)v2LeftMargin, (int)effectImageTopSpace, 0, 0));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.playTogether(ObjectAnimator.ofFloat(v, View.SCALE_X, endScale),
                ObjectAnimator.ofFloat(v, View.SCALE_Y, endScale),
                ObjectAnimator.ofFloat(v, View.TRANSLATION_X, translationX),
                ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, translationY)
                );
        animatorSet.setDuration(700);

        imageView.imageReceiver.setZeroFrame();
        effectsImageView.imageReceiver.setZeroFrame();
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                emojiView.imageView.setAlpha(0);
                imageView.imageReceiver.startLottie();
                effectsImageView.imageReceiver.startLottie();
                animatorSet.start();

                return true;
            }
        });
    }

    void animateShowingUp() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 5f),
                ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 5f));
        animatorSet.setDuration(400);
        animatorSet.start();
    }
}
