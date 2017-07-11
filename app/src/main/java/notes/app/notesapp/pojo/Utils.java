package notes.app.notesapp.pojo;

import android.app.Activity;
import android.content.Intent;

import notes.app.notesapp.R;

/**
 * Created by simranjain1507 on 11/07/17.
 */

public class Utils {
    private final static int THEME_DEFAULT = 0;
    private final static int THEME_ORANGE = 1;
    private final static int THEME_PINK = 2;
    private final static int THEME_RED = 3;
    private final static int THEME_BLUE = 4;
    private final static int THEME_BROWN = 5;
    private final static int THEME_AMBER = 6;
    private final static int THEME_TEAL = 7;

    private static int stheme;

    public static void changeTotheme(Activity activity, int theme) {
        stheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));

    }

    public static void onActivityCreateSetTheme(Activity activity) {
        switch (stheme) {
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;
            case THEME_ORANGE:
                activity.setTheme(R.style.AppTheme_Orange);
                break;
            case THEME_PINK:
                activity.setTheme(R.style.AppTheme_Pink);
                break;
            case THEME_RED:
                activity.setTheme(R.style.AppTheme_Red);
                break;
            case THEME_BLUE:
                activity.setTheme(R.style.AppTheme_Blue);
                break;
            case THEME_BROWN:
                activity.setTheme(R.style.AppTheme_Brown);
                break;
            case THEME_AMBER:
                activity.setTheme(R.style.AppTheme_Amber);
                break;
            case THEME_TEAL:
                activity.setTheme(R.style.AppTheme_Teal);
                break;
        }
    }
}
