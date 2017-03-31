package com.blindstick.el213_grp3.sticklocater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    String trackingId;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference root,user;
    Context context;
    Button btn_locate;
    Button btn_trackingAnotherStick;
    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        View trackingIdDialogView =
                getActivity().getLayoutInflater().inflate(
                        R.layout.fragment_tracking_id, null);
        builder.setView(trackingIdDialogView);
        setCancelable(false);
        et_tracingId = (EditText)trackingIdDialogView.findViewById(R.id.et_trackingId);

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
                        Toast.makeText(context, "Please Enter Valid Tracking Id", Toast.LENGTH_SHORT).show();
                        et_tracingId.setText("");
                    } else {
                        firebaseDatabase = FirebaseDatabase.getInstance();
                        root = firebaseDatabase.getReference();
                        root.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(trackingId)) {
                                    Toast.makeText(context, "Locating...", Toast.LENGTH_LONG).show();
                                    btn_trackingAnotherStick.setVisibility(View.VISIBLE);
                                    dataPasser.onDataPass(trackingId);
                                    dismiss();
                                } else {
                                    Toast.makeText(context, "user doesn't exist", Toast.LENGTH_LONG).show();
                                    et_tracingId.setText("");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
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


