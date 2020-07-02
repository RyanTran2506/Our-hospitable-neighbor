package com.example.ourhospitableneighbor.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.function.Function;

public class PanelView extends LinearLayout {
    public Function<MotionEvent,Boolean> interceptTouchEventHandler;
    private Integer touchSlop;
    private boolean isScrolling;
    private float initialY;

    public PanelView(Context context) {
        super(context);
    }

    public PanelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PanelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();

        // Always handle the case of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Release the scroll.
            isScrolling = false;
            return false; // Do not intercept touch event, let the child handle it
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                initialY = ev.getY();
                return false;
            }
            case MotionEvent.ACTION_MOVE: {
                if (isScrolling) {
                    // We're currently scrolling, so yes, intercept the
                    // touch event!
                    return true;
                }

                // If the user has dragged her finger vertically more than
                // the touch slop, start the scroll
                final float yDiff = Math.abs(ev.getY() - initialY);

                // Touch slop should be calculated using ViewConfiguration
                // constants.
                if (yDiff > getTouchSlop()) {
                    // Start scrolling!
                    isScrolling = true;
                    return true;
                }
                break;
            }
        }

        // In general, we don't want to intercept touch events. They should be
        // handled by the child view.
        return false;
    }

    private Integer getTouchSlop() {
        if (touchSlop == null) {
            ViewConfiguration vc = ViewConfiguration.get(getContext());
            touchSlop = vc.getScaledTouchSlop();
        }
        return touchSlop;
    }
}
