package br.com.DnotasTecnologia.Dnotas.activities;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CupomApiService {
    @POST("/nfc-e-solicitar")
    Call<ResponseBody> solicitarNfce(@Body CupomRequest request);
}
