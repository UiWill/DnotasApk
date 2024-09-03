package br.com.DnotasTecnologia.Dnotas.activities;

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



public abstract class BaseTransactionActivity<T extends BaseTransactionProvider> extends AppCompatActivity implements StoneActionCallback, View.OnClickListener {
    private BaseTransactionProvider transactionProvider;
    protected final TransactionObject transactionObject = new TransactionObject();
    RadioGroup transactionTypeRadioGroup;
    Spinner installmentsSpinner;
    Spinner stoneCodeSpinner;
    TextView installmentsTextView;
    CheckBox captureTransactionCheckBox;
    TextView amountEditText;
    TextView logTextView;
    Button sendTransactionButton;
    Button cancelTransactionButton;
     Button numeroZero,numeroUm,numeroDois,numeroTres,numeroQuatro,numeroCinco,numeroSeis,numeroSete,numeroOito,numeroNove,
            soma,subtracao,multiplicacao,botao_limpar,ZeroZero;

     TextView txtExpressao,txtResultado;


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
        cancelTransactionButton = findViewById(R.id.cancelTransactionButton);


        spinnerAction();
        radioGroupClick();
        sendTransactionButton.setOnClickListener(v -> initTransaction());
        cancelTransactionButton.setOnClickListener(v -> transactionProvider.abortPayment());


        builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
                
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

        botao_limpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtExpressao.setText("");
                txtResultado.setText("");
            }
        });




       




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
        txtExpressao = findViewById(R.id.txt_espressao);
        txtResultado = findViewById(R.id.amountEditText);
    }

    public void AcrescentarUmaExpressao(String string, boolean limpar_dados) {
        if (limpar_dados) {
            txtResultado.setText("");
        }

        // Obtém o texto atual
        String currentText = txtResultado.getText().toString().replaceAll("[,.]", "");

        // Concatena o novo número digitado
        String newText = currentText + string;

        // Converte o texto em um número e o formata
        double number = Double.parseDouble(newText) / 100.0;
        String formattedText = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(number);

        // Atualiza o TextView com o número formatado
        txtResultado.setText(formattedText);
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
        }
    }

}
