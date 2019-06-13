package andreyskakunenko.sunrisesunset.Retrofit;

import andreyskakunenko.sunrisesunset.models.Sun;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MyAPI {

    @GET
    Call<Sun> getData(@Url String url);

}
