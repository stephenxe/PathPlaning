
/*
* Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hitszcerc.activity;



import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hitszcerc.astar.AStar;
import com.hitszcerc.astar.MapList;
import com.hitszcerc.ui.PathSurfaceView;
import com.hitszcerc.walk.StepGo;

/**
 * This is the main Activity that displays the current chat session.
 * @param <MainActivity>
 */
public class BluetoothMainActivity<MainActivity> extends Activity implements OnTouchListener{
    // Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    
    PathSurfaceView mySurfaceView;// 声明引用
	ImageButton start_button;// 开始按钮
	Button set_button;// 设置按钮
	AStar aStar;
	
	int setcode = 0;
	int width;

	private int [][] map;
	
	private int startX = -1;
	private int startY = -1;
	private int endX = -1;
	private int endY = -1;
	
    
    // 来自蓝牙消息类型
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // 蓝牙会话关键名
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent 请求码 用于跳转
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;
    
    // 布局视图
    // Layout Views
    private TextView mTitle;
   /* private EditText mOutEditText;
    
    private Button mStop;
    private Button mGoForward;
    private Button mGoBack;
    private Button mTurnLeft;
    private Button mTurnRight;*/

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
  //  private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
       // setContentView(R.layout.activity_main);
        
        
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        // Set up the window layout
        //允许修改标题
      requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
      setContentView(R.layout.activity_bluetooth_main);
      //设置标题的布局文件
      getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        // Set up the custom title
      mTitle = (TextView) findViewById(R.id.title_left_text);
      //设置左标题
      mTitle.setText(R.string.app_name);
      //右标题
      mTitle = (TextView) findViewById(R.id.title_right_text);
        // Get local Bluetooth adapter
      
      
      mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            //结束Activity
            finish();
            return;
        }
        System.out.println("ahahahh");
        initData();
        initView();
        
        initEvent();
    }
    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
        	//请求打开蓝牙
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //This system activity will return once Bluetooth has completed turning on, 
            //or the user has decided not to turn Bluetooth on
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }
    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }
    //控制按键，对于
    private void setupChat() {
        Log.d(TAG, "setupChat()");
       // sendMessage("w");
       /* mStop=(Button) findViewById(R.id.btn_stop);
        mGoForward=(Button) findViewById(R.id.go_forward);
        mGoBack=(Button) findViewById(R.id.go_back);
        mTurnLeft=(Button) findViewById(R.id.turn_left);
        mTurnRight=(Button) findViewById(R.id.turn_right);

        mGoBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendMessage("w");
			}
		});
        mGoForward.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendMessage("d");
			}
		});
        mTurnLeft.setOnClickListener(new OnClickListener() {
	
        	@Override
        	public void onClick(View v) {
        		sendMessage("a");
        	}
        });
        mTurnRight.setOnClickListener(new OnClickListener() {
	
        	@Override
        	public void onClick(View v) {
        		sendMessage("s");
        	}
        });
        mStop.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		sendMessage("c");
        	}
        });*/
        
       /* // Initialize the array adapter for the conversation thread
        //输入的信息存储在这个in ListView里面 和ArrayAdapter配套使用
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
               TextView view = (TextView) findViewById(R.id.edit_text_out);
               String message = view.getText().toString();
               sendMessage(message);
            }
        });*/
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
           Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
            // Reset out string buffer to zero and clear the edit text field
          //  mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }
    // The action listener for the EditText widget, to listen for the return key
    /*private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            if(D) Log.i(TAG, "END onEditorAction");
            return true;
        }
    };*/
    // The Handler that gets information back from the BluetoothChatService
    //Handler对象是在主线程中初始化的，因为它需要绑定在主线程的消息队列中。
    //不能在子线程改变控件，只能在handler
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                //已连接
                case BluetoothChatService.STATE_CONNECTED:
                    mTitle.setText(R.string.title_connected_to);
                    mTitle.append(mConnectedDeviceName);
                    System.out.println(mTitle.toString());
                  //  mConversationArrayAdapter.clear();
                    break;
                 //正在连接
                case BluetoothChatService.STATE_CONNECTING:
                   mTitle.setText(R.string.title_connecting);
                    break;
                //监听
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                   mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            //写
            case MESSAGE_WRITE:
//                byte[] writeBuf = (byte[]) msg.obj;
//                // construct a string from the buffer
//                String writeMessage = new String(writeBuf);
             //   mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            //读
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
//                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
             //   mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
            //	System.out.println("readMessage"+readMessage);
                break;
            //设备名字
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BLuetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bluetooth_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
       switch (item.getItemId()) {
       case R.id.secure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;
       case R.id.discoverable:
            // Ensure this device is discoverable by others
            ensureDiscoverable();
            return true;
        }
        return false;
    }
  //根据地图绘制方框
    private void initView() {
    	start_button = (ImageButton) findViewById(R.id.btn_start);// 通过ID获得Button
		set_button =  (Button) findViewById(R.id.btn_setpoint);
    	mySurfaceView = new PathSurfaceView(this);
    	
		LinearLayout ly = (LinearLayout) findViewById(R.id.LinearLayout02);
		ly.addView(mySurfaceView);
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ly
				.getLayoutParams();
		linearParams.height = 1080;
		ly.setLayoutParams(linearParams);
		
		ly.setOnTouchListener(this);
		
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;// 宽度
    }
    
    //初始化地图数据
    private void initData() {
    	reset();
    	aStar=new AStar(map, map.length, map[0].length);
    	startY=MapList.source[0];
    	startX=MapList.source[1];
    	endY=MapList.target[1];
    	endX=MapList.target[0];
    	
    }
    
    //点击搜索按钮进行搜索
    private void initEvent() {
    	set_button.setOnClickListener(new OnClickListener() {

			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				if (setcode == 0) {
					setcode = 1;
					set_button.setBackgroundResource(R.drawable.target_sel);
					System.out.println("startX:"+startX+"startY:"+startY+"endX:"+endX+"endY:"+endY);
				} else if (setcode == 1) {
					setcode = 0;
					set_button.setBackgroundResource(R.drawable.start_sel);
				}
			}
		});
		start_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initData();
				if(mChatService!=null){
					search();
				}
				//map=null;
				//start_button.setClickable(false);// 设置为不可点击
			}

		});
    }
    
    //搜索过程
    private void search() {
    	
    	if (startX == -1 || endX == -1 || startY == -1 || endY == -1) {
    		Toast.makeText(this, "未找到起点或终点", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	int flag = aStar.search(startX, startY, endY, endX);
		if (flag == -1) {
			System.out.println("传输数据有误！");
		} else if (flag == 0) {
			System.out.println("没找到！");
			Toast.makeText(getApplicationContext(), "不能找到路径", 1).show();
		} else {
			//aStar.search(startX, startY, endX, endY);
			
			List<Integer> comand = aStar.generateComand();
			StepGo runCar = new StepGo(mChatService, comand);
			//runCar.runAllStep();
			runCar.runrun();
			mySurfaceView.repaint(mySurfaceView.getHolder());// 重绘
		}
    }
    
    private void reset() {
    	map = null;
    	map = MapList.map;
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if(event.getAction()==MotionEvent.ACTION_UP){
			final int[] getpoint = getPoint(event.getX(), event.getY());
			final int hang = getpoint[0];
			final int lie =getpoint[1];
			if (isBlock(hang, lie)) {
				Toast.makeText(this, "障碍区不能设置为结点", Toast.LENGTH_SHORT).show();
			} else {
				//Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
				
				if (setcode == 0) {
					AlertDialog.Builder builder = new Builder(this);
					builder.setTitle("提示");
					String text="设置地图X坐标："+hang+"Y坐标："+lie+"为起点";
					builder.setMessage(text);
					builder.setNegativeButton("设为起点", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
							MapList.source = getpoint;
							startX=hang;
							startY=lie;
							AStar.resultList=null;
							mySurfaceView.repaint(mySurfaceView.getHolder());// 重绘
							dialog.dismiss();
						}
					});
					builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
							
							dialog.dismiss();
						}
					});
					builder.create().show();
					
				} else if (setcode == 1) {
					AlertDialog.Builder builder = new Builder(this);
					builder.setTitle("提示");
					String text="设置地图X坐标："+hang+"Y坐标："+lie+"为终点";
					builder.setMessage(text);
					builder.setNegativeButton("设为终点", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
							MapList.target = getpoint;
							endX = hang;
							endX = lie;
							AStar.resultList=null;
							mySurfaceView.repaint(mySurfaceView.getHolder());// 重绘
							dialog.dismiss();
						}
					});
					builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
							
							dialog.dismiss();
						}
					});
					builder.create().show();
					
				}	
			}
			
		 
		
		}
		return true;
		
	}

	private boolean isBlock(int hang, int lie) {
		if (MapList.map[lie][hang] == 1) {
			return true;
		}
		return false;

	}

	private int[] getPoint(float x, float y) {

		int hang = (int) x * (map.length) / width;
		int lie = (int) y * (map[0].length) / width;
		System.out.println(map.length);
		System.out.println(map[0].length);
		System.out.println("x:" + x + "y:" + y);
		System.out.println("hang:" + hang + "lie" + lie);
		int[] piont = { hang, lie };
		return piont;

	}
}
