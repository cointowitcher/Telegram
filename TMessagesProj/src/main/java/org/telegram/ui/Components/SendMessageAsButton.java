package org.telegram.ui.Components;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class SendMessageAsButton extends FrameLayout {
    private CheckBox2 checkBox2;
    private boolean visible = false;
    private boolean checked = false;

    public SendMessageAsButton(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        checkBox2 = new CheckBox2(context, AndroidUtilities.dp(11), resourcesProvider, true);
        checkBox2.setDrawUnchecked(true);
        checkBox2.setColor(null, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
        checkBox2.setDrawUnchecked(false);
        checkBox2.setDrawBackgroundAsArc(3);
        addView(checkBox2);
        setOnClickListener(v -> {
            checked = !checked;
            this.checkBox2.setChecked(checked, true);
        });
        checkBox2.checkBoxBase.backgroundColorKey = Theme.key_voipgroup_overlayBlue1;
        checkBox2.checkBoxBase.background2ColorKey = null;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (visible) {

        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        checkBox2.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public boolean getVisible() {
        return visible;
    }

    public int getPossibleWidth() {
        if (!visible) {
            return 0;
        }
        return getMeasuredWidth();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        checkBox2.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        checkBox2.onDetachedFromWindow();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        checkBox2.setBounds(0, 0, right - left, bottom - top);
    }
}
