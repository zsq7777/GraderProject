package com.lh.grader.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lh.grader.R;
import com.lh.grader.model.ResultData;

import java.util.List;

/**
 * @author zhao
 */
public class DataAdapter extends BaseQuickAdapter<ResultData, BaseViewHolder> {


    public DataAdapter() {
        super(R.layout.item_view);
    }

    @Override
    protected void convert(BaseViewHolder helper, ResultData item) {
        helper.setText(R.id.tvTime,"时间："+item.getTime());
        helper.setText(R.id.tvDevice,"设备："+item.getSn());
        helper.setText(R.id.tvLat,"经度-"+item.getGps().getLongUnit()+"："+item.getGps().getLong());
        helper.setText(R.id.tvLong,"纬度-"+item.getGps().getLatUnit()+"："+item.getGps().getLat());
        helper.setText(R.id.tvAltitude,"高程："+item.getGps().getAltitude()+""+item.getGps().getUnitStr1());
    }
}
