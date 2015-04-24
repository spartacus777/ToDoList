package todolist.kizema.anton.todolist.app;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class App extends Application {

    private static int height;
    private static int width;
    private static DisplayMetrics metrics;

    private static final int WIDTH_HD = 1080;
    private static final int HEIGHT_HD = 1920;

    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
        init();
    }

    public static Context getAppContext() {
        return context;
    }

    private void init(){
        context = getApplicationContext();
        height = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
        width = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        if (height < width){
            int tmp = width;
            width = height;
            height = tmp;
        }

        metrics = getResources().getDisplayMetrics();
    }

    public static int getW() {
        return width;
    }

    public static int getH() {
        return height;
    }

    public static int getRelativeW() {
        return width / WIDTH_HD;
    }

    public static int getRelativeH() {
        return height / HEIGHT_HD;
    }

    public static int getPixel(int dpi){
        return (int)(metrics.density * dpi);
    }

    public static float getSP( float px) {
        return px/metrics.scaledDensity;
    }
}