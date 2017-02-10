package com.uoit.calvin.thesis_2016;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class XAxisFormatterDefault implements IAxisValueFormatter
{


    public XAxisFormatterDefault() {
        // maybe do something here or provide parameters in constructor

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.valueOf((int)value);
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
