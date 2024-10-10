package br.com.DnotasTecnologia.Dnotas.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import br.com.DnotasTecnologia.Dnotas.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;

import android.widget.CheckBox;

import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import stone.application.enums.Action;
import stone.application.enums.ErrorsEnum;
import stone.application.enums.InstalmentTransactionEnum;
import stone.application.enums.TypeOfTransactionEnum;
import stone.application.interfaces.StoneActionCallback;
import stone.database.transaction.TransactionObject;
import stone.providers.BaseTransactionProvider;
import stone.user.UserModel;
import stone.utils.Stone;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.ParseException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.ResponseBody;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.SharedPreferences;


public class CupomActivity extends AppCompatActivity {

    private EditText destinatario;
    private EditText valorCupom;
    private Button sendCupom;
    private String storedStoneCode;  // Para armazenar o Stone Code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cupom);

        destinatario = findViewById(R.id.dest);
        valorCupom = findViewById(R.id.valorC);
        sendCupom = findViewById(R.id.secdC);

        // Obtém a lista de usuários Stone
        List<UserModel> userModelList = Stone.sessionApplication.getUserModelList();

        // Verifica se há pelo menos um usuário e obtém o primeiro Stone Code
        if (userModelList != null && !userModelList.isEmpty()) {
            storedStoneCode = userModelList.get(0).getStoneCode();
        } else {
            storedStoneCode = null;
        }

        // Verifica se o Stone Code foi encontrado
        if (storedStoneCode == null) {
            Toast.makeText(this, "Stone Code não encontrado. Verifique a ativação.", Toast.LENGTH_SHORT).show();
            return; // Interrompe o processo se não houver Stone Code
        }

        // Configurando o clique do botão
        sendCupom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cpfCnpj = destinatario.getText().toString().trim();
                String valor = valorCupom.getText().toString().trim();

                if (!valor.isEmpty()) {
                    sendCupomToApi(cpfCnpj, valor);
                } else {
                    Toast.makeText(CupomActivity.this, "Preencha o valor do cupom.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendCupomToApi(String cpfCnpj, String valor) {
        // Configurando Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.dnotas.com.br/")  // Coloque aqui a URL correta da sua API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CupomApiService apiService = retrofit.create(CupomApiService.class);

        // Usando o StoneCode obtido do primeiro usuário
        CupomRequest request = new CupomRequest(storedStoneCode, cpfCnpj, valor);

        // Fazendo a chamada para a API
        Call<ResponseBody> call = apiService.solicitarNfce(request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CupomActivity.this, "Cupom enviado com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CupomActivity.this, "Erro ao enviar cupom: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CupomActivity.this, "Falha na comunicação: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}



