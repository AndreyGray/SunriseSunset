package andreyskakunenko.sunrisesunset.Retrofit;

import andreyskakunenko.sunrisesunset.models.GPlaceModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface GoogleAPI {
    @GET
    Call<GPlaceModel> getLocation(@Url String url);
}
