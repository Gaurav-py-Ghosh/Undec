package com.example.undec;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class BottomNavView extends LinearLayout {
    private static final String TAG = "BottomNavView";
    private static final long CLICK_DEBOUNCE_TIME = 300; // ms

    private int[] iconIds = {
            R.drawable.ic_home,
            R.drawable.ic_tasks,
            R.drawable.ic_notes,
            R.drawable.ic_profile,
            R.drawable.ic_settings
    };

    private ImageView[] iconViews = new ImageView[5];
    private int selectedIndex = 0;
    private int activeColor;
    private int inactiveColor;
    private boolean isClickEnabled = true;
    private Handler handler = new Handler(Looper.getMainLooper());

    public interface OnNavItemSelected {
        void onItemSelected(int index);
    }

    private OnNavItemSelected listener;

    public BottomNavView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.bottom_nav_view, this, true);
        Log.d(TAG, "Initializing BottomNavView");

        // Get colors from resources
        activeColor = ContextCompat.getColor(context, R.color.nav_active);
        inactiveColor = ContextCompat.getColor(context, R.color.nav_inactive);

        iconViews[0] = findViewById(R.id.nav_home);
        iconViews[1] = findViewById(R.id.nav_tasks);
        iconViews[2] = findViewById(R.id.nav_notes);
        iconViews[3] = findViewById(R.id.nav_profile);
        iconViews[4] = findViewById(R.id.nav_settings);

        // Check if the views were found
        for (int i = 0; i < iconViews.length; i++) {
            if (iconViews[i] == null) {
                Log.e(TAG, "Failed to find navigation icon view at index " + i);
            }
        }

        for (int i = 0; i < iconViews.length; i++) {
            if (iconViews[i] != null) {
                final int index = i;
                iconViews[i].setImageResource(iconIds[i]);
                iconViews[i].setColorFilter(inactiveColor);
                
                // Set a custom touch effect
                iconViews[i].setOnClickListener(v -> {
                    if (!isClickEnabled) return; // Prevent rapid clicks
                    
                    // Disable clicks temporarily to prevent double-tap issues
                    isClickEnabled = false;
                    
                    // Visual feedback
                    v.animate()
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .setDuration(100)
                        .withEndAction(() -> 
                            v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start()
                        )
                        .start();
                    
                    Log.d(TAG, "Navigation icon clicked: " + index);
                    select(index);
                    
                    // Call the listener with a slight delay to allow animation to complete
                    if (listener != null) {
                        Log.d(TAG, "Calling listener for navigation index: " + index);
                        handler.postDelayed(() -> listener.onItemSelected(index), 50);
                    } else {
                        Log.e(TAG, "Navigation listener is null");
                        // Show a toast to indicate click was received but no listener
                        Toast.makeText(context, "Navigation item " + (index + 1) + " clicked", Toast.LENGTH_SHORT).show();
                    }
                    
                    // Re-enable clicks after a short delay
                    handler.postDelayed(() -> isClickEnabled = true, CLICK_DEBOUNCE_TIME);
                });
            }
        }

        select(0); // Default selected
        Log.d(TAG, "BottomNavView initialization complete");
    }

    private void select(int index) {
        Log.d(TAG, "Selecting navigation index: " + index);
        for (int i = 0; i < iconViews.length; i++) {
            if (iconViews[i] != null) {
                iconViews[i].setColorFilter(i == index ? activeColor : inactiveColor);
                
                // Only animate the scale for the selected item
                if (i == index) {
                    iconViews[i].animate()
                            .scaleX(1.15f)
                            .scaleY(1.15f)
                            .setDuration(200)
                            .start();
                } else {
                    iconViews[i].animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(200)
                            .start();
                }
            }
        }
        selectedIndex = index;
    }

    public void setOnNavItemSelected(OnNavItemSelected listener) {
        Log.d(TAG, "Setting navigation listener: " + (listener != null ? "valid" : "null"));
        this.listener = listener;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        Log.d(TAG, "setSelectedIndex called with index: " + index);
        if (index >= 0 && index < iconViews.length) {
            select(index);
        } else {
            Log.e(TAG, "Invalid index provided to setSelectedIndex: " + index);
        }
    }
    
    // Make sure the layout is fully shown by setting minimum dimensions
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Ensure minimum height
        int minHeight = (int) (getResources().getDisplayMetrics().density * 60); // 60dp
        int heightSpec = MeasureSpec.makeMeasureSpec(minHeight, MeasureSpec.EXACTLY);
        
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
