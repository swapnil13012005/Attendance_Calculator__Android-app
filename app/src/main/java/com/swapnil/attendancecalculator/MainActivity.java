package com.swapnil.attendancecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText presentInput, totalInput;
    Spinner percentageSpinner;
    Button calculateBtn;
    TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presentInput = findViewById(R.id.presentInput);
        totalInput = findViewById(R.id.totalInput);
        percentageSpinner = findViewById(R.id.percentageSpinner);
        calculateBtn = findViewById(R.id.calculateBtn);
        resultText = findViewById(R.id.resultText);

        String[] percentages = {"60%", "65%", "70%", "75%", "80%", "85%", "90%"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                percentages
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        percentageSpinner.setAdapter(adapter);
        percentageSpinner.setSelection(3);

        calculateBtn.setOnClickListener(v -> {

            // Close keyboard
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            int present = parseIntSafe(presentInput.getText().toString());
            int total = parseIntSafe(totalInput.getText().toString());
            int percentage = parseIntSafe(
                    percentageSpinner.getSelectedItem().toString().replace("%", "")
            );

            if (present < 0 || total <= 0 || present > total) {
                resultText.setText("Proper values please ¯\\_(ツ)_/¯");
                return;
            }

            double currentPercent = (present * 100.0) / total;

            if (currentPercent >= percentage) {

                int bunk = (int) Math.floor(
                        (100.0 * present - percentage * total) / percentage
                );

                int newTotal = total + bunk;
                double newPercent = (present * 100.0) / newTotal;

                resultText.setText(
                        "You can bunk " + bunk + " more classes.\n\n" +
                                "Current Attendance: " + present + "/" + total +
                                " -> " + format(currentPercent) + "%\n" +
                                "Attendance Then: " + present + "/" + newTotal +
                                " -> " + format(newPercent) + "%"
                );

            } else {

                int attend = (int) Math.ceil(
                        (percentage * total - 100.0 * present) / (100.0 - percentage)
                );

                int newPresent = present + attend;
                int newTotal = total + attend;
                double newPercent = (newPresent * 100.0) / newTotal;

                resultText.setText(
                        "You need to attend " + attend + " more classes to attain "
                                + percentage + "% attendance\n\n" +
                                "Current Attendance: " + present + "/" + total +
                                " -> " + format(currentPercent) + "%\n" +
                                "Attendance Required: " + newPresent + "/" + newTotal +
                                " -> " + format(newPercent) + "%"
                );
            }
        });
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private String format(double value) {
        return String.format(Locale.US, "%.2f", value);
    }
}
