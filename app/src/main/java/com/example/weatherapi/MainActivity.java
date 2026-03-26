package com.example.weatherapi;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private TextView result;
    private Button search;
    private Spinner time_spinner, element_spinner, location_spinner;
    private String selected_time, selected_element, selected_location;
    private ApiClient apiClient;
    private GetApi getApi;
    private String[] location_data, element_data, time_data, tw_element;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.main_result_rt);
        search = findViewById(R.id.main_search_btn);
        time_spinner = findViewById(R.id.time);
        element_spinner = findViewById(R.id.elementName);
        location_spinner = findViewById(R.id.locationName);
        apiClient = new ApiClient();
        getApi = apiClient.myWeatherApi().create(GetApi.class);
        location_data = getResources().getStringArray(R.array.location_data);
        element_data = getResources().getStringArray(R.array.element_data);
        time_data = getResources().getStringArray(R.array.time_data);
        tw_element = getResources().getStringArray(R.array.tw_element);

        setSpinner();
        search.setOnClickListener(view -> getWeather(selected_location, selected_element, selected_time));
    }

    private void setSpinner() {
        ArrayAdapter location_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, location_data);
        ArrayAdapter element_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, element_data);
        ArrayAdapter time_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, time_data);


        location_spinner.setAdapter(location_adapter);
        element_spinner.setAdapter(element_adapter);
        time_spinner.setAdapter(time_adapter);


        location_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_location = location_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        element_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_element = element_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        time_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_time = time_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getWeather(String selectedLocation, String selectedElement, String selectedTime) {
        String authorization = "CWA-28DB28C0-2379-42E2-AA41-F0F5CE248C9C";
        if (selectedElement.equals("All")) selectedElement = "";
        String finalSelectedElement = selectedElement;


        getApi.getWeatherApi(authorization, selectedLocation, selectedElement)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<weatherResponse>(){
                    @Override
                    public void onNext(@NonNull weatherResponse weatherResponse) {
                        result.setText("");

                        List<String> time_list = Arrays.asList(time_data);
                        List<String> element_list = Arrays.asList(element_data);

                        try {
                            if (weatherResponse.getElementSize() != 1) {
                                for (int i = 0; i < weatherResponse.getElementSize(); i++) {
                                    result.append(tw_element[i] + weatherResponse.getDataByTime(i, time_list.indexOf(selectedTime)) + "\n");

                                }
                            } else {
                                result.setText(tw_element[element_list.indexOf(finalSelectedElement)] + weatherResponse.getDataByTime(0, time_list.indexOf(selectedTime)) + "\n");
                            }
                        } catch (Exception e) {
                            Log.e("test", "onNext: " + e);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("test", "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d("test", "onComplete: ");
                    }
                });
    }
}