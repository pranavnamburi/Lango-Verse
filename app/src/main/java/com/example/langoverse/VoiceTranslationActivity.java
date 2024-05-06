package com.example.langoverse;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VoiceTranslationActivity extends AppCompatActivity {


    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    ConnectionClass connectionClass;
    private TextView sourceLanguage;
    private MaterialButton sourceLanguageChooseBtn;
    private MaterialButton destinationLanguageChooseBtn;
    private ImageButton voiceInputBtn;
    private MaterialButton speakOutBtn;
    private TextView destinationLanguageTv;

    private TextToSpeech textToSpeech;
    private Translator translator;
    private ProgressDialog progressDialog;
    private ArrayList<ModelLanguage> languageArrayList;
    private static final String TAG = "MAIN_TAG";
    private String sourceLanguageCode = "en";
    private String sourceLanguageTitle = "English";
    private String destinationLanguageCode = "hi";
    private String destinationLanguageTitle = "Hindi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_translation);

        sourceLanguage = findViewById(R.id.sourceLanguage);
        sourceLanguageChooseBtn = findViewById(R.id.sourceLanguageChooseBtn);
        destinationLanguageChooseBtn = findViewById(R.id.destinationLanguageChooseBtn);
        voiceInputBtn = findViewById(R.id.voiceInputBtn);
        speakOutBtn = findViewById(R.id.speakOutBtn);

        // Initialize destinationLanguageTv
        destinationLanguageTv = findViewById(R.id.destinationLanguageTv);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadAvailableLanguage();

        sourceLanguageChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceLanguageChoose();
            }
        });

        destinationLanguageChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationLanguageChoose();
            }
        });

        voiceInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        speakOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Speak out the translated text
                speakOutTranslatedText();
            }
        });

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int langResult = textToSpeech.setLanguage(Locale.getDefault());

                    if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e(TAG, "Language is not supported.");
                    } else {
                        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onStart(String utteranceId) {
                                // Not used in this example
                            }

                            @Override
                            public void onDone(String utteranceId) {
                                // Handle speech completion if needed
                            }

                            @Override
                            public void onError(String utteranceId) {
                                // Handle error if needed
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "Initialization failed.");
                }
            }
        });
        connectionClass = new ConnectionClass();

