package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends ComponentActivity {
    private EditText dialText;
    private Button backspaceButton;
    private static final int REQUEST_CALL_PERMISSION = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ArrayList<Button> numberButtons = new ArrayList<>();
        dialText = findViewById(R.id.dialText);
        Button clearButton = findViewById(R.id.clear);
        backspaceButton = findViewById(R.id.backspace);
        Button mPhone = findViewById(R.id.phone);

        int[] buttonIds = {
                R.id.one, R.id.two, R.id.three, R.id.four, R.id.five,
                R.id.six, R.id.seven, R.id.eight, R.id.nine, R.id.zero, R.id.hash, R.id.star
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
            if (!currentText.isEmpty()) {
                dialText.setText(currentText.substring(0, currentText.length() - 1));
                updateBackspaceVisibility();
            }
        });

        updateBackspaceVisibility();

        mPhone.setOnClickListener(v -> makeCall());

        // Handle incoming ACTION_DIAL intent
        Intent intent = getIntent();
        if (Intent.ACTION_DIAL.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                String number = uri.getSchemeSpecificPart();
                dialText.setText(number);
            }
        }

        if (savedInstanceState != null) {
            dialText.setText(savedInstanceState.getString("dialText"));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("dialText", dialText.getText().toString());
    }

    private void updateBackspaceVisibility() {
        if (dialText.getText().length() > 0) {
            backspaceButton.setVisibility(View.VISIBLE);
        } else {
            backspaceButton.setVisibility(View.GONE);
        }
    }

    public void makeCall() {
        String number = dialText.getText().toString();

        if (!number.isEmpty()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
            } else {
                callPhoneNumber(number);
            }
        } else {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String number = dialText.getText().toString();
                callPhoneNumber(number);
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void callPhoneNumber(String number) {
        Uri uri = Uri.parse("tel:" + number);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        try {
            startActivity(intent);
        } catch (SecurityException e) {
            Toast.makeText(this, "A security error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
