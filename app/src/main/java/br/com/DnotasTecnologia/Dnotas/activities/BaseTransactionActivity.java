package br.com.DnotasTecnologia.Dnotas.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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



public abstract class BaseTransactionActivity<T extends BaseTransactionProvider> extends AppCompatActivity implements StoneActionCallback, View.OnClickListener {
    private BaseTransactionProvider transactionProvider;
    protected final TransactionObject transactionObject = new TransactionObject();
    RadioGroup transactionTypeRadioGroup;
    Spinner installmentsSpinner;
    Spinner stoneCodeSpinner;
    TextView installmentsTextView;
    CheckBox captureTransactionCheckBox;
    CheckBox captureItens;
    TextView amountEditText;
    TextView logTextView;
    TextView valorTotal;
    Button sendTransactionButton;
    Button cancelTransactionButton;
    Button numeroZero,numeroUm,numeroDois,numeroTres,numeroQuatro,numeroCinco,numeroSeis,numeroSete,numeroOito,numeroNove,
            soma,subtracao,multiplicacao,botao_limpar,ZeroZero, Additens;

    TextView txtExpressao,txtResultado;

    private int itemCount = 1; // Adiciona um contador para itens

    String current = "";
    NumberFormat numberFormat = new DecimalFormat("#,##");
/* 
    private Retrofit retrofit;
    private ApiService apiService;
    */
    int transactionId = 1;

    private static final int REQUEST_CODE_ADD_PRODUTO = 1;
    ListView painel;
    List<String> produtosList;
     List<Double> valoresList;
    private CustomAdapter adapter;





    Dialog builder;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        transactionTypeRadioGroup = findViewById(R.id.transactionTypeRadioGroup);
        installmentsTextView = findViewById(R.id.installmentsTextView);
        installmentsSpinner = findViewById(R.id.installmentsSpinner);
        stoneCodeSpinner = findViewById(R.id.stoneCodeSpinner);
        captureTransactionCheckBox = findViewById(R.id.captureTransactionCheckBox);
        amountEditText = findViewById(R.id.amountEditText);
        logTextView = findViewById(R.id.logTextView);
        sendTransactionButton = findViewById(R.id.sendTransactionButton);
        cancelTransactionButton = findViewById(R.id.cancelar);
        Button addItensButton = findViewById(R.id.Additens);
        captureItens = findViewById(R.id.captureItens);
        painel = findViewById(R.id.painel);
        valorTotal = findViewById(R.id.tvResult);
        

          // Configura a lista e o adaptador
        produtosList = new ArrayList<>();
        valoresList = new ArrayList<>();
        adapter = new CustomAdapter(this, produtosList, valoresList, valorTotal, totalValue);



        painel.setAdapter((ListAdapter) adapter);

        // Inicia a Add_produtos para adicionar produtos
        Button addButton = findViewById(R.id.Additens);
        addButton.setOnClickListener(v -> {
           
        });
    

        

