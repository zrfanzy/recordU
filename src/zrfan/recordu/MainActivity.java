package zrfan.recordu;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar; 
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;


public class MainActivity extends Activity {
	//DatagramSocket sendSocket, receiveSocket;
	//DatagramPacket sendPack, receivePack;
	//private InetAddress sendIP;
	//private int sendPort, receivePort;
	
	private Handler weatherHandler;
	private Handler clockHandler;
	private ClockTask clockTask = null;
	private Timer mTimer;
	int Ttime;
	int now;
	private static final String APP_ID = "wxa185852aa88140ad";
	private IWXAPI api;
	String Wgps = "";
	static String Wtemp = "13";
	String Wfigure = "qing";
	String Wstatus = "晴";
	int Wclock = 0;
	boolean link;
	static String Wname = "a";
	static String Wpassword = "a";
	static String WServer = "59.66.181.48:20139";
	static String describe = "";
	static String Wgps1 = "--";
	static String Wgps2 = "--";
	static String Wwifi = "--";
	static String Wtime = "";
	static File Wimg = null;
	boolean Wgood = true;  
	private CameraView cv;
    private Camera mCamera = null;
    private Bitmap mBitmap = null; 
    static Socket socket;
    static PrintStream os;
    HashMap<String, String> map;
    List<String> list =  new  ArrayList<String>(); 
    List<String> list1 =  new  ArrayList<String>(); 
    static byte[] img;
	
