package org.telegram.ui.Components;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
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
    LinearLayout v;


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
        imageView = new BackupImageView(getContext());
        imageView.setAspectFit(true);
        imageView.setLayerNum(1);
        imageView.imageReceiver.setAllowStartLottieAnimation(false);


        v = new LinearLayout(getContext());
        v.setLayoutParams(LayoutHelper.createFrame(200, 200, Gravity.CENTER));
        v.setBackgroundColor(0x11321298);
        addView(v);
    }

    public void configure(FrameLayout emojiView, FrameLayout emojiScroll, float statusBarHeight) {
        int[] loc = new int[2];
        emojiView.getLocationOnScreen(loc);

        EmojisScrollComponent.EmojisCell emojisCell = (EmojisScrollComponent.EmojisCell) emojiView;
        TLRPC.Document select_animation = emojisCell.select_animation;
        SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(select_animation, Theme.key_windowBackgroundGray, 1.0f);
        imageView.setImage(ImageLocation.getForDocument(select_animation), "66_66", null, svgThumb, this);
        v.setLayoutParams(LayoutHelper.createFrameWithoutDp(emojiView.getWidth(), emojiView.getHeight(), Gravity.LEFT | Gravity.TOP, loc[0], (int) (loc[1] - statusBarHeight), 0, 0));
        v.addView(imageView, LayoutHelper.createFrame(80, 80, Gravity.CENTER, 1, 1, 0, 0));
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                emojisCell.imageView.setVisibility(GONE);
                animateShowingUp();
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
