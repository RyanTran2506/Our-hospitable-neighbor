package com.example.ourhospitableneighbor.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.ourhospitableneighbor.R;
import com.example.ourhospitableneighbor.model.Job;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PanelView extends LinearLayout {
    private int touchSlop;
    private boolean isScrolling;
    private boolean isCollapsed = true;

    private Float initialY;
    private VelocityTracker tracker;
    private final float VELOCITY_CUTOFF = 200;

    private PanelView panel;
    private ViewGroup panelHeader;
    private ViewGroup panelItemsContainer;
    private List<Job> jobs;
    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");

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

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;

        findViewById(R.id.progress_job_count).setVisibility(INVISIBLE);

        TextView txtJobCount = findViewById(R.id.txt_job_count);
        txtJobCount.setVisibility(VISIBLE);
        txtJobCount.setText(getResources().getQuantityString(R.plurals.number_of_job_found, jobs.size(), jobs.size()));

        LayoutInflater inflater = LayoutInflater.from(getContext());
        panelItemsContainer.removeAllViews();

        for (int i = 0; i < jobs.size() && i < 3; i++) {
            Job job = jobs.get(i);

            View panelItem = inflater.inflate(R.layout.panel_item, null);
            TextView title = panelItem.findViewById(R.id.txt_title);
            title.setText(job.getJobTitle());

            TextView address = panelItem.findViewById(R.id.txt_address);
            address.setText(job.getAddress());

            String thumbnail = job.getThumbnail();
            if (thumbnail != null) {
                ImageView img = panelItem.findViewById(R.id.imageView);
                Glide.with(getContext()).load(storageReference.child(thumbnail)).into(img);
            }

            panelItemsContainer.addView(panelItem);
        }

        panelItemsContainer.invalidate();
        panelItemsContainer.requestLayout();
        snapItemsContainer(!isCollapsed);
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
        panelItemsContainer = findViewById(R.id.panel_item_container);
        panelHeader = this.findViewById(R.id.panel_header);
        panelHeader.setOnClickListener(v -> {
            toggleCollapse();
            snap(true);
        });
        panel.setOnTouchListener(this::onPanelTouch);
        panel.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            snap(false);
        });
    }

    private void toggleCollapse() {
        isCollapsed = !isCollapsed;
    }

    private void snapItemsContainer(boolean shouldAnimate) {
        if (shouldAnimate) {
            if (panelAnimator != null) {
                panelAnimator.end();
                panelAnimator = null;
            }

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
                    snap(false);
                }
            });
            anim.start();
            itemsContainerAnimator = anim;
        } else {
            ViewGroup.LayoutParams layoutParams = panelItemsContainer.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            panelItemsContainer.setLayoutParams(layoutParams);
        }
    }


    private void snap(boolean shouldAnimate) {
        if (shouldAnimate) {
            if (itemsContainerAnimator != null) {
                itemsContainerAnimator.end();
                itemsContainerAnimator = null;
                return;
            }

            float headerHeight = getPanelHeaderHeight();
            float panelHeight = getPanelHeight();
            float target = isCollapsed ? (panelHeight - headerHeight) : 0;

            ObjectAnimator animation = ObjectAnimator.ofFloat(panel, "translationY", target);
            animation.setDuration((long) (300 * Math.abs((panel.getTranslationY() - target) / (panelHeight - headerHeight))));
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.start();
            panelAnimator = animation;
        } else {
            float headerHeight = getPanelHeaderHeight();
            float panelHeight = getPanelHeight();
            float target = isCollapsed ? (panelHeight - headerHeight) : 0;
            panel.setTranslationY(target);
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
