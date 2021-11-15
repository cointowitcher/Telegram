package org.telegramsr1.ui.Charts.view_data;

import android.graphics.Paint;

import org.telegramsr1.ui.Charts.BaseChartView;
import org.telegramsr1.ui.Charts.data.ChartData;

public class StackLinearViewData extends LineViewData {

    public StackLinearViewData(ChartData.Line line) {
        super(line);
        paint.setStyle(Paint.Style.FILL);
        if (BaseChartView.USE_LINES) {
            paint.setAntiAlias(false);
        }
    }
}
