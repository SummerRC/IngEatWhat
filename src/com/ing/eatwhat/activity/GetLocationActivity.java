package com.ing.eatwhat.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.ing.eatwhat.R;

/**
 * 
 * @author dlx
 * @date 2014/8/24
 * @version v1.2
 *
 */

public class GetLocationActivity extends Activity implements
		OnGetPoiSearchResultListener, BaiduMap.OnMapClickListener,
		OnGetRoutePlanResultListener {

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	//定义导航方式
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker = null;
	// 初始位置的相关信息
	BDLocation currentLocation = null;
	//经纬度信息
	LatLng currentData = null;
	// 是否首次定位
	boolean isFirstLoc = true;

	// UI
	OnCheckedChangeListener radioButtonListener;
	Button requestLocButton;

	// POISearch
	private PoiSearch mPoiSearch = null;
	String target="工业南路44号闫府私房菜";
//	String target="济南国际会展中心";
	// Map
	public  BaiduMap mBaiduMap = null;
	MapView mMapView = null;

	// 关于路线规划
	// 节点索引,供浏览节点时使用
	int nodeIndex = -1;
	protected RouteLine route = null;
	public OverlayManager routeOverlay = null;
	boolean useDefaultIcon = false;
	// 泡泡覆盖物
	private TextView popupText = null;
	// 路线搜索
	RoutePlanSearch mSearch = null;
	public TransitRouteResult RouteResult =null;
	public int routeSize=0;
	public TransitRouteOverlay overlay= null;
	int index=0;
	int choosedRoute=0;
	Button btnBus = null;
    BaiduMapOptions mapOptions = new BaiduMapOptions();  
    String title=null;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.baiduactivity_main);
		// 初始化地图
		mMapView = (MapView) findViewById(R.id.bmapView);
		
		int count = mMapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
 //            隐藏百度logo ZoomControl
             if (child instanceof ImageView || child instanceof ZoomControls)
             {
            	 child.setVisibility(View.GONE);
        }
        }
        //设置初始缩放程度
        MapStatus.Builder mapBuilder = new MapStatus.Builder();
        mapBuilder.zoom(15);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapBuilder.build());
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(mapStatusUpdate);
		// 初始化定位节点
		requestLocButton = (Button) findViewById(R.id.button1);
		mCurrentMode = LocationMode.NORMAL;
		requestLocButton.setText("普通");

		// 初始化marker
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));

		OnClickListener btnClickListener = new OnClickListener() {
			public void onClick(View v) {
				switch (mCurrentMode) {
				case NORMAL:
					requestLocButton.setText("跟随");
					mCurrentMode = LocationMode.FOLLOWING;
					// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));

					break;
				case FOLLOWING:
					requestLocButton.setText("普通");
					mCurrentMode = LocationMode.NORMAL;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				}
			}
		};
		requestLocButton.setOnClickListener(btnClickListener);

		// 是否可以定位
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		//LocationClientOption类，该类用来设置定位SDK的定位方式
		LocationClientOption option = new LocationClientOption();
		// 打开gps
		option.setOpenGps(true);
		// 设置返回类型，all = 表示返回所有的数据,否则只返回经纬度，一定注意！！！
		option.setAddrType("all");
		// 设置坐标类型，百度坐标系
		option.setCoorType("bd09ll");
		//设置发起定位请求的间隔时间为1000ms
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		// 初始化poi检索
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);

		// 将本类设置为点击监听器
		mBaiduMap.setOnMapClickListener(this);
		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);
		
		btnBus = (Button) findViewById(R.id.btnBus);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	/**
	 * 影响搜索按钮点击事件
	 */
	public void searchButtonProcess(View v) {
		// 设置初始位置坐标信息
		currentData = new LatLng(currentLocation.getLatitude(),
				currentLocation.getLongitude());
		/*
		 *这是测试距离的方法
		 * 测试距离=DistanceUtil.getDistance(起点经纬度, 终点经纬度)
		LatLng a = new LatLng(currentLocation.getLatitude()+5,currentLocation.getLongitude()+5);
		Double b =DistanceUtil.getDistance(currentData, a);
		System.out.println("********************************"+b);
		*/
		mPoiSearch.searchInCity((new PoiCitySearchOption())
				.city(currentLocation.getCity().toString()).keyword(target).pageCapacity(1));

	}
	
	public void show(View v){
	//用于控制执行哪一种模式的onGetTransitRouteResult
		index=1;
		currentData = new LatLng(currentLocation.getLatitude(),
				currentLocation.getLongitude());

		mPoiSearch.searchInCity((new PoiCitySearchOption())
				.city(currentLocation.getCity().toString()).keyword(target).pageCapacity(1));
		//模拟按钮点击
		btnBus.performClick();
	}

	//响应btn，选择哪一种交通方式
