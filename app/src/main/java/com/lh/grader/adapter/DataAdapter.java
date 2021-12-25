package com.lh.grader.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lh.grader.R;
import com.lh.grader.model.ResultData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author zhao
 */
public class DataAdapter extends BaseQuickAdapter<ResultData, BaseViewHolder> {


    public DataAdapter() {
        super(R.layout.item_view);
    }

    @Override
    protected void convert(BaseViewHolder helper, ResultData item) {
        String s = utc2Local(String.valueOf(item.getGps().getUtcTime()), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
        helper.setText(R.id.tvTime,"时间："+s);
        helper.setText(R.id.tvDevice,"设备："+item.getSn());
        helper.setText(R.id.tvLat,"经度-"+item.getGps().getLongUnit()+"："+item.getGps().getLong());
        helper.setText(R.id.tvLong,"纬度-"+item.getGps().getLatUnit()+"："+item.getGps().getLat());
        helper.setText(R.id.tvAltitude,"高程："+item.getGps().getAltitude()+""+item.getGps().getUnitStr1());
    }


    public static String utc2Local(String utcTime, String utcTimePatten, String localTimePatten) {
        SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
        //时区定义并进行时间获取
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return utcTime;
        }
        SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
        localFormater.setTimeZone(TimeZone.getDefault());
        String localTime = localFormater.format(gpsUTCDate.getTime());
        return localTime;
    }

}