        spinnerAction();
        radioGroupClick();
        radioGroupClick2();
        sendTransactionButton.setOnClickListener(v -> {
            // Verifica se o captureItens está marcado
            if (captureItens.isChecked()) {
                // Se estiver marcado, obtém o valor de tvResult
                TextView valorTotal = findViewById(R.id.tvResult);
                String amount = valorTotal.getText().toString();

                // Remove tudo que não é número
                amount = amount.replaceAll("[^\\d]", "");

                amountEditText.setText(amount);

                // Verifica se o valor é válido
                if (!amount.isEmpty()) {
                    // Define o valor formatado no objeto de transação
                    transactionObject.setAmount(amount);

                    // Inicia a transação
                    initTransaction();

                } else {
                    // Exibe uma mensagem de erro se o valor não for válido
                    Toast.makeText(this, "Por favor, insira um valor válido.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Se captureItens não estiver marcado, obtém o valor de amountEditText normalmente
                String amount = amountEditText.getText().toString();

                // Remove tudo que não é número
                amount = amount.replaceAll("[^\\d]", "");

                amountEditText.setText(amount);

                // Verifica se o valor é válido
                if (!amount.isEmpty()) {
                    // Define o valor formatado no objeto de transação
                    transactionObject.setAmount(amount);

                    // Inicia a transação
                    initTransaction();
                } else {
                    // Exibe uma mensagem de erro se o valor não for válido
                    Toast.makeText(this, "Por favor, insira um valor válido.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelTransactionButton .setOnClickListener(v -> {
            // Abortar a transação
            transactionProvider.abortPayment();
        
            // Gerar log indicando o cancelamento da transação
            Log.d("TransactionStatus", "Transação cancelada com sucesso.");
        });
        
        


        builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        IniciarComponentes();
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.hide();

        ZeroZero.setOnClickListener(this);
        numeroZero.setOnClickListener(this);
        numeroUm.setOnClickListener(this);
        numeroDois.setOnClickListener(this);
        numeroTres.setOnClickListener(this);
        numeroQuatro.setOnClickListener(this);
        numeroCinco.setOnClickListener(this);
        numeroSeis.setOnClickListener(this);
        numeroSete.setOnClickListener(this);
        numeroOito.setOnClickListener(this);
        numeroNove.setOnClickListener(this);
        soma.setOnClickListener(this);
        subtracao.setOnClickListener(this);
        multiplicacao.setOnClickListener(this);
        addItensButton.setOnClickListener(this);

        botao_limpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtExpressao.setText("");
                txtResultado.setText("");
            }
        });
/* 
        // Inicialize o Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.dnotas.com.br/") // Substitua pela URL base da sua API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        */



    }





    private void radioGroupClick() {
        transactionTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioPix:
                case R.id.radioDebit:
                case R.id.radioVoucher:
                    installmentsTextView.setVisibility(View.GONE);
                    installmentsSpinner.setVisibility(View.GONE);
                    break;
                case R.id.radioCredit:
                    installmentsTextView.setVisibility(View.VISIBLE);
                    installmentsSpinner.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void radioGroupClick2() {
        captureItens.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Se captureItens estiver marcado, exiba o ListView e o Button
                findViewById(R.id.painel).setVisibility(View.VISIBLE);
                findViewById(R.id.Additens).setVisibility(View.VISIBLE);
                findViewById(R.id.tvResult).setVisibility(View.VISIBLE);
                findViewById(R.id.textView19).setVisibility(View.VISIBLE);
            } else {
                // Caso contrário, esconda o ListView e o Button
                findViewById(R.id.painel).setVisibility(View.GONE);
                findViewById(R.id.Additens).setVisibility(View.GONE);
                findViewById(R.id.tvResult).setVisibility(View.GONE);
                findViewById(R.id.textView19).setVisibility(View.GONE);
            }
        });
    }

    private void spinnerAction() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.installments_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        installmentsSpinner.setAdapter(adapter);

        ArrayAdapter<String> stoneCodeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        stoneCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (UserModel userModel : Stone.sessionApplication.getUserModelList()) {
            stoneCodeAdapter.add(userModel.getStoneCode());
        }
        stoneCodeSpinner.setAdapter(stoneCodeAdapter);
    }

    public void initTransaction() {
        InstalmentTransactionEnum installmentsEnum = InstalmentTransactionEnum.getAt(installmentsSpinner.getSelectedItemPosition());

        // Informa a quantidade de parcelas.
        transactionObject.setInstalmentTransaction(InstalmentTransactionEnum.getAt(installmentsSpinner.getSelectedItemPosition()));

        // Verifica a forma de pagamento selecionada.
        TypeOfTransactionEnum transactionType;
        switch (transactionTypeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radioCredit:
                transactionType = TypeOfTransactionEnum.CREDIT;
                break;
            case R.id.radioDebit:
                transactionType = TypeOfTransactionEnum.DEBIT;
                break;
            case R.id.radioVoucher:
                transactionType = TypeOfTransactionEnum.VOUCHER;
                break;
            case R.id.radioPix:
                transactionType = TypeOfTransactionEnum.PIX;
                break;
            default:
                transactionType = TypeOfTransactionEnum.CREDIT;
        }

        transactionObject.setInitiatorTransactionKey(null);
        transactionObject.setTypeOfTransaction(transactionType);
        transactionObject.setCapture(captureTransactionCheckBox.isChecked());
        transactionObject.setAmount(amountEditText.getText().toString());

        transactionProvider = buildTransactionProvider();
        transactionProvider.setConnectionCallback(this);
        transactionProvider.execute();
       /* 
        // Após a transação, faça a chamada à API para enviar os dados
        new Thread(() -> {
            try {
                sendTransactionToAPI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        */ 
    }
