package delta.serviciosandroidphp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnIngresar;
    EditText txtUsu,txtPas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        txtUsu=(EditText)findViewById(R.id.txtusu);
        txtPas=(EditText)findViewById(R.id.txtpas);
        btnIngresar=(Button)findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener( this);
    }
    public void onClick(View v) {

        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado=enviarDatosGET(txtUsu.getText().toString(), txtPas.getText().toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int r = obtDatosJSON(resultado);
                        Toast.makeText(getApplicationContext(), r+"", Toast.LENGTH_LONG).show();

                        if (r > 0) {
                            Intent i = new Intent(getApplicationContext(), RegistroNotas.class);
                            i.putExtra("cod", txtUsu.getText().toString());
                            startActivity(i);
                        } else {
                            Toast.makeText(getApplicationContext(), "Usuario o Pas Incorrectos", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        };
        tr.start();
    }

    public String enviarDatosGET(String usu, String pas){

        URL url=null;
        String linea="";
        int respuesta=0;
        StringBuilder resul=null;

        try{
            url=new URL("http://192.168.1.101:8080/WebService/valida.php?usu="+usu+"&pas="+pas);
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

        }catch (Exception e){

        }

        return resul.toString();

    }

    public int obtDatosJSON(String response){
        int res=0;
        try{
            JSONArray json=new JSONArray(response);
            if(json.length()>0){
                res=1;
            }
        }catch(Exception e){}
        return res;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
