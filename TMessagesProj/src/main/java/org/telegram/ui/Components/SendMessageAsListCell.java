package org.telegram.ui.Components;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.MemberRequestCell;

import java.util.Random;

public class SendMessageAsListCell extends FrameLayout {

    private BackupImageView avatarImageView;
    private TextView topTextView;
    private TextView bottomTextView;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private SendMessageAsListCellDelegate delegate;
    private long chatId = 0;

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
                setBackgroundColor(0x22000000);
                delegate.onSelected(chatId);
            }

            @Override
            public void onActionUp(View view, MotionEvent motionEvent) {
                setBackgroundColor(0x00000000);
            }
        }));
    }

    private void setup() {
        Context context = getContext();
        avatarImageView = new BackupImageView(context);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(24));
        addView(avatarImageView, LayoutHelper.createFrame(41, 41, Gravity.TOP | Gravity.LEFT, 9.82f, 9.82f, 0, 0));

        topTextView = new TextView(context);
        topTextView.setTextColor(0xff222222);
        topTextView.setTextSize(17.45f);
        addView(topTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 65.46f, 8, 0, 0));

        bottomTextView = new TextView(context);
        bottomTextView.setTextColor(0xff8a8a8a);
        bottomTextView.setTextSize(14.19f);
        addView(bottomTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 65.46f, 34.19f, 0, 0));
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