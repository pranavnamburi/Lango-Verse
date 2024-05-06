package com.example.langoverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.time.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextTranslationActivity extends AppCompatActivity {




    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    ConnectionClass connectionClass;
    private EditText sourceLanguage;
    private TextView destinationLanguageTv;
    private MaterialButton sourceLanguageChooseBtn;
    private MaterialButton destinationLanguageChooseBtn;
    private MaterialButton translateBtn;
    private ImageButton speakResultBtn;
    private TextToSpeech textToSpeech;
    private Translator translator;
    private ProgressDialog progressDialog;
    private ArrayList<ModelLanguage> languageArrayList;
    private static final String TAG = "MAIN_TAG";
    private String sourceLanguageCode = "en";
    private String sourceLanguagetitle = "English";
    private String destinationLanguageCode = "hi";
    private String destinationLanguageTitle = "Hindi";
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_translation);
        sourceLanguage = findViewById(R.id.sourceLanguage);
        destinationLanguageTv = findViewById(R.id.destinationLanguageTv);
        sourceLanguageChooseBtn = findViewById(R.id.sourceLanguageChooseBtn);
        destinationLanguageChooseBtn = findViewById(R.id.destinationLanguageChooseBtn);
        translateBtn = findViewById(R.id.translateBtn);
        speakResultBtn = findViewById(R.id.speakResultBtn);

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

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
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

        speakResultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakResult();
            }
        });
        connectionClass = new ConnectionClass();
    }

    private String sourceLanguageText = "";

    private void validateData() {
        sourceLanguageText = sourceLanguage.getText().toString().trim();
        Log.d(TAG, "validateData: sourceLanguageText: " + sourceLanguageText);

        if (sourceLanguageText.isEmpty()) {
            Toast.makeText(this, "Enter text to translate...", Toast.LENGTH_SHORT).show();
        } else {
            startTranslation();
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
                        translator.translate(sourceLanguageText)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String translatedText) {
                                        Log.d(TAG, "onSuccess: TranslatedText: " + translatedText);
                                        progressDialog.dismiss();
                                        destinationLanguageTv.setText(translatedText);
                                        long currentTimeMillis = System.currentTimeMillis();
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Date resultdate = new Date(currentTimeMillis);
                                        String userEmail = User.getInstance().getEmail();
                                        addHistoryToDB(userEmail, sourceLanguagetitle, sourceLanguageText, translatedText, destinationLanguageTitle, sdf.format(resultdate));
                                    }

                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Log.e(TAG, "onFailure: ", e);
                                        Toast.makeText(TextTranslationActivity.this, "Failed to translate due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.e(TAG, "onFailure: ", e);
                        Toast.makeText(TextTranslationActivity.this, "Failed to ready model due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sourceLanguageChoose() {
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
                sourceLanguagetitle = languageArrayList.get(position).languageTitle;
                sourceLanguageChooseBtn.setText(sourceLanguagetitle);
                sourceLanguage.setHint("Enter " + sourceLanguagetitle);

                Log.d(TAG, "onMenuItemClick: sourcelanguageCode: " + sourceLanguageCode);
                Log.d(TAG, "onMenuItemClick: sourcelanguageTitle: " + sourceLanguagetitle);

                return false;
            }
        });
    }

    private void destinationLanguageChoose() {
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

    private void speakResult() {
        String translatedText = destinationLanguageTv.getText().toString();

        if (!translatedText.isEmpty()) {
            textToSpeech.speak(translatedText, TextToSpeech.QUEUE_FLUSH, null, "UtteranceId");
        } else {
            Toast.makeText(this, "No text to speak", Toast.LENGTH_SHORT).show();
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
