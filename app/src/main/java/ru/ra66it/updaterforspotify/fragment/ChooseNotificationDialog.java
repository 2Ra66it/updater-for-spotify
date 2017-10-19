package ru.ra66it.updaterforspotify.fragment;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import ru.ra66it.updaterforspotify.QueryPreferneces;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.notification.PollService;

/**
 * Created by 2Rabbit on 14.10.2017.
 */

public class ChooseNotificationDialog extends DialogFragment {

    private LinearLayout layoutSptfDF;
    private LinearLayout layoutSptfOrigin;
    private Button buttonAccept;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.choose_notif_dialog, container, false);


        layoutSptfDF = (LinearLayout) v.findViewById(R.id.layout_choose_sptf_df);
        layoutSptfDF.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        layoutSptfDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutSptfDF.setBackgroundColor(getResources().getColor(R.color.darkChoose));
                layoutSptfOrigin.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

            }
        });

        layoutSptfOrigin = (LinearLayout) v.findViewById(R.id.layout_choose_sptf_origin);
        layoutSptfOrigin.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        layoutSptfOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutSptfOrigin.setBackgroundColor(getResources().getColor(R.color.darkChoose));
                layoutSptfDF.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

            }
        });


        buttonAccept = (Button) v.findViewById(R.id.btn_accept);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ColorDrawable viewColorDF = (ColorDrawable) layoutSptfDF.getBackground();
                int colorDF = viewColorDF.getColor();

                ColorDrawable viewColorOR = (ColorDrawable) layoutSptfOrigin.getBackground();
                int colorOR = viewColorOR.getColor();

                if (colorDF == Color.parseColor("#0c0c0f")) {
                    QueryPreferneces.setNotificationDogFood(getActivity(), true);
                    QueryPreferneces.setNotificationOrigin(getActivity(), false);
                    PollService.setServiceAlarm(getActivity(), QueryPreferneces.getNotificationDogFood(getActivity()));
                    dismiss();
                } else if (colorOR == Color.parseColor("#0c0c0f")){
                    QueryPreferneces.setNotificationOrigin(getActivity(), true);
                    QueryPreferneces.setNotificationDogFood(getActivity(), false);
                    PollService.setServiceAlarm(getActivity(), QueryPreferneces.getNotificationOrigin(getActivity()));
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), R.string.toast_choose_version, Toast.LENGTH_SHORT).show();
                    return;
                }

                QueryPreferneces.setFirstLaunch(getActivity(), false);

            }
        });


        return v;
    }
}
