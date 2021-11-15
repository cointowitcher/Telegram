/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegramsr1.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.core.graphics.ColorUtils;

import org.telegramsr1.messenger.AndroidUtilities;
import org.telegramsr1.ui.ActionBar.Theme;

public class DividerCell extends View {

    private boolean forceDarkTheme;
    private Paint paint = new Paint();

    public DividerCell(Context context) {
        super(context);
        setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), getPaddingTop() + getPaddingBottom() + 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (forceDarkTheme) {
            paint.setColor(ColorUtils.blendARGB(Color.BLACK, Theme.getColor(Theme.key_voipgroup_dialogBackground),  0.2f));
        } else {
            paint.setColor(Theme.getColor(Theme.key_divider));
        }

        canvas.drawLine(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getPaddingTop(), paint);
    }

    public void setForceDarkTheme(boolean forceDarkTheme) {
        this.forceDarkTheme = forceDarkTheme;
    }
}
