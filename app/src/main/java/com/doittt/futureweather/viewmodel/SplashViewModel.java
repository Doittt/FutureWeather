package com.doittt.futureweather.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.doittt.futureweather.db.bean.Province;
import com.doittt.futureweather.repository.CityRepository;
import com.doittt.library.base.BaseViewModel;

import java.util.List;

/**
 * @author doittt
 */
public class SplashViewModel extends BaseViewModel {

    public MutableLiveData<List<Province>> listMutableLiveData = new MutableLiveData<>();

    /**
     * 添加城市数据
     */
    public void addCityData(List<Province> provinceList) {
        CityRepository.getInstance().addCityData(provinceList);
    }

    /**
     * 获取所有城市数据
     */
    public void getAllCityData() {
        CityRepository.getInstance().getCityData(listMutableLiveData);
    }
}

