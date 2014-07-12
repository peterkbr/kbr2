package hu.flexisys.kbr.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import hu.flexisys.kbr.R;

/**
 * Created by Peter on 2014.07.09..
 */
public class NotificationDialog extends KbrDialog {

    private String title;
    private String message;

    public static NotificationDialog newInstance(String title, String message) {
        NotificationDialog f = new NotificationDialog();
        f.layoutResId = R.layout.dialog_notification;
        f.title = title;
        f.message = message;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        if (title != null) {
            TextView titleTextView = (TextView) v.findViewById(R.id.dialog_notification_title);
            titleTextView.setText(title);
        }
        if (message != null) {
            TextView messageTextView = (TextView) v.findViewById(R.id.dialog_notification_message);
            messageTextView.setText(message);
        }
        Button ok = (Button) v.findViewById(R.id.dialog_notification_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }

}
