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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class EmojisScrollComponent extends FrameLayout {
    private Theme.ResourcesProvider resourcesProvider;
    private FrameLayout contentView;
    private HorizontalScrollView scrollView;
    private LinearLayout linearLayoutScroll;
    private ArrayList<EmojisCell> cells;
    private ArrayList<TLRPC.TL_availableReaction> reactions;
    private int type = 0;
    private Handler handler;
    private OnClickListenerx callback;
    private ValueAnimator widthAnimator;

    public EmojisScrollComponent(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        cells = new ArrayList<>();
        reactions = new ArrayList<>();
        handler = new Handler();
        this.callback = (v) -> {};
        setup();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (widthAnimator != null) {
            widthAnimator.cancel();
        }
        widthAnimator = null;
        handler = null;
        contentView = null;
        scrollView = null;
        linearLayoutScroll = null;
        cells = null;
        reactions = null;
        callback = null;
    }

    public void setupOnClickListener(OnClickListenerx callback) {
        this.callback = callback;
    }

    private void setup() {
        float r = 26f;
        this.contentView = new RoundedHorizontalScrollView(getContext(), AndroidUtilities.dp(r));
        addView(contentView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48)); // 48
        contentView.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48));
        View bgView = new View(getContext());
        bgView.setBackgroundColor(0xffffffff);
        contentView.addView(bgView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        scrollView = new HorizontalScrollView(getContext());
        contentView.addView(scrollView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        linearLayoutScroll = new LinearLayout(getContext());
        scrollView.addView(linearLayoutScroll, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT));
        ImageLoader.getInstance().clearMemory();
    }

    public void addItems(ArrayList<TLRPC.TL_availableReaction> reactions) {
        if (reactions.size() == cells.size()) {
            return;
        }
        //wqe
        this.reactions = reactions;
        cells = new ArrayList<>();
        linearLayoutScroll.removeAllViews();
        WeakReference weakReference = new WeakReference(this);
        for(int i = 0; i < reactions.size(); i++) {
            EmojisCell emojisCell = new EmojisCell(getContext(), i == 0 ? 0 : 2.5f);
            emojisCell.configure(reactions.get(i));
            emojisCell.setOnClickListener(v -> {
                EmojisScrollComponent emojisScrollComponent = (EmojisScrollComponent)weakReference.get();
                if(emojisScrollComponent == null) { return; }
                emojisScrollComponent.callback.selected((FrameLayout) v);
            });
            linearLayoutScroll.addView(emojisCell);
            cells.add(emojisCell);
        }
    }

    public void animateAppearing(long duration) {
        contentView.setLayoutParams(LayoutHelper.createFrame((int)(getLayoutParams().width * 0.1f), 48, Gravity.RIGHT));
        scrollView.setLayoutParams(LayoutHelper.createFrame((int)(getLayoutParams().width * 0.1f), 48, Gravity.RIGHT));
        widthAnimator = ValueAnimator.ofInt((int)(getLayoutParams().width * 0.1f), getLayoutParams().width).setDuration(duration);
        widthAnimator.addUpdateListener(animation -> {
            Integer value = (Integer) animation.getAnimatedValue();
            contentView.getLayoutParams().width = value;
            contentView.requestLayout();
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
                if (handler == null) { return; }
                // Repeat this task again another 2 seconds
                for(int i = 0; i < cells.size(); i++) {
                    if (played.contains(i) || random.nextInt(4) != 0) {
                        continue;
                    }
                    played.add(i);
                    cells.get(i).shouldPlay = true;
                    cells.get(i).tryPlay();
                }
                if (played.size() != cells.size()) {
                    handler.postDelayed(this, 500);
                }
            }
        };

        handler.postDelayed(runTask, 500);
    }

    public class EmojisCell extends FrameLayout {
        public BackupImageView imageView;
        public TLRPC.TL_availableReaction reaction;
        boolean shouldPlay = false;
        boolean didSetImage = false;

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            imageView = null;
            reaction = null;
        }

        public EmojisCell(@NonNull Context context, float leftMargin) {
            super(context);
            setLayoutParams(LayoutHelper.createFrame(48, 48, Gravity.CENTER_VERTICAL | Gravity.LEFT, leftMargin, 0f, 0f, 0f));
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            setBackgroundColor(color);

            imageView = new BackupImageView(context);
            imageView.setAspectFit(true);
            imageView.setLayerNum(1);
            addView(imageView);
        }

        void configure(TLRPC.TL_availableReaction reaction) {
            this.reaction = reaction;
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(reaction.select_animation, Theme.key_windowBackgroundGray, 1.0f);
            imageView.setLayoutParams(LayoutHelper.createFrame(40, 40, Gravity.CENTER, 0, 0, 0, 0));
            imageView.setAspectFit(true);
            imageView.setLayerNum(2);
            imageView.imageReceiver.setAllowStartLottieAnimation(false);
            imageView.imageReceiver.setAutoRepeat(0);
//            receiverJustForClickedImage.setImage(ImageLocation.getForDocument(reaction.activate_animation), null, null, "webp", null);
            imageView.imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() {
                @Override
                public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
                    didSetImage = true;

                    tryPlay();
                }
            });
            imageView.setImage(ImageLocation.getForDocument(reaction.select_animation), "66_66", null, svgThumb, this);
        }

        void tryPlay() {
            if (!shouldPlay || !didSetImage) { return; }
            play();
        }

        private void play() {
            if (imageView == null || imageView.imageReceiver == null || imageView.imageReceiver.getLottieAnimation() == null) {
                return;
            }
            imageView.imageReceiver.startLottie();
        }
    }

    @FunctionalInterface
    public interface OnClickListenerx {
        void selected(FrameLayout frameLayout);
    }

}

class RoundedHorizontalScrollView extends FrameLayout {
    private Path mClip;
    private float mRadius;
    float radii[];

    public RoundedHorizontalScrollView(Context context, float mRadius) {
        super(context);
        this.mRadius = mRadius;
        radii = new float[8];
        for(int i = 0; i < 8; i++) {
            radii[i] = mRadius;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mClip = new Path();
        RectF rectRound = new RectF(0, 0, w, h);
        mClip.addRoundRect(rectRound, radii, Path.Direction.CW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int saveCount = canvas.save();
        if (mRadius > 0) {
            canvas.clipPath(mClip);
        }
        super.dispatchDraw(canvas);
        canvas.restoreToCount(saveCount);
    }
}