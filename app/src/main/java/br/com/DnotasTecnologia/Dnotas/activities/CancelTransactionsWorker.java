package br.com.DnotasTecnologia.Dnotas.activities;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import stone.providers.ReversalProvider;
import stone.application.interfaces.StoneCallbackInterface;

public class CancelTransactionsWorker extends Worker {

    public CancelTransactionsWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
public Result doWork() {
    final Context context = getApplicationContext();
    final ReversalProvider reversalProvider = new ReversalProvider(context);

    final android.os.CountDownTimer timer = new android.os.CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            // Você pode usar esse método para atualizar uma notificação ou algo semelhante
        }

        @Override
        public void onFinish() {
            reversalProvider.execute();
        }
    };

    reversalProvider.setDialogMessage("Cancelando transações com erro");
    reversalProvider.setConnectionCallback(new StoneCallbackInterface() {
        @Override
        public void onSuccess() {
            new android.os.Handler(context.getMainLooper()).post(() -> 
                Toast.makeText(context, "Transações canceladas com sucesso", Toast.LENGTH_SHORT).show()
            );
            timer.cancel();  // Para o temporizador
            // Pode usar o método abaixo para garantir que o trabalho é sinalizado como bem-sucedido após algum tempo ou lógica adicional.
            // return Result.success();
        }

        @Override
        public void onError() {
            new android.os.Handler(context.getMainLooper()).post(() -> 
                Toast.makeText(context, "Ocorreu um erro durante o cancelamento das tabelas: " + reversalProvider.getListOfErrors(), Toast.LENGTH_SHORT).show()
            );
            timer.cancel();  // Para o temporizador
            // Retornar falha se ocorrer um erro
            // return Result.failure();
        }
    });
    
    // Retornar um resultado intermediário e usar o temporizador para decidir quando retornar sucesso/falha
    return Result.retry(); 
}
}

