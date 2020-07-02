package com.example.ourhospitableneighbor.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.ourhospitableneighbor.R;

public class PanelView extends LinearLayout {
    private int touchSlop;
    private boolean isScrolling;
    private boolean isCollapsed = true;

    private Float initialY;
    private VelocityTracker tracker;
    private final float VELOCITY_CUTOFF = 200;

    private PanelView panel;
    private ViewGroup panelHeader;


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
                if (yDiff > touchSlop) {
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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        touchSlop = vc.getScaledTouchSlop();
        configurePanel();
    }

    private void configurePanel() {
        panel = this;
        panelHeader = this.findViewById(R.id.panel_header);
        panelHeader.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            int height = bottom - top;
            panel.setTranslationY(-height);
        });
        panelHeader.setOnClickListener(v -> {
            toggleCollapse();
            animateSnap();
        });
        panel.setOnTouchListener(this::onPanelTouch);
    }

    private void toggleCollapse() {
        isCollapsed = !isCollapsed;
    }

    private void animateSnap() {
        float headerHeight = getPanelHeaderHeight();
        float panelHeight = getPanelHeight();
        float target = isCollapsed ? -headerHeight : -panelHeight;
        ObjectAnimator animation = ObjectAnimator.ofFloat(panel, "translationY", target);
        animation.setDuration((long) (300 * Math.abs((panel.getTranslationY() - target) / (panelHeight - headerHeight))));
        animation.start();
    }

    private int getPanelHeight() {
        return panel.getMeasuredHeight();
    }

    private int getPanelHeaderHeight() {
        return panelHeader.getMeasuredHeight();
    }

    private boolean onPanelTouch(View v, MotionEvent event) {
        float headerHeight = getPanelHeaderHeight();
        float panelHeight = getPanelHeight();
        float initialPosition = isCollapsed ? headerHeight : panelHeight;
        event.offsetLocation(0, panel.getTranslationY() + initialPosition);

        float currentY = event.getY();
        if (initialY == null) initialY = currentY;

        float diff = initialY - currentY;
        float translationY = -Math.max(Math.min(initialPosition + diff, panelHeight), headerHeight);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                tracker = VelocityTracker.obtain();
                tracker.addMovement(event);

                initialY = event.getY();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (tracker == null) tracker = VelocityTracker.obtain();
                tracker.addMovement(event);

                panel.setTranslationY(translationY);
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (tracker == null) tracker = VelocityTracker.obtain();
                tracker.addMovement(event);
                tracker.computeCurrentVelocity(1000);
                float velocity = tracker.getYVelocity();
                tracker.recycle();
                tracker = null;

                if (Math.abs(velocity) >= VELOCITY_CUTOFF) {
                    isCollapsed = velocity > 0;
                } else {
                    if (isCollapsed) {
                        isCollapsed = diff <= (panelHeight - headerHeight) / 2;
                    } else {
                        isCollapsed = -diff >= (panelHeight - headerHeight) / 2;
                    }
                }

                animateSnap();
                initialY = null;
                return true;
        }
        return false;
    }
}