
package com.qianfeng.libraries.liaoliao.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends ActionBarActivity implements ServiceConnection, MessageListener, PacketListener {
    private String userJID;


    /**
     * 从服务获取的Binder，用于进行消息的发送
     */
    private ChatService.ChatController controller;
    private Chat chat;
    private EditText editText;
    private String thread;
    private String body;
    private ChatMessageAdapter adapter;
    private ArrayList<ChatMessage> chatMessages;

    /**
     * 聊天界面，从其他Activity传递的参数：userJID，代表需要聊天的对象
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //接收目标联系人

        Intent intent = getIntent();
        userJID = intent.getStringExtra("userJID");
        setTitle(userJID);
        thread = intent.getStringExtra("thread");
        body = intent.getStringExtra("body");

        Intent service = new Intent(this, ChatService.class);

        //参数1：Intent代表服务
        //参数2：服务绑定的回调借口
        //参数3：

        bindService(service, this, BIND_AUTO_CREATE);

        editText = (EditText) findViewById(R.id.chat_message);
        //listView    左侧收到的信息   右侧的发出的信息
        ListView listView = (ListView) findViewById(R.id.main_roster_list);
        chatMessages = new ArrayList<ChatMessage>();
        adapter = new ChatMessageAdapter(this, chatMessages);

             listView.setAdapter(adapter);


        //绑定服务，用于发送消息



    }

    @Override
    protected void onDestroy() {
        unbindService(this);
        super.onDestroy();
    }

    ////////////////////////////////////////////////////////////////////////
//点击事件代码部分  发送按钮点击事件
    public void btnSendOnClick(View v) {

        String content = editText.getText().toString();
        if (chat != null) {

            try {
                chat.sendMessage(content);

                //创建消息实体

                ChatMessage msg = new ChatMessage();
                msg.setBody(content);
                msg.setSourceType(ChatMessage.SOURCE_TYPE_SEND);
                chatMessages.add(msg);
                adapter.notifyDataSetChanged();


            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }

        }

    }
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        controller = ((ChatService.ChatController) service);
        // 绑定成功后进行聊天回哈的创建 Chat对象
        chat = controller.openChat(userJID, null, this);
        controller.addPackListener(this);


    }

    @Override


    public void onServiceDisconnected(ComponentName name) {
   controller.removePackListener(this);
        if(chat!=null)
        {
            chat.close();
        }
        controller = null;
    }



    ////////////////////////////////////////////////////////////////////////
    @Override
    public void processMessage(Chat chat, Message message) {
        //处理消息的发送和接收
        String from = message.getFrom();
        String to = message.getTo();
        String body = message.getBody();
        //显示信息 用于判断   发送出去的消息     方法是否回调    接收的消息内不能取到
        //接收的消息能否取到
        Log.d("ChatActivity", "message from" + from + "to" + to + " " + body);

    }

    @Override
    public void processPacket(Packet packet) throws SmackException.NotConnectedException {
        if(packet  instanceof  Message )
        {
            Message   msg=(Message)packet;
            String from=msg.getFrom();
            if(from.startsWith(userJID))
            {

    ChatMessage   chatMessage=new ChatMessage();
                chatMessage.setBody(msg.getBody());
                chatMessage.setFrom(from);
                chatMessage.setTo(msg.getTo());
                chatMessage.setSourceType(ChatMessage.SOURCE_TYPE_RECEIVED);
                chatMessage.setTime(System.currentTimeMillis());
                chatMessages.add(chatMessage);

                //因为processPacket 执行在子线程中
                //ListView更新应该主线程更新
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });


            }
        }

    }
}


