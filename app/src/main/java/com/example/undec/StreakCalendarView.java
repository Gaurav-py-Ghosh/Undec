package com.example.undec;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreakCalendarView extends View {
    // Add these properties at the top
    private int streakCount = 0;
    private List<Integer> completedDays = new ArrayList<>();
    private Map<Integer, DayStatus> dayStatusMap = new HashMap<>();
    private Paint completedPaint, notCompletedPaint, inProgressPaint, textPaint;
    private Rect dayRect = new Rect();



    // Day status enum
    public enum DayStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }

    public StreakCalendarView(Context context) {
        super(context);
        init();
    }

    public StreakCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        completedPaint = new Paint();
        completedPaint.setColor(getResources().getColor(R.color.streak_completed));
        completedPaint.setStyle(Paint.Style.FILL);

        notCompletedPaint = new Paint();
        notCompletedPaint.setColor(getResources().getColor(R.color.streak_future));
        notCompletedPaint.setStyle(Paint.Style.FILL);
        
        inProgressPaint = new Paint();
        inProgressPaint.setColor(getResources().getColor(R.color.streak_in_progress));
        inProgressPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.text_primary));
        textPaint.setTextSize(36f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Calculate desired height (6 rows of cells)
        int cellSize = MeasureSpec.getSize(widthMeasureSpec) / 7;
        int desiredHeight = cellSize * 6;

        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                resolveSize(desiredHeight, heightMeasureSpec)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int cellSize = width / 7;

        // Draw calendar grid
        for (int day = 1; day <= 31; day++) {
            int row = (day - 1) / 7;
            int col = (day - 1) % 7;

            int left = col * cellSize;
            int top = row * cellSize;
            int right = left + cellSize;
            int bottom = top + cellSize;

            dayRect.set(left, top, right, bottom);

            // Determine the paint to use based on day status
            Paint dayPaint;
            if (dayStatusMap.containsKey(day)) {
                DayStatus status = dayStatusMap.get(day);
                if (status == DayStatus.COMPLETED) {
                    dayPaint = completedPaint;
                } else if (status == DayStatus.IN_PROGRESS) {
                    dayPaint = inProgressPaint;
                } else {
                    dayPaint = notCompletedPaint;
                }
            } else if (completedDays.contains(day)) {
                dayPaint = completedPaint;
            } else {
                dayPaint = notCompletedPaint;
            }
            
            canvas.drawRect(dayRect, dayPaint);

            // Draw day number
            float x = dayRect.centerX();
            float y = dayRect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2;
            canvas.drawText(String.valueOf(day), x, y, textPaint);
        }

        // Draw streak count at bottom
        canvas.drawText("Streak: " + streakCount + " days",
                width / 2,
                getHeight() - 20,
                textPaint);
    }

    public void setCompletedDays(List<Integer> days) {
        this.completedDays = new ArrayList<>(days);
        invalidate();
    }

    public void setStreakCount(int count) {
        this.streakCount = count;
        invalidate();
    }
    
    /**
     * Get the current streak count
     * @return The current streak count
     */
    public int getStreakCount() {
        return streakCount;
    }
    
    /**
     * Set the status of a specific day
     * @param day The day of the month (1-31)
     * @param status The status to set for the day
     */
    public void setDayStatus(int day, DayStatus status) {
        if (day >= 1 && day <= 31) {
            dayStatusMap.put(day, status);
            invalidate();
        }
    }
}