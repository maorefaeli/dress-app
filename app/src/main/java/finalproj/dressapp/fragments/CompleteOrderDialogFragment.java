package finalproj.dressapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.LinearLayout;

import finalproj.dressapp.R;
import finalproj.dressapp.httpclient.APIClient;
import finalproj.dressapp.httpclient.APIInterface;
import finalproj.dressapp.httpclient.models.OrderReview;

public class CompleteOrderDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final LinearLayout dialogContainer =
                (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.complete_order_window, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogContainer);

        final Dialog dialog = builder.create();

        dialogContainer.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialogContainer.findViewById(R.id.yes).setOnClickListener(view -> {
            dialog.dismiss();
            // send order completed message to server

            ReviewDialogFragment reviewDialogFragment = new ReviewDialogFragment();
            reviewDialogFragment.setArguments(getArguments());
            reviewDialogFragment.show(getFragmentManager(), "reviewDialog");
            getActivity().recreate();
        });
        return dialog;
    }
}
