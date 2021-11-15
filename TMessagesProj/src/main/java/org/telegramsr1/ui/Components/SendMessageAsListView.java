package org.telegramsr1.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.telegramsr1.messenger.AndroidUtilities;
import org.telegramsr1.tgnet.TLRPC;

import java.util.ArrayList;

public class SendMessageAsListView extends FrameLayout {
    private AnimatorSet animatorSet;
    SendMessageAsListScrollView mainView;
    FrameLayout bgView;
    View dim;
    boolean isHiding = false;
    int calculatedHeight;
    SendMessageAsListCell.SendMessageAsListCellDelegate cellDelegate;

    public SendMessageAsListView(Context context, SendMessageAsListCell.SendMessageAsListCellDelegate cellDelegate) {
        super(context);
        this.cellDelegate = cellDelegate;
    }

    public void setup(ArrayList<TLRPC.ChatFull> chatFulls, ArrayList<TLRPC.Chat> chats, OnClickListener bgClicked, long checkedId) {
        createBgView(bgClicked);
        createDimView();
        createMainView(chatFulls, chats, checkedId);
        animation();
    }

    private void createMainView(ArrayList<TLRPC.ChatFull> chatFulls, ArrayList<TLRPC.Chat> chats, long checkedId) {
        mainView = new SendMessageAsListScrollView(getContext(), cellDelegate);

        int maxHeight = AndroidUtilities.dp(454f);
        calculatedHeight = AndroidUtilities.dp(44f + 61 * chatFulls.size());

        ViewGroup.LayoutParams layoutParams = LayoutHelper.createFrameWithoutDp(AndroidUtilities.dp(283.63f), Math.min(calculatedHeight, maxHeight), Gravity.BOTTOM | Gravity.LEFT, AndroidUtilities.dp(6.55f), 0, 0, AndroidUtilities.dp(6.91f));
        mainView.setLayoutParams(layoutParams);
        mainView.configure(chatFulls, chats, checkedId);
        addView(mainView);
    }

    private void createBgView(OnClickListener callback) {
        bgView = new FrameLayout(getContext());
        bgView.setBackgroundColor(0x00000000);
        addView(bgView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        bgView.setOnClickListener(callback);
    }

    private void createDimView() {
        dim = new View(bgView.getContext());
        dim.setBackgroundColor(0x33000000);
        addView(dim, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    private void animation() {
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(dim, View.ALPHA, 0.0f, 1.0f),
                ObjectAnimator.ofFloat(mainView, View.ALPHA, 0.0f, 1.0f),
                ObjectAnimator.ofFloat(mainView, View.SCALE_Y, 0.3f, 1.0f),
                ObjectAnimator.ofFloat(mainView, View.SCALE_X, 0.3f, 1.0f)
        );
        animatorSet.setDuration(300);
        mainView.setPivotX(0);
        mainView.setPivotY(calculatedHeight);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorSet = null;
            }
        });
        animatorSet.setDuration(300);
        animatorSet.start();
    }

    public void hide(Callback callback) {
        if (isHiding) {
            return;
        }
        isHiding = true;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(dim, View.ALPHA, 1.0f, 0.0f),
                ObjectAnimator.ofFloat(mainView, View.ALPHA, 1.0f, 0.0f),
                ObjectAnimator.ofFloat(mainView, View.SCALE_Y, 1.0f, 0.3f),
                ObjectAnimator.ofFloat(mainView, View.SCALE_X, 1.0f, 0.3f)
        );
        animatorSet.setDuration(300);
        mainView.setPivotX(0);
        mainView.setPivotY(mainView.getMeasuredHeight());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorSet = null;
                callback.callback();
            }
        });
        animatorSet.setDuration(300);
        animatorSet.start();
    }

    public boolean getIsHiding() {
        return isHiding;
    }

    public void show(View chatActivityEnterView, ObjectAnimator animator) {
        // POSITION

        // VISIBILITY
    }

    public interface Callback {
        void callback();
    }
}