    public static void sendMessage() {
		String msg = "<info>";
		String msg1 = "";
		msg = msg + "<descr>" + describe + "</descr>" + "<GPS>" + "<lat>" + Wgps1 + "</lat>" + "<lng>" + Wgps2 + "</lng></GPS>";
		msg = msg + "<time>" + Wtime + "</time>" + "<temp>" + Wtemp + "</temp>" + "<wifi>" + Wwifi + "</wifi>";
		msg1 = msg + "<pic>";
		if (Wimg != null) {
			msg = msg + "<pic>" + Wimg.toString() + "</pic>";
		}
		msg = msg + "</info>\n";
		//msg1 = msg1 + "</info>\n";
		describe = "";
    	try {
    		Pattern p = Pattern.compile(":");
    		Matcher m = p.matcher(WServer);
    		m.find();
    		String ip = WServer.substring(0, m.start());
    		int port = Integer.parseInt(WServer.substring(m.end()));
    		socket = new Socket();    
    		InetSocketAddress isa = new InetSocketAddress(ip, port);
    		socket.connect(isa, 5000);
    		os=new PrintStream(new PrintStream(socket.getOutputStream()));
    	    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/recordu/send.txt");
			if (!file.exists()) file.createNewFile();
			FileWriter fw = new FileWriter(file.toString(), true);
			BufferedWriter bf = new BufferedWriter(fw);
			bf.append(msg);  
		    bf.flush();  
		    bf.close();  
		    fw.close();
    		//os.write((Wname + "\n" + Wpassword + "\n" + msg1).getBytes());
    		//os.write(new String(b).getBytes());
    		//os.write("</pic></info>\n".getBytes());
    		if (Wimg != null) {
    			os.print((Wname + "\n" + Wpassword + "\n" + msg1));
    			try {
    				//File file1 = new File(Wimg.toString());
    				File file1 = 
    						new File(Environment.getExternalStorageDirectory().getPath() + 
    								"/photo/a.jpg");
    				BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file1));
    				int r;
    				int count;
    				byte[] b = new byte[1024 * 8];
    				while ((count = fis.read(b))!=-1) {
    					for (int i = 0; i < count; ++i) {
    						os.write(b[i]);
    					}
    				}
    			}
    			catch (Exception e) {
    				//
    			}
    			os.println("</pic></info>");
    		}
    		else {
    			os.println(Wname);
    			os.println(Wpassword);
    			os.print(msg1);
    			os.println("</pic></info>");
    			//os.println((Wname + "\n" + Wpassword + "\n" + msg1 + "</pic></info>"));
    		}
    		os.flush();
    		os.close();
    	}
    	catch (Exception e) {
    		
    			try {File file = new File(Environment.getExternalStorageDirectory().getPath() + "/recordu/unsend.txt");
    			if (!file.exists()) file.createNewFile();
    			FileWriter fw = new FileWriter(file.toString(), true);
    			BufferedWriter bf = new BufferedWriter(fw);
    			bf.append(msg);  
    		    bf.flush();  
    		    bf.close();  
    		    fw.close();}
    			catch (Exception e2) {
    				
    			}
    		}
	}

    private void setIP() {
    	try {
    		Pattern p = Pattern.compile(":");
    		Matcher m = p.matcher(WServer);
    		m.find();
    		String ip = WServer.substring(0, m.start());
    		int port = Integer.parseInt(WServer.substring(m.end()));
    		socket = new Socket();   
    		InetSocketAddress isa = new InetSocketAddress(ip, port);
    		socket.connect(isa, 5000);
    		os=new PrintStream(new PrintStream(socket.getOutputStream()));
    		Toast.makeText(getApplicationContext(), "闹钟设置成功！成功连入服务器!", Toast.LENGTH_LONG).show();
    		os.println(Wname);
    		os.println(Wpassword);
    		os.flush();
    		os.close();
    		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/recordu/unsend.txt");
			if (file.exists()) {
				try {
					BufferedReader bw = new BufferedReader(new FileReader(file));
					String str;
					while ((str = bw.readLine()) != null) {
						// get gps
						Matcher m2 = Pattern.compile("<lat>").matcher(str);
						m2.find();
						Matcher m1 = Pattern.compile("lat>").matcher(str);
						m1.find();m1.find();
						Wgps1 = str.substring(m2.end(), m1.start() - 2);
						m2 = Pattern.compile("<lng>").matcher(str);
						m1 = Pattern.compile("lng>").matcher(str);
						m2.find();m1.find();m1.find();
						Wgps2 = str.substring(m2.end(), m1.start() - 2);
						// get temp
						m2 = Pattern.compile("<temp>").matcher(str);
						m1 = Pattern.compile("temp>").matcher(str);
						m2.find();m1.find();m1.find();
						String temp = str.substring(m2.end(), m1.start() - 2);
						// get wifi
						m2 = Pattern.compile("<wifi>").matcher(str);
						m1 = Pattern.compile("wifi>").matcher(str);
						m2.find();m1.find();m1.find();
						Wwifi = str.substring(m2.end(), m1.start() - 2);
						// get describe
						m2 = Pattern.compile("<descr>").matcher(str);
						m1 = Pattern.compile("descr>").matcher(str);
						m2.find();m1.find();m1.find();
						describe = str.substring(m2.end(), m1.start() - 2);
						//get time
						m2 = Pattern.compile("<time>").matcher(str);
						m1 = Pattern.compile("time>").matcher(str);
						m2.find();m1.find();m1.find();
						Wtime = str.substring(m2.end(), m1.start() - 2);
						// get photo
						m2 = Pattern.compile("<pic>").matcher(str);
						m1 = Pattern.compile("pic>").matcher(str);
						String pic;
						if (m2.find() && m1.find() && m1.find()) {
							pic = str.substring(m2.end(), m1.start() - 2);
							Wimg = new File(pic);
						}
						else Wimg = null;
						sendMessage();
					file.delete();
					}
				}
				catch (Exception e) {
				}
			}
			
    	}
    	catch (Exception e) {
    		Toast.makeText(getApplicationContext(), "闹钟已设置！网络连接错误!", Toast.LENGTH_LONG).show();
    		link = false;
    	}
    }
    
    public Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
    	
        public void onPictureTaken(byte[] data, Camera camera) {
                Toast.makeText(getApplicationContext(), "正在保存……", Toast.LENGTH_LONG).show();
                //用BitmapFactory.decodeByteArray()方法可以把相机传回的裸数据转换成Bitmap对象
                mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                img = data;
                //接下来的工作就是把Bitmap保存成一个存储卡中的文件
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        		Wtime = df.format(new Date());
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/recordu/" +
                		Wtime + ".jpg");
                Wimg = file;
                try {
                        file.createNewFile();
                        BufferedOutputStream ot = new BufferedOutputStream(new FileOutputStream(file));
                        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, ot);
                        ot.flush();
                        ot.close();
                        Toast.makeText(getApplicationContext(), "图片保存完毕", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                        // e.printStackTrace();
                }
        }

};

	public void setCamera() {
        now = 2;
		//窗口设置为全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置窗口为半透明
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        //提供一个帧布局
        FrameLayout  fl = new FrameLayout(this);      

        //创建一个照相预览用的SurfaceView子类，并放在帧布局的底层
        cv = new CameraView(this);
                fl.addView(cv);

                //创建一个文本框添加在帧布局中，我们可以看到，文字自动出现在了SurfaceView的前面，由此你可以在预览窗口做出各种特殊效果
                TextView tv = new TextView(this);
                tv.setText("请按\"相机\"按钮拍摄");
                fl.addView(tv);

                //设置Activity的根内容视图
                setContentView(fl);

        }
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_FOCUS) {
            if (mCamera != null) {
                 //当按下相机按钮时，执行相机对象的takePicture()方法,该方法有三个回调对象做入参，不需要的时候可以设null
                 mCamera.takePicture(null, null, pictureCallback);
            }
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if (now == 2) {
        		setContentView(R.layout.inform);
        		setRecord();
        		if (Wimg != null) {
        			try {
        				ImageView iv = (ImageView)findViewById(R.id.pic);
        				FileInputStream inputStream = new FileInputStream(Wimg);
        				Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, null);
        				iv.setImageBitmap(bitmap);
        			}
        			catch (Exception e) {
        				
        			}
        		}
        	}
        	else if (now == 1) {
        		setContentView(R.layout.activity_main);
        		setMainActivity();
        		Wimg = null;
        	}
        }
        return cv.onKeyDown(keyCode, event);
}	

	private void setselect() {
		now = 1;
		setBackButton();
		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/recordu/send.txt");
		try {
			TextView tv = (TextView)findViewById(R.id.TextViewa);
			FileInputStream fis = new FileInputStream(file);// FileInputStream
			// 从文件系统中的某个文件中获取字节
			InputStreamReader isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
			BufferedReader br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象
			String str;
			list.clear();
			list1.clear();
			while ((str = br.readLine()) != null) {
			    Matcher m = Pattern.compile("<time>").matcher(str);
			    if (m.find()){
			    String time = str.substring(m.end(), m.end() + 19);
			    list.add(time);
			    list1.add(str);
			    }
			}
			Spinner sp = (Spinner)findViewById(R.id.spinner1);
			final ArrayAdapter<String> adapter = new  ArrayAdapter<String>( this ,android.R.layout.simple_spinner_item, list);  
	        //第三步：为适配器设置下拉列表下拉时的菜单样式。   
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
	        //第四步：将适配器添加到下拉列表上   
	        sp.setAdapter(adapter);  
	        
	        sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

	        	@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					String key = adapter.getItem(arg2);
					String value = list1.get(arg2);
					String value1 = value;
					TextView tv = (TextView)findViewById(R.id.TextViewa);
					
					// get gps
					Matcher m = Pattern.compile("<lat>").matcher(value);
					m.find();
					Matcher m1 = Pattern.compile("lat>").matcher(value);
					m1.find();m1.find();
					String lat = value.substring(m.end(), m1.start() - 2);
					m = Pattern.compile("<lng>").matcher(value);
					m1 = Pattern.compile("lng>").matcher(value);
					m.find();m1.find();m1.find();
					String lng = value.substring(m.end(), m1.start() - 2);
					tv = (TextView)findViewById(R.id.TextViewa);
					tv.setText("GPS：" + lat + "," + lng);
					// get temp
					m = Pattern.compile("<temp>").matcher(value);
					m1 = Pattern.compile("temp>").matcher(value);
					m.find();m1.find();m1.find();
					String temp = value.substring(m.end(), m1.start() - 2);
					tv = (TextView)findViewById(R.id.TextViewb);
					tv.setText("温度：" + temp);
					// get wifi
					m = Pattern.compile("<wifi>").matcher(value);
					m1 = Pattern.compile("wifi>").matcher(value);
					m.find();m1.find();m1.find();
					String wifi = value.substring(m.end(), m1.start() - 2);
					tv = (TextView)findViewById(R.id.TextViewc);
					tv.setText("WIFI：" + wifi);
					tv = (TextView)findViewById(R.id.TextViewd);
					tv.setText("时间：" + key);
					// get describe
					m = Pattern.compile("<descr>").matcher(value);
					m1 = Pattern.compile("descr>").matcher(value);
					m.find();m1.find();m1.find();
					String descr = value.substring(m.end(), m1.start() - 2);
					tv = (TextView)findViewById(R.id.TextViewe);
					tv.setText("描述：" + descr);
					// get photo
					m = Pattern.compile("<pic>").matcher(value);
					m1 = Pattern.compile("pic>").matcher(value);
					if (m.find() && m1.find() && m1.find()) {
					String pic = value.substring(m.end(), m1.start() - 2);
					if (pic.length() > 1) {
						ImageView iv = (ImageView)findViewById(R.id.pic);
        				FileInputStream inputStream;
						try {
							inputStream = new FileInputStream(pic);
							Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, null);
	        				iv.setImageBitmap(bitmap);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							//
						}
        				
					}
					else {
						ImageView iv = (ImageView)findViewById(R.id.pic);
						iv.setImageResource(R.drawable.backback);
					}
					}
					else
					{
						ImageView iv = (ImageView)findViewById(R.id.pic);
						iv.setImageResource(R.drawable.backback);
					}
				    arg0.setVisibility(View.VISIBLE);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}

				
	        });  
		}
		catch (Exception e) {
			
		}
	}
	
	private void regToWX() {
		api = WXAPIFactory.createWXAPI(this, APP_ID, false);
		api.registerApp(APP_ID);
	}
	
	private void setWX() {
		setBackButton();
		EditText et = (EditText)findViewById(R.id.editText1);
		String wxsend = "当当当当。今天北京" + Wtemp + "度，" + Wstatus;
		if (Wgood) wxsend = wxsend + "哦。好天气要有好心情:)" + "      " + Wname;
		else wxsend = wxsend + "呢。出门记得带伞哟XD"  + "      " + Wname;
		et.setText(wxsend);
		Button b = (Button)findViewById(R.id.sendbButton);
		b.setOnClickListener(new OnClickListener(){     
            @Override    
            public void onClick(View v) {     
            	      
            		WXTextObject textObj = new WXTextObject();  
                    EditText et1 = (EditText)findViewById(R.id.editText1);
                    textObj.text = et1.getText().toString();  
                    WXMediaMessage msg = new WXMediaMessage();  
                    msg.mediaObject = textObj;    
                    msg.title = "From 爪哇大作业";
                    msg.description = textObj.text;  
                    SendMessageToWX.Req req = new SendMessageToWX.Req();  
                    req.transaction = String.valueOf(System.currentTimeMillis());
                    req.message = msg;
                    CheckBox cb = (CheckBox)findViewById(R.id.checkBox);
                    if (cb.isChecked())
                    	req.scene = SendMessageToWX.Req.WXSceneTimeline;
                    else req.scene = SendMessageToWX.Req.WXSceneSession;
                    api.sendReq(req);   

            }
		});
	}
	
	private void refresh() {
		try {
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);    
			Criteria criteria = new Criteria();    
			criteria.setAccuracy(Criteria.ACCURACY_FINE);  //设置为最大精度  
			criteria.setAltitudeRequired(false);  //不获取海拔信息  
			criteria.setBearingRequired(false);  //不获取方位信息  
			criteria.setCostAllowed(false);  //是否允许付费  
			criteria.setPowerRequirement(Criteria.POWER_LOW);  // 使用省电模式  
			// 获得当前的位置提供者    
			String provider = locationManager.getBestProvider(criteria, true);    
			// 获得当前的位置    
			Location location = locationManager.getLastKnownLocation(provider);
			Wgps = "" + String.format("%.4f", location.getLatitude()) + "," + String.format("%.4f", location.getLongitude());
			Wgps1 = String.format("%.4f", location.getLatitude());
			Wgps2 = String.format("%.4f", location.getLongitude());
		}
		catch (Exception e) {
	    	Wgps = "--";
	    	Wgps1 = "--";
	    	Wgps2 = "--";
	    }
		try {
			WifiManager wifiManager=(WifiManager) getSystemService(WIFI_SERVICE);
			List<ScanResult> srList = wifiManager.getScanResults();//搜索到的设备列表
			int max = -100000;
			String name = "--";
	        for (ScanResult sr : srList) {
	            if (sr.level > max) {
	            	max = sr.level;
	            	name = sr.SSID;
	            }
	        }
	        Wwifi = name;
		}
		catch (Exception e) {
			Wwifi = "--";
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Wtime = df.format(new Date());
	}
	
	private void setRecord() {
		now = 1;
		setBackButton();
		TextView tv = (TextView)findViewById(R.id.TextView01);
		refresh();
		tv.setText("GPS：" + Wgps);
		tv = (TextView)findViewById(R.id.TextView02);
		tv.setText("温度：" + Wtemp + "℃");
		tv = (TextView)findViewById(R.id.TextView03);
		tv.setText("WIFI：" + Wwifi);
		tv = (TextView)findViewById(R.id.TextView04);
		tv.setText("时间：" + Wtime);
		
		Button b = (Button)findViewById(R.id.photoB);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setCamera();
			}  
		});
		
		Button b1 = (Button)findViewById(R.id.sendB);
		b1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText tv1 = (EditText)findViewById(R.id.editText1);
				describe = tv1.getText().toString();
				sendMessage();
			}  
		});
	}
	
	private void setMainActivity() {
		now = 0;
		TextView datv = (TextView)findViewById(R.id.weatherDate);
		Calendar c = Calendar.getInstance();
    	datv.setText(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE));
		TextView tv2 = (TextView)findViewById(R.id.weatherTemp);
    	tv2.setText(Wtemp + "℃");
    	if (Wfigure != null) {
        	Matcher m = Pattern.compile("lei").matcher(Wfigure);
        	RelativeLayout l = (RelativeLayout)findViewById(R.id.layout);
        	if (m.find()) {
        		l.setBackgroundResource(R.drawable.thunder);
        	}
        	else {
        		m = Pattern.compile("yun").matcher(Wfigure);
        		Wgood = true;
        		if (m.find()) {
        			l.setBackgroundResource(R.drawable.cloud);
        		}
        		else {
        			m = Pattern.compile("xue").matcher(Wfigure);
        			Wgood = false;
        			if (m.find()) {
        				l.setBackgroundResource(R.drawable.snow);
        			}
        			else {
        				m = Pattern.compile("yu").matcher(Wfigure);
        				Wgood = false;
        				if (m.find()) {
        					l.setBackgroundResource(R.drawable.rain);
        				}
        				else {
        					Wgood = true;
        					l.setBackgroundResource(R.drawable.sunny);
        				}
        			}
        		}
        	}
        }
        if (Wstatus != null) {
        	TextView tv1 = (TextView)findViewById(R.id.weather);
        	tv1.setText(Wstatus);
        }
		
		Button settingButton = (Button)findViewById(R.id.settingButton);
		settingButton.setOnClickListener(new OnClickListener() {
			@Override    
            public void onClick(View v) {     
                    
                    	setContentView(R.layout.setting);
                    	setSetting();
			}
		});
		
		Button wxButton = (Button)findViewById(R.id.wxButton);
		wxButton.setOnClickListener(new OnClickListener() {
			@Override    
            public void onClick(View v) {     
                    setContentView(R.layout.wx);
                    setWX();
			}
		});
		
		Button recordButton = (Button)findViewById(R.id.recordButton);
		recordButton.setOnClickListener(new OnClickListener() {
			@Override    
            public void onClick(View v) {     
                    setContentView(R.layout.inform);
                    setRecord();
			}
		});
		
		Button checkButton = (Button)findViewById(R.id.checkButton);
		checkButton.setOnClickListener(new OnClickListener() {
			@Override    
            public void onClick(View v) {     
                    setContentView(R.layout.informshow);
                    setselect();
			}
		});
		
		ImageButton refreshButton = (ImageButton)findViewById(R.id.refresh);
		refreshButton.setOnClickListener(new OnClickListener(){     
            @Override    
            public void onClick(View v) {     
                           
                    		WeatherThread wt = new WeatherThread();
                    		new Thread(wt).start();
                    
            }     
    });   
		
		weatherHandler = new Handler(){
			public void handleMessage(Message msg){
				TextView datv = (TextView)findViewById(R.id.weatherDate);
				Calendar c = Calendar.getInstance();
            	datv.setText(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE));
				Bundle b = msg.getData();
	            String s1 = b.getString("temperature");
	            if (s1 != null) {
	            	TextView tv = (TextView)findViewById(R.id.weatherTemp);
	            	tv.setText(s1 + "℃");
	            	Wtemp = s1;
	            }
	            s1 = b.getString("figure");
	            if (s1 != null) {
	            	Wfigure = s1;
	            	Matcher m = Pattern.compile("lei").matcher(s1);
	            	RelativeLayout l = (RelativeLayout)findViewById(R.id.layout);
	            	if (m.find()) {
	            		l.setBackgroundResource(R.drawable.thunder);
	            	}
	            	else {
	            		m = Pattern.compile("yun").matcher(s1);
	            		if (m.find()) {
	            			l.setBackgroundResource(R.drawable.cloud);
	            		}
	            		else {
	            			m = Pattern.compile("xue").matcher(s1);
	            			if (m.find()) {
	            				l.setBackgroundResource(R.drawable.snow);
	            			}
	            			else {
	            				m = Pattern.compile("yu").matcher(s1);
	            				if (m.find()) {
	            					l.setBackgroundResource(R.drawable.rain);
	            				}
	            				else {
	            					l.setBackgroundResource(R.drawable.sunny);
	            				}
	            			}
	            		}
	            	}
	            }
	            s1 = b.getString("status");
	            if (s1 != null) {
	            	Wstatus = s1;
	            	TextView tv = (TextView)findViewById(R.id.weather);
	            	tv.setText(s1);
	            }
			}
		};
		
	}
	private void setClock() {
		Ttime = 60;
		if (Wclock == 1) Ttime = 60 * 30;
		else if (Wclock == 2) Ttime = 60 * 60;
		if (mTimer != null){
		      if (clockTask != null){
		       clockTask.cancel(); 
		      }
		      clockTask = new ClockTask();     
		      mTimer.schedule(clockTask, Ttime * 1000);
		}
    }
		
	private void setSetting() {
		now = 1;
		setBackButton();
		EditText et = (EditText)findViewById(R.id.nameEdit);
		et.setText(Wname);
		et = (EditText)findViewById(R.id.password);
		et.setText(Wpassword);
		et = (EditText)findViewById(R.id.ipEdit);
		et.setText(WServer);
		RadioButton rb = null;
		if (Wclock == 0) rb = (RadioButton)findViewById(R.id.radio0);
		else if (Wclock == 1) rb = (RadioButton)findViewById(R.id.Radio1);
		else rb = (RadioButton)findViewById(R.id.Radio2);
		rb.setChecked(true);
		Button button = (Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText et1 = (EditText)findViewById(R.id.nameEdit);
				Wname = et1.getText().toString();
				et1 = (EditText)findViewById(R.id.password);
				Wpassword = et1.getText().toString();
				et1 = (EditText)findViewById(R.id.ipEdit);
				WServer = et1.getText().toString();
				RadioGroup rp = (RadioGroup)findViewById(R.id.RadioGroup01);
				if (rp.getCheckedRadioButtonId() == R.id.radio0) Wclock = 0;
				else if (rp.getCheckedRadioButtonId() == R.id.Radio1) Wclock = 1;
				else Wclock = 2;
				setClock();
				setIP();
				// TODO: add dialog confirm
			}
			
		});
	}
	
	private void setBackButton() {
		now = 1;
		Button backButton = (Button)findViewById(R.id.backButton);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                	setContentView(R.layout.activity_main);
                	setMainActivity();
                	Wimg = null;
			}
		});
	}
	
	class ClockTask extends TimerTask{
		public void run() {
			Message msg = clockHandler.obtainMessage(1);
			msg.sendToTarget();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		regToWX();
		setContentView(R.layout.activity_main);
		setMainActivity();
		//WeatherThread wt = new WeatherThread();
		//new Thread(wt).start();
		clockHandler = new Handler() {
			public void handleMessage(Message msg) {
				Vibrator vibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
		    	vibrator.vibrate(new long[] {100, 1000, 10, 100}, -1);
		    	refresh();
		    	describe = "AUTO SEND";
				MainActivity.sendMessage();
		    	setClock();
			}
		};
		mTimer = new Timer(true);
		setClock();
		setIP();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

    class CameraView extends SurfaceView {

        private SurfaceHolder holder = null;

        //构造函数
        public CameraView(Context context) {
                super(context);
                // 操作surface的holder
                holder = this.getHolder();
                // 创建SurfaceHolder.Callback对象
                holder.addCallback(new SurfaceHolder.Callback() {

                        @Override
                        public void surfaceDestroyed(SurfaceHolder holder) {
                                // 停止预览
                                mCamera.stopPreview();
                                // 释放相机资源并置空
                                mCamera.release();
                                mCamera = null;
                        }

                        @Override
                        public void surfaceCreated(SurfaceHolder holder) {
                                //当预览视图创建的时候开启相机
                        		
                                mCamera = Camera.open();
                                try {
                                        //设置预览
                                        mCamera.setPreviewDisplay(holder);
                                } catch (Exception e) {
                                        // 释放相机资源并置空
                                        mCamera.release();
                                        mCamera = null;
                                }

                        }

                        //当surface视图数据发生变化时，处理预览信息
                        @Override
                        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                                //获得相机参数对象
                                Camera.Parameters parameters = mCamera.getParameters();
                                //parameters.set("orientation", "portrait");
                                //parameters.set("rotation", 90);
                                mCamera.setDisplayOrientation(90);
                               
                                parameters.setPictureFormat(PixelFormat.JPEG);
                                parameters.setPreviewSize(480, 800);
                                parameters.setFocusMode("auto");
                                parameters.setPictureSize(960, 1600);
                                //给相机对象设置刚才设定的参数
                                mCamera.setParameters(parameters);
                                //开始预览
                                mCamera.startPreview();
                        }
                });
                // 设置Push缓冲类型，说明surface数据由其他来源提供，而不是用自己的Canvas来绘图，在这里是由摄像头来提供数据
                holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

}
	
	class WeatherThread implements Runnable {

		public void run() {
            String link = "http://php.weather.sina.com.cn/xml.php?city=%B1%B1%BE%A9&password=DJOYnieT8234jlsK&day=0";
            try {
    			URL url = new URL(link);
    			InputStream is = url.openStream();
    			InputStreamReader isr = new InputStreamReader(is);
    			XmlPullParser parser = Xml.newPullParser();
    			parser.setInput(isr);
    			Message msg = new Message();
				Bundle b = new Bundle();
    			int evtype = parser.getEventType();
    			int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    			while (evtype != XmlPullParser.END_DOCUMENT) {
    				if (evtype == XmlPullParser.START_TAG) {
    					String tagName = parser.getName();
    					// temperature
    					if (tagName == "temperature2" && (hour >= 17)) {
    						b.putString("temperature", parser.nextText());	   						
    					}
    					if (tagName == "temperature1" && (hour < 17)) {
    						b.putString("temperature", parser.nextText());
    					}
    					// weather
    					if (tagName == "figure2" && (hour >= 17)) {
    						b.putString("figure", parser.nextText());
    					}
    					if (tagName == "figure1" && (hour < 17)) {
    						b.putString("figure", parser.nextText());
    					}
    					// weather(chinese)
    					if (tagName == "status2" && (hour >= 17)) {
    						b.putString("status", parser.nextText());
    					}
    					if (tagName == "status1" && (hour < 17)) {
    						b.putString("status", parser.nextText());
    					}
    				}
    				evtype = parser.next();
    			}
				msg.setData(b);
				MainActivity.this.weatherHandler.sendMessage(msg);
    		} catch (Exception e) {
    			// todo: cannot connect
    			Toast.makeText(getApplicationContext(), "网络故障，不能更新天气！", Toast.LENGTH_LONG).show();
    		}
            
		}
		
	}

}
 


