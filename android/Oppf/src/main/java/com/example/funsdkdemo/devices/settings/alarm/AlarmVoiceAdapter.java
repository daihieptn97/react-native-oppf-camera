package com.example.funsdkdemo.devices.settings.alarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.funsdkdemo.R;
import com.lib.sdk.bean.AbilityVoiceTip;

import java.util.List;

public class AlarmVoiceAdapter extends BaseAdapter {
    private Context mContext;
    private List<AbilityVoiceTip.VoiceTip> mVoiceTips;

    public AlarmVoiceAdapter(Context context, List<AbilityVoiceTip.VoiceTip> data) {
        mContext = context;
        mVoiceTips = data;
    }

    @Override
    public int getCount() {
        return mVoiceTips.size();
    }

    @Override
    public Object getItem(int position) {
        return mVoiceTips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_alarm_voice, parent, false);
            holder.relativeLayout = convertView.findViewById(R.id.layoutRoot);
            holder.ivSelect = convertView.findViewById(R.id.iv_select);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AbilityVoiceTip.VoiceTip voiceTip = mVoiceTips.get(position);
        holder.tvName.setText(voiceTip.VoiceText);
        if (voiceTip.selected) {
            holder.ivSelect.setImageResource(R.drawable.correct_sel);
        } else {
            holder.ivSelect.setImageResource(R.drawable.correct_nor);
        }
        return convertView;
    }

    class ViewHolder {
        RelativeLayout relativeLayout;
        ImageView ivSelect;
        TextView tvName;
    }
}
