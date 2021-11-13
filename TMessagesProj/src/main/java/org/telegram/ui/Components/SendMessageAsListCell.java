package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC;

import java.util.Random;

public class SendMessageAsListCell extends FrameLayout {

    private BackupImageView avatarImageView;
    private TextView topTextView;
    private TextView bottomTextView;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();

    public SendMessageAsListCell(Context context) {
        super(context);
        setup();
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
}
