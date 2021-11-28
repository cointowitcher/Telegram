package org.telegram.ui.Components;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.StickerEmojiCell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class EmojisScrollComponent extends FrameLayout {
    private Theme.ResourcesProvider resourcesProvider;
    private View backgroundView;
    private HorizontalScrollView scrollView;
    private LinearLayout linearLayoutScroll;
    private ArrayList<EmojisCell> cells;
    private ArrayList<TLRPC.TL_availableReaction> reactions;
    private int type = 0;
    private Handler handler;
    private OnClickListenerx callback;

    public EmojisScrollComponent(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        cells = new ArrayList<>();
        reactions = new ArrayList<>();
        handler = new Handler();
        this.callback = (v) -> {};
        setup();
    }

    public void setupOnClickListener(OnClickListenerx callback) {
        this.callback = callback;
    }

    private void setup() {
        this.backgroundView = new View(getContext());
        this.backgroundView.setBackgroundColor(Color.WHITE);
        addView(backgroundView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        scrollView = new HorizontalScrollView(getContext());
        addView(scrollView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        linearLayoutScroll = new LinearLayout(getContext());
        scrollView.addView(linearLayoutScroll, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT));
    }

    public void addItems(ArrayList<TLRPC.TL_availableReaction> reactions) {
        if (reactions.size() == cells.size()) {
            return;
        }
        //wqe
        this.reactions = reactions;
        cells = new ArrayList<>();
        linearLayoutScroll.removeAllViews();
        for(int i = 0; i < reactions.size(); i++) {
            EmojisCell emojisCell = new EmojisCell(getContext(), i == 0 ? 0 : 2.5f);
            emojisCell.configure(reactions.get(i).select_animation);
            emojisCell.setOnClickListener(v -> {
                callback.selected((FrameLayout) v);
            });
//            StickerEmojiCell cell = new StickerEmojiCell(getContext(), false);
//            cell.imageView.imageReceiver.setAutoRepeat(0);
//            cell.imageView.imageReceiver.setAllowStartAnimation(false);
//            cell.imageView.imageReceiver.setAllowStartLottieAnimation(false);
//            cell.setSticker(reactions.get(i).select_animation, null, false);
            linearLayoutScroll.addView(emojisCell);
            cells.add(emojisCell);
        }
    }

    public void animateAppearing(long duration) {
        backgroundView.setLayoutParams(LayoutHelper.createFrame((int)(getLayoutParams().width * 0.1f), getLayoutParams().height, Gravity.RIGHT));
        scrollView.setLayoutParams(LayoutHelper.createFrame((int)(getLayoutParams().width * 0.1f), getLayoutParams().height, Gravity.RIGHT));
        ValueAnimator widthAnimator = ValueAnimator.ofInt((int)(getLayoutParams().width * 0.1f), getLayoutParams().width).setDuration(duration);
        widthAnimator.addUpdateListener(animation -> {
            Integer value = (Integer) animation.getAnimatedValue();
            backgroundView.getLayoutParams().width = value;
            backgroundView.requestLayout();
            scrollView.getLayoutParams().width = value;
            scrollView.requestLayout();
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.play(widthAnimator);
        animatorSet.start();


        Set<Integer> played = new HashSet<>();
        Random random = new Random();
        Runnable runTask = new Runnable() {
            @Override
            public void run() {
                // Execute tasks on main thread

                // Repeat this task again another 2 seconds
                for(int i = 0; i < cells.size(); i++) {
                    if (played.contains(i) || random.nextInt(4) != 0) {
                        continue;
                    }
                    played.add(i);
                    cells.get(i).play();
                }
                if (played.size() != cells.size()) {
                    handler.postDelayed(this, 500);
                }
            }
        };

        handler.postDelayed(runTask, 500);
    }

    class EmojisCell extends FrameLayout {
        public BackupImageView imageView;
        public TLRPC.Document select_animation;

        public EmojisCell(@NonNull Context context, float leftMargin) {
            super(context);
            setLayoutParams(LayoutHelper.createFrame(44, 44, Gravity.CENTER_VERTICAL | Gravity.LEFT, leftMargin, 0f, 0f, 0f));
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            setBackgroundColor(color);

            imageView = new BackupImageView(context);
            imageView.setAspectFit(true);
            imageView.setLayerNum(1);
            addView(imageView);
            imageView.imageReceiver.setAllowStartLottieAnimation(false);
        }

        void configure(TLRPC.Document select_animation) {
            this.select_animation = select_animation;
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(select_animation, Theme.key_windowBackgroundGray, 1.0f);
            imageView.setLayoutParams(LayoutHelper.createFrame(40, 40, Gravity.CENTER, 0, 0, 0, 0));
            imageView.setImage(ImageLocation.getForDocument(select_animation), "66_66", null, svgThumb, this);
        }

        void play() {
            if (imageView == null || imageView.imageReceiver == null || imageView.imageReceiver.getLottieAnimation() == null) {
                return;
            }
            imageView.imageReceiver.getLottieAnimation().autoRepeatPlayCount = 1;
            imageView.imageReceiver.setAutoRepeat(2);
            imageView.imageReceiver.startAnimation();
        }
    }

    @FunctionalInterface
    public interface OnClickListenerx {
        void selected(FrameLayout frameLayout);
    }

}
