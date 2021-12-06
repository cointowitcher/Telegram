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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.util.Log;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.R;
import org.telegram.messenger.Randoms;
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
    private AnimatorSet animatorSet;
    private Drawable shadowDrawable;
    private int itemWidths = 0;
    private Circlex circleOne;
    private Circlex circleTwo;
    private OnScrollChangeListener onScrollChangeListener;

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
        if (animatorSet != null) {
            animatorSet.cancel();
            animatorSet.removeAllListeners();;
        }
        animatorSet = null;
        handler = null;
        contentView = null;
        scrollView = null;
        linearLayoutScroll = null;
        cells = null;
        reactions = null;
        callback = null;
        circleOne = null;
        circleTwo = null;
        onScrollChangeListener = null;
    }

    public void setupOnClickListener(OnClickListenerx callback) {
        this.callback = callback;
    }

    private void setup() {
        float r = 26f;
//        shadowDrawable = getResources().getDrawable(R.drawable.shadow_35039);
//        ImageView imageView = new ImageView(getContext());
//        imageView.setImageDrawable(shadowDrawable);
//        addView(imageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        circleOne = new Circlex(getContext(), AndroidUtilities.dp(15.2727f));
        addView(circleOne, LayoutHelper.createFrame(15.2727f, 15.2727f, Gravity.TOP | Gravity.RIGHT, 0, 38.9f, 33.45f, 0));
        View v11 = new View(getContext());
        v11.setBackgroundColor(Color.WHITE);
        circleOne.addView(v11, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        circleTwo = new Circlex(getContext(), AndroidUtilities.dp(7.6363f));
        addView(circleTwo, LayoutHelper.createFrame(7.6363f, 7.6363f, Gravity.TOP | Gravity.RIGHT, 0, 58.9f, 32, 0));
        circleTwo.addView(new View(getContext()));
        View v12 = new View(getContext());
        v12.setBackgroundColor(Color.WHITE);
        circleTwo.addView(v12, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        this.contentView = new RoundedHorizontalScrollView(getContext(), AndroidUtilities.dp(r));
        addView(contentView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.RIGHT)); // 48
        View bgView = new View(getContext());
        bgView.setBackgroundColor(0xffffffff);
        contentView.addView(bgView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        scrollView = new HorizontalScrollView(getContext());
        scrollView.setFadingEdgeLength(AndroidUtilities.dp(20));
        scrollView.setHorizontalFadingEdgeEnabled(true);
        contentView.addView(scrollView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setHorizontalScrollBarEnabled(false);

        final float minScale = 0.5f;
        final float cellSize = AndroidUtilities.dp(48);
        onScrollChangeListener = new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d("sergey", String.format("981 scrollX: %s scrollY: %s, oldScrollX: %s, oldScrollY: %s width: %s", scrollX, scrollY, oldScrollX, oldScrollY, getWidth()));
                float minScale = 0.4f;
                float width = getWidth();
                for(int i = 0; i < cells.size(); i++) {
                    EmojisCell cell = cells.get(i);
                    if (cell.getRight() < scrollX || cell.getLeft() > scrollX + width) { continue; }
                    if (cell.getLeft() < scrollX) {
                        float part = (scrollX - cell.getLeft()) / cellSize;
                        float scale;
                        if (part > 0.7) {
                            scale = minScale;
                        } else {
                            scale = minScale + (1 - minScale) * (1f - part);
                        }
                        cell.setScaleX(scale);
                        cell.setScaleY(scale);
                    } else if (cell.getRight() > (scrollX + width)) {
                        float part = (cell.getRight() - (scrollX + width)) / cellSize;
                        float scale;
                        if (part > 0.7) {
                            scale = minScale;
                        } else {
                            scale = minScale + (1 - minScale) * (1f - part);
                        }

                        cell.setScaleX(scale);
                        cell.setScaleY(scale);
                    } else {
                        cell.setScaleY(1f);
                        cell.setScaleX(1f);
                    }
                }
            }
        };

        scrollView.setOnScrollChangeListener(onScrollChangeListener);

        linearLayoutScroll = new LinearLayout(getContext());
        scrollView.addView(linearLayoutScroll, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT));

        setClipChildren(false);
    }

    public void addItems(ArrayList<TLRPC.TL_availableReaction> reactions) {
        if (reactions.size() == cells.size()) {
            return;
        }
        itemWidths = (int)(reactions.size() * AndroidUtilities.dp(48) + AndroidUtilities.dp(2.5f) * (reactions.size() - 1));
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
        int fromWidth = (int)(getLayoutParams().width * 0.1f);
        int toWidth = (int)(Math.min(itemWidths, getLayoutParams().width));
        contentView.getLayoutParams().width = fromWidth;
        scrollView.getLayoutParams().width = fromWidth;
        ValueAnimator widthAnimator = ValueAnimator.ofInt(fromWidth, toWidth);
        widthAnimator.addUpdateListener(animation -> {
            Integer value = (Integer) animation.getAnimatedValue();
            contentView.getLayoutParams().width = value;
            contentView.requestLayout();
            scrollView.getLayoutParams().width = value;
            scrollView.requestLayout();
        });

        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0, 1f);
        alphaAnimator.addUpdateListener(animation -> {
            float value = (float)animation.getAnimatedValue();
            contentView.setAlpha(value);
            circleOne.setAlpha(value);
            circleOne.setAlpha(value);
        });

        ValueAnimator circleOneScaleAnimator = ValueAnimator.ofFloat(0f,1f);
        circleOneScaleAnimator.addUpdateListener(animation -> {
            float value = (float)animation.getAnimatedValue();
            if (value <= 0.8) {
                value = -1.875f * value * value + 3 * value;
            } else {
                value = -5 * value * value + 8 * value - 2;
            }
            circleOne.setScaleX(value);
            circleOne.setScaleY(value);
        });
        ValueAnimator circleTwoScaleAnimator = ValueAnimator.ofFloat(0f,1f);
        circleTwoScaleAnimator.addUpdateListener(animation -> {
            float value = (float)animation.getAnimatedValue();
            if (value <= 0.8) {
                value = -1.875f * value * value + 3 * value;
            } else {
                value = -5 * value * value + 8 * value - 2;
            }
            circleTwo.setScaleX(value);
            circleTwo.setScaleY(value);
        });

        animatorSet = new AnimatorSet();
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(widthAnimator, alphaAnimator, circleOneScaleAnimator, circleTwoScaleAnimator);
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
//            Random rnd = new Random();
//            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//            setBackgroundColor(color);

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
                    imageReceiver.resetLottie();
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

class Circlex extends RoundedHorizontalScrollView {
    public Circlex(Context context, float size) {
        super(context, size);
    }
}