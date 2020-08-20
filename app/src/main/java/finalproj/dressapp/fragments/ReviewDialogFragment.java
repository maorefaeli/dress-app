package finalproj.dressapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import finalproj.dressapp.R;

public class ReviewDialogFragment extends DialogFragment {
    private RatingBar ratingBar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final LinearLayout dialogContainer =
                (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.review_window, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogContainer);

        final Dialog dialog = builder.create();
        ratingBar = dialogContainer.findViewById(R.id.ratingBar);

        ((TextView) dialogContainer.findViewById(R.id.itemTitle)).setText(getArguments().getString("title"));

        dialogContainer.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialogContainer.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rating = ratingBar.getNumStars();
                // send rating to server

                dialog.dismiss();
            }
        });

        return dialog;
    }
}
