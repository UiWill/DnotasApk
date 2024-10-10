package br.com.DnotasTecnologia.Dnotas.activities;

import android.app.Dialog;
import android.content.Intent;
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
import java.util.Arrays;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.ParseException;



public class falecomnos extends AppCompatActivity {

    private EditText problemDescription;
    private EditText emailInput;
    private Button sendReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Associa o layout XML ao código da Activity
        setContentView(R.layout.falecomnos);

        // Inicializa os componentes da interface
        problemDescription = findViewById(R.id.problemDescription);
        emailInput = findViewById(R.id.editTextTextEmailAddress);
        sendReportButton = findViewById(R.id.sendReportButton);

        // Adiciona o clique ao botão
        sendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pega o conteúdo do formulário
               
                String subject =  emailInput.getText().toString();
                String content = problemDescription.getText().toString();
                String to_email = "suporte@dnotas.com.br";

                // Verifica se os campos estão preenchidos
                if (!subject.isEmpty() && !content.isEmpty()) {
                    // Chama o método para enviar o e-mail
                    sendEmail(subject, content, to_email);
                } else {
                    // Exibe uma mensagem de erro se os campos estiverem vazios
                    Toast.makeText(falecomnos.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para enviar o e-mail
    public void sendEmail(String subject, String content, String to_email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to_email});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Escolha um cliente de e-mail:"));
    }
}
