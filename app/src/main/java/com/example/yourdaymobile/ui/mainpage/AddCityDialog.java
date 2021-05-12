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

public class AddCityDialog extends DialogFragment {
    private MainPageViewModel mViewModel;
    private String city="";
    private DialogFragment dialog;
    private View view;
    private WeatherCallback oncityAdded;

    public AddCityDialog(MainPageViewModel mViewModel, WeatherCallback oncityAdded) {
        this.mViewModel = mViewModel;
        this.oncityAdded = oncityAdded;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Zmień miasto");
        View view = getActivity().getLayoutInflater().inflate(R.layout.weather_dialog, null);
        builder.setView(view);

        EditText cityEdit = view.findViewById(R.id.etcity);
        TextView textViewAdd = view.findViewById(R.id.tvAdd);
        TextView textViewCancel = view.findViewById(R.id.tvCancel);


        textViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city = cityEdit.getText().toString();

                if ((!city.matches(""))) {

                    //interfejs informuje nas kiedy doda sie nowe zadanie i przekazujemy to do fragmentu żeby zaaktualizować liste
                    mViewModel.getWeather(city, new WeatherCallback() {
                        @Override
                        public void onCallback(String city,String temp, String weatherDescription) {
                            oncityAdded.onCallback(city,temp,weatherDescription);
                        }
                    });


                    AddCityDialog.this.getDialog().cancel();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Wprowadź prawidłowe dane ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCityDialog.this.getDialog().cancel();
            }
        });



        return builder.create();
    }






}
