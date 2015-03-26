package com.thefuzzybrain.ishan0445.ribbit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<ParseObject> {
    protected Context mContext;
    protected List<ParseObject> mMessages;

    public MessageAdapter(Context context ,List<ParseObject> messages){

        super(context,R.layout.message_item,messages);

        mContext = context;
        mMessages = messages;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item,null);
            viewHolder = new ViewHolder();
            viewHolder.iconImageView = (ImageView) convertView.findViewById(R.id.ivType);
            viewHolder.nameLabel = (TextView) convertView.findViewById(R.id.tvSendersLabel);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ParseObject message = mMessages.get(position);
        if(message.getString(ParseConstants.KEY_FLE_TYPE).equals(ParseConstants.TYPE_IMAGE)){
            viewHolder.iconImageView.setImageResource(R.drawable.ic_action_editor_insert_photo);
        }else{
            viewHolder.iconImageView.setImageResource(R.drawable.ic_action_av_videocam);
        }
        viewHolder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
        return convertView;
    }

    public static class ViewHolder{
        ImageView iconImageView;
        TextView nameLabel;
    }
}
