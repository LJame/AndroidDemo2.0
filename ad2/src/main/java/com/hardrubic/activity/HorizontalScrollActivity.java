package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import com.hardrubic.adapter.AreaAdapter;
import com.hardrubic.entity.Area;
import com.hardrubic.util.ScreenUtils;
import com.hardrubic.util.ViewUtil;
import java.util.ArrayList;
import java.util.List;

public class HorizontalScrollActivity extends TitleActivity {

    private Context mContext;

    private List<Area> mAreaList = new ArrayList<>();
    private List<Area> mProvinceList = new ArrayList<>();
    private List<Area> mCityList = new ArrayList<>();
    private List<Area> mRegionList = new ArrayList<>();
    private List<Area> mStreetList = new ArrayList<>();

    private final static int PROVINCE = 1;
    private final static int CITY = 2;
    private final static int REGION = 3;
    private final static int STREET = 4;

    private static int mLastAreaType = STREET;
    private static int mCurrentAreaType = PROVINCE;

    HorizontalScrollView mLayoutMain;
    CardView mCardProvince;
    CardView mCardCity;
    CardView mCardRegion;
    CardView mCardStreet;
    ListView mLvProvince;
    ListView mLvCity;
    ListView mLvRegion;
    ListView mLvStreet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_scroll);
        this.mContext = this;

        updateTitle();
        initData();
        findView();
        initView();
    }

    private void updateTitle() {
        if (mCurrentAreaType == PROVINCE) {
            setTitleText("省份");
        } else if (mCurrentAreaType == CITY) {
            setTitleText("城市");
        } else if (mCurrentAreaType == REGION) {
            setTitleText("县区");
        } else {
            setTitleText("街道");
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
        }
        return super.dispatchTouchEvent(ev);
    }

    private void initView() {
        //初始化
        final int screenWidth = ScreenUtils.getScreenWidthPixels(mContext);
        ViewUtil.setWidth(mCardProvince, (int) (screenWidth * 0.5));
        ViewUtil.setWidth(mCardCity, (int) (screenWidth * 0.5));
        ViewUtil.setWidth(mCardRegion, (int) (screenWidth * 0.5));
        ViewUtil.setWidth(mCardStreet, (int) (screenWidth * 0.5));
        if (mLastAreaType == CITY) {
            mCardCity.setVisibility(View.VISIBLE);
        }
        if (mLastAreaType == REGION) {
            mCardCity.setVisibility(View.VISIBLE);
            mCardRegion.setVisibility(View.VISIBLE);
        }
        if (mLastAreaType == STREET) {
            mCardCity.setVisibility(View.VISIBLE);
            mCardRegion.setVisibility(View.VISIBLE);
            mCardStreet.setVisibility(View.VISIBLE);
        }

        final int scrollWidth = screenWidth * 2 * mLastAreaType / 4;   //滚动全长
        mLvProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchProvince(mProvinceList.get(position).getAreaid());
                mLayoutMain.scrollTo(0, 0);
            }
        });
        mLvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchCity(mCityList.get(position).getAreaid());
                mLayoutMain.scrollTo(scrollWidth / mLastAreaType, 0);
            }
        });
        mLvRegion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchRegion(mRegionList.get(position).getAreaid());
                mLayoutMain.scrollTo(scrollWidth / mLastAreaType * 2, 0);
            }
        });

        findProvinceList();
        AreaAdapter provinceAdapter = new AreaAdapter(mContext, R.layout.item_area, mProvinceList);
        mLvProvince.setAdapter(provinceAdapter);

        Area area = mProvinceList.get(0);
        if (area != null) {
            switchProvince(area.getAreaid());
        }
    }

    private void switchProvince(Long provinceAreaId) {
        //清空
        mRegionList.clear();
        AreaAdapter regionAdapter = new AreaAdapter(mContext, R.layout.item_area, mRegionList);
        mLvRegion.setAdapter(regionAdapter);
        mStreetList.clear();
        AreaAdapter streetAdapter = new AreaAdapter(mContext, R.layout.item_area, mStreetList);
        mLvStreet.setAdapter(streetAdapter);

        findCityListByProvince(provinceAreaId);
        AreaAdapter cityAdapter = new AreaAdapter(mContext, R.layout.item_area, mCityList);
        mLvCity.setAdapter(cityAdapter);

        if (!mCityList.isEmpty()) {
            Area area = mCityList.get(0);
            if (area != null) {
                switchCity(area.getAreaid());
            }
        }
    }

    private void switchCity(Long cityAreaId) {
        //清空
        mStreetList.clear();
        AreaAdapter streetAdapter = new AreaAdapter(mContext, R.layout.item_area, mStreetList);
        mLvStreet.setAdapter(streetAdapter);

        findRegionListByCity(cityAreaId);
        AreaAdapter regionAdapter = new AreaAdapter(mContext, R.layout.item_area, mRegionList);
        mLvRegion.setAdapter(regionAdapter);

        if (!mRegionList.isEmpty()) {
            Area area = mRegionList.get(0);
            if (area != null) {
                switchRegion(area.getAreaid());
            }
        }
    }

    private void switchRegion(Long regionAreaId) {
        findStreetListByRegion(regionAreaId);
        AreaAdapter streetAdapter = new AreaAdapter(mContext, R.layout.item_area, mStreetList);
        mLvStreet.setAdapter(streetAdapter);
    }

    private void findProvinceList() {
        mProvinceList = new ArrayList<>();
        for (Area area : mAreaList) {
            if (area.getType() == PROVINCE) {
                mProvinceList.add(area);
            }
        }
    }

    private void findCityListByProvince(Long provinceAreaId) {
        mCityList = new ArrayList<>();
        for (Area area : mAreaList) {
            if (area.getType() == CITY && area.getFatherid().longValue() == provinceAreaId.longValue()) {
                mCityList.add(area);
            }
        }
    }

    private void findRegionListByCity(Long cityAreaId) {
        mRegionList = new ArrayList<>();
        for (Area area : mAreaList) {
            if (area.getType() == REGION && area.getFatherid().longValue() == cityAreaId.longValue()) {
                mRegionList.add(area);
            }
        }
    }

    private void findStreetListByRegion(Long regionAreaId) {
        mStreetList = new ArrayList<>();
        for (Area area : mAreaList) {
            if (area.getType() == STREET && area.getFatherid().longValue() == regionAreaId.longValue()) {
                mStreetList.add(area);
            }
        }
    }

    private void findView() {
        mLayoutMain = (HorizontalScrollView) findViewById(R.id.layout_main);
        mCardProvince = (CardView) findViewById(R.id.card_province);
        mCardCity = (CardView) findViewById(R.id.card_city);
        mCardRegion = (CardView) findViewById(R.id.card_region);
        mCardStreet = (CardView) findViewById(R.id.card_street);
        mLvProvince = (ListView) findViewById(R.id.lv_province);
        mLvCity = (ListView) findViewById(R.id.lv_city);
        mLvRegion = (ListView) findViewById(R.id.lv_region);
        mLvStreet = (ListView) findViewById(R.id.lv_street);
    }

    private void initData() {
        Area area = new Area(1l, "广东省", 0l, PROVINCE);
        mAreaList.add(area);
        area = new Area(2l, "青海省", 0l, PROVINCE);
        mAreaList.add(area);
        area = new Area(3l, "广西省", 0l, PROVINCE);
        mAreaList.add(area);
        area = new Area(4l, "河北省", 0l, PROVINCE);
        mAreaList.add(area);
        area = new Area(5l, "河南省", 0l, PROVINCE);
        mAreaList.add(area);
        area = new Area(6l, "山东省", 0l, PROVINCE);
        mAreaList.add(area);
        area = new Area(7l, "山西省", 0l, PROVINCE);
        mAreaList.add(area);
        area = new Area(8l, "湖北省", 0l, PROVINCE);
        mAreaList.add(area);
        area = new Area(9l, "湖南省", 0l, PROVINCE);
        mAreaList.add(area);
        area = new Area(10l, "浙江省", 0l, PROVINCE);
        mAreaList.add(area);

        area = new Area(100l, "广州市", 1l, CITY);
        mAreaList.add(area);
        area = new Area(101l, "深圳市", 1l, CITY);
        mAreaList.add(area);
        area = new Area(102l, "东莞市", 1l, CITY);
        mAreaList.add(area);
        area = new Area(103l, "中山市", 1l, CITY);
        mAreaList.add(area);
        area = new Area(104l, "珠海市", 1l, CITY);
        mAreaList.add(area);
        area = new Area(105l, "佛山市", 1l, CITY);
        mAreaList.add(area);
        area = new Area(106l, "江门市", 1l, CITY);
        mAreaList.add(area);
        area = new Area(107l, "云浮市", 1l, CITY);
        mAreaList.add(area);
        area = new Area(108l, "清远市", 1l, CITY);
        mAreaList.add(area);
        area = new Area(109l, "韶关市", 1l, CITY);
        mAreaList.add(area);
        area = new Area(110l, "河源市", 1l, CITY);
        mAreaList.add(area);

        area = new Area(200l, "桂林市", 3l, CITY);
        mAreaList.add(area);
        area = new Area(201l, "北海市", 3l, CITY);
        mAreaList.add(area);
        area = new Area(202l, "南宁市", 3l, CITY);
        mAreaList.add(area);

        area = new Area(1000l, "越秀区", 100l, REGION);
        mAreaList.add(area);
        area = new Area(1001l, "天河区", 100l, REGION);
        mAreaList.add(area);
        area = new Area(1002l, "海珠区", 100l, REGION);
        mAreaList.add(area);
        area = new Area(1003l, "白云区", 100l, REGION);
        mAreaList.add(area);
        area = new Area(1004l, "番禺区", 100l, REGION);
        mAreaList.add(area);
        area = new Area(1005l, "荔湾区", 100l, REGION);
        mAreaList.add(area);

        area = new Area(10000l, "北京街", 1000l, STREET);
        mAreaList.add(area);
        area = new Area(10001l, "同仁街", 1000l, STREET);
        mAreaList.add(area);
        area = new Area(10002l, "人民街", 1000l, STREET);
        mAreaList.add(area);
    }
}
