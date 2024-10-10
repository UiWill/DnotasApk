package br.com.DnotasTecnologia.Dnotas.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import br.com.DnotasTecnologia.Dnotas.R;
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
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.ParseException;

import java.util.List;
import java.util.ArrayList;
import android.widget.ListView;

import android.app.Activity;


import android.content.Intent;




public class Add_produtos extends AppCompatActivity implements View.OnClickListener {

    ImageView leite, suco, cafe, croissant, queijo, bolo, pao, geleia, pudin,expresso,cappucino,vitamina;
    List<String> produtosList;
    String produtoSelecionado;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comidas); // Use o layout correto

        // Inicializa os componentes da interface
        queijo = findViewById(R.id.que);
        pao = findViewById(R.id.pao);
        croissant = findViewById(R.id.cro);
        geleia = findViewById(R.id.gel);
        bolo = findViewById(R.id.bol);
        pudin = findViewById(R.id.pud);
        suco = findViewById(R.id.suc);
        cafe = findViewById(R.id.caf);
        leite = findViewById(R.id.lei);
        expresso = findViewById(R.id.exp);
        cappucino = findViewById(R.id.cap);
        vitamina = findViewById(R.id.vit);

        // Configura os listeners para as imagens
        queijo.setOnClickListener(this);
        pao.setOnClickListener(this);
        croissant.setOnClickListener(this);
        geleia.setOnClickListener(this);
        bolo.setOnClickListener(this);
        pudin.setOnClickListener(this);
        suco.setOnClickListener(this);
        cafe.setOnClickListener(this);
        leite.setOnClickListener(this);
        vitamina.setOnClickListener(this);
        cappucino.setOnClickListener(this);
        expresso.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.que:
                produtoSelecionado = "Queijo";
                break;
            case R.id.pao:
                produtoSelecionado = "Pão";
                break;
            case R.id.cro:
                produtoSelecionado = "Croissant";
                break;
            case R.id.gel:
                produtoSelecionado = "Geleia";
                break;
            case R.id.bol:
                produtoSelecionado = "Bolo";
                break;
            case R.id.pud:
                produtoSelecionado = "Pudim";
                break;
            case R.id.suc:
                produtoSelecionado = "Suco";
                break;
            case R.id.caf:
                produtoSelecionado = "Café";
                break;
            case R.id.lei:
                produtoSelecionado = "Leite";
                break;

            case R.id.vit:
                produtoSelecionado = "Vitamina";
                break;
            case R.id.exp:
                produtoSelecionado = "Café expresso";
                break;
            case R.id.cap:
                produtoSelecionado = "cappuccino";
                break;
        }

        if (produtoSelecionado != null) {
            // Cria o Intent para enviar o resultado de volta
            Intent resultIntent = new Intent();
            resultIntent.putExtra("produto", produtoSelecionado);
            setResult(Activity.RESULT_OK, resultIntent);
            finish(); // Finaliza a atividade e retorna para a atividade anterior
        }
    }
}
            // Navega para a PosTransactionActivity
           // Intent intent = new Intent(this, PosTransactionActivity.class);
           // startActivity(intent);
  