package com.example.undec;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class BottomNavBar extends LinearLayout {

    private int[] iconResIds = {
            R.drawable.ic_launcher_foreground,
            R.drawable.ic_launcher_foreground,
            R.drawable.ic_launcher_foreground,
            R.drawable.ic_launcher_foreground
    };

    private int selectedIndex = 0;

    public BottomNavBar(Context context) {
        super(context);
        init();
    }

    public BottomNavBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomNavBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        for (int i = 0; i < iconResIds.length; i++) {
            final int index = i;

            ImageView icon = new ImageView(getContext());
            LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
            params.gravity = Gravity.CENTER;
            icon.setLayoutParams(params);
            icon.setImageResource(iconResIds[i]);
            icon.setPadding(24, 24, 24, 24);

            if (i == selectedIndex) {
                wrapWithCircle(icon);
            }

            icon.setOnClickListener(v -> {
                selectedIndex = index;
                refreshIcons();
            });

            addView(icon);
        }
    }

    private void refreshIcons() {
        for (int i = 0; i < getChildCount(); i++) {
            ImageView icon = (ImageView) getChildAt(i);
            if (i == selectedIndex) {
                wrapWithCircle(icon);
            } else {
                unwrap(icon);
            }
        }
    }

    private void wrapWithCircle(ImageView icon) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(ContextCompat.getColor(getContext(), R.color.infoColor));
        background.setCornerRadius(100f); // circle
        icon.setBackground(background);
        icon.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white));
    }

    private void unwrap(ImageView icon) {
        icon.setBackground(null);
        icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.textSecondary));
    }
}
