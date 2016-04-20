package com.hitszcerc.walk;

import java.util.Iterator;
import java.util.List;

import com.hitszcerc.activity.BluetoothChatService;

/**
 * 控制小车行走的类
 * 根据标识码给蓝牙发送相应指令
 * @author Administrator
 *
 */

public class StepGo {
	private final int GO_UP=0;
	private final int GO_DOWN=1;
	private final int GO_RIGHT=2;
	private final int GO_LEFT=3;
	private final int GO_UP_RIGHT=4;
	private final int GO_DOWN_RIGHT=5;
	private final int GO_UP_LEFT=6;
	private final int GO_DOWN_LEFT=7;
	private final int DDELAY_TIME=3000;
	
	private BluetoothChatService mChatService;
	private List<Integer> mCoder;
	public StepGo(BluetoothChatService mChatService, List<Integer> mCoder) {
		super();
		this.mChatService = mChatService;
		this.mCoder = mCoder;
	}
	public void runAllStep(){
		new Thread(){
	         public void run() {
		for (Iterator<Integer> iter=mCoder.iterator();iter.hasNext();){
			
			int a = iter.next();
			oneStep(a);
		}
	         }
		}.start();
	      
			
	}
	
	public void runrun(){
		
		String result=null;
		for (Iterator<Integer> iter=mCoder.iterator();iter.hasNext();){
			int a = iter.next();
			result+=convertChar(a);
		}
		sendMessage("#"+result);
	}
	public void oneStep(final int comamd){
		
	        	 long startTime = System.currentTimeMillis();
	        	 if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
	                 //Toast.makeText(get, R.string.not_connected, Toast.LENGTH_SHORT).show();
	                  return;
	              }
	        	 
	        	 runComand(comamd);
	        	
	             
	        	 long endTime = System.currentTimeMillis();
					// 我们花了多少时间
					long dTime = endTime - startTime;
					// 2000
					if (dTime < DDELAY_TIME) {
						try {
							Thread.sleep(DDELAY_TIME - dTime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} 
	}
	private void sendMessage(String message) {

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
            System.out.println("message:"+message);
            // Reset out string buffer to zero and clear the edit text field
          //  mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }
	private char convertChar(int comand){
		char r = 0;
		switch (comand) {
		case GO_UP:
			r='w';
			break;
		case GO_DOWN:
			r='r';
			break;					
		case GO_RIGHT:
			r='d';
			break;
		case GO_LEFT:
			r='c';
			break;
		case GO_UP_RIGHT:
			r='a';
			break;
		case GO_DOWN_RIGHT:
			r='a';
			break;
		case GO_UP_LEFT:
			r='a';
			break;
		case GO_DOWN_LEFT:
			r='a';
			break;
		default:
			break;
		}
		return r;
	}
	
	private void runComand( final int comand) {
		switch (comand) {
		case GO_UP:
			sendMessage("a");
			break;
		case GO_DOWN:
			sendMessage("b");
			break;					
		case GO_RIGHT:
			sendMessage("c");
			break;
		case GO_LEFT:
			sendMessage("d");
			break;
		case GO_UP_RIGHT:
			sendMessage("e");
			break;
		case GO_DOWN_RIGHT:
			sendMessage("f");
			break;
		case GO_UP_LEFT:
			sendMessage("g");
			break;
		case GO_DOWN_LEFT:
			sendMessage("h");
			break;
		default:
			break;
		}		
	} 

}
