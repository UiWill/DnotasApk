package br.com.DnotasTecnologia.Dnotas.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

import br.com.stone.posandroid.providers.PosPrintReceiptProvider;
import br.com.stone.posandroid.providers.PosTransactionProvider;
import br.com.DnotasTecnologia.Dnotas.controller.PrintController;
import stone.application.enums.Action;
import stone.application.enums.ErrorsEnum;
import stone.application.enums.ReceiptType;
import stone.application.enums.TransactionStatusEnum;
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
import stone.application.enums.InstalmentTransactionEnum;


public class PosTransactionActivity extends BaseTransactionActivity<PosTransactionProvider> {

    private Retrofit retrofit;
    private ApiService apiService;
    int transactionId = 1;

    @Override
    protected PosTransactionProvider buildTransactionProvider() {
        return new PosTransactionProvider(this, transactionObject, getSelectedUserModel());
    }

    protected PosTransactionProvider getTransactionProvider() {
        return (PosTransactionProvider) super.getTransactionProvider();
    }

    @Override
    public void onSuccess() {
        if (transactionObject.getTransactionStatus() == TransactionStatusEnum.APPROVED) {

            // Pegue o código de autorização após a aprovação da transação
            String authorizationCode = transactionObject.getAuthorizationCode();

            // Pegue a bandeira do cartao após a aprovação da transação
            String cardBrandName = transactionObject.getCardBrandName();

            // Pegue o STONE ID após a aprovação da transação
            String acquirerTransactionKey = transactionObject.getAcquirerTransactionKey();

            // Pegar o valor do enum instalmentTransaction
        InstalmentTransactionEnum instalmentTransaction = transactionObject.getInstalmentTransaction();

        // Converter o valor do enum para String
           String instalmentTransactionString = instalmentTransaction.name();

            final PrintController printMerchant = new PrintController(PosTransactionActivity.this,
                    new PosPrintReceiptProvider(this.getApplicationContext(),
                            transactionObject, ReceiptType.MERCHANT));

            printMerchant.print();

            // Inicialize o Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.dnotas.com.br/") // Substitua pela URL base da sua API
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);

            // Agora você pode usar o authorizationCode para enviar com outros dados da
            // transação
            sendTransactionToAPI(authorizationCode, cardBrandName, acquirerTransactionKey, instalmentTransactionString);
            /*
             * // Após a transação, faça a chamada à API para enviar os dados
             * new Thread(() -> {
             * try {
             * sendTransactionToAPI();
             * } catch (Exception e) {
             * e.printStackTrace();
             * }
             * }).start();
             */

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Transação aprovada! Deseja imprimir a via do cliente?");

            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final PrintController printClient = new PrintController(PosTransactionActivity.this,
                            new PosPrintReceiptProvider(getApplicationContext(),
                                    transactionObject, ReceiptType.CLIENT));
                    printClient.print();

                    // Após concluir o processo de aprovação, navegue de volta para MainActivity
                    startActivity(new Intent(PosTransactionActivity.this, MainActivity.class));
                }
            });

            // builder.setNegativeButton(android.R.string.no, null);
            // Botão "Não" - navega diretamente para MainActivity sem imprimir
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Navega para MainActivity se o usuário escolher "Não"
                    startActivity(new Intent(PosTransactionActivity.this, MainActivity.class));
                }
            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.show();

                }
            });

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            getApplicationContext(),
                            "Erro na transação: \"" + getAuthorizationMessage() + "\"",
                            Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    public void onError() {
        super.onError();
        if (providerHasErrorEnum(ErrorsEnum.DEVICE_NOT_COMPATIBLE)) {
            Toast.makeText(
                    this,
                    "Dispositivo não compatível ou dependência relacionada não está presente",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(final Action action) {
        super.onStatusChanged(action);

        runOnUiThread(() -> {

            switch (action) {
                case TRANSACTION_WAITING_PASSWORD:
                    Toast.makeText(
                            PosTransactionActivity.this,
                            "Pin tries remaining to block card: ${transactionProvider?.remainingPinTries}",
                            Toast.LENGTH_LONG).show();
                    break;
                case TRANSACTION_TYPE_SELECTION:
                    List<String> options = getTransactionProvider().getTransactionTypeOptions();
                    showTransactionTypeSelectionDialog(options);
            }

        });
    }

    private void showTransactionTypeSelectionDialog(final List<String> optionsList) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione o tipo de transação");
        String[] options = new String[optionsList.size()];
        optionsList.toArray(options);
        builder.setItems(
                options,
                (dialog, which) -> getTransactionProvider().setTransactionTypeSelected(which));
        builder.show();
    }

    // Método separado para enviar a transação para a API
    private void sendTransactionToAPI(String authorizationCode, String cardBrandName, String acquirerTransactionKey, String instalmentTransactionString) {
        // Obtenha os valores dinâmicos de transactionObject
        String valor1 = String.valueOf(getNextTransactionId());
        String valor2 = transactionObject.getTypeOfTransaction().toString(); // Tipo de transação
        String valor3 = instalmentTransactionString;
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
                authorizationCode, // valor6: Substitua se necessário
                cardBrandName, // valor7: BANDEIRA DO CARTAO
                "16501555000823", // valor8: CNPJ
                getSelectedUserModel().getStoneCode(), // Stone code dinâmico
                acquirerTransactionKey // valor6: Código de autorização da transação
        );

        // Envio da transação para a API usando Retrofit
        apiService.enviarTransacao(transacao).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast
                            .makeText(PosTransactionActivity.this, "Transação enviada com sucesso!", Toast.LENGTH_SHORT)
                            .show());
                } else {
                    runOnUiThread(
                            () -> Toast
                                    .makeText(PosTransactionActivity.this,
                                            "Erro ao enviar a transação: " + response.message(), Toast.LENGTH_SHORT)
                                    .show());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(
                        () -> Toast
                                .makeText(PosTransactionActivity.this,
                                        "Falha na comunicação com a API: " + t.getMessage(), Toast.LENGTH_SHORT)
                                .show());
            }
        });
    }

    /**
     * Converte um valor formatado de string para centavos (removendo a formatação).
     * 
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
     * 
     * @param valorEmCentavos O valor em centavos como long.
     * @return O valor formatado em reais como string.
     */
    private String convertCentsToReais(long valorEmCentavos) {
        // Divide o valor em centavos por 100 para obter o valor em reais e formata para
        // duas casas decimais
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

}
