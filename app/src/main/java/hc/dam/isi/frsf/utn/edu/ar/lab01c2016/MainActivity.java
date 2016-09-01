package hc.dam.isi.frsf.utn.edu.ar.lab01c2016;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;


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
        EditText et = (EditText)findViewById(R.id.edtImporte);
        TextView tvMensaje = (TextView) findViewById(R.id.tvMensaje);
        double importeIngresado = 0;

        //Si el monto ingresado no es correcto mostrar error
        try{
            importeIngresado = Double.parseDouble((String.valueOf(et.getText())));
        }
        catch (NumberFormatException e) {
            // Error en ingreso de monto
            tvMensaje.setTextColor(getResources().getColor(R.color.colorMensajeError));
            tvMensaje.setText("Error en el importe ingresado");
        }

        tvMonto.setText("$"+calcularInteres(sbDias.getProgress(),importeIngresado,obtenerTasa(importeIngresado,sbDias.getProgress())));

        tvMensaje.setTextColor(getResources().getColor(R.color.colorMensajeCorrecto));
        tvMensaje.setText(R.string.mensaje_correcto);

    }
    private String calcularInteres(int cantidad_dias,double importe, double tasa){
    //Calcula el monto total para mostrar
        String resultado = "0.00";
        DecimalFormat formato = new DecimalFormat("#.00");

        resultado = (formato.format(importe+(importe * ((Math.pow(1 + tasa, (cantidad_dias/360))) - 1))));
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

}
