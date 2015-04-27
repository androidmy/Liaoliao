package com.qianfeng.libraries.liaoliao.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.jivesoftware.smack.RosterEntry;

import java.util.ArrayList;
import java.util.List;

//--------------------------------------------------------------------------
public class MainActivity extends ActionBarActivity implements ServiceConnection, AdapterView.OnItemClickListener {

    private TextView txtUserJID;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> data;

    // 服务调用接口
    private ChatService.ChatController chatController;
    private List<RosterEntry> rosterEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtUserJID = (TextView) findViewById(R.id.main_user_jid);

        Intent intent = getIntent();
        // 登录成功之后，传递过来的。
        String userJID = intent.getStringExtra("userJID");

        txtUserJID.setText(userJID);


        //  联系人列表部分

        ListView listView = (ListView) findViewById(R.id.main_roster_list);

        data = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                data
                );

        listView.setAdapter(adapter);
       listView.setOnItemClickListener(this);

        // 绑定聊天服务

        Intent service = new Intent(this, ChatService.class);

        bindService(service, this, BIND_AUTO_CREATE);

    }
//-------------------------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();

        updateRosterList();
    }
//-------------------------------------------------------------------------------
    private void updateRosterList() {
        if(chatController != null) {
            // 每次显示的时候，及时获取联系人列表，进行刷新操作
            // 获取联系人信息

            rosterEntries = chatController.getRosterEntries();
            data.clear();
            for (RosterEntry entry : rosterEntries) {

                String user = entry.getUser();
                data.add(user);
            }

            adapter.notifyDataSetChanged();
        }
    }
//-------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        // 解除绑定
        unbindService(this);
        super.onDestroy();
    }
//----------------------------------------------------------------------------------------------
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        chatController = (ChatService.ChatController) service;

        updateRosterList();

    }
//-----------------------------------------------------------------------------------------
    @Override
    public void onServiceDisconnected(ComponentName name) {
        chatController = null;
    }
    /*点击联系人 启动会话

           *
           * */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RosterEntry entry = rosterEntries.get(position);
        String userJID = entry.getUser();
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userJID",userJID);
        startActivity(intent);

    }
    //-----------------------------------------------------------------------
}
