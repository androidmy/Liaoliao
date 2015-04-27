package com.qianfeng.libraries.liaoliao.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ChatService extends Service {

    // 1. 创建XMPP连接   HttpURLConnection
    //                  XMPPTCPConnection

    private static XMPPTCPConnection conn;

    public class ChatController extends Binder {

  public   void  removePackListener(PacketListener  listener)
  {
   if(listener!=null)
   {
       if(conn!=null)
       {
           conn.removePacketListener(listener);
       }

   }

  }
        public    void  addPackListener(PacketListener listener)
        {

           if(listener!=null)
           {
               if(conn!=null)
               {
                   conn.addPacketListener(listener, new MessageTypeFilter(Message.Type.chat));
               }

           }
        }


 /*开启聊天会话   在chatActivity中使用  用于发送和接收消息
 target  需要和谁聊天
 listener MessageListener 用来监听消息
 return  Chat对象   可以通过chat来发送消息   （调用chat）


 * */

        public  Chat    openChat(String target, String thread,final MessageListener listener){
           Chat  ret=null;
            if(target!=null)
            {
                if(conn!=null)
                {

                    if(conn.isAuthenticated()){
                        //已经登录的情况
                        ChatManager chatManager = ChatManager.getInstanceFor(conn);
             ret=chatManager.createChat(target,thread,listener);//创建聊天会话


                    }
                }

            }

            return ret;

        }
        /**
         * 给外部的 LoginActivity提供直接调用登录的功能
         *
         * @param userName
         * @param password
         * @return
         */
        public String login(String userName, String password) {

            String ret = null;

            if (userName != null && password != null) {

                if (conn != null) {
                    try {
                        if(!conn.isAuthenticated()) {
                            // 登录
                            conn.login(userName, password);
                        }

                        ret = conn.getUser();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            return ret;
        }

        /**
         * 获取当前登录账号中的 联系人信息
         *
         * @return
         */
        public List<RosterEntry> getRosterEntries() {
            List<RosterEntry> ret = null;

            if (conn != null) {
                // 如果当前已经登录过，那么获取
                if(conn.isAuthenticated()){

                    Roster roster = conn.getRoster();

                    if (roster != null) {
                        // 获取联系人列表
                        Collection<RosterEntry> entries = roster.getEntries();

                        ret = new LinkedList<RosterEntry>();
                        // 联系人获取出来
                        ret.addAll(entries);

                    }

                }

            }

            return ret;
        }


    }

    public ChatService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return new ChatController();

    }

    private ChatThread thread;

    @Override
    public void onCreate() {
        super.onCreate();

        if (conn != null){
            try {
                conn.disconnect();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
            conn = null;
        }





        // TODO 连接服务器
        conn = new XMPPTCPConnection("10.0.154.100");

    }

    /**
     * 服务的启动
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // TODO 启动线程

        if (thread == null) {
            thread = new ChatThread();
            thread.start();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        if (thread != null) {
            thread.stopThread();
            thread = null;
        }

        super.onDestroy();
    }

    /**
     * 实际的聊天的线程部分
     */
    class ChatThread extends Thread {


        // 标志线程
        private boolean running;

        public void stopThread() {
            running = false;
        }

        @Override
        public void run() {

            running = true;

            // 进行实际的连接服务器操作
            try {
                // Smark API 当中，大部分方法发生错误的时候
                // 直接抛异常
                conn.connect();

//                // 账号注册
//                // 获取账号管理器，进行注册的操作
//                AccountManager accountManager
//                        = AccountManager.getInstance(conn);
//
//                try {
//                    // 注册账号
//                // TODO 获取联系人列表
//                // getRoster() 会自动从服务器获取当前登录用户的联系人列表
//                // 并且返回的 Roster 类对象
//                // 所有的添加修改的操作，都会直接影响到账号实际的联系人内容
//                Roster roster = conn.getRoster();
//
//                // 获取联系人的个数
//                int entryCount = roster.getEntryCount();
//                // 获取所有的联系人信息
//                Collection<RosterEntry> entries = roster.getEntries();
//
//                // TODO 遍历每一个联系人信息
//                for (RosterEntry entry : entries) {
//                    // 昵称
//                    String name = entry.getName();
//                    // 收发信息时用到的内容。
//                    String user1 = entry.getUser();
//
//                    // 获取状态
//                    RosterPacket.ItemStatus status = entry.getStatus();
//
////                    Log.d("ChatThread", "打印联系人信息");
//                    Log.d("ChatThread", "Roster: " + user1);
//                }
//
//                // TODO 创建联系人
//
//                // 第一个参数 时 JID 形式的，也就是  用户名@域名 方式
//                // 第二个参数，添加联系人时的 备注名称
//                // 第三个参数，属于哪些组
//                roster.createEntry("zz@10.0.154.195","Zhu Xinyu", null);

                // TODO 接收消息
                // 向连接中，添加数据包的监听器，当服务器给客户端转发消息的时候
                // XMPPTCPConnection 会自动调用 PacketListener 的回调
                // 两个参数：第一个：数据包监听器，用于处理数据
                //          第二个参数：监听器要监听哪些类型的数据
                //         因为 conn 内部所有的操作都是数据包，例如 获取联系人，其实也在发送数据包

                PacketListener packetListener = new PacketListener() {
                    @Override
                    public void processPacket(Packet packet) throws SmackException.NotConnectedException {
                        // TODO 处理消息类型的数据包
                        // 因为 Message 类型继承了 Packet 所以检查是否时 Message
                        if (packet instanceof Message) {
                            Message msg = (Message) packet;

                            // 消息内容
                            String body = msg.getBody();
                            // 会话的主题
                            String subject = msg.getSubject();
                            // 从谁发过来的
                            String from = msg.getFrom();
                            // 发给谁的
                            String to = msg.getTo();
                            //聊天会话   通过这个主题  就可以确定 另一个发送者的创建的Chat对象
                            //这个Thread类似于对讲机之间的联系
                            String thread = msg.getThread();

                            Log.d("ChatThread", "Packet from : " + from + " to: " + to + " content: " + body);
                            //当收到信息   就模拟qq的通知栏的信息
                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(ChatService.this);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(ChatService.this);
                            builder.setContentTitle("你有新消息");
                            builder.setContentText(body);
                            Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);


                             chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            chatIntent.putExtra("userJID",from);
                            //主题标示     两个账户之间的联系
                            chatIntent.putExtra("thread",thread);
                            //内容
                            chatIntent.putExtra("body",body);
                            PendingIntent pendingIntent = PendingIntent.getActivity(ChatService.this, 998, chatIntent, PendingIntent.FLAG_ONE_SHOT);


                            builder.setContentIntent(pendingIntent);
                            builder.setSmallIcon(R.mipmap.ic_launcher);
                            Notification notification = builder.build();
                            managerCompat.notify((int)System.currentTimeMillis(),notification);


                        }
                    }
                };
                // !!! 在开始会话之前，进行PackageListener 的设置
                conn.addPacketListener(packetListener, new MessageTypeFilter(Message.Type.chat));

//                // 创建会话管理器
//                ChatManager chatManager = ChatManager.getInstanceFor(conn);
//
//                // 创建会话，需要给其他人发消息
//                if(entries != null && !entries.isEmpty()) {
//
//                    Iterator<RosterEntry> iterator = entries.iterator();
//
//                    // 找到第一个联系人
////                    RosterEntry rosterEntry = iterator.next();
////                    String jid = rosterEntry.getUser();
//
//                    String jid = "zyc@10.0.154.195";
//
//                    Log.d("ChatThread", "send to " + jid);
//
//                    // 创建聊天会话，有一个Chat 的对象，进行会话的管理
//                    // 当使用Chat进行发送消息的时候，会自动的，通过底层的 XMPPTCPConnection 发送消息数据包
//                    Chat chat = chatManager.createChat(jid, new MessageListener() {
//                        @Override
//                        public void processMessage(Chat chat, Message msg) {
//                            // TODO 处理会话过程中的消息数据
//                            // 消息内容
//                            String body = msg.getBody();
//                            // 会话的主题
//                            String subject = msg.getSubject();
//                            // 从谁发过来的
//                            String from = msg.getFrom();
//                            // 发给谁的
//                            String to = msg.getTo();
//
//                            Log.d("ChatThread", "Message from : " + from + " to: " + to + " content: " + body);
//
//                        }
//                    });
//
//                    // 发送消息
//                    chat.sendMessage("Hello world by vhly from XMPP client.");
//                }


                // 进行循环，等待消息，以及进行发送处理
                while (running) {
                    Thread.sleep(300);
                }

            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    // 关闭连接
                    try {
                        conn.disconnect();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    conn = null;
                }
            }
        }
    }
}
