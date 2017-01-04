package odu.vibe_communication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Main extends AppCompatActivity implements View.OnClickListener {
    Button btnSender ;

    Button btnReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSender = (Button)findViewById(R.id.btnSender);
        btnSender.setOnClickListener(this);

        btnReceiver = (Button)findViewById(R.id.btnReceiver);
        btnReceiver.setOnClickListener(this);

//        etInput = (EditText)findViewById(R.id.etInput);
//
//        txtOutput = (TextView)findViewById(R.id.txtOutput);
//        txtBinay = (TextView)findViewById(R.id.txtBinary);
//        txtBinaryToStr =(TextView)findViewById(R.id.txtBinaryToString);
//        txtMorse = (TextView)findViewById(R.id.txtMorse);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            case R.id.btnSender:
                StartSender();

                break;


            case R.id.btnReceiver:
                StartReceiver();

                break;
            default:
                break;
        }
    }

    private void StartReceiver() {
        Intent receiverIntent = new Intent(this, Receiver.class);
        this.startActivity(receiverIntent);
    }

    private void StartSender() {
        Intent senderIntent = new Intent(this, Sender.class);
        this.startActivity(senderIntent);
    }

//    private void Encode() {

//        byte[] bytesEncoded = Base64.encode(message.getBytes(), 0);
//        txtOutput.setText("ecncoded value is " + new String(bytesEncoded ));
//        String binaryStr = strToBinary(message);
//
//        txtBinay.setText("Binary ecode is:"+binaryStr);
//        txtBinaryToStr.setText("String was:"+ binaryToString(binaryStr));
//
//    }
//
//
//
//
//
//    public static String strToBinary(String inputString){
//
//        int[] ASCIIHolder = new int[inputString.length()];
//
//        //Storing ASCII representation of characters in array of ints
//        for(int index = 0; index < inputString.length(); index++){
//            ASCIIHolder[index] = (int)inputString.charAt(index);
//        }
//
//
//        StringBuffer binaryStringBuffer = new StringBuffer();
//        for(int index =0;index <inputString.length();index ++){
//        binaryStringBuffer.append(Integer.toBinaryString(ASCIIHolder[index]));
//        }
//
//
//        String binaryToBeReturned = binaryStringBuffer.toString();
//
//        binaryToBeReturned.replace(" ", "");
//
//        return binaryToBeReturned;
//    }
//    public String binaryToString(String binaryString){
//        String returnString = "";
//        int charCode;
//        for(int i = 0; i < binaryString.length(); i+=7)
//        {
//            charCode = Integer.parseInt(binaryString.substring(i, i+7), 2);
//            String returnChar = new Character((char)charCode).toString();
//            returnString += returnChar;
//        }
//        return returnString;
//    }

}
