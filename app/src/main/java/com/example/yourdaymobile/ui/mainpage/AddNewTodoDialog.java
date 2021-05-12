package com.example.yourdaymobile.ui.mainpage;

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
import com.example.yourdaymobile.utilities.OnHttpActionDone;

import java.util.List;

public class AddNewTodoDialog extends DialogFragment {
    private MainPageViewModel mViewModel;
    private String todoDescription="";
    private DialogFragment dialog;
    private View view;
    private OnHttpActionDone onTodoAdded;

    public AddNewTodoDialog(MainPageViewModel mViewModel, OnHttpActionDone onTodoAdded) {
        this.mViewModel = mViewModel;
        this.onTodoAdded = onTodoAdded;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Dodaj nowe zadanie");
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_todo, null);
        builder.setView(view);

        EditText editTextTodoDesc = view.findViewById(R.id.etTodo);
        TextView textViewAdd = view.findViewById(R.id.tvAdd);
        TextView textViewCancel = view.findViewById(R.id.tvCancel);


        textViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todoDescription = editTextTodoDesc.getText().toString();

                if ((!todoDescription.matches(""))) {

                    //interfejs informuje nas kiedy doda sie nowe zadanie i przekazujemy to do fragmentu żeby zaaktualizować liste
                    mViewModel.createTodo(todoDescription, new OnHttpActionDone() {
                        @Override
                        public void onDone() {
                            onTodoAdded.onDone();
                        }
                    });

                    AddNewTodoDialog.this.getDialog().cancel();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Wprowadź prawidłowe dane ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTodoDialog.this.getDialog().cancel();
            }
        });



        return builder.create();
    }






}
