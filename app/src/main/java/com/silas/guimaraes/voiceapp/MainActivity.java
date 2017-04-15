package com.silas.guimaraes.voiceapp;

import java.util.ArrayList;
import java.util.Locale;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements
        TextToSpeech.OnInitListener{

    private String answer;
    private String command;
    private TextToSpeech tts;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, this);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);


        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    promptSpeechInput();
            }
        });

    }

    /**
     * Exibir google voz dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Entrada de voz
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
                case REQ_CODE_SPEECH_INPUT: {
                    if (resultCode == RESULT_OK && null != data) {


                        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        command = result.get(0).toString().toLowerCase();
                        answer = "Comando inválido";

                        if(command.contains("qual resumo diário") || command.contains("qual o resumo diário")||command.contains("qual meu resumo diário")||command.contains("qual meu resumo diario") || command.contains("qual o meu resumo diário")||command.contains("qual o meu resumo diario")) {
                            answer = "Olá Márcio. Vou apresentar seu resumo diário de vendas. financeiro. e fabril. Sua equipe realizou 67 pedidos de vendas. no total de um milhão e trezentos mil reais. Seu vendedor que mais vendeu foi a Renata. seguido do Rafael. Seu resumo financeiro foi excelente, de acordo com os indicadores configurados. Teve um faturamento de cinco milhões e cem mil reais, todos com pagamentos a vista. Seus fornecedores foram todos pagos. Seu saldo atual está positivo em 3 milhões de reais. Agora vamos ao seu resumo fabril: Sua fábrica produziu com cem por cento da sua capacidade total. De acordo com os parâmetros indicados. hoje seu dia foi satisfatório. Parabéns e boa noite";
                        }

                        txtSpeechInput.setText(command);
                        speakOut(answer);
                        result.clear();
                        command = null;
                    }
                    break;
                }
            }
    }

    private void speakOut(String answer) {
        tts.speak(answer, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Idioma não suportado");
            } else {
                btnSpeak.setEnabled(true);
                speakOut(command);
            }
        } else {
            Log.e("TTS", "Falha no método 'onInit()' ");
        }
    }
}