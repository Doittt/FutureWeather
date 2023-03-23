package com.doittt.futureweather;

import org.junit.Test;

import static org.junit.Assert.*;

import com.baidu.location.BDLocation;
import com.doittt.futureweather.location.LocationCallback;
import com.doittt.futureweather.location.MyLocationListener;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest implements LocationCallback {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    private MyLocationListener locationListener;

    public void test1(){
        locationListener.setCallback(this);
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

    }
}