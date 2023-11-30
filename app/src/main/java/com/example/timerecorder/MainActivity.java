package com.example.timerecorder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button timeInButton, timeOutButton;
    private TextView recordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeInButton = findViewById(R.id.timeInButton);
        timeOutButton = findViewById(R.id.timeOutButton);
        recordTextView = findViewById(R.id.recordTextView);

        // Disable buttons if already clicked for the day
        disableButtonsIfClicked();

        timeInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordTime("Time In");
            }
        });

        timeOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordTime("Time Out");
            }
        });

        // Automatically display all data when the activity is created
        displayAllData();
    }

    private void disableButtonsIfClicked() {
        try {
            String filename = "time_monitoring.txt";
            BufferedReader br = new BufferedReader(new FileReader(getFilesDir() + "/" + filename));
            String line;
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

            while ((line = br.readLine()) != null) {
                // Check if Time In or Time Out already recorded for today
                if (line.contains("Time In") && line.contains(dateFormat.format(new Date()))) {
                    timeInButton.setEnabled(false);
                } else if (line.contains("Time Out") && line.contains(dateFormat.format(new Date()))) {
                    timeOutButton.setEnabled(false);
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recordTime(String eventType) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            String currentDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
            String currentTime = dateFormat.format(new Date());

            String record = eventType + ": " + currentTime + "\n";

            // Check if it's a new day, add separator and date at the beginning
            if (!readAllData().contains(currentDate)) {
                record = "-------------------------------\n" + "Date: " + currentDate + "\n" + record;
            }

            // Save to file
            saveToFile(record);

            // Update TextView
            updateRecordTextView();

            // Disable the button after clicking
            if (eventType.equals("Time In")) {
                timeInButton.setEnabled(false);
            } else if (eventType.equals("Time Out")) {
                timeOutButton.setEnabled(false);
            }

            // Show toast
            Toast.makeText(this, "Recorded successfully", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error recording time", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToFile(String record) throws IOException {
        String filename = "time_monitoring.txt";
        FileOutputStream fos = openFileOutput(filename, MODE_APPEND);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        osw.write(record);
        osw.close();
    }

    private void updateRecordTextView() {
        try {
            String filename = "time_monitoring.txt";
            String filePath = getFilesDir().getPath() + "/" + filename;

            // Display date, time, and file directory
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            String currentTime = dateFormat.format(new Date());

            String displayText = "Last Recorded:\n" + "Date and Time: " + currentTime + "\nFile Directory: " + filePath;

            // Append all data from the file
            displayText += "\n\nAll Data:\n" + readAllData();

            recordTextView.setText(displayText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readAllData() {
        StringBuilder data = new StringBuilder();

        try {
            String filename = "time_monitoring.txt";
            BufferedReader br = new BufferedReader(new FileReader(getFilesDir() + "/" + filename));
            String line;

            while ((line = br.readLine()) != null) {
                data.append(line).append("\n");
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data.toString();
    }

    private void displayAllData() {
        // Automatically display all data when the activity is created
        updateRecordTextView();
    }
}
