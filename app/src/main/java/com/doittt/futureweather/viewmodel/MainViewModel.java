package com.doittt.futureweather.viewmodel;

/**
 * @author doittt
 */

import androidx.lifecycle.MutableLiveData;

import com.doittt.futureweather.db.bean.DailyWeatherResponse;
import com.doittt.futureweather.db.bean.LifestyleResponse;
import com.doittt.futureweather.db.bean.NowWeatherResponse;
import com.doittt.futureweather.db.bean.Province;
import com.doittt.futureweather.db.bean.SearchCityResponse;
import com.doittt.futureweather.repository.CityRepository;
import com.doittt.futureweather.repository.NowWeatherRepository;
import com.doittt.futureweather.repository.SearchCityRepository;
import com.doittt.futureweather.ui.MainActivity;
import com.doittt.library.base.BaseViewModel;

import java.util.List;

/**
 * 主页面ViewModel
 * {@link MainActivity}
 * @author doittt
 */
public class MainViewModel extends BaseViewModel {

    public MutableLiveData<SearchCityResponse> searchCityResponseMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<NowWeatherResponse> nowWeatherResponseMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<DailyWeatherResponse> dailyResponseMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<LifestyleResponse> lifestyleResponseMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Province>> cityMutableLiveData = new MutableLiveData<>();

    /**
     * 搜索城市
     * @param cityName 城市名称
     */
    public void searchCity(String cityName) {
        SearchCityRepository.getInstance().searchCity(searchCityResponseMutableLiveData, failed, cityName);
    }

    /**
     * 实况天气
     * @param cityId
     */
    public void nowWeather(String cityId){
        NowWeatherRepository.getInstance().nowWeather(nowWeatherResponseMutableLiveData,failed,cityId);
    }

    /**
     * 每日天气预报
     * @param cityId
     */
    public void dailyWeather(String cityId) {
        NowWeatherRepository.getInstance().dailyWeather(dailyResponseMutableLiveData, failed, cityId);
    }

    /**
     * 生活指数
     * @param cityId
     */
    public void lifestyle(String cityId) {
        NowWeatherRepository.getInstance().lifestyle(lifestyleResponseMutableLiveData, failed, cityId);
    }

    /**
     * 获得所有城市信息
     */
    public void getAllCity() {
        CityRepository.getInstance().getCityData(cityMutableLiveData);
    }


}