public void RoutePlaneMethods(View v){
	//重置
	route = null;
	mBaiduMap.clear();
	//定义起点、终点
	PlanNode stNode = PlanNode.withLocation(currentData);
	PlanNode enNode = PlanNode.withCityNameAndPlaceName(currentLocation.getCity().toString(), target);
	if(v.getId()==R.id.btnBus){
		 mSearch.transitSearch((new TransitRoutePlanOption())
                 .from(stNode)
                 .city(currentLocation.getCity().toString())
                 .to(enNode));
	}
	if(v.getId()==R.id.btnWalk){
		mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode)
				.to(enNode));
	}
	if (v.getId()==R.id.btnCar) {
		mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
	}
}


	// 返回poi检索结果
	public void onGetPoiResult(PoiResult result) {
		// 没找到location
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			return;
		}
		// 检索结果正常返回
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			// 覆盖物
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();//zoom缩放
			return;
		}
		// 检索词有岐义
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(GetLocationActivity.this, strInfo, Toast.LENGTH_LONG)
					.show();
		}
	}

	// 检索poi详细检索信息
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(GetLocationActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(GetLocationActivity.this,
					result.getName() + ": " + result.getAddress(),
					Toast.LENGTH_SHORT).show();
Log.e("444",result.getAddress() );
		}
	}

	//自定义覆盖物监听器，主要为了重写onPoiClick实现详细查询
	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			return true;
		}
	}

	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不再处理新接收的位置
			if (location == null || mMapView == null)
				return;
			currentLocation = location;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				// 改变地图状态
				mBaiduMap.animateMapStatus(u);
			}
		}

		// 获取定位信息，此方法获取poi信息
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	/**
	 * 节点浏览示例
	 */
	public void nodeClick(View v) {
		if (route == null || route.getAllStep() == null) {
			return;
		}
		// 获取节结果信息
		 LatLng nodeLocation = null;
	        String nodeTitle = null;
	        Object step = route.getAllStep().get(nodeIndex);
	        if (step instanceof DrivingRouteLine.DrivingStep) {
	        	// weather step is the example of DrivingRouteLine.DrivingStep
	            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrace().getLocation();
	            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
	        } else if (step instanceof WalkingRouteLine.WalkingStep) {
	        	//getEntrance 获得路段入口信息
	            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrace().getLocation();
	            // 	getInstructions()   获取该路段换乘说明
	            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
	        } else if (step instanceof TransitRouteLine.TransitStep) {
	            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrace().getLocation();
	            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
	        }

		if (nodeLocation == null || nodeTitle == null) {
			return;
		}

		  //移动节点至中心
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(GetLocationActivity.this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        //在地图中显示一个信息窗口，可以设置一个View作为该窗口的内容，也可以设置一个 BitmapDescriptor 作为该窗口的内容。
        mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, null));

	}

	//实现了步行的导航
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(GetLocationActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
					.show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			nodeIndex = -1;
			route = result.getRouteLines().get(0);
			WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			routeOverlay = overlay;
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}

	}

	public void onGetTransitRouteResult(TransitRouteResult result) {

		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(GetLocationActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
					.show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			nodeIndex = -1;
			// TransitRouteLine.TransitStep TransitRouteResult
			RouteResult = result;
			//结果.获得所有乘车路线.得到集合的大小
			routeSize = result.getRouteLines().size();
			if(index==0){
			Intent intent = new Intent(GetLocationActivity.this, BusActivity.class);
			intent.putExtra("size", routeSize);
			for (int i = 0; i < routeSize; i++) {
				route = result.getRouteLines().get(i);
				//get(1)是为了下一步获得公交路线名称   get(0)可以理解为当前所在地，可能不是公交车站
				//所以会报错，get（n>1）由于中途可能会有换乘或者步行，故也有可能报错，推荐用1
				Object step = route.getAllStep().get(1);
				//.getVehicleInfo().getTitle()     获得交通工具信息.获得titile（EG:116路、119路）
//				Log.i("gettitle",((TransitRouteLine.TransitStep) step).getInstructions());
					title = ((TransitRouteLine.TransitStep) step)
							.getVehicleInfo().getTitle();
				System.out.println("******************************" + title);
				String ss = "route" + i;
				intent.putExtra(ss, title);
			}
			startActivity(intent);
			}else{
			route = result.getRouteLines().get(choosedRoute);
			 overlay = new TransitRouteOverlay(mBaiduMap);
			 mBaiduMap.setOnMarkerClickListener(overlay);
			 routeOverlay = overlay;
			 overlay.setData(result.getRouteLines().get(choosedRoute));
			 overlay.addToMap();
			 overlay.zoomToSpan();
			}
			//参数，用来控制什么时候可以执行show方法
			index=0;
		}
	}

	    @Override
	    public void onGetDrivingRouteResult(DrivingRouteResult result) {
	        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
	            Toast.makeText(GetLocationActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
	        }
	        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
	            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
	            //result.getSuggestAddrInfo()
	            return;
	        }
	        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
	            nodeIndex = -1;
	            route = result.getRouteLines().get(0);
	            DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
	            routeOverlay = overlay;
	            mBaiduMap.setOnMarkerClickListener(overlay);
	            overlay.setData(result.getRouteLines().get(0));
	            overlay.addToMap();
	            overlay.zoomToSpan();
	        }
	    }

	@Override
	public void onMapClick(LatLng arg0) {
		mBaiduMap.hideInfoWindow();
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		choosedRoute = getIntent().getIntExtra("choosedRoute", 0);
    	if (choosedRoute!=0) {
    		System.out.println("onResumeonResumeonResusumeonResumeonResumeonResume"+choosedRoute);
		}
		super.onResume();
		
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mPoiSearch.destroy();
		mSearch.destroy();
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}
}