package org.telegramsr1.ui.Components;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import org.telegramsr1.messenger.AndroidUtilities;
import org.telegramsr1.tgnet.TLRPC;
import org.telegramsr1.ui.ActionBar.Theme;

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
        addView(avatarImageView, LayoutHelper.createFrame(30, 30, Gravity.CENTER, 0, 0, 0, 0));

        checkBox2 = new CheckBox2(context, 33, resourcesProvider, true);
        checkBox2.setColor(null, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
        checkBox2.setDrawUnchecked(false);
        checkBox2.setDrawBackgroundAsArc(3);
        addView(checkBox2);
        checkBox2.checkBoxBase.backgroundColorKey = Theme.key_chat_sendAsButtonColor;
        checkBox2.checkBoxBase.background2ColorKey = null;
        checkBox2.setChecked(true, false);
        checkBox2.setScaleX(0.0f);
        checkBox2.setScaleY(0.0f);
        checkBox2.setAlpha(0.0f);
    }

    public void setObject(Object object) {
        currentObject = object;
        if (object == null) { return; }
        if (object instanceof TLRPC.TL_channel) {
            TLRPC.TL_channel channel = (TLRPC.TL_channel) object;
            avatarDrawable.setInfo(channel);
            avatarImageView.setForUserOrChat(channel, avatarDrawable);
        }
        if (object instanceof TLRPC.TL_chat) {
            TLRPC.TL_chat chat = (TLRPC.TL_chat) object;
            avatarDrawable.setInfo(chat);
            avatarImageView.setForUserOrChat(chat, avatarDrawable);
        }
//        update(0);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setChecked(boolean enabled, boolean animated) {
        // SERTODO: Maybe create some kind of flag to toggle two ways of animating this
//        this.checkBox2.setChecked(enabled, animated);
        if (!animated) { return; }
        View view1;
        View view2;
        if (enabled) {
            view1 = checkBox2;
            view2 = avatarImageView;
        } else {
            view1 = avatarImageView;
            view2 = checkBox2;
        }

        AnimatorSet animationOpen = new AnimatorSet();
        animationOpen.playTogether(
                ObjectAnimator.ofFloat(view1, View.ALPHA, 1.0f),
                ObjectAnimator.ofFloat(view1, View.SCALE_X, 1.0f),
                ObjectAnimator.ofFloat(view1, View.SCALE_Y, 1.0f),
                ObjectAnimator.ofFloat(view2, View.ALPHA, 0.0f),
                ObjectAnimator.ofFloat(view2, View.SCALE_X, 0.0f),
                ObjectAnimator.ofFloat(view2, View.SCALE_Y, 0.0f)
        );
        animationOpen.setDuration(300);
        animationOpen.start();
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
