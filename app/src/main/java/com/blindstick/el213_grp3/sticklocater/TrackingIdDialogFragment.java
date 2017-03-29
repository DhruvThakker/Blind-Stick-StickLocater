package com.blindstick.el213_grp3.sticklocater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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

public class TrackingIdDialogFragment extends DialogFragment {

    EditText et_tracingId;
    String trackingId;
    Button btn_locate;
    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        View trackingIdDialogView =
                getActivity().getLayoutInflater().inflate(
                        R.layout.fragment_tracking_id, null);
        builder.setView(trackingIdDialogView);
        et_tracingId = (EditText)trackingIdDialogView.findViewById(R.id.et_trackingId);

        builder.setTitle("Enter Tracking ID:");

        builder.setPositiveButton("Locate",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                }
        );

        return builder.create();
    }
    @Override
    public void onStart()
    {
        super.onStart();
        AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Boolean wantToCloseDialog = false;
                    trackingId = et_tracingId.getText().toString();
                    if(!trackingId.matches("[A-Za-z0-9]+")){
                        Toast.makeText(getContext(),"Please Enter Valid Tracking Id", Toast.LENGTH_SHORT).show();
                        et_tracingId.setText("");
                    }
                    else{
                        Toast.makeText(getContext(),"Locating", Toast.LENGTH_SHORT).show();
                        wantToCloseDialog = true;
                    }

                    if(wantToCloseDialog)
                        dismiss();
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            });
        }
    }


}


