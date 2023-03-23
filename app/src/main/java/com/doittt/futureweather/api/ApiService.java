package com.doittt.futureweather.api;

import static com.doittt.futureweather.Constant.API_KEY;

import com.doittt.futureweather.db.bean.LifestyleResponse;
import com.doittt.futureweather.db.bean.NowWeatherResponse;
import com.doittt.futureweather.db.bean.SearchCityResponse;
import com.doittt.futureweather.db.bean.DailyWeatherResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author doittt
 */
public interface ApiService {

    /**
     * 搜索城市  模糊搜索，国内范围 返回10条数据
     *
     * @param location 城市名
     * @return NewSearchCityResponse 搜索城市数据返回
     */
    @GET("/v2/city/lookup?key=" + API_KEY + "&range=cn")
    Observable<SearchCityResponse> searchCity(@Query("location") String location);

    /**
     * 实时天气，根据之前searchCity搜索到的location来查找
     * @param location
     * @return
     */
    @GET("/v7/weather/now?key=" + API_KEY)
    Observable<NowWeatherResponse> nowWeather(@Query("location") String location);

    /**
     * 每日天气预报
     * @param location
     * @return
     */
    @GET("/v7/weather/7d?key=" + API_KEY)
    Observable<DailyWeatherResponse> dailyWeather(@Query("location") String location);

    /**
     *
     * 生活指数
     * @param type
     * @param location
     * @return
     */
    @GET("/v7/indices/1d?key=" + API_KEY)
    Observable<LifestyleResponse> lifestyle(@Query("type") String type, @Query("location") String location);



}
