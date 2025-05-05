package com.example.undec;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StreakCalendarView extends View {
    private final int columns = 7;
    private final int rows = 5;
    private final int totalDays = 31;
    private Paint filledPaint, emptyPaint, firePaint, capsulePaint, textPaint, inProgressPaint;

    enum DayStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }
    private DayStatus[] dayStatus = new DayStatus[totalDays];

    private int streakCount = 0;
    private static final String TAG = "StreakCalendarView";

    private final float circleRadius = 40f;
    private final float spacing = 30f;

    private final String[] weekdays = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

    public StreakCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
        // Initialize all days as NOT_STARTED
        Arrays.fill(dayStatus, DayStatus.NOT_STARTED);
        Log.d(TAG, "StreakCalendarView initialized");
    }

    private void initPaints() {
        filledPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        filledPaint.setColor(Color.parseColor("#FF9800"));
        filledPaint.setStyle(Paint.Style.FILL);

        emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPaint.setColor(Color.WHITE);
        emptyPaint.setStyle(Paint.Style.FILL);

        capsulePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        capsulePaint.setColor(Color.parseColor("#22223B"));
        capsulePaint.setStyle(Paint.Style.FILL);

        firePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        firePaint.setColor(Color.WHITE);
        firePaint.setTextSize(40f);
        firePaint.setTextAlign(Paint.Align.CENTER);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(35f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        inProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        inProgressPaint.setColor(Color.parseColor("#FFC107")); // Yellow for in-progress
        inProgressPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float requiredHeight = spacing + // Top padding
                spacing + // Weekday labels height
                (rows * (2 * circleRadius + spacing)) + // Days grid
                spacing * 2; // Bottom padding + streak count text

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) requiredHeight;

        Log.d(TAG, "onMeasure - width: " + width + ", height: " + height);
        setMeasuredDimension(width, height);
    }

    public void setCompletedDays(List<Integer> completedDays) {
        Arrays.fill(dayStatus, DayStatus.NOT_STARTED);
        for (int day : completedDays) {
            if (day >= 1 && day <= 31) {
                dayStatus[day - 1] = DayStatus.COMPLETED;
            }
        }
        Log.d(TAG, "setCompletedDays: " + completedDays.size() + " days marked as completed");
        invalidate();
    }

    public void setDayStatus(int day, DayStatus status) {
        if (day >= 1 && day <=dayStatus.length) {
            dayStatus[day - 1] = status;
            invalidate();
        }
    }


    public int getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(int count) {
        this.streakCount = count;
        invalidate(); // Redraw the view to show updated streak count
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startX = spacing;
        float startY = spacing * 2;

        Log.d(TAG, "onDraw - drawing calendar with width: " + getWidth() + ", height: " + getHeight());

        // Draw day labels
        for (int i = 0; i < 7; i++) {
            canvas.drawText(weekdays[i], startX + i * (circleRadius * 2 + spacing), spacing, textPaint);
        }

        // Draw calendar days
        for (int i = 0; i < totalDays; i++) {
            int row = i / columns;
            int col = i % columns;

            float cx = startX + col * (circleRadius * 2 + spacing);
            float cy = startY + row * (circleRadius * 2 + spacing);

            // Only draw capsule connecting streak days if they're completed
            if (isPartOfStreak(i)) {
                canvas.drawRoundRect(
                        cx - circleRadius - 10,
                        cy - circleRadius - 10,
                        cx + circleRadius + 10,
                        cy + circleRadius + 10,
                        30,
                        30,
                        capsulePaint
                );
            }

            // Draw all circles as white by default
            canvas.drawCircle(cx, cy, circleRadius, emptyPaint);

            // Handle different day statuses
            switch (dayStatus[i]) {
                case COMPLETED:
                    // Glow effect for completed days
                    Paint glowPaint = new Paint();
                    glowPaint.set(filledPaint);
                    glowPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
                    canvas.drawCircle(cx, cy, circleRadius + 5, glowPaint);

                    // Draw orange filled circle over the white one
                    canvas.drawCircle(cx, cy, circleRadius, filledPaint);

                    // Draw the fire emoji
                    canvas.drawText("🔥", cx, cy + 15, firePaint);
                    break;

                case IN_PROGRESS:
                    // Draw yellow circle for in-progress days
                    canvas.drawCircle(cx, cy, circleRadius, inProgressPaint);
                    break;

                case NOT_STARTED:
                default:
                    canvas.drawCircle(cx, cy, circleRadius, textPaint);
                    break;

            }

            // Draw the day number (1-31)
            String dayText = String.valueOf(i + 1);
            Paint dayTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            dayTextPaint.setColor(dayStatus[i] == DayStatus.COMPLETED ? Color.WHITE :
                    dayStatus[i] == DayStatus.IN_PROGRESS ? Color.BLACK : Color.BLACK);
            dayTextPaint.setTextSize(25f);
            dayTextPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(dayText, cx, cy - 10, dayTextPaint);
        }

        // Draw streak count
        String streakText = streakCount + " day streak";
        canvas.drawText(streakText, getWidth() / 2f, getHeight() - spacing, textPaint);
    }

    private boolean isPartOfStreak(int i) {
        return dayStatus[i] == DayStatus.COMPLETED &&
                ((i > 0 && dayStatus[i - 1] == DayStatus.COMPLETED) ||
                        (i < dayStatus.length - 1 && dayStatus[i + 1] == DayStatus.COMPLETED));
    }

    public void markTodayComplete() {
        int currentDay = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
        if (currentDay >= 1 && currentDay <= 31) {
            dayStatus[currentDay - 1] = DayStatus.COMPLETED;
            Log.d(TAG, "markTodayComplete: day " + currentDay + " marked as complete");
            postDelayed(this::animateCompletionEffect, 100);
        }
        invalidate();
    }

    public void markTodayInProgress() {
        int currentDay = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
        if (currentDay >= 1 && currentDay <= 31) {
            dayStatus[currentDay - 1] = DayStatus.IN_PROGRESS;
            Log.d(TAG, "markTodayInProgress: day " + currentDay + " marked as in progress");
            invalidate();
        }
    }

    private void animateCompletionEffect() {
        setAlpha(0.7f);
        animate()
                .alpha(1f)
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(200)
                .withEndAction(() ->
                        animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start()
                )
                .start();
    }
}