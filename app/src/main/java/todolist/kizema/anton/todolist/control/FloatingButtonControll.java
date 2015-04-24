package todolist.kizema.anton.todolist.control;

import android.view.View;

import todolist.kizema.anton.todolist.app.App;
import todolist.kizema.anton.todolist.app.AppConstants;

public class FloatingButtonControll {

    private View view;
    private float y;
//    boolean isFirstTime = true;
    boolean scrollUp = true;

    public FloatingButtonControll(View view, float y){
        this.view = view;
        this.y = y;
    }

    public void onScroll(boolean up){

        if (up == scrollUp){
            return;
        }

        if (!up){

//            if (isFirstTime) {
//                y = view.getY();
//                isFirstTime = false;
//            }

            view.animate().y(App.getH()+100).setDuration(AppConstants.FLOAT_BTN_DURATION).start();
        } else {
            view.animate().y(y).setDuration(AppConstants.FLOAT_BTN_DURATION).start();
        }

        scrollUp = up;
    }

}
