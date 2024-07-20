package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MainActivity extends ComponentActivity {
    private ArrayList<Button> numberButtons;
    private EditText dialText;
    private Button clearButton;
    private Button backspaceButton;
    private Button mPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        numberButtons = new ArrayList<>();
        dialText = findViewById(R.id.dialText);
        clearButton = findViewById(R.id.clear);
        backspaceButton = findViewById(R.id.backspace);
        mPhone = findViewById(R.id.phone);

        int[] buttonIds = {
                R.id.one, R.id.two, R.id.three, R.id.four, R.id.five,
                R.id.six, R.id.seven, R.id.eight, R.id.nine, R.id.zero
        };
        for (int id : buttonIds) {
            Button button = findViewById(id);
            numberButtons.add(button);
        }

        for (Button button : numberButtons) {
            button.setOnClickListener(v -> {
                String buttonText = ((Button) v).getText().toString();
                dialText.append(buttonText);
                updateBackspaceVisibility();
            });
        }

        clearButton.setOnClickListener(v -> {
            dialText.setText("");
            updateBackspaceVisibility();
        });

        backspaceButton.setOnClickListener(v -> {
            String currentText = dialText.getText().toString();
            if (currentText.length() > 0) {
                dialText.setText(currentText.substring(0, currentText.length() - 1));
                updateBackspaceVisibility();
            }
        });

        updateBackspaceVisibility();

        mPhone.setOnClickListener(v -> {makeCall(mPhone);});
    }

    private void updateBackspaceVisibility() {
        if (dialText.getText().length() > 0) {
            backspaceButton.setVisibility(View.VISIBLE);
        } else {
            backspaceButton.setVisibility(View.GONE);
        }
    }


    public void makeCall(View v) {
        String number = dialText.getText().toString();

        if (!number.isEmpty()) {
            Uri uri = Uri.parse("tel:" + number);
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            try {
                startActivity(intent);
            } catch (SecurityException e) {
                Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
        }
    }}
