package todolist.kizema.anton.todolist.view;

import android.content.res.Configuration;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import todolist.kizema.anton.todolist.App;
import todolist.kizema.anton.todolist.R;
import todolist.kizema.anton.todolist.model.Entry;

/**
 * Created by Anton on 09.04.2015.
 */
public class ToDoViewEntry {

    private static final int DURATION = 500;

    private static final float SCALE_BIG = 1.2f;
    private static final float SCALE_NORM = 1.f;

    private RelativeLayout parent;

    private TextView titleView;
    private TextView descrView;
    private ImageView crossView;
    private ImageView removeView;

    private Entry model;

    private DeleteTouchListener deleteDelegate;
    private OnRemoveListener onRemoveListener;

    public interface OnRemoveListener{
        void onRemove(Entry entry);
    }

    public ToDoViewEntry(RelativeLayout parent) {
        this.parent = parent;
        init();
    }

    public Entry getEntry(){
        return model;
    }

    public void setEntry(Entry entry){
        if (model != null && entry.alive == model.alive){
            //all is ok
        } else {
            deleteDelegate.reset(entry.isAlive());
        }
        this.model = entry;

        titleView.setText(entry.title);
        descrView.setText(entry.description);
    }

    public void setOnRemoveListener(OnRemoveListener onRemoveListener){
        this.onRemoveListener = onRemoveListener;
    }

    private void init() {
        titleView = (TextView) parent.findViewById(R.id.tvTitle);
        descrView = (TextView) parent.findViewById(R.id.tvDescr);
        crossView = (ImageView) parent.findViewById(R.id.crossView);
        removeView = (ImageView) parent.findViewById(R.id.remove);

        removeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (removeView.getAlpha() == 1f) {
                    onRemoveListener.onRemove(model);
                }
            }
        });

        deleteDelegate = new DeleteTouchListener();
        crossView.setOnTouchListener(deleteDelegate);

        deleteDelegate.init();
    }

    private class DeleteTouchListener implements View.OnTouchListener{

        private static final int DELTA_TIME = 400;
        private int DELTA_DISTANCE = App.getPixel(3);

        private boolean userInteraction = false;
        private ImageView blurImage;
        float posX = getParentWidth() - App.getPixel(36+10+10);

        float downX;
        long timeDown;

        public DeleteTouchListener(){

        }

        private void reset(boolean isAlive){
            Log.d("ANT", "RESET:: isAlive "+isAlive);
            if (isAlive) {
                blurImage.setX(getParentWidth());
                crossView.setX(posX);
                removeView.setAlpha(0f);
            } else {
                blurImage.setX(0);
                crossView.setX(0);
                removeView.setAlpha(1f);
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    downX = crossView.getX();
                    timeDown = System.currentTimeMillis();
                    userInteraction = true;
                    pen_down();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    Log.d("ANT", "event.getX():"+event.getX() + "  event.getRawX():"+event.getRawX() +"  final coords x:"+(event.getRawX()- crossView.getWidth()/2));
                    crossView.setX(event.getRawX() - crossView.getWidth() / 2);
                    blurImage.setX(crossView.getX());
                    return true;

                case MotionEvent.ACTION_UP:
                    Log.d("ANT", "ACTION_UP");
                    userInteraction = false;
                    pen_up();
                    touchUpCancel();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    Log.d("ANT", "ACTION_CANCEL");
                    userInteraction = false;
                    pen_up();
                    touchUpCancel();
                    break;
            }

            return true;
        }

        private void touchUpCancel(){
            if (System.currentTimeMillis() - timeDown < DELTA_TIME && Math.abs(downX- crossView.getX()) > DELTA_DISTANCE){
                if (downX- crossView.getX() > 0){
                    performSwipeLeft();
                } else {
                    performSwipeRight();
                }
            } else {
                if (crossView.getX() > parent.getWidth()/2){
                    performSwipeRight();
                } else {
                    performSwipeLeft();
                }
            }
        }

        private void performSwipeLeft(){
            int dur = (int) ( DURATION * crossView.getX()/(parent.getWidth()- crossView.getWidth()) );

            if (dur<0)
                dur = 10;

            crossView.animate().x(0f).setDuration(dur).start();
            blurImage.animate().x(0f).setDuration(dur).start();
            removeView.animate().alpha(1f).setDuration(dur).start();
            model.alive = false;
        }

        private void performSwipeRight(){
            int dur = (int) ( DURATION * (1- crossView.getX()/(parent.getWidth()- crossView.getWidth())) );

            if (dur<0)
                dur = 10;

            Log.d("ANT", "posX "+posX);
            crossView.animate().x(posX).setDuration(dur).start();
            blurImage.animate().x(getParentWidth()).setDuration(dur).start();
            removeView.animate().alpha(0f).setDuration(dur).start();
            model.alive = true;
        }

        private void pen_down(){
            crossView.setScaleX(SCALE_BIG);
            crossView.setScaleY(SCALE_BIG);
        }

        private void pen_up(){
            crossView.setScaleX(SCALE_NORM);
            crossView.setScaleY(SCALE_NORM);
        }

        public void init() {
            Log.d("ANT", "onWindowFocusChanged");

            if (blurImage!=null) {
                parent.removeView(blurImage);
            }

            blurImage = new ImageView(parent.getContext());

            Log.d("ANT", "parent.getHeight() "+parent.getMeasuredHeight()+" parent.getW "+parent.getMeasuredWidth());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    App.getPixel(50));

            blurImage.setLayoutParams(params);
            blurImage.setBackgroundColor(parent.getResources().getColor(R.color.blur_color));
            blurImage.setX(getParentWidth());
            parent.addView(blurImage);

            removeView.bringToFront();
            crossView.bringToFront();
        }

    }

    public int getParentWidth(){
        return (int) ( getW() - 2*parent.getContext().getResources().getDimension(R.dimen.activity_horizontal_margin) );
    }

    public int getW(){
        if ( parent.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            return App.getW();

        return  App.getH();
    }
}
