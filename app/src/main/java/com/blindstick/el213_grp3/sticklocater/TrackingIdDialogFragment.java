package com.blindstick.el213_grp3.sticklocater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onurciner.toastox.ToastOX;
import dmax.dialog.SpotsDialog;

public class TrackingIdDialogFragment extends DialogFragment {

    public interface OnDataPass {
        public void onDataPass(String data);
    }
    OnDataPass dataPasser;

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        dataPasser = (OnDataPass) a;
    }

    EditText et_tracingId;
    int flag=0;
    String trackingId;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference root;
    private AlertDialog progressDialog;
    Context context;
    Button btn_trackingAnotherStick;
    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.myBackgroundStyle));
        View trackingIdDialogView =
                getActivity().getLayoutInflater().inflate(
                        R.layout.fragment_tracking_id, null);
        builder.setView(trackingIdDialogView);
        setCancelable(false);
        et_tracingId = (EditText)trackingIdDialogView.findViewById(R.id.et_trackingId);

        progressDialog = new SpotsDialog(getContext(), R.style.Custom);
        progressDialog.setCancelable(false);


        builder.setTitle("Enter Tracking ID:");

        builder.setPositiveButton("Locate",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                }
        );

        context=getContext();
        return builder.create();
    }
    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        btn_trackingAnotherStick = (Button) getActivity().findViewById(R.id.btn_trackAnotherStick);
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    trackingId = et_tracingId.getText().toString();
                    if (!trackingId.matches("^[A-Za-z][A-Za-z0-9]*[0-9]$")) {
                        ToastOX.error(getContext(), "Please Enter Valid Tracking ID");
                        et_tracingId.setText("");
                    } else {
                        progressDialog.show();
                        final Runnable progressRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if(flag==0) {
                                    ToastOX.Tnull(context, "Please Check Your Network Connection and Try Again Later...",Toast.LENGTH_LONG);
                                    progressDialog.dismiss();
                                }
                            }
                        };

                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 30000);
                        firebaseDatabase = FirebaseDatabase.getInstance();
                        root = firebaseDatabase.getReference();
                        root.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(trackingId)) {
                                    progressDialog.dismiss();
                                    flag=1;
                                    btn_trackingAnotherStick.setVisibility(View.VISIBLE);
                                    dataPasser.onDataPass(trackingId);
                                    dismiss();
                                } else {
                                    ToastOX.error(getContext(), "User Not Found");
                                    progressDialog.dismiss();
                                    flag=1;
                                    et_tracingId.setText("");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        ((MapsActivity)getActivity()).getLocation();
    }

}


