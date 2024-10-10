package br.com.DnotasTecnologia.Dnotas.activities;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
import static br.com.DnotasTecnologia.Dnotas.activities.ValidationActivityPermissionsDispatcher.initiateAppWithPermissionCheck;
import static stone.environment.Environment.valueOf;

import android.Manifest;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.DnotasTecnologia.Dnotas.R;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import stone.application.StoneStart;
import stone.application.interfaces.StoneCallbackInterface;
import stone.environment.Environment;
import stone.providers.ActiveApplicationProvider;
import stone.user.UserModel;
import stone.utils.Stone;
import stone.utils.keys.StoneKeyType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RuntimePermissions
public class ValidationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ValidationActivity";
    private static final int REQUEST_PERMISSION_SETTINGS = 100;
    private EditText stoneCodeEditText;

    private List<UserModel> user; // 

    // Cria o cliente HTTP
    private final OkHttpClient client = new OkHttpClient();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);
        initiateAppWithPermissionCheck(this);
//        Stone.setEnvironment(SANDBOX);
        Stone.setAppName("DEMO APP"); // Setando o nome do APP (obrigatorio)
        findViewById(R.id.activateButton).setOnClickListener(this);
        stoneCodeEditText = findViewById(R.id.stoneCodeEditText);
        Spinner environmentSpinner = findViewById(R.id.environmentSpinner);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        for (Environment env : Environment.values()) {
            adapter.add(env.name());
        }
        environmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Environment environment = valueOf(adapter.getItem(position));
//                Stone.setEnvironment(environment);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                Stone.setEnvironment(PRODUCTION);
            }
        });
        environmentSpinner.setAdapter(adapter);

        Stone.setAppName("Demo SDK");

    }

    @Override
    public void onClick(View v) {
        // Obter o Stone Code digitado
        String stoneCode = stoneCodeEditText.getText().toString();

        // Validar o Stone Code
        validateStoneCode(stoneCode);
    }

    // Método para validar o Stone Code
    private void validateStoneCode(String stoneCode) {
        String url = "https://api.dnotas.com.br/validate-stone-code";


        // Criar JSON para o Stone Code
        String json = "{\"stoneCode\":\"" + stoneCode + "\"}";
        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);

        // Criar requisição HTTP
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // Enviar a requisição
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(ValidationActivity.this, "Erro na validação. Tente novamente, entre em contato com a Dnotas", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro na validação do Stone Code", e);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(ValidationActivity.this, "Stone Code válido!", Toast.LENGTH_SHORT).show();

                        // Armazenar o Stone Code validado nas SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("stoneCode", stoneCode);
                        editor.apply(); // Salvar Stone Code

                        // Agora pode ativar o aplicativo com o Stone Code armazenado
                        activateApplication(stoneCode);
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(ValidationActivity.this, "Stone Code não cadastrado na Dnotas, entre em contato antes de usar o app", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }


    private void activateApplication(String stoneCode) {
        List<String> stoneCodeList = new ArrayList<>();
        stoneCodeList.add(stoneCode);

        final ActiveApplicationProvider provider = new ActiveApplicationProvider(this);
        provider.setDialogMessage("Ativando o aplicativo...");
        provider.setDialogTitle("Aguarde");
        provider.setConnectionCallback(new StoneCallbackInterface() {
            public void onSuccess() {
                Toast.makeText(ValidationActivity.this, "Ativado com sucesso, iniciando o aplicativo", Toast.LENGTH_SHORT).show();
                continueApplication();
            }

            public void onError() {
                Toast.makeText(ValidationActivity.this, "Erro na ativação do aplicativo, verifique a lista de erros do provider", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onError: " + provider.getListOfErrors().toString());
            }
        });
        provider.activate(stoneCodeList);
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE})
    public void initiateApp() {


           // Inicializa o Stone SDK com o Stone Code armazenado
           Map<StoneKeyType, String> keys = new HashMap<>();
           keys.put(StoneKeyType.QRCODE_PROVIDERID, "8e407830-d30f-4a9b-aa98-1771dcd0bb5c");
           keys.put(StoneKeyType.QRCODE_AUTHORIZATION, "b8bc86b4-46ac-479a-8e26-9850d26752eb");
 
           user = StoneStart.init(this, keys); // Atribua sem redeclarar o tipo


         
        // Recupera o Stone Code armazenado nas SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String storedStoneCode = sharedPreferences.getString("stoneCode", null);
       
        if (storedStoneCode != null) {
            // Se já existe um Stone Code armazenado, continuar normalmente
            Log.d(TAG, "Stone Code já armazenado: " + storedStoneCode);
            iniciarComStoneCode(storedStoneCode);
        } else {
            // Caso não exista, direcionar para a validação do Stone Code
            Log.d(TAG, "Nenhum Stone Code armazenado. Iniciando validação.");
            solicitarValidacaoStoneCode();
        }
    }

    private void solicitarValidacaoStoneCode() {
        // Verifica se o EditText stoneCodeEditText foi inicializado corretamente
        if (stoneCodeEditText == null) {
            Toast.makeText(this, "Digite o Stone code", Toast.LENGTH_SHORT).show();
            return; // Não continua se o campo não foi encontrado
        }

        // Verifica se o campo está vazio
        String stoneCode = stoneCodeEditText.getText().toString().trim();
        if (stoneCode.isEmpty()) {
            Toast.makeText(this, "Por favor, insira o Stone Code", Toast.LENGTH_SHORT).show();
            return; // Não continua se o campo está vazio
        }

        // Redireciona para a validação do Stone Code
        validateStoneCode(stoneCode);
    }

    private void iniciarComStoneCode(String stoneCode) {
        // Inicializa o Stone SDK com o Stone Code armazenado
      //  Map<StoneKeyType, String> keys = new HashMap<>();
      //  keys.put(StoneKeyType.QRCODE_PROVIDERID, "8e407830-d30f-4a9b-aa98-1771dcd0bb5c");
      //  keys.put(StoneKeyType.QRCODE_AUTHORIZATION, "b8bc86b4-46ac-479a-8e26-9850d26752eb");

        if (user != null) {
            continueApplication(); // Se estiver tudo ok, continuar o fluxo normal
        } else {
            Toast.makeText(this, "Erro ao iniciar com Stone Code armazenado.", Toast.LENGTH_SHORT).show();
        }
    }
    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE})
    void showDenied() {
        buildPermissionDialog(new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initiateAppWithPermissionCheck(ValidationActivity.this);
            }
        });
    }

    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE})
    void showNeverAskAgain() {
        buildPermissionDialog(new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION_SETTINGS);
            }
        });
    }

    private void continueApplication() {
        Intent mainIntent = new Intent(ValidationActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE})
    void showRationale(final PermissionRequest request) {
        buildPermissionDialog(new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request.proceed();
            }
        });
    }

    private void buildPermissionDialog(OnClickListener listener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Android 6.0")
                .setCancelable(false)
                .setMessage("Com a versão do android igual ou superior ao Android 6.0," +
                        " é necessário que você aceite as permissões para o funcionamento do app.\n\n")
                .setPositiveButton("OK", listener)
                .create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_SETTINGS) {
            initiateAppWithPermissionCheck(this);
        }
        ValidationActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}

