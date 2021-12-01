package org.telegram.ui.Components;

import android.animation.Animator;
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
import org.telegram.ui.Cells.ChatMessageCell;

public class FullEmojiView extends FrameLayout {

    FrameLayout bgView;
    BackupImageView imageView;
    BackupImageView effectsImageView;
    BackupImageView staticImageView;
    LinearLayout v;
    LinearLayout v2;
    LinearLayout v3;
    FullEmojiViewDelegate delegate;

    public FullEmojiView(@NonNull Context context) {
        super(context);

        createBgView();
        createImageView();
    }

    public void setDelegate(FullEmojiViewDelegate delegate) {
        this.delegate = delegate;
    }

    private void createBgView() {
        bgView = new FrameLayout(getContext());
        bgView.setBackgroundColor(0x00000000);
        addView(bgView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    private void createImageView() {
        v = new LinearLayout(getContext());

        v3 = new LinearLayout(getContext());
        addView(v3);

        v.setLayoutParams(LayoutHelper.createFrame(200, 200, Gravity.CENTER));
        addView(v);

        v2 = new LinearLayout(getContext());
        addView(v2);


        imageView = new BackupImageView(getContext());
        imageView.setAspectFit(true);
        imageView.setLayerNum(2);
        imageView.imageReceiver.setAllowDecodeSingleFrame(true);
        imageView.imageReceiver.setAllowStartLottieAnimation(false);
        imageView.imageReceiver.setAutoRepeat(0);

        effectsImageView = new BackupImageView(getContext());
        effectsImageView.setAspectFit(true);
        effectsImageView.setLayerNum(2);
        effectsImageView.imageReceiver.setAllowStartLottieAnimation(false);
        effectsImageView.imageReceiver.setAutoRepeat(0);

        staticImageView = new BackupImageView(getContext());
        staticImageView.setAspectFit(true);
        staticImageView.setLayerNum(2);

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
    float endSize;
    float endX;
    float endY;
    float endScale;
    float translationX;
    float translationY;
    int startX;
    int startY;
    float startWidth;
    float startHeight;
    int[] endLocation;
    float statusBarHeight;
    TLRPC.Document staticIcon;

    public void configure(EmojisScrollComponent.EmojisCell emojiView, FrameLayout emojiScroll, float statusBarHeight) {
        this.staticIcon = emojiView.reaction.static_icon;

        int[] loc = new int[2];
        this.emojiView = emojiView;
        emojiView.getLocationOnScreen(loc);
        this.statusBarHeight = statusBarHeight;

        v.addView(imageView);
        startWidth = emojiView.getWidth() * 1.02f;
        startHeight = emojiView.getHeight() * 1.02f;
        startX = loc[0];
        startY = (int)(loc[1] - statusBarHeight);
        v.setLayoutParams(LayoutHelper.createFrameWithoutDp((int)(startWidth), (int)(startHeight), Gravity.LEFT | Gravity.TOP, startX, startY, 0, 0));

        endSize = 0.48f * AndroidUtilities.getRealScreenSize().x;
        endX = AndroidUtilities.getRealScreenSize().x * 0.5f - endSize * 0.5f;
        endY = AndroidUtilities.getRealScreenSize().y * 0.5f - endSize * 0.5f;
        endScale = endSize / (startWidth);
        translationX = endX - startX;
        translationY = endY - startY;

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
        ImageReceiver.ImageReceiverDelegate delegate = new ImageReceiver.ImageReceiverDelegate() {
            @Override
            public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
                imageReceiver.startLottie();
            }
        };

        imageView.imageReceiver.setDelegate(delegate);
        effectsImageView.imageReceiver.setDelegate(delegate);
    }

    public void disappear(int[] endLocation) {
//        View v4 = new View(getContext());
//        v4.setBackgroundColor(0x99763961);
//        v4.setLayoutParams(LayoutHelper.createFrameWithoutDp(endLocation[0], endLocation[1], Gravity.TOP | Gravity.LEFT, endLocation[2], endLocation[3] - (int)statusBarHeight, 0, 0));
//        addView(v4);

        v3.addView(staticImageView);
        int v3Size = AndroidUtilities.dp(ChatMessageCell.reactionSmallImageSize);
        int v3Left = (int)endX + v3Size;
        int v3Top = (int)endY + v3Size;
        v3.setLayoutParams(LayoutHelper.createFrameWithoutDp(v3Size, v3Size, Gravity.TOP | Gravity.LEFT, v3Left, v3Top, 0, 0));
        staticImageView.setImage(ImageLocation.getForDocument(staticIcon), null, "webp", null, this);
        float scaleV3 = endSize / v3Size;
        v3.setScaleX(scaleV3);
        v3.setScaleY(scaleV3);
        v3.setAlpha(0);

        endLocation[3] -= (int)statusBarHeight;
        this.endLocation = endLocation;

        float translationX = endLocation[2] - startX - endLocation[0];
        float translationY = endLocation[3] - startY - endLocation[1];
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.setDuration(2000);

        float endScale = (endLocation[0] * 1.5f) / startWidth;
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(v, View.TRANSLATION_X, translationX),
                ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, translationY),
                ObjectAnimator.ofFloat(v, View.SCALE_X, endScale),
                ObjectAnimator.ofFloat(v, View.SCALE_Y, endScale),
                ObjectAnimator.ofFloat(v3, View.SCALE_X, 1.0f),
                ObjectAnimator.ofFloat(v3, View.SCALE_Y, 1.0f),
                ObjectAnimator.ofFloat(v3, View.TRANSLATION_X, endLocation[2] - v3Left),
                ObjectAnimator.ofFloat(v3, View.TRANSLATION_Y, endLocation[3] - v3Top)
                );
        animatorSet.start();
        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet2.setDuration(500);
        animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet2.playTogether(
                ObjectAnimator.ofFloat(v, View.ALPHA, 0),
                ObjectAnimator.ofFloat(v3, View.ALPHA, 1)
        );
        animatorSet2.setStartDelay(1500);
        animatorSet2.start();
        animatorSet2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                if (delegate != null) {
                    delegate.finishedAnimating();
                }
                v3.setAlpha(1);
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    void animateShowingUp() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 5f),
                ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 5f));
        animatorSet.setDuration(2000);
        animatorSet.start();
    }

    @FunctionalInterface
    public interface FullEmojiViewDelegate {
        void finishedAnimating();
    }
}
