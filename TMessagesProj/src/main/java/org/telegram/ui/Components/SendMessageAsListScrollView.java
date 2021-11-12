package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class SendMessageAsListScrollView extends FrameLayout {
    GradientDrawable background;

    
    public SendMessageAsListScrollView(Context context) {
        super(context);
        setup();
    }

    public void setup() {
        background = new GradientDrawable();
        background.setColor(0xffffffff);
        background.setCornerRadius(AndroidUtilities.dp(5.8f));
        setBackground(background);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