/* 
    private void sendTransactionToAPI() {
        // Obtenha os valores dinâmicos de transactionObject
        String valor1 = String.valueOf(getNextTransactionId());
        String valor2 = transactionObject.getTypeOfTransaction().toString(); // Tipo de transação
        String valor3 = InstalmentTransactionEnum.getAt(installmentsSpinner.getSelectedItemPosition()).toString(); // Tipo de parcelamento
        String valor4 = getCurrentDateTime(); // Data e hora atuais

        // Pegue o valor do campo de texto e formate como moeda
        long valorEmCentavos = convertStringToCents(amountEditText.getText().toString());

        // Converta o valor em centavos para reais
         String valorEmReais = convertCentsToReais(valorEmCentavos);

        // Crie a transação com os valores
        Transacao transacao = new Transacao(
                valor1, // valor1: adcionar logica de id
                valor2, // valor2: Tipo de transação
                valor3, // valor3: Tipo de parcelamento
                valor4, // valor4: Data e hora
                valorEmReais, // valor5: Valor do campo de texto
                "123456", // valor6: Substitua se necessário
                "VISA", // valor7: BANDEIRA DO CARTAO
                "16501555000823", // valor8: CNPJ
                getSelectedUserModel().getStoneCode() // Stone code dinâmico
        );

        // Envio da transação para a API usando Retrofit
        apiService.enviarTransacao(transacao).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(BaseTransactionActivity.this, "Transação enviada com sucesso!", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(BaseTransactionActivity.this, "Erro ao enviar a transação: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(BaseTransactionActivity.this, "Falha na comunicação com a API: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
        */
    /**
 * Converte um valor formatado de string para centavos (removendo a formatação).
 * @param valorFormatado O valor formatado como string.
 * @return O valor em centavos como long.
 */
private long convertStringToCents(String valorFormatado) {
    // Remove qualquer símbolo de moeda e separadores de milhar
    String valorLimpo = valorFormatado.replaceAll("[^\\d]", ""); // Remove caracteres não numéricos
    return Long.parseLong(valorLimpo); // Converte para long
}

/**
 * Converte o valor em centavos para reais (com duas casas decimais).
 * @param valorEmCentavos O valor em centavos como long.
 * @return O valor formatado em reais como string.
 */
