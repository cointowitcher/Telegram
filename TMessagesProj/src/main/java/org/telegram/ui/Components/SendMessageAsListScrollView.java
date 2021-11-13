package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

import java.util.ArrayList;
import java.util.Random;

public class SendMessageAsListScrollView extends FrameLayout {
    GradientDrawable background;
    FrameLayout frameLayout;
    TextView sendMessageAsTextView;
    ScrollView scrollView;
    SendMessageAsListCell[] cells;
    LinearLayout linearLayout;


    public SendMessageAsListScrollView(Context context) {
        super(context);
        setup();
    }

    public void setup() {
        background = new GradientDrawable();
        background.setColor(0xffffffff);
        background.setCornerRadius(AndroidUtilities.dp(5.8f));
        setBackground(background);
        frameLayout = new FrameLayout(getContext());
        addView(frameLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        sendMessageAsTextView = new TextView(getContext());
        sendMessageAsTextView.setText(LocaleController.getString("SendMessageAs", R.string.SendMessageAs));
        sendMessageAsTextView.setTextColor(0xff5493cf);
        sendMessageAsTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.36f);
        frameLayout.addView(sendMessageAsTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT, Gravity.TOP, 17.45f, 17.45f, 0, 0));

        scrollView = new ScrollView(getContext());
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setHorizontalScrollBarEnabled(false);
        frameLayout.addView(scrollView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP, 0, 44f, 0, 0));
        linearLayout = new LinearLayout(getContext());
        scrollView.addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
    }

    void configure(ArrayList<TLRPC.ChatFull> chatFulls, ArrayList<TLRPC.Chat> chats) {
        int numsOfItems = chatFulls.size();
        cells = new SendMessageAsListCell[numsOfItems];
        for (int i = 0; i < numsOfItems; i++) {
            TLRPC.ChatFull chatFull = chatFulls.get(i);
            TLRPC.Chat chat = chats.get(i);
            SendMessageAsListCell cell = new SendMessageAsListCell(getContext());
            cell.configure(chat.title, "Subscribers " + String.valueOf(chatFull.participants_count), chat);
            linearLayout.addView(cell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 61, Gravity.TOP));
            cells[i] = cell;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
