package org.telegram.ui.ActionBar;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.LayoutHelper;

public class ForwardRestrictedInfo extends FrameLayout {
    private TextView textView;

    private final Theme.ResourcesProvider resourcesProvider;

    private int textColor;
    private int selectorColor;

    public ForwardRestrictedInfo(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;

        textColor = getThemedColor(Theme.key_actionBarDefaultSubmenuItem);
        selectorColor = getThemedColor(Theme.key_dialogButtonSelector);

        updateBackground();

        textView = new TextView(context);
        textView.setLines(1);
        textView.setSingleLine(false);
        textView.setGravity(Gravity.LEFT);
        textView.setTextColor(textColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.28f);

        setPadding(AndroidUtilities.dp(17.46f), AndroidUtilities.dp(10.55f), AndroidUtilities.dp(17.46f), AndroidUtilities.dp(10.55f));
        addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL));
        setEnabled(false);
    }

    public void setTextViewText(String text) {
        textView.setText(text);
    }

    private int getThemedColor(String key) {
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color : Theme.getColor(key);
    }

    void updateBackground() {
        setBackground(Theme.createRadSelectorDrawable(selectorColor, 6, 6));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.AT_MOST));
    }
}
