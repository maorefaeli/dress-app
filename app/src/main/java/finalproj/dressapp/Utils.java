package finalproj.dressapp;

import android.app.Activity;
import android.app.ProgressDialog;

public class Utils {
    static ProgressDialog dialog = null;
    public static void showPopupProgressSpinner(Activity activity, Boolean isShowing) {
        if (isShowing) {
            dialog = ProgressDialog.show(activity, "", "Just a moment...", true);
        } else {
            dialog.dismiss();
        }

    }
}