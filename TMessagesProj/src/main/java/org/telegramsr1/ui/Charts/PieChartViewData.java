package org.telegramsr1.ui.Charts;

import android.animation.Animator;

import org.telegramsr1.ui.Charts.data.ChartData;
import org.telegramsr1.ui.Charts.view_data.StackLinearViewData;

public class PieChartViewData extends StackLinearViewData {

    float selectionA;
    float drawingPart;
    Animator animator;

    public PieChartViewData(ChartData.Line line) {
        super(line);
    }
}
