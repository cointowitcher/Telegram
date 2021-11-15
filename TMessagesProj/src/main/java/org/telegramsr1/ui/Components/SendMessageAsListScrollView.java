package org.telegramsr1.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.telegramsr1.messenger.AndroidUtilities;
import org.telegramsr1.messenger.LocaleController;
import org.telegramsr1.messenger.R;
import org.telegramsr1.tgnet.TLRPC;
import org.telegramsr1.ui.ActionBar.Theme;

import java.util.ArrayList;

public class SendMessageAsListScrollView extends FrameLayout {
    GradientDrawable background;
    FrameLayout frameLayout;
    TextView sendMessageAsTextView;
    ScrollView scrollView;
    SendMessageAsListCell[] cells;
    LinearLayout linearLayout;
    SendMessageAsListCell.SendMessageAsListCellDelegate cellDelegate;


    public SendMessageAsListScrollView(Context context, SendMessageAsListCell.SendMessageAsListCellDelegate cellDelegate) {
        super(context);
        this.cellDelegate = cellDelegate;
        setup();
    }

    public void setup() {
        background = new GradientDrawable();
        background.setColor(Theme.getColor(Theme.key_dialogBackground));
        background.setCornerRadius(AndroidUtilities.dp(5.8f));
        setBackground(background);
        frameLayout = new FrameLayout(getContext());
        addView(frameLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        sendMessageAsTextView = new TextView(getContext());
        sendMessageAsTextView.setText(LocaleController.getString("SendMessageAs", R.string.SendMessageAs));
        sendMessageAsTextView.setTextColor(Theme.getColor(Theme.key_chat_sendMessageAsTitleColor));
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

    SendMessageAsListCell previous;

    boolean shouldProceedMoreFromDelegate = true;

    void configure(ArrayList<TLRPC.ChatFull> chatFulls, ArrayList<TLRPC.Chat> chats, long checkedId) {
        int numsOfItems = chatFulls.size();
        cells = new SendMessageAsListCell[numsOfItems];
        for (int i = 0; i < numsOfItems; i++) {
            TLRPC.ChatFull chatFull = chatFulls.get(i);
            TLRPC.Chat chat = chats.get(i);
            SendMessageAsListCell cell = new SendMessageAsListCell(getContext(), new SendMessageAsListCell.SendMessageAsListCellDelegate() {
                @Override
                public void onSelected(long id) {
                    if (previous == null || cells == null || !shouldProceedMoreFromDelegate) { return; };
                    shouldProceedMoreFromDelegate = false;
                    previous.setChecked(false, true);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            cellDelegate.onSelected(id);
                        }
                    }, 320);
                }
                @Override
                public boolean canSelect() {
                    return shouldProceedMoreFromDelegate;
                }
            });
            cell.configure(chat.title, String.valueOf(chatFull.participants_count) + " subscribers", chat);
            linearLayout.addView(cell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 61, Gravity.TOP));
            cells[i] = cell;
            if (chat.id == checkedId) {
                cell.setChecked(true, false);
                previous = cell;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
