package org.telegram.ui.Components;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class SendMessageAsButton extends FrameLayout {
    private CheckBox2 checkBox2;
    private BackupImageView avatarImageView;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private Object currentObject;

    private boolean visible = false;
    private boolean checked = false;

    public SendMessageAsButton(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        avatarImageView = new BackupImageView(context);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(24));
        addView(avatarImageView, LayoutHelper.createFrame(AndroidUtilities.dp(11), AndroidUtilities.dp(11), Gravity.CENTER, 0, 0, 0, 0));

        checkBox2 = new CheckBox2(context, AndroidUtilities.dp(11.7f), resourcesProvider, true);
        checkBox2.setColor(null, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
        checkBox2.setDrawUnchecked(false);
        checkBox2.setDrawBackgroundAsArc(3);
        addView(checkBox2);
        checkBox2.checkBoxBase.backgroundColorKey = Theme.key_voipgroup_overlayBlue1;
        checkBox2.checkBoxBase.background2ColorKey = null;
    }

    public void setObject(Object object) {
        currentObject = object;
        if (object instanceof TLRPC.TL_channel) {
            TLRPC.TL_channel channel = (TLRPC.TL_channel) object;
            avatarDrawable.setInfo(channel);
            avatarImageView.setForUserOrChat(channel, avatarDrawable);
        }
//        update(0);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (visible) {

        }
    }

    public void setChecked(boolean enabled, boolean animated) {
        this.checkBox2.setChecked(enabled, animated);
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
        return AndroidUtilities.dp(48);
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
