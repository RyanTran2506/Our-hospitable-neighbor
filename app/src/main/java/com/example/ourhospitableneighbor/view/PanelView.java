package com.example.ourhospitableneighbor.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.ourhospitableneighbor.ListPostInAreaActivity;
import com.example.ourhospitableneighbor.R;
import com.example.ourhospitableneighbor.model.Post;

import java.util.List;

public class PanelView extends LinearLayout {
    private int touchSlop;
    private boolean isScrolling;
    private boolean isCollapsed = true;

    private Float initialY;
    private VelocityTracker tracker;

    private PanelView panel;
    private ViewGroup panelHeader;
    private ViewGroup panelItemsContainer;

    private Animator panelAnimator;
    private Animator itemsContainerAnimator;

    public PanelView(Context context) {
        super(context);
    }

    public PanelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    public void setPosts(List<Post> posts) {
        findViewById(R.id.progress_post_count).setVisibility(INVISIBLE);

        TextView txtPostCount = findViewById(R.id.txt_post_count);
        txtPostCount.setVisibility(VISIBLE);
        txtPostCount.setText(getResources().getQuantityString(R.plurals.number_of_post_found, posts.size(), posts.size()));

        panelItemsContainer.removeAllViews();
        for (int i = 0; i < posts.size() && i < 3; i++) {
            Post post = posts.get(i);
            PanelItemView item = new PanelItemView(getContext());
            item.setPost(post);
            panelItemsContainer.addView(item);
        }

        panelItemsContainer.invalidate();
        panelItemsContainer.requestLayout();
        snap(false);
        snapItemsContainer(!isCollapsed);
    }

    public void setCollapse(boolean isCollapsed, boolean shouldAnimate) {
        this.isCollapsed = isCollapsed;
        snap(shouldAnimate);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ViewConfiguration vc = ViewConfiguration.get(getContext());
        touchSlop = vc.getScaledTouchSlop();

        configurePanel();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void configurePanel() {
        panel = this;
        panel.setOnTouchListener(this::onPanelTouch);

        panelItemsContainer = findViewById(R.id.panel_item_container);

        panelHeader = this.findViewById(R.id.panel_header);
        panelHeader.setOnClickListener(v -> {
            toggleCollapse();
            snap(true);
        });

        snap(false);

        Button viewAllButton = findViewById(R.id.btn_view_all);
        viewAllButton.setOnClickListener(v -> {
            getContext().startActivity(new Intent(this.getContext(), ListPostInAreaActivity.class));
        });
    }

    private void toggleCollapse() {
        isCollapsed = !isCollapsed;
    }

    private void snapItemsContainer(boolean shouldAnimate) {
        if (shouldAnimate && panelAnimator == null) {
            int oldHeight = panelItemsContainer.getHeight();
            panelItemsContainer.measure(0, 0);
            int newHeight = panelItemsContainer.getMeasuredHeight();

            ValueAnimator anim = ValueAnimator.ofInt(oldHeight, newHeight);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = panelItemsContainer.getLayoutParams();
                layoutParams.height = val;
                panelItemsContainer.setLayoutParams(layoutParams);
            });
            anim.setDuration(300);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    itemsContainerAnimator = null;
                }
            });
            anim.start();
            itemsContainerAnimator = anim;
        } else {
            stopSnapAnimation();

            ViewGroup.LayoutParams layoutParams = panelItemsContainer.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            panelItemsContainer.setLayoutParams(layoutParams);

            snap(false);
        }
    }

    public void snap(boolean shouldAnimate) {
        if (shouldAnimate && itemsContainerAnimator == null) {
            panel.measure(getWidth(), 0);
            float headerHeight = getPanelHeaderHeight();
            float panelHeight = getPanelHeight();
            float target = isCollapsed ? (panelHeight - headerHeight) : 0;

            ObjectAnimator animation = ObjectAnimator.ofFloat(panel, "translationY", target);
            animation.setDuration((long) (300 * Math.abs((panel.getTranslationY() - target) / (panelHeight - headerHeight))));
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    panelAnimator = null;
                }
            });
            animation.start();
            panelAnimator = animation;
        } else {
            stopItemsContainerSnapAnimation();

            panel.measure(getWidth(), 0);
            float headerHeight = getPanelHeaderHeight();
            float panelHeight = getPanelHeight();
            float target = isCollapsed ? (panelHeight - headerHeight) : 0;

            panel.setTranslationY(target);
        }
    }

    private void stopSnapAnimation() {
        if (panelAnimator != null) {
            panelAnimator.end();
            panelAnimator = null;
        }
    }

    private void stopItemsContainerSnapAnimation() {
        if (itemsContainerAnimator != null) {
            itemsContainerAnimator.end();
            itemsContainerAnimator = null;
        }
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
        float initialPosition = isCollapsed ? (panelHeight - headerHeight) : 0;
        event.offsetLocation(0, panel.getTranslationY() - initialPosition);

        float currentY = event.getY();
        if (initialY == null) initialY = currentY;

        float diff = initialY - currentY;
        float translationY = Math.max(Math.min(initialPosition - diff, panelHeight - headerHeight), 0);

        float VELOCITY_CUTOFF = 200;
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

                snap(true);
                initialY = null;
                return true;
        }
        return false;
    }
}
