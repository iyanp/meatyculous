package com.example.mainfunctext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class FastScroller extends View {
    private RecyclerView recyclerView;
    private Paint paint = new Paint();
    private int thumbHeight = 50;
    private int thumbWidth = 20;
    private int scrollY = 0;

    public FastScroller(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.GRAY); // Customize color
        paint.setStyle(Paint.Style.FILL);
    }

    public void attachToRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                updateThumbPosition();
            }
        });
    }

    private void updateThumbPosition() {
        if (recyclerView != null) {
            int verticalRange = recyclerView.computeVerticalScrollRange();
            int verticalOffset = recyclerView.computeVerticalScrollOffset();
            int verticalExtent = recyclerView.computeVerticalScrollExtent();

            float proportion = (float) verticalOffset / (float) (verticalRange - verticalExtent);
            scrollY = (int) (proportion * (getHeight() - thumbHeight));
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw the thumb
        canvas.drawRect(getWidth() - thumbWidth, scrollY, getWidth(), scrollY + thumbHeight, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            float y = event.getY();
            float proportion = y / (float) getHeight();
            if (recyclerView != null) {
                int targetScrollY = (int) (proportion * (recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent()));
                recyclerView.scrollBy(0, targetScrollY - recyclerView.computeVerticalScrollOffset());
                updateThumbPosition();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }
}
