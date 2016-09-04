package hc.dam.isi.frsf.utn.edu.ar.lab01c2016;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button boton= (Button) findViewById(R.id.btOk);
        boton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                obtenerMonto();
            }
        });

        SeekBar sb = (SeekBar)findViewById(R.id.sbDias);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                TextView tvDias = (TextView)findViewById(R.id.tvDiasSeleccionados);
                tvDias.setText(String.valueOf(progress) + " días");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void obtenerMonto(){
        TextView tvMonto = (TextView)findViewById(R.id.tvMontoRendimiento);
        SeekBar sbDias = (SeekBar)findViewById(R.id.sbDias);
        EditText etMonto = (EditText)findViewById(R.id.edtImporte);
        TextView tvMensaje = (TextView) findViewById(R.id.tvMensaje);
        EditText etCUIT = (EditText) findViewById(R.id.edtCuit);
        EditText etMail = (EditText) findViewById(R.id.edtCorreo);
        double importeIngresado = 0;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //Para personalizar el mensaje de error valido si cada entrada tiene texto y luego si es válido
        //Correo vacio
        if(((String.valueOf(etMail.getText()))).isEmpty()){
            tvMensaje.setTextColor(getResources().getColor(R.color.colorMensajeError));
            tvMensaje.setText(R.string.error_correo_vacio);

            //setea focus, muestra error y desplega el teclado
            etMail.requestFocus();
            Toast.makeText(MainActivity.this,R.string.error_correo_vacio , Toast.LENGTH_LONG).show();
            imm.showSoftInput(etMail, InputMethodManager.SHOW_IMPLICIT);
            return;
        }
        //Validar Correo
        if(!validarMail((String.valueOf(etMail.getText())))){
            tvMensaje.setTextColor(getResources().getColor(R.color.colorMensajeError));
            tvMensaje.setText(R.string.error_correo);

            //setea focus, muestra error y desplega el teclado
            etMail.requestFocus();
            Toast.makeText(MainActivity.this,R.string.error_correo , Toast.LENGTH_LONG).show();
            imm.showSoftInput(etMail, InputMethodManager.SHOW_IMPLICIT);

            return;
        }
        //CUIT vacio
        if((String.valueOf(etCUIT.getText())).isEmpty()){
            tvMensaje.setTextColor(getResources().getColor(R.color.colorMensajeError));
            tvMensaje.setText(R.string.error_cuit_vacio);

            //setea focus, muestra error y desplega el teclado
            etCUIT.requestFocus();
            Toast.makeText(MainActivity.this,R.string.error_cuit_vacio , Toast.LENGTH_LONG).show();
            imm.showSoftInput(etCUIT, InputMethodManager.SHOW_IMPLICIT);

            return;
        }
        //Validar CUIT
        if(((String.valueOf(etCUIT.getText())).length() != 11) || (!validarCUIT(String.valueOf(etCUIT.getText())))){
            tvMensaje.setTextColor(getResources().getColor(R.color.colorMensajeError));
            tvMensaje.setText(R.string.error_cuit);

            //setea focus, muestra error y desplega el teclado
            etCUIT.requestFocus();
            Toast.makeText(MainActivity.this,R.string.error_cuit , Toast.LENGTH_LONG).show();
            imm.showSoftInput(etCUIT, InputMethodManager.SHOW_IMPLICIT);

            return;
        }

        //Validar importe
        if((String.valueOf(etMonto.getText())).isEmpty()){
            tvMensaje.setTextColor(getResources().getColor(R.color.colorMensajeError));
            tvMensaje.setText(R.string.error_importe_vacio);

            //setea focus, muestra error y desplega el teclado
            etMonto.requestFocus();
            Toast.makeText(MainActivity.this,R.string.error_importe_vacio , Toast.LENGTH_LONG).show();
            imm.showSoftInput(etMonto, InputMethodManager.SHOW_IMPLICIT);

            return;
        }

        //Si el monto ingresado no es correcto mostrar error
        try{
            importeIngresado = Double.parseDouble((String.valueOf(etMonto.getText())));
        }
        catch (NumberFormatException e) {
            // Error en ingreso de monto
            tvMensaje.setTextColor(getResources().getColor(R.color.colorMensajeError));
            tvMensaje.setText(R.string.error_importe);

            //setea focus, muestra error y desplega el teclado
            etMonto.requestFocus();
            Toast.makeText(MainActivity.this,R.string.error_importe , Toast.LENGTH_LONG).show();
            imm.showSoftInput(etMonto, InputMethodManager.SHOW_IMPLICIT);

        }

        double tasa = obtenerTasa(importeIngresado,sbDias.getProgress());

        tvMonto.setText("$"+calcularMontoTotal(sbDias.getProgress(),importeIngresado,tasa));
        tvMensaje.setTextColor(getResources().getColor(R.color.colorMensajeCorrecto));
        tvMensaje.setText(R.string.mensaje_correcto);

    }
    private String calcularMontoTotal(int cantidad_dias,double importe, double tasa){
    //Calcula el monto total para mostrar
        String resultado = "0.00";
        DecimalFormat formato = new DecimalFormat("#.00");

        float prop_dias =  (float)cantidad_dias/(float)360;

        resultado = (formato.format(importe+(importe * ((Math.pow(1 + tasa, prop_dias)) - 1))));
        return resultado;
    }

    private double obtenerTasa(double unMonto, int dias){
    //Obtiene la tasa del archivo xml
        double resultado = 0.00;

        if(unMonto > 0 & unMonto <= 5000)
        {
           if(dias < 30)
           {
             resultado = Double.parseDouble(getResources().getString(R.string.menos_5000_menos_30));
           }
           else
           {
               resultado = Double.parseDouble(getResources().getString(R.string.menos_5000_mas_30));
           }
        }
        if(unMonto > 5000 & unMonto <= 99999)
        {
            if(dias < 30)
            {
                resultado = Double.parseDouble(getResources().getString(R.string.mas_5000_menos_99999_menos_30));
            }
            else
            {
                resultado = Double.parseDouble(getResources().getString(R.string.mas_5000_menos_99999_mas_30));
            }
        }
        if(unMonto > 99999)
        {
            if(dias < 30)
            {
                resultado = Double.parseDouble(getResources().getString(R.string.mas_99999_menos_30));
            }
            else
            {
                resultado = Double.parseDouble(getResources().getString(R.string.mas_99999_mas_30));
            }
        }
        return resultado;
    }

    private boolean validarCUIT(String cuit){
        boolean esValido = false;
        // la secuencia de valores de factor es 5, 4, 3, 2, 7, 6, 5, 4, 3, 2
        int factor = 5;

        int[] c = new int[11];
        c[10] = Integer.parseInt(cuit.substring(10));
        int resultado = 0;

        // se toma el valor de cada cifra
        for (int i = 0; i < 10; i++) {
          c[i] = Integer.parseInt(Character.toString(cuit.charAt(i)));
            resultado = resultado + c[i] * factor;
            factor = (factor == 2) ? 7 : factor - 1;
        }

        // se obtiene el valor calculado a comparar
        int control = (11 - (resultado % 11)) % 11;

        // Si la cifra de control es igual al valor calculado
        if (control == c[10]) {
            esValido = true;
        }
        return esValido;
    }
    private boolean validarMail(String correo){
        boolean esValido = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = correo;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            esValido = true;
        }
        return esValido;
    }


}
