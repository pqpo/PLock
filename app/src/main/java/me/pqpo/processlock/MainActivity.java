package me.pqpo.processlock;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import me.pqpo.plocklib.PLock;


/**
 *
 * PLock is a simple and efficient cross-process lock, also support read-write lock.
 *
 * Created by pqpo on 2018/4/28.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int MSG_GET_LOCK = 1;
    public static final int MSG_TRY_LOCK = 2;
    public static final int MSG_TRY_READ_LOCK = 3;
    public static final int MSG_TRY_WRITE_LOCK = 4;
    public static final int MSG_UNLOCK = 5;
    public static final int MSG_CLEAR = 6;

    public static final int MSG_UPDATE_RESULT = 10;
    public static final int MSG_BLOCK = 11;

    private TextView tvResult;
    private ScrollView scrollView;

    private Handler threadHandler;

    private Handler mHandler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvResult = findViewById(R.id.tv_result);
        scrollView = findViewById(R.id.scrollView);

        final PLock pLock = PLock.getDefault();

        HandlerThread handlerThread = new HandlerThread("PLock");
        handlerThread.start();
        threadHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                final int what = msg.what;
                String msgStr = "";
                switch (what) {
                    case MSG_GET_LOCK:
                        msgStr = "Lock result : " + pLock.lock();
                        break;
                    case MSG_TRY_LOCK:
                        msgStr = "Try lock result : " + pLock.tryLock();
                        break;
                    case MSG_TRY_READ_LOCK:
                        msgStr = "Try read lock result : " + pLock.tryReadLock();
                        break;
                    case MSG_TRY_WRITE_LOCK:
                        msgStr = "Try write lock result : " + pLock.tryWriteLock();
                        break;
                    case MSG_UNLOCK:
                        pLock.unlock();
                        msgStr = "Unlock";
                        break;
                    case MSG_CLEAR:
                        msgStr = "";
                        break;
                }
                mHandler.obtainMessage(MSG_UPDATE_RESULT, msgStr).sendToTarget();
                // remove block-check message
                mHandler.removeMessages(MSG_BLOCK);
            }
        };

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int what = msg.what;
                if (what == MSG_UPDATE_RESULT || what == MSG_BLOCK) {
                    String msgStr = (String) msg.obj;
                    if (!TextUtils.isEmpty(msgStr)) {
                        tvResult.append(msg.obj + "\n");
                    } else {
                        tvResult.setText("");
                    }
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }
        };

        findViewById(R.id.btn_get_lock).setOnClickListener(this);
        findViewById(R.id.btn_try_lock).setOnClickListener(this);
        findViewById(R.id.btn_try_read_lock).setOnClickListener(this);
        findViewById(R.id.btn_try_write_lock).setOnClickListener(this);
        findViewById(R.id.btn_unlock).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        int msgId = -1;
        switch (id) {
            case R.id.btn_get_lock:
                msgId = MSG_GET_LOCK;
                break;
            case R.id.btn_try_lock:
                msgId = MSG_TRY_LOCK;
                break;
            case R.id.btn_try_read_lock:
                msgId = MSG_TRY_READ_LOCK;
                break;
            case R.id.btn_try_write_lock:
                msgId = MSG_TRY_WRITE_LOCK;
                break;
            case R.id.btn_unlock:
                msgId = MSG_UNLOCK;
                break;
            case R.id.btn_clear:
                msgId = MSG_CLEAR;
                break;
        }

        //After the background thread is processed, the following message will be removed,
        //and if the message has not been removed after 500 milliseconds, the background thread is blocked.
        Message message = mHandler.obtainMessage(MSG_BLOCK, "--------- blocked --------");
        mHandler.sendMessageDelayed(message, 500);

        threadHandler.obtainMessage(msgId).sendToTarget();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PLock.releaseDefault();
    }
}
