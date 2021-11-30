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
import org.telegram.messenger.ImageReceiver;
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
        imageView.imageReceiver.setAllowDecodeSingleFrame(true);
        imageView.imageReceiver.setAllowStartLottieAnimation(true);

        effectsImageView = new BackupImageView(getContext());
        effectsImageView.setAspectFit(true);
        effectsImageView.setLayerNum(2);
        effectsImageView.imageReceiver.setAllowStartLottieAnimation(true);

        imageView.imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() {
            @Override
            public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setAlpha(1);
                        imageView.setVisibility(VISIBLE);
                        imageView.imageReceiver.startLottie();
                        effectsImageView.imageReceiver.startLottie();
                        animatorSet.start();
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                emojiView.imageView.setAlpha(0);
                            }
                        });
                    }
                }, 30);
            }
        });
    }


    EmojisScrollComponent.EmojisCell emojiView;
    AnimatorSet animatorSet;
    public void configure(EmojisScrollComponent.EmojisCell emojiView, FrameLayout emojiScroll, float statusBarHeight) {
        int[] loc = new int[2];
        this.emojiView = emojiView;
        emojiView.getLocationOnScreen(loc);

        v.addView(imageView);
        float startWidth = emojiView.getWidth() * 1.02f;
        int startX = loc[0];
        int startY = (int)(loc[1] - statusBarHeight);
        v.setLayoutParams(LayoutHelper.createFrameWithoutDp((int)(startWidth), (int)(emojiView.getHeight() * 1.02f), Gravity.LEFT | Gravity.TOP, startX, startY, 0, 0));

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

        animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.playTogether(ObjectAnimator.ofFloat(v, View.SCALE_X, endScale),
                ObjectAnimator.ofFloat(v, View.SCALE_Y, endScale),
                ObjectAnimator.ofFloat(v, View.TRANSLATION_X, translationX),
                ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, translationY)
                );
        animatorSet.setDuration(2000);

        imageView.imageReceiver.setZeroFrame();
        effectsImageView.imageReceiver.setZeroFrame();
        effectsImageView.setImage(ImageLocation.getForDocument(emojiView.reaction.effect_animation), null, "webp", null, this);
        imageView.setImage(ImageLocation.getForDocument(emojiView.reaction.activate_animation), null, "webp", null, this);
    }

    void animateShowingUp() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 5f),
                ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 5f));
        animatorSet.setDuration(2000);
        animatorSet.start();
    }
}
