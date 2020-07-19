package com.example.ourhospitableneighbor.helper;

import android.content.res.Resources;
import android.util.TypedValue;

public class SizeConverter {
    public static int fromDpToPx(Resources res, int dp) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                res.getDisplayMetrics())
        );
    }
}
