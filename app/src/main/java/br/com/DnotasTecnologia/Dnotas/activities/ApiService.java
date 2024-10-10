package br.com.DnotasTecnologia.Dnotas.activities;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import okhttp3.ResponseBody;

public interface ApiService {
    @POST("/tosend")
    Call<ResponseBody> enviarTransacao(@Body Transacao transacao);  // Certifique-se de importar a classe correta
}
