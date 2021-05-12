package com.example.yourdaymobile.ui.wall;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.yourdaymobile.R;
import com.example.yourdaymobile.ui.mainpage.MainPageViewModel;
import com.example.yourdaymobile.utilities.OnHttpActionDone;

import java.util.List;

public class AddNewPostDialog extends DialogFragment {
    private WallViewModel mViewModel;
    private String postDescription="";
    private DialogFragment dialog;
    private View view;
    private OnHttpActionDone onPostAdded;

    public AddNewPostDialog(WallViewModel mViewModel, OnHttpActionDone onPostAdded) {
        this.mViewModel = mViewModel;
        this.onPostAdded = onPostAdded;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Dodaj nowego posta");
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_todo, null);
        builder.setView(view);

        EditText editTextPostDesc = view.findViewById(R.id.etTodo);
        TextView textViewAdd = view.findViewById(R.id.tvAdd);
        TextView textViewCancel = view.findViewById(R.id.tvCancel);


        textViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDescription = editTextPostDesc.getText().toString();

                if ((!postDescription.matches(""))) {

                    //interfejs informuje nas kiedy doda sie nowy post i przekazujemy to do fragmentu żeby zaaktualizować liste
                    mViewModel.createPost(postDescription, new OnHttpActionDone() {
                        @Override
                        public void onDone() {
                            onPostAdded.onDone();
                        }
                    });

                    AddNewPostDialog.this.getDialog().cancel();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Wprowadź prawidłowe dane ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewPostDialog.this.getDialog().cancel();
            }
        });



        return builder.create();
    }






}