private String convertCentsToReais(long valorEmCentavos) {
    // Divide o valor em centavos por 100 para obter o valor em reais e formata para duas casas decimais
    return String.format(Locale.US, "%.2f", valorEmCentavos / 100.0);
}


    // Método para obter a data e hora atuais
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date()); // Retorna a data e hora atuais no formato desejado
    }

    private int getNextTransactionId() {
        return transactionId++;
    }

    protected String getAuthorizationMessage() {
        return transactionProvider.getMessageFromAuthorize();
    }

    protected abstract T buildTransactionProvider();

    protected boolean providerHasErrorEnum(ErrorsEnum errorsEnum) {
        return transactionProvider.theListHasError(errorsEnum);
    }

    @Override
    public void onError() {
        runOnUiThread(() -> Toast.makeText(BaseTransactionActivity.this, "Erro: " + transactionProvider.getListOfErrors(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onStatusChanged(final Action action) {
        runOnUiThread(() -> logTextView.append(action.name() + "\n"));

        if (action == Action.TRANSACTION_WAITING_QRCODE_SCAN) {
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(transactionObject.getQRCode());

            runOnUiThread(() -> {
                builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                builder.show();
            });
        } else {
            runOnUiThread(() -> builder.dismiss());
        }
    }

    protected BaseTransactionProvider getTransactionProvider() {
        return transactionProvider;
    }

    protected UserModel getSelectedUserModel() {
        return Stone.getUserModel(stoneCodeSpinner.getSelectedItemPosition());
    }

    private void IniciarComponentes(){
        numeroZero = findViewById(R.id.zero);
        ZeroZero =findViewById(R.id.zerozero);
        numeroUm = findViewById(R.id.one);
        numeroDois = findViewById(R.id.two);
        numeroTres = findViewById(R.id.three);
        numeroQuatro = findViewById(R.id.four);
        numeroCinco = findViewById(R.id.five);
        numeroSeis = findViewById(R.id.six);
        numeroSete = findViewById(R.id.seven);
        numeroOito = findViewById(R.id.eight);
        numeroNove = findViewById(R.id.nine);
        botao_limpar = findViewById(R.id.clear);
        soma = findViewById(R.id.plus);
        multiplicacao = findViewById(R.id.multiply);
        subtracao = findViewById(R.id.minus);
        txtExpressao = findViewById(R.id.amountEditText);
        txtResultado = findViewById(R.id.txt_espressao);
    }

    public void AcrescentarUmaExpressao(String string, boolean limpar_dados) {
        if (txtResultado.getText().equals("")) {
            txtExpressao.setText(" ");
        }

        if (limpar_dados) {
            txtResultado.setText(" ");
            txtExpressao.append(string);

        } else {
            txtExpressao.append(txtResultado.getText());
            txtExpressao.append(string);
            txtResultado.setText(" ");
        }
        formatarParaMoeda();
    }
    private void formatarParaMoeda() {
        try {
            // Obtém o texto atual de txtExpressao.
            String texto = txtExpressao.getText().toString();

            // Remove caracteres não numéricos e converte o texto para um número inteiro (representando centavos).
            String textoNumerico = texto.replaceAll("[^\\d]", ""); // Remove qualquer caractere que não seja número
            if (textoNumerico.isEmpty()) {
                txtExpressao.setText("0,00");
                return;
            }

            // Converte o texto para um número inteiro representando centavos.
            long centavos = Long.parseLong(textoNumerico);

            // Cria uma instância de NumberFormat para formatar o número como moeda brasileira.
            NumberFormat formatador = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            // Converte os centavos para reais e formata o número.
            double reais = centavos / 100.0;
            String textoFormatado = formatador.format(reais);

            // Atualiza o texto de txtExpressao com o formato de moeda.
            txtExpressao.setText(textoFormatado);
        } catch (NumberFormatException e) {
            // Caso ocorra uma exceção de formatação, define o texto como "0,00".
            txtExpressao.setText("0,00");
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.zero:
                AcrescentarUmaExpressao("0",true);
                break;

            case R.id.zerozero:
                AcrescentarUmaExpressao("00",true);
                break;

            case R.id.one:
                AcrescentarUmaExpressao("1",true);
                break;

            case R.id.two:
                AcrescentarUmaExpressao("2",true);
                break;

            case R.id.three:
                AcrescentarUmaExpressao("3",true);
                break;

            case R.id.four:
                AcrescentarUmaExpressao("4",true);
                break;

            case R.id.five:
                AcrescentarUmaExpressao("5",true);
                break;

            case R.id.six:
                AcrescentarUmaExpressao("6",true);
                break;

            case R.id.seven:
                AcrescentarUmaExpressao("7",true);
                break;

            case R.id.eight:
                AcrescentarUmaExpressao("8",true);
                break;

            case R.id.nine:
                AcrescentarUmaExpressao("9",true);
                break;

            case R.id.plus:
                AcrescentarUmaExpressao("+",false);
                break;

            case R.id.multiply:
                AcrescentarUmaExpressao("*",false);
                break;

            case R.id.minus:
                AcrescentarUmaExpressao("-",false);
                break;

            case R.id.Additens:
                // Obtém o valor do amountEditText
                String valor = amountEditText.getText().toString().trim();

                // Verifica se o valor é vazio
                if (!valor.isEmpty()) {
                    // Se não estiver vazio, inicie a nova Activity
                    Intent Additens = new Intent(BaseTransactionActivity.this, Add_produtos.class);
                    startActivityForResult(Additens, REQUEST_CODE_ADD_PRODUTO);
                } else {
                    // Se estiver vazio, exiba uma mensagem ao usuário
                    Toast.makeText(BaseTransactionActivity.this, "Favor colocar um valor.", Toast.LENGTH_SHORT).show();
                }
                break;






        }
    }
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            // Tenta converter o valor para Double após substituição de vírgula por ponto
            Double.parseDouble(str.replace(",", "."));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private double[] totalValue = {0.0}; // Use um array para permitir a mutabilidade
    // Variável para armazenar o valor total
 

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    
        if (requestCode == REQUEST_CODE_ADD_PRODUTO && resultCode == Activity.RESULT_OK && data != null) {
            String produto = data.getStringExtra("produto");
            if (produto != null) {
                String valorProdutoStr = amountEditText.getText().toString()
                    .replace("R$", "")
                    .replace(",", ".")
                    .replaceAll("\\s", "")
                    .trim();
    
                try {
                    double valorProduto = Double.parseDouble(valorProdutoStr);
                    amountEditText.setText("");
    
                    totalValue[0] += valorProduto;
    
                    valorTotal.setText(String.format("R$ %.2f", totalValue[0]));
    
                    // Evite adicionar itens duplicados
                    String produtoComValor = produto; // Use apenas o nome do produto
                    produtosList.add(produtoComValor);
                    valoresList.add(valorProduto);
    
                    adapter.notifyDataSetChanged();
    
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Erro ao formatar o valor do produto.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



}
