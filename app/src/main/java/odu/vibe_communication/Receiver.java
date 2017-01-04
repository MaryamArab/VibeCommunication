package odu.vibe_communication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Receiver extends AppCompatActivity implements SensorEventListener, View.OnClickListener  {

    Button btnReceive;
    Button btnStop;
    TextView tvMessage;
    TextView tvAccX;
    TextView tvAccY;
    TextView tvAccZ;

    float ALPHA = 0.25f;



    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float[] accelVals;


    String path;
    PrintWriter file=null ;
    File settingFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);

        btnReceive= (Button)findViewById(R.id.btnReceive);
        btnReceive.setOnClickListener(this);

        btnStop =(Button)findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this);

        tvMessage = (TextView)findViewById(R.id.tvMessage);
        tvAccX = (TextView)findViewById(R.id.tvAccX);
        tvAccY = (TextView)findViewById(R.id.tvAccY);
        tvAccZ = (TextView)findViewById(R.id.tvAccZ);

        String fSaveName = Environment.getExternalStorageDirectory().toString();
        File folder = new File(fSaveName,"AccData");
        folder.mkdir();
        // Create the file
        path = Environment.getExternalStorageDirectory().getPath();
        settingFile = new File(path+"/AccData/MyFile_aaaa.csv");
        try {
            file  = new PrintWriter( new FileWriter( settingFile, false ) );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_receiver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btnReceive:
                ActivateSensor();
                break;
            case R.id.btnStop: {
                mSensorManager.unregisterListener(this, mAccelerometer);
                file.close();
                break;
            }
            default:
                break;
        }
    }

        private void ActivateSensor() {
            mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

    final long BASE_TIME = 1000000000;
    final float g_base = 9.85f;
    final float start_high_length = 3 * BASE_TIME;
    final float min_domain = 1.2f;
    final float max_domain = 0.3f;


    boolean communcation_started = false;
    boolean start_high_received = false;
    long current_period_length = 0;
    long current_time = 0;
    float current_sum = 0;
    long current_count = 0;

    boolean current_level = false; // high=true low=false

    ArrayList<Integer> time_lengths = new ArrayList<>();

    ArrayList<Double> last_second_values = new ArrayList<>();
    ArrayList<Long> last_second_timestamps = new ArrayList<>();

    ArrayList<Boolean> levels = new ArrayList<>();

    ArrayList<Long> timestamps = new ArrayList<>();
    ArrayList<Double> accelerations = new ArrayList<>();
    int timestamp_head = 0;
    ArrayList<Integer> result = new ArrayList<>();
    final double BANBAN = 0.8;
    private void handleState(long timestamp, double acceleration) {
        timestamps.add(timestamp);
        accelerations.add(acceleration);
        int size = accelerations.size();
        if (size > 2) {
            levels.add((accelerations.get(size - 1) > BANBAN) || (accelerations.get(size - 2) > BANBAN) || (accelerations.get(size - 3) > BANBAN));
            if (size > 3) {
                if (levels.get(size - 4) != levels.get(size - 3)) {
                    int sign = (levels.get(size - 4) ? 1:-1);
                    long nano_seconds = sign * (timestamps.get(size - 1) - timestamps.get(timestamp_head));
                    int v = (int) (Math.round(1.0 * nano_seconds / 1_000_000_000));
                    result.add(v);
                    if (v == 4) {
                        getWord();
                    }
                }
            }
        }
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

    private void getWord() {
        int i = 0;
        while (result.get(i) < 0) { i++; }

        String full_text = "";
        String current_code = "";
        while (i < result.size()) {
            i++;
            int v = result.get(i);
            if ((v != -1) && (v != -3) && (v != -7) && (v != 4))
                System.out.println("Bad Code");
            else if ((v == -3) || (v == 4)) {
                full_text = full_text + getLetter(current_code);
                current_code = "";
            }
            else if (v == -7) {
                full_text = full_text + " " + getLetter(current_code);
                current_code = "";
            }

            i++;
            v = result.get(i);
            if ((v != 1) && (v != 3))
                System.out.println("Bad Code");
            else if (v == 1)
                current_code = current_code + ".";
            else if (v == 3)
                current_code = current_code + "-";
        }

        levels = new ArrayList<>();
        timestamps = new ArrayList<>();
        accelerations = new ArrayList<>();
        timestamp_head = 0;
        result = new ArrayList<>();

        System.out.println(full_text);
    }

    private String getLetter(String current_code) {
        if (current_code.length() == 0)
            return "";
        for (int i = 0; i < dottie.length; i++) {
            if (dottie[i].equals(current_code))
                return alpha[i];
        }
        return "?";
    }

    private void handleState_old(long timestamp, double acceleration) {
        System.out.println("state," + timestamp + "," + acceleration);
        if (current_time != 0) {
            current_period_length += timestamp - current_time;
            System.out.println("Current Period Length: " + current_period_length);
        }
        current_time = timestamp;
        current_count++;
        current_sum += acceleration;

        if (!communcation_started) {
            checkCommuncationStart(timestamp, acceleration);
        } else {
            if (current_period_length > BASE_TIME) {
                current_period_length = 0;
                float avg = current_sum / current_count;
                current_sum = 0;
                current_count = 0;
                System.out.println("Running Average: " + avg);
                current_level = avg > min_domain;
                if (levels.size() == 0) {
                    levels.add(current_level);
                    time_lengths.add(1);
                } else {
                    if (levels.get(levels.size() - 1) == current_level) {
                        int last_index = time_lengths.size() - 1;
                        time_lengths.set(last_index, time_lengths.get(last_index) + 1);
                    } else {
                        levels.add(current_level);
                        time_lengths.add(1);
                    }
                }

                printArrays();
            }
        }
    }

    private void printArrays() {
        String time_length_string = "";
        for (Integer tl : time_lengths)
        {
            time_length_string += tl + "\t";
        }

        String levels_string = "";
        for (Boolean l : levels)
        {
            levels_string += l.toString() + "\t";
        }

        System.out.println("Time Length & Levels:");
        System.out.println(time_length_string);
        System.out.println(levels_string);
    }

    private void checkCommuncationStart(long timestamp, double acceleration) {
        if (!start_high_received) {
            float current_avg = current_sum / current_count;
            System.out.println("Current Average in Check Communication Start: " + current_avg);
            current_level = current_avg > min_domain;
            if (current_level) {
                if (current_time > start_high_length) {
                    start_high_received = true;
                    System.out.println("Start High Received...");
                    current_count = 0;
                    current_sum = 0;
                    current_period_length = 0;
                }
            } else if (current_period_length > (BASE_TIME / 2)) {
                System.out.println("Resetting while checking communication start.");
                current_time = 0;
                current_period_length = 0;
                current_count = 0;
                current_sum = 0;
            }
        } else {
            last_second_timestamps.add(timestamp);
            last_second_values.add(acceleration);
            if ((last_second_timestamps.get(last_second_timestamps.size() - 1) - last_second_timestamps.get(0)) > BASE_TIME) {
                float last_second_avg = lastSecondAvg();
                System.out.println("Last Second Average: " + last_second_avg);
                if (last_second_avg < max_domain) {
                    System.out.println("Communication Started...");
                    communcation_started = true;
                    current_sum = 0;
                    current_count = 0;
                    current_period_length = 0;
                    last_second_timestamps = new ArrayList<>();
                    last_second_values = new ArrayList<>();
                }
            }
        }
    }

    private float lastSecondAvg() {
        int size = last_second_timestamps.size();
        int i = size;
        float sum = 0;
        int count = 0;
        while (i > 0) {
            i--;
            if ((last_second_timestamps.get(size - 1) - last_second_timestamps.get(i)) > BASE_TIME)
                break;
            count++;
            sum += last_second_values.get(i);
        }
        if (count > 0)
            return sum / count;
        else
            return 0.0f;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String line = String.valueOf(event.timestamp)
                    + "," + event.values[0]
                    + "," + event.values[1]
                    + "," + event.values[2] + "\n";
        file.append(line);

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            accelVals = lowPass(event.values.clone(), accelVals);
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if(event.values[0] > accelVals[0])
                tvAccX.setText("Accel X: " + event.values[0]  + "grav 0 :"+ accelVals[0]);
            if(event.values[1] > accelVals[1])
                tvAccY.setText("Accel Y: " + event.values[1]);
            if(event.values[2] > accelVals[2])
                tvAccZ.setText("Accel Z: " + event.values[2]);
        }

        handleState(event.timestamp, Math.sqrt(event.values[0]*event.values[0] + event.values[1]*event.values[1] + (event.values[2] - g_base) * (event.values[2] - g_base)));
//        handleState(event.timestamp, Math.sqrt(accelVals[0]*accelVals[0] + accelVals[1]*accelVals[1] + (accelVals[2] - g_base) * (accelVals[2] - g_base)));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }
}
