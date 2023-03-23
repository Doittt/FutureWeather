package com.doittt.futureweather.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.doittt.futureweather.R;
import com.doittt.futureweather.ui.adapter.DailyWeatherAdapter;
import com.doittt.futureweather.ui.adapter.LifestyleAdapter;
import com.doittt.futureweather.db.bean.DailyWeatherResponse;
import com.doittt.futureweather.db.bean.LifestyleResponse;
import com.doittt.futureweather.db.bean.NowWeatherResponse;
import com.doittt.futureweather.db.bean.SearchCityResponse;
import com.doittt.futureweather.databinding.ActivityMainBinding;
import com.doittt.futureweather.location.LocationCallback;
import com.doittt.futureweather.location.MyLocationListener;
import com.doittt.futureweather.utils.CityDialog;
import com.doittt.futureweather.viewmodel.MainViewModel;
import com.doittt.library.base.NetworkActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends NetworkActivity<ActivityMainBinding> implements LocationCallback,CityDialog.SelectedCityCallback {

    /**
     * ActivityMainBinding是ViewBinding的一个具体实现
     */
//    private ActivityMainBinding binding;

    public LocationClient mLocationClient = null;
    private final MyLocationListener myListener = new MyLocationListener();

    //权限数组
    private final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求权限意图
    private ActivityResultLauncher<String[]> requestPermissionIntent;

    private MainViewModel viewModel;

    private final List<DailyWeatherResponse.DailyBean> dailyBeanList = new ArrayList<>();
    private final DailyWeatherAdapter dailyAdapter = new DailyWeatherAdapter(dailyBeanList);

    private final List<LifestyleResponse.DailyBean> lifestyleList = new ArrayList<>();

    private final LifestyleAdapter lifestyleAdapter = new LifestyleAdapter(lifestyleList);

    //城市弹窗
    private CityDialog cityDialog;



    /**
     * Activity是应用程序的主要组件，用于管理应用程序的界面和交互。当用户启动应用程序时，
     * 系统会自动启动应用程序中的某个Activity作为应用程序的入口点，即启动Activity的onCreate()方法作为应用程序的入口。
     */
    @Override
    protected void onCreate(/*Bundle savedInstanceState*/) {
        registerIntent();
//        super.onCreate(savedInstanceState);
//        //绑定Activity的布局文件，从而可以在代码中访问布局文件中的视图
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        //初始化定位
        initLocation();
        //请求权限
        requestPermission();
        //初始化视图
        initView();
        //绑定ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        //获取城市数据
        viewModel.getAllCity();

    }

    /**
     * 初始化定位功能，并设置定位参数
     */
    private void initLocation() {
        try {
            mLocationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mLocationClient != null) {
            //将当前类作为回调接口，以便在定位结果返回后能够及时处理定位结果
            myListener.setCallback(this);
            //注册定位监听
            mLocationClient.registerLocationListener(myListener);
            LocationClientOption option = new LocationClientOption();
            //如果开发者需要获得当前点的地址信息，此处必须为true
            option.setIsNeedAddress(true);
            //可选，设置是否需要最新版本的地址信息。默认不需要，即参数为false
            option.setNeedNewVersionRgc(true);
            //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
            mLocationClient.setLocOption(option);
        }
    }

    /**
     *
     * 获取Location信息,并打印在Text上
     * @param bdLocation 定位数据
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        double latitude = bdLocation.getLatitude();    //获取纬度信息
        double longitude = bdLocation.getLongitude();    //获取经度信息
        float radius = bdLocation.getRadius();    //获取定位精度，默认值为0.0f
        String coorType = bdLocation.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
        int errorCode = bdLocation.getLocType();//161  表示网络定位结果
        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
        String addr = bdLocation.getAddrStr();    //获取详细地址信息
        String country = bdLocation.getCountry();    //获取国家
        String province = bdLocation.getProvince();    //获取省份
        String city = bdLocation.getCity();    //获取城市
        String district = bdLocation.getDistrict();    //获取区县
        String street = bdLocation.getStreet();    //获取街道信息
        String locationDescribe = bdLocation.getLocationDescribe();    //获取位置描述信息
//        binding.tvAddressDetail.setText(addr);//设置文本显示

//        searchCity(district);
        if (viewModel != null && district != null) {
            //显示当前城市
            binding.tvCity.setText(district);
            //搜索城市
            viewModel.searchCity(district);

        } else {
            Log.e("TAG", "district: " + district);
        }

    }

    /**
     *
     *
     */
    private void startLocation() {
        if (mLocationClient != null) {
            mLocationClient.start();
        }
    }

    /**
     * 注册意图
     */
    private void registerIntent() {
        //请求权限意图
        requestPermissionIntent = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean fineLocation = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
            boolean writeStorage = Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));
            if (fineLocation && writeStorage) {
                //权限已经获取到，开始定位
                startLocation();
            }
        });
    }

    private void requestPermission() {
        //因为项目的最低版本API是23，所以肯定需要动态请求危险权限，只需要判断权限是否拥有即可
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //开始权限请求
            requestPermissionIntent.launch(permissions);
            return;
        }
        //开始定位
        startLocation();
    }

