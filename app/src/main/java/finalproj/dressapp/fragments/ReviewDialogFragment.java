package finalproj.dressapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import finalproj.dressapp.Utils;
import finalproj.dressapp.httpclient.models.MyAppContext;

import java.util.List;

import finalproj.dressapp.R;
import finalproj.dressapp.activities.OrdersActivity;
import finalproj.dressapp.httpclient.APIClient;
import finalproj.dressapp.httpclient.APIInterface;
import finalproj.dressapp.httpclient.models.RentProduct;
import finalproj.dressapp.httpclient.models.OrderReview;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewDialogFragment extends DialogFragment {
    private RatingBar ratingBar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final LinearLayout dialogContainer =
                (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.review_window, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogContainer);

        final Dialog dialog = builder.create();
        ratingBar = dialogContainer.findViewById(R.id.ratingBar);
        ratingBar.setOnClickListener(view ->
                dialogContainer.findViewById(R.id.pleaseReview).setVisibility(View.GONE));

        ((TextView) dialogContainer.findViewById(R.id.itemTitle)).setText(getArguments().getString("title"));

        dialogContainer.findViewById(R.id.ok).setOnClickListener(view -> {
            int rating = (int) ratingBar.getRating();
            if (rating > 0) {
                sendReview(rating);
            } else {
                dialogContainer.findViewById(R.id.pleaseReview).setVisibility(View.VISIBLE);
            }

            dialog.dismiss();
        });

        final Activity activity = getActivity();
        dialogContainer.findViewById(R.id.openDispute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
                Call<Boolean> call = apiInterface.disputeOrder(Utils.getRentId());
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.code() == 200) {
                            Toast.makeText(getContext(),
                                    "Opened Dispute!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        Toast.makeText(getContext(),
                                "Cannot open dispute right now, please try again later",
                                Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.disputeOpened)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            dialog.dismiss();
                            activity.recreate();
                        });

                TextView textView = new TextView(getContext());
                textView.setText(R.string.disputeContact);
                textView.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setGravity(Gravity.CENTER);
                builder.setView(textView);

                builder.create().show();
            }
        });

        return dialog;
    }

    private void sendReview(int rating) {
        String rentId = Utils.getRentId();
        final Activity activity = getActivity();
        OrderReview orderReview = new OrderReview(rentId, rating);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<Boolean> call = apiInterface.rentFinish(orderReview);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.code() == 200) {
                    Toast.makeText(MyAppContext.getContext(), "Thank you for the feedback! You got 10 coins.", Toast.LENGTH_LONG).show();
                    Utils.setRentId("");
                    Utils.loadUserDetails();
                    activity.recreate();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                new AlertDialog.Builder(MyAppContext.getContext())
                        .setTitle("failure")
                        .setMessage(t.getMessage())
                        .show();
                call.cancel();
            }
        });
    }
}
