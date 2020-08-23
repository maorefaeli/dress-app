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
                String rentId = Utils.getRentId();
                OrderReview orderReview = new OrderReview(rentId, rating);
                APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
                Call<Boolean> call = apiInterface.rentFinish(orderReview);
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.code() == 200) {
                            Toast.makeText(MyAppContext.getContext(), "Thank you for the feedback! You got 10 coins.", Toast.LENGTH_LONG).show();
                            Utils.setRentId("");
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

                dialog.dismiss();
            }
        });

        return dialog;
    }
}
