package br.com.DnotasTecnologia.Dnotas.activities;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import br.com.stone.posandroid.providers.PosPrintProvider;
import br.com.stone.posandroid.providers.PosValidateTransactionByCardProvider;
import br.com.DnotasTecnologia.Dnotas.R;
import stone.application.enums.Action;
import stone.application.interfaces.StoneActionCallback;
import stone.application.interfaces.StoneCallbackInterface;
import stone.database.transaction.TransactionObject;
import stone.providers.ActiveApplicationProvider;
import stone.providers.DisplayMessageProvider;
import stone.providers.ReversalProvider;
import stone.utils.Stone;

public class Maisopcoes extends AppCompatActivity implements View.OnClickListener {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_opcoes);


     findViewById(R.id.manageStoneCodeOption).setOnClickListener(this);
     findViewById(R.id.posValidateCardOption).setOnClickListener(this);
     findViewById(R.id.posMifareProvider).setOnClickListener(this);
     findViewById(R.id.cancelTransactionsOption).setOnClickListener(this);
     findViewById(R.id.posPrinterProvider).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.manageStoneCodeOption:
                startActivity(new Intent(Maisopcoes.this, ManageStoneCodeActivity.class));
                break;
            case R.id.posValidateCardOption:
                final PosValidateTransactionByCardProvider posValidateTransactionByCardProvider = new PosValidateTransactionByCardProvider(this);
                posValidateTransactionByCardProvider.setConnectionCallback(new StoneActionCallback() {
                    @Override
                    public void onStatusChanged(final Action action) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Maisopcoes.this, action.name(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final List<TransactionObject> transactionsWithCurrentCard = posValidateTransactionByCardProvider.getTransactionsWithCurrentCard();
                                if (transactionsWithCurrentCard.isEmpty())
                                    Toast.makeText(Maisopcoes.this, "Cartão não fez transação.", Toast.LENGTH_SHORT).show();
                                Toast.makeText(Maisopcoes.this, "Success", Toast.LENGTH_SHORT).show();
                                Log.i("posValidateCardOption", "onSuccess: " + transactionsWithCurrentCard);
                            }
                        });

                    }

                    @Override
                    public void onError() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Maisopcoes.this, "Error", Toast.LENGTH_SHORT).show();
                                Log.e("posValidateCardOption", "onError: " + posValidateTransactionByCardProvider.getListOfErrors());
                            }
                        });
                    }

                });
                posValidateTransactionByCardProvider.execute();
                break;
        

            case R.id.cancelTransactionsOption:
                final ReversalProvider reversalProvider = new ReversalProvider(this);
                reversalProvider.setDialogMessage("Cancelando transações com erro");
                reversalProvider.setConnectionCallback(new StoneCallbackInterface() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(Maisopcoes.this, "Transações canceladas com sucesso", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(Maisopcoes.this, "Ocorreu um erro durante o cancelamento das tabelas: " + reversalProvider.getListOfErrors(), Toast.LENGTH_SHORT).show();
                    }
                });
                reversalProvider.execute();
                break;  

                case R.id.posPrinterProvider:
                    final PosPrintProvider customPosPrintProvider = new PosPrintProvider(getApplicationContext());
                    customPosPrintProvider.addLine("PAN : " + "123");
                    customPosPrintProvider.addLine("DATE/TIME : 01/01/1900");
                    customPosPrintProvider.addLine("AMOUNT : 200.00");
                    customPosPrintProvider.addLine("ATK : 123456789");
                    customPosPrintProvider.addLine("Signature");
                    customPosPrintProvider.addBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.signature));
                    customPosPrintProvider.setConnectionCallback(new StoneCallbackInterface() {
                        @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Recibo impresso", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getApplicationContext(), "Erro ao imprimir: " + customPosPrintProvider.getListOfErrors(), Toast.LENGTH_SHORT).show();
                    }
                });
                customPosPrintProvider.execute();

            case R.id.posMifareProvider:
                startActivity(new Intent(Maisopcoes.this, MifareActivity.class));
                break;  

            default:
            break;

        }

    }
}
