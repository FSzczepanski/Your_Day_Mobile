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

public class EditTodoDialog extends DialogFragment {
    private MainPageViewModel mViewModel;
    private String todoDescription="";
    private DialogFragment dialog;
    private View view;
    private OnHttpActionDone done;
    private String id;

    public EditTodoDialog(MainPageViewModel mViewModel,String id, OnHttpActionDone done) {
        this.mViewModel = mViewModel;
        this.id = id;
        this.done = done;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Edytuj zadanie");
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_todo, null);
        builder.setView(view);

        EditText editTextTodoDesc = view.findViewById(R.id.etTodo);
        TextView textViewAdd = view.findViewById(R.id.tvAdd);
        TextView textViewCancel = view.findViewById(R.id.tvCancel);
        TextView textViewDelete = view.findViewById(R.id.tvDelete);


        textViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todoDescription = editTextTodoDesc.getText().toString();

                if ((!todoDescription.matches(""))) {

                    //interfejs informuje nas kiedy doda sie nowe zadanie i przekazujemy to do fragmentu żeby zaaktualizować liste
                    mViewModel.updateTodo(todoDescription, id, new OnHttpActionDone() {
                        @Override
                        public void onDone() {
                            done.onDone();
                        }
                    });

                    EditTodoDialog.this.getDialog().cancel();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Wprowadź prawidłowe dane ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTodoDialog.this.getDialog().cancel();
            }
        });

        textViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.deleteTodo(id, new OnHttpActionDone() {
                    @Override
                    public void onDone() {
                        done.onDone();
                        EditTodoDialog.this.getDialog().cancel();
                    }
                });
            }
        });



        return builder.create();
    }
}