//    /**
//     *
//     * @param district
//     */
//    private void searchCity(String district) {
//        //使用Get异步请求
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                //拼接访问地址
//                .url("https://geoapi.qweather.com/v2/city/lookup?key=a0e4b5e560b84031aeda46ba758c1303&location="+district)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if(response.isSuccessful()){//回调的方法执行在子线程。
//                    Log.d("a","获取数据成功了");
//                    Log.d("a","response.code()=="+response.code());
//                    Log.d("a","response.body().string()=="+response.body().string());
//                }
//            }
//        });
//    }

    /**
     * 数据观察
     */
    @Override
    protected void onObserveData() {
        if (viewModel != null) {
            //城市数据返回
            viewModel.searchCityResponseMutableLiveData.observe(this, searchCityResponse -> {
                List<SearchCityResponse.LocationBean> location = searchCityResponse.getLocation();
                if (location != null && location.size() > 0) {
                    String id = location.get(0).getId();
                    //获取到城市的ID
                    if (id != null) {
                        //通过城市ID查询城市实时天气
                        viewModel.nowWeather(id);
                        //通过城市id查询天气预报
                        viewModel.dailyWeather(id);
                        //通过城市id查询生活指数
                        viewModel.lifestyle(id);
                    }
                }
            });
            //实况天气返回
            viewModel.nowWeatherResponseMutableLiveData.observe(this, nowWeatherResponse -> {
                NowWeatherResponse.NowBean now = nowWeatherResponse.getNow();
                if (now != null) {
                    binding.tvInfo.setText(now.getText());
                    binding.tvTemp.setText(now.getTemp());
                    binding.tvUpdateTime.setText("最近更新时间：" + dateConvert(nowWeatherResponse.getUpdateTime()));

                    binding.tvWindDirection.setText("风向     " + now.getWindDir());//风向
                    binding.tvWindPower.setText("风力     " + now.getWindScale() + "级");//风力
                    binding.wwBig.startRotate();//大风车开始转动
                    binding.wwSmall.startRotate();//小风车开始转动

                }
            });
            //天气预报返回
            viewModel.dailyResponseMutableLiveData.observe(this, dailyResponse -> {
                List<DailyWeatherResponse.DailyBean> daily = dailyResponse.getDaily();
                if (daily != null) {
                    if (dailyBeanList.size() > 0) {
                        dailyBeanList.clear();
                    }
                    dailyBeanList.addAll(daily);
                    dailyAdapter.notifyDataSetChanged();
                }
            });
            //生活指数返回
            viewModel.lifestyleResponseMutableLiveData.observe(this, lifestyleResponse -> {
                List<LifestyleResponse.DailyBean> daily = lifestyleResponse.getDaily();
                if (daily != null) {
                    if (lifestyleList.size() > 0) {
                        lifestyleList.clear();
                    }
                    lifestyleList.addAll(daily);
                    lifestyleAdapter.notifyDataSetChanged();
                }
            });

            //城市信息返回
            viewModel.cityMutableLiveData.observe(this, provinces -> {
                //城市弹窗初始化
                cityDialog = CityDialog.getInstance(MainActivity.this, provinces);
                cityDialog.setSelectedCityCallback(this);
            });


            //错误信息返回
            viewModel.failed.observe(this, this::showLongMsg);
        }
    }


    private void initView() {
        setToolbarMoreIconCustom(binding.materialToolbar);//修改主菜单图标
        binding.rvDaily.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDaily.setAdapter(dailyAdapter);
        binding.rvLifestyle.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLifestyle.setAdapter(lifestyleAdapter);

    }


    /**
     *
     * 字符串时间转换
     * "yyyy-MM-dd'T'HH:mmXXX" --> "yyyy-MM-dd HH:mm"
     * @param input
     * @return
     */
    public static String dateConvert(String input) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = inputFormat.parse(input);
            String output = outputFormat.format(date);
            return output;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return input;
    }

    /**
     * 改变主菜单图标
     * @param toolbar
     */
    public void setToolbarMoreIconCustom(Toolbar toolbar) {
        if (toolbar == null) return;
        toolbar.setTitle("");
        Drawable moreIcon = ContextCompat.getDrawable(toolbar.getContext(), R.drawable.ic_round_add_32);
        if (moreIcon != null) toolbar.setOverflowIcon(moreIcon);
        setSupportActionBar(toolbar);
    }

    /**
     * 点击menu的处理
     * @param menu The options menu in which you place your items.
     *
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 主菜单下拉内容
     * @param item The menu item that was selected.
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_switching_cities) {
            if (cityDialog != null) cityDialog.show();
        }
        return true;
    }


    @Override
    public void selectedCity(String cityName) {
        //搜索城市
        viewModel.searchCity(cityName);
        //显示所选城市
        binding.tvCity.setText(cityName);
    }

}