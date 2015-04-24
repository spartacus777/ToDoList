package todolist.kizema.anton.todolist.helpers;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import todolist.kizema.anton.todolist.app.App;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private static final int SWIPE_THRESHOLD = 250;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private int swipeTreshold;

    private final GestureDetector gestureDetector;
    private OnSwipeListener listener;

    public interface OnSwipeListener{
        void onSwipeRight();
        void onSwipeLeft();
        void onSwipeTop();
        void onSwipeBottom();
    }


    public OnSwipeTouchListener(Context ctx) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        swipeTreshold = App.getRelativeW() * SWIPE_THRESHOLD;
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > swipeTreshold
                            && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            getOnSwipeListener().onSwipeRight();
                        } else {
                            getOnSwipeListener().onSwipeLeft();
                        }
                    }
                    result = true;
                } else if (Math.abs(diffY) > swipeTreshold
                        && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        getOnSwipeListener().onSwipeBottom();
                    } else {
                        getOnSwipeListener().onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void setOnSwipeListener(OnSwipeListener listener){
        this.listener = listener;
    }

    public OnSwipeListener getOnSwipeListener(){
        return listener;
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
