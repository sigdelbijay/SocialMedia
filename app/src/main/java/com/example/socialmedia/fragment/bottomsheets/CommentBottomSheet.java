package com.example.socialmedia.fragment.bottomsheets;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CommentBottomSheet extends BottomSheetDialogFragment {

    Context context;
    @BindView(R.id.comments_txt)
    TextView commentsTxt;
    @BindView(R.id.top_section)
    LinearLayout topSection;
    @BindView(R.id.comment_recy)
    RecyclerView commentRecy;
    @BindView(R.id.comment_edittext)
    EditText commentEdittext;
    @BindView(R.id.comment_send)
    ImageView commentSend;
    @BindView(R.id.comment_send_wrapper)
    RelativeLayout commentSendWrapper;
    @BindView(R.id.comment_top_wrapper)
    LinearLayout commentTopWrapper;
    Unbinder unbinder;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(context, R.layout.bottom_sheet_layout, null);
        unbinder = ButterKnife.bind(this, view);
        dialog.setContentView(view);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog dialog1 = (BottomSheetDialog) dialog;
//                FrameLayout bottomsheet = dialog1.findViewById()
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
