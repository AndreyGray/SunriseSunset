package andreyskakunenko.sunrisesunset.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static andreyskakunenko.sunrisesunset.MainActivity.SUN_URL;

public class RetrofitClient {
    private static Retrofit ourInstance;

    public static Retrofit getInstance() {
        if(ourInstance == null)
            ourInstance = new Retrofit.Builder()
                    .baseUrl(SUN_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        return ourInstance;
    }

    private RetrofitClient() {
    }

}
