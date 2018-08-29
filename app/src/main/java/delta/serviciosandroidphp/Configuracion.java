package delta.serviciosandroidphp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ResponseCache;
import java.net.URL;
import java.text.RuleBasedCollator;
public class Configuracion extends AppCompatActivity {
    TextView txtCodA,txtNomA;
    EditText txtDisA, txtEstadoA, txtPasA;
    Button btnActualizarA;

    String recuperado="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        txtCodA=(TextView)findViewById(R.id.txtCodC);
        txtNomA=(TextView)findViewById(R.id.txtNomC);
        txtDisA=(EditText)findViewById(R.id.txtDisC);
        txtEstadoA=(EditText)findViewById(R.id.txtEstadoC);
        txtPasA=(EditText)findViewById(R.id.txtPasC);

        final Bundle recupera=getIntent().getExtras();
        if(recupera!=null){
            recuperado=recupera.getString("cod");
        }

        txtCodA.setText(recuperado);

        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado=MostrarAlumnoGET(recuperado);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MostrarDatos(resultado);
                    }
                });

            }
        };
        tr.start();

        btnActualizarA=(Button)findViewById(R.id.btnGrabar);
        btnActualizarA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread tr2=new Thread(){
                    @Override
                    public void run() {
                        ActualizarPost(recuperado, txtDisA.getText().toString(),
                                txtEstadoA.getText().toString(),txtPasA.getText().toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Datos Actualizados",
                                        Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(getApplicationContext(),RegistroNotas.class);
                                i.putExtra("cod",recuperado);
                                startActivity(i);
                            }
                        });
                    }
                };
                tr2.start();
            }
        });

    }

    public String MostrarAlumnoGET(String cod){
        URL url=null;
        String linea="";
        int respuesta=0;
        StringBuilder resul=null;

        try{
            url=new URL("http://192.168.1.101:8080/WebService/listaralucod.php?alu="+cod);
            HttpURLConnection conection=(HttpURLConnection)url.openConnection();
            respuesta=conection.getResponseCode();

            resul=new StringBuilder();

            if(respuesta==HttpURLConnection.HTTP_OK){
                InputStream in=new BufferedInputStream(conection.getInputStream());
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));

                while((linea=reader.readLine())!=null){
                    resul.append(linea);
                }
            }
        }catch(Exception e){}
        return resul.toString();
    }

    public void MostrarDatos(String response){
        try{
            JSONArray json=new JSONArray(response);
            for(int i=0;i<json.length();i++){
                txtNomA.setText(json.getJSONObject(i).getString("nombreAlu"));
                txtDisA.setText(json.getJSONObject(i).getString("Distrito"));
                txtEstadoA.setText(json.getJSONObject(i).getString("EstadoCivil"));
                txtPasA.setText(json.getJSONObject(i).getString("pasUsu"));
            }
        }catch(Exception e){}
    }

    public void ActualizarPost(String c,String d,String e,String p){
        String urlParameters="cod="+c+"&dis="+d+"&est="+e+"&pas="+p;
        HttpURLConnection conection=null;
        try{
            URL url=new URL("http://192.168.1.101:8080/WebService/actualizardatos.php");
            conection=(HttpURLConnection)url.openConnection();

            //estableciendo el metodo
            conection.setRequestMethod("POST");

            //longitud de datos que se envian
            conection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));

            //comando para la salida de datos
            conection.setDoOutput(true);

            DataOutputStream wr=new DataOutputStream(conection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            InputStream is=conection.getInputStream();


        }catch (Exception ex){}
    }


}
