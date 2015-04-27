package com.qianfeng.libraries.liaoliao.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by aaa on 15-4-24.
 */
public class ChatMessageAdapter   extends BaseAdapter {
    private List<ChatMessage>  messages;
    private Context context;
    private LayoutInflater  inflater;

    public ChatMessageAdapter(Context context,List<ChatMessage> messages){
        this.context=context;
        this.messages=messages;
        inflater=LayoutInflater.from(context);
    }

    @Override
    /*获取所有的数据
    *
    * */
    public int getCount() {

          int   ret=0;
        if(messages!=null)
        {
             ret= messages.size();

        }
        return ret;
    }


    /*
    * getItem获取指定的索引的实际数据对象
    * */
    @Override
    public Object getItem(int position) {
        Object  ret=null;
        if(messages!=null)
        {

            ret=messages.get(position);
        }

        return ret;
    }



    /*
    * 获取数据ID   对于cursorAdapter  这个方法    返回的是数据库记录的id
    * 另外一种应用就是listView :设置为可以多选的情况
    * */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
    * 告诉ListView内部的布局一共有多少种
    *
    *
    * */

    @Override
    public int getViewTypeCount() {
        /*2  是指发送的接收的 两种布局
        左侧接收   右侧发送
        * */
        return 2;
    }

    /*
    * 每次listView显示的Item的时候向   Adapter指定位置的item的是什么类型
    * int   注意   返回的数值必须是从0到 getViewTypeCon
    * position  根据位置  获取数据类型
    *
    * */
    @Override
    public int getItemViewType(int position) {

        ChatMessage chatMessage = messages.get(position);
        int ret=0;
        int sourceType = chatMessage.getSourceType();

        if(sourceType==ChatMessage.SOURCE_TYPE_SEND)
        {
            ret=1;
        }
        else  if(sourceType==chatMessage.SOURCE_TYPE_RECEIVED) {
            ret = 0;
        }
        return ret;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View ret= null;
       //
        ChatMessage chatMessage = messages.get(position);
        /*更具不同的来源类型  进行不同布局的加载和显示
        *
        * */
        int sourceType = chatMessage.getSourceType();//获取来源类型
        String  body=chatMessage.getBody();
        if(sourceType==ChatMessage.SOURCE_TYPE_RECEIVED)
        {

            //收到信息  显示在左侧
            if(convertView!=null)
            {
                ret=convertView;

            }
            else  {
                ret=inflater.inflate(R.layout.left_layout,parent,false);
            }
            //显示消息内容
            //左侧的TextView   id
            TextView txtMessage = (TextView) ret.findViewById(R.id.chat_message_left);
                        txtMessage.setText(body);





        }
        else if(sourceType==chatMessage.SOURCE_TYPE_SEND)
        {
            //    发送的显示在右侧

            if(convertView!=null)
            {ret=convertView;

            }
            else {
                ret=inflater.inflate(R.layout.right_layout,parent,false);

            }
            TextView  txtMessage=(TextView)ret.findViewById(R.id.chat_message_right);
            txtMessage.setText(body);

        }


        return ret;



    }
}
