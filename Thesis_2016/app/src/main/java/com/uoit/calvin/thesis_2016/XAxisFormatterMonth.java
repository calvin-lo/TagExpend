package com.uoit.calvin.thesis_2016;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class XAxisFormatterMonth implements IAxisValueFormatter
{

    protected String[] mMonths = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    public XAxisFormatterMonth() {
        // maybe do something here or provide parameters in constructor

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mMonths[(int) value];
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
