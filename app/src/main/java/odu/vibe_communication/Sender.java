package odu.vibe_communication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Sender extends AppCompatActivity implements View.OnClickListener {
    static String message="";

    EditText etInput;
    Button btnSend;
    Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);

        etInput = (EditText)findViewById(R.id.etInput);

        btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);

        btnDone = (Button)findViewById(R.id.btnDone);
        btnDone.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sender, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnSend: {
                EncodeAndSend();
                break;
            }
            default:
                break;
        }
    }

    private void EncodeAndSend() {
        message = etInput.getText().toString();
        if(message == "")
        {
            Toast.makeText(this, "fill the message", Toast.LENGTH_SHORT).show();
            return;
        }


        String[] alpha = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8",
                "9", "0", " " };
        String[] dottie = { ".-", "-...", "-.-.", "-..", ".", "..-.", "--.",
                "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.",
                "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-",
                "-.--", "--..", ".----", "..---", "...--", "....-", ".....",
                "-....", "--...", "---..", "----.", "-----", "|" };

        char[] translates = (message.toLowerCase()).toCharArray();
        String morseCode = toMorse(translates, dottie);
        System.out.println(morseCode);


       //long[] pattern = MorseCodeConverter.pattern(morseCode);
       long[] pattern = MorseCodeConverter.pattern(message.toLowerCase());

        Vibrator mVibrator  = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mVibrator.vibrate(pattern, -1);

        // Start the vibration
        //Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        //vibrator.vibrate(pattern, -1);
    }

    public static String toMorse(char [] translates, String [] dottie)
    {
        String morse = "";
        for (int j = 0; j < translates.length; j++)
        {
            char a = translates[j];
            if(Character.isLetter(a))
            {
                morse += dottie[a - 'a'];
            }
        }
        return morse;
    }
}
