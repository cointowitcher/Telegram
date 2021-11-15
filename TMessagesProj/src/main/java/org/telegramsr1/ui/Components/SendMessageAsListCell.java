package org.telegramsr1.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.telegramsr1.messenger.AndroidUtilities;
import org.telegramsr1.tgnet.TLRPC;
import org.telegramsr1.ui.ActionBar.Theme;

import java.util.Random;

public class SendMessageAsListCell extends FrameLayout {

    private BackupImageView avatarImageView;
    private TextView topTextView;
    private TextView bottomTextView;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private SendMessageAsListCellDelegate delegate;
    private long chatId = 0;
    private GradientDrawable circle;
    private View selectCircle;

    private View circleSelection2;
    private GradientDrawable circleSelectionCircle2;
    AnimatorSet bganimatorSet;

    public SendMessageAsListCell(Context context, SendMessageAsListCellDelegate delegate) {
        super(context);
        this.delegate = delegate;
        setup();
        setClickListeners();
    }
    private void setClickListeners() {
        setOnTouchListener(new SendMessageAsTouchListener(new SendMessageAsTouchListener.OnListener() {
            @Override
            public void onActionDown(View view, MotionEvent motionEvent) {
                if (!delegate.canSelect()) { return; }
                AnimatorSet bganimatorSet2 = new AnimatorSet();
                bganimatorSet2.playTogether(
                        ObjectAnimator.ofFloat(circleSelection2, View.SCALE_X, 24),
                        ObjectAnimator.ofFloat(circleSelection2, View.SCALE_Y, 24)
                        );
                bganimatorSet2.setDuration(300);
                bganimatorSet2.setInterpolator(CubicBezierInterpolator.EASE_IN);
                bganimatorSet2.start();
                bganimatorSet = new AnimatorSet();
                bganimatorSet.playTogether(
                        ObjectAnimator.ofFloat(circleSelection2, View.ALPHA, 1.0f)
                );
                bganimatorSet.setDuration(200);
                bganimatorSet.start();
                bganimatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (circleSelection2 == null) { return; }
                        AnimatorSet bganimatorSet = new AnimatorSet();
                        bganimatorSet.playTogether(
                                ObjectAnimator.ofFloat(circleSelection2, View.ALPHA, 0.0f)
                        );
                        bganimatorSet.setDuration(200);
                        bganimatorSet.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }

            @Override
            public void onActionUp(View view, MotionEvent motionEvent) {
                if (!delegate.canSelect()) { return; }
                if ((motionEvent.getActionMasked() & MotionEvent.ACTION_CANCEL) != MotionEvent.ACTION_CANCEL) {
                    delegate.onSelected(chatId);
                    setChecked(!isChecked, true);
                }
            }
        }));
    }

    public long getChatId() {
        return chatId;
    }

    boolean isChecked = false;

    void setChecked(boolean checked, boolean animated) {
        if (checked == isChecked) {
            return;
        }
        isChecked = checked;
        float alphaTo;
        float scaleTo;
        if (checked) {
            alphaTo = 1.0f;
            scaleTo = 0.8f;
        } else {
            alphaTo = 0;
            scaleTo = 1;
        }
        if (animated) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(200);
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(selectCircle, View.ALPHA, alphaTo),
                    ObjectAnimator.ofFloat(avatarImageView, View.SCALE_X, scaleTo),
                    ObjectAnimator.ofFloat(avatarImageView, View.SCALE_Y, scaleTo)
            );
            animatorSet.start();
        } else {
            selectCircle.setAlpha(alphaTo);
            avatarImageView.setScaleX(scaleTo);
            avatarImageView.setScaleY(scaleTo);
        }
    }

    private void setup() {
        Context context = getContext();

        // x-x-x-x
        circleSelectionCircle2 = new GradientDrawable();
        circleSelectionCircle2.setShape(GradientDrawable.OVAL);
        circleSelectionCircle2.setCornerRadius(AndroidUtilities.dp(5));
        circleSelectionCircle2.setColor(0x19000000);
        circleSelection2 = new View(context);
        circleSelection2.setBackground(circleSelectionCircle2);
        addView(circleSelection2, LayoutHelper.createFrame(12, 12, Gravity.TOP | Gravity.LEFT, 139f, 28f, 0, 0));
        circleSelection2.setAlpha(0.2f);


        selectCircle = new View(context);
        addView(selectCircle, LayoutHelper.createFrame(41, 41, Gravity.TOP | Gravity.LEFT, 9.82f, 9.82f, 0, 0));

        avatarImageView = new BackupImageView(context);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(24));
        addView(avatarImageView, LayoutHelper.createFrame(41, 41, Gravity.TOP | Gravity.LEFT, 9.82f, 9.82f, 0, 0));

        topTextView = new TextView(context);
        topTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        topTextView.setTextSize(17.45f);
        topTextView.setLines(1);
        addView(topTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 65.46f, 8, 0, 0));

        bottomTextView = new TextView(context);
        bottomTextView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelTrendingDescription));
        bottomTextView.setTextSize(14.19f);
        addView(bottomTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 65.46f, 34.19f, 0, 0));

        circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setCornerRadius(AndroidUtilities.dp(24));
        circle.setStroke(AndroidUtilities.dp(2), Theme.getColor(Theme.key_chat_sendAsButtonColor));
        selectCircle.setBackground(circle);
        selectCircle.setAlpha(0f);
    }

    public void configure(String text1, String text2, TLRPC.Chat chat) {
        chatId = chat.id;
        topTextView.setText(text1);
        bottomTextView.setText(text2);
        avatarDrawable.setInfo(chat);
        avatarImageView.setForUserOrChat(chat, avatarDrawable);
    }

    public void configure(String str1, String str2) {
        topTextView.setText(str1);
        bottomTextView.setText(str2);

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        avatarDrawable.setColor(color);
        avatarImageView.setImageDrawable(avatarDrawable);
    }

    public interface SendMessageAsListCellDelegate {
        void onSelected(long id);
        boolean canSelect();
    }
}

class SendMessageAsTouchListener implements View.OnTouchListener {

    interface OnListener {
        void onActionDown(View view, MotionEvent motionEvent);
        void onActionUp(View view, MotionEvent motionEvent);
    }
    OnListener listener;
    public SendMessageAsTouchListener(OnListener listener) {
        super();
        this.listener = listener;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.listener.onActionDown(view, motionEvent);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                this.listener.onActionUp(view, motionEvent);
                break;
        }
        return true;
    }
}