//        speakOutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                speakOut();
//            }
//        });
    }

    private void startVoiceInput() {
        // Check for RECORD_AUDIO permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 2);
        } else {
            // Permission already granted, start voice input
            startVoiceInputInternal();
        }
    }
    private void speakOutTranslatedText() {
        String translatedText = destinationLanguageTv.getText().toString().trim();

        if (!translatedText.isEmpty()) {
            textToSpeech.speak(translatedText, TextToSpeech.QUEUE_FLUSH, null, "UtteranceId");
        } else {
            Toast.makeText(this, "No text to speak", Toast.LENGTH_SHORT).show();
        }
    }
    private void startTranslation() {
        progressDialog.setMessage("Processing language Model...");
        progressDialog.show();
        TranslatorOptions translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(destinationLanguageCode)
                .build();
        translator = Translation.getClient(translatorOptions);
        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "OnSuccess: model ready, starting translate...");
                        progressDialog.setMessage("Translating...");
                        translator.translate(sourceLanguage.getText().toString().trim())
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String translatedText) {
                                        Log.d(TAG, "onSuccess: TranslatedText: " + translatedText);
                                        progressDialog.dismiss();

                                        // Display the translated text in destinationLanguageTv
                                        destinationLanguageTv.setText(translatedText);
                                        long currentTimeMillis = System.currentTimeMillis();
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Date resultdate = new Date(currentTimeMillis);

                                        addHistoryToDB(User.getInstance().getEmail(), sourceLanguageTitle, sourceLanguage.getText().toString().trim(), translatedText, destinationLanguageTitle, sdf.format(resultdate));


                                        // Speak out the translated text
                                        speakOutTranslatedText();


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Log.e(TAG, "onFailure: ", e);
                                        Toast.makeText(VoiceTranslationActivity.this, "Failed to translate due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.e(TAG, "onFailure: ", e);
                        Toast.makeText(VoiceTranslationActivity.this, "Failed to ready model due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void startVoiceInputInternal() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, sourceLanguageCode);

        try {
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Speech-to-Text not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = result.get(0);
            sourceLanguage.setText(spokenText);

            // Automatically translate and speak out the result
            startTranslation();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start voice input
                startVoiceInputInternal();
            } else {
                Toast.makeText(this, "Permission denied. Voice input cannot be started.", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    private void startTranslation() {
//        progressDialog.setMessage("Processing language Model...");
//        progressDialog.show();
//        TranslatorOptions translatorOptions = new TranslatorOptions.Builder()
//                .setSourceLanguage(sourceLanguageCode)
//                .setTargetLanguage(destinationLanguageCode)
//                .build();
//        translator = Translation.getClient(translatorOptions);
//        DownloadConditions downloadConditions = new DownloadConditions.Builder()
//                .requireWifi()
//                .build();
//        translator.downloadModelIfNeeded(downloadConditions)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Log.d(TAG, "OnSuccess: model ready, starting translate...");
//                        progressDialog.setMessage("Translating...");
//                        translator.translate(sourceLanguage.getText().toString().trim())
//                                .addOnSuccessListener(new OnSuccessListener<String>() {
//                                    @Override
//                                    public void onSuccess(String translatedText) {
//                                        Log.d(TAG, "onSuccess: TranslatedText: " + translatedText);
//                                        progressDialog.dismiss();
//                                        // Handle translated text as needed
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        progressDialog.dismiss();
//                                        Log.e(TAG, "onFailure: ", e);
//                                        Toast.makeText(VoiceTranslationActivity.this, "Failed to translate due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Log.e(TAG, "onFailure: ", e);
//                        Toast.makeText(VoiceTranslationActivity.this, "Failed to ready model due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

//    private void speakOut() {
//        String translatedText = sourceLanguage.getText().toString().trim();
//
//        if (!translatedText.isEmpty()) {
//            textToSpeech.speak(translatedText, TextToSpeech.QUEUE_FLUSH, null, "UtteranceId");
//        } else {
//            Toast.makeText(this, "No text to speak", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void sourceLanguageChoose() {
        // Implement source language choose logic if needed
        PopupMenu popupMenu = new PopupMenu(this, sourceLanguageChooseBtn);
        for (int i = 0; i < languageArrayList.size(); i++) {
            popupMenu.getMenu().add(Menu.NONE, i, i, languageArrayList.get(i).languageTitle);
        }
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int position = item.getItemId();

                sourceLanguageCode = languageArrayList.get(position).languageCode;
                sourceLanguageTitle = languageArrayList.get(position).languageTitle;
                sourceLanguageChooseBtn.setText(sourceLanguageTitle);
                sourceLanguage.setHint("Enter " + sourceLanguageTitle);

                Log.d(TAG, "onMenuItemClick: sourcelanguageCode: " + sourceLanguageCode);
                Log.d(TAG, "onMenuItemClick: sourcelanguageTitle: " + sourceLanguageTitle);

                // Call startTranslation() after the user has chosen the source language
                startTranslation();

                return false;
            }
        });
    }

    private void destinationLanguageChoose() {
        // Implement destination language choose logic if needed
        PopupMenu popupMenu = new PopupMenu(this, destinationLanguageChooseBtn);
        for (int i = 0; i < languageArrayList.size(); i++) {
            popupMenu.getMenu().add(Menu.NONE, i, i, languageArrayList.get(i).getLanguageTitle());
        }
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int position = item.getItemId();

                destinationLanguageCode = languageArrayList.get(position).languageCode;
                destinationLanguageTitle = languageArrayList.get(position).languageTitle;
                destinationLanguageChooseBtn.setText(destinationLanguageTitle);

                Log.d(TAG, "onMenuItemClick: destinationLanguageCode: " + destinationLanguageCode);
                Log.d(TAG, "onMenuItemClick: destinationLanguageTitle: " + destinationLanguageTitle);

                return false;
            }
        });
    }

    private void loadAvailableLanguage() {
        languageArrayList = new ArrayList<>();

        List<String> languageCodeList = TranslateLanguage.getAllLanguages();

        for (String languageCode : languageCodeList) {
            String languageTitle = new Locale(languageCode).getDisplayLanguage();

            Log.d(TAG, "loadAvailableLanguage: languageCode: " + languageCode);
            Log.d(TAG, "loadAvailableLanguage: languageTitle: " + languageTitle);

            ModelLanguage modelLanguage = new ModelLanguage(languageCode, languageTitle);
            languageArrayList.add(modelLanguage);
        }
    }
    private void addHistoryToDB(String userEmail, String sourceLanguage, String targetLanguage, String sourceText, String targetText, String date) {
        executorService.execute(() -> {
            try (Connection conn = connectionClass.CONN()) {
                if (conn == null) {
                    throw new SQLException("Failed to establish connection");
                }

                String sql = "insert into translatedb values(?,?,?,?,?,?)";
                try (PreparedStatement statement = conn.prepareStatement(sql)) {
                    statement.setString(1, userEmail);
                    statement.setString(4, sourceText);
                    statement.setString(3, sourceLanguage);
                    statement.setString(5, targetText);
                    statement.setString(2, targetLanguage);
                    statement.setString(6, date);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        this.runOnUiThread(() -> {
                            Toast.makeText(this, "History added successfully", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        this.runOnUiThread(() -> Toast.makeText(this, "History addition failed", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    this.runOnUiThread(() -> Toast.makeText(this, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
