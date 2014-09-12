package com.jsondemo.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity { 
	private EditText mEtName;
	private Button mBtnLogin;
	private TextView mTvResult;
	private String mStrName, mStrResult;
	private MyTask mTask;
	private InputTask inTask;
	
	private EditText inName;
	private EditText inAge;
	private EditText inSex;
	private Button inSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//显示app的界面
		setContentView(R.layout.activity_main);
		//获取app的组件信息
		mEtName = (EditText) findViewById(R.id.et_hello);
		mTvResult = (TextView) findViewById(R.id.tv_result);
		mBtnLogin = (Button) findViewById(R.id.btn_login);
		
		inName=(EditText) findViewById(R.id.editname);
		inAge =(EditText) findViewById(R.id.editage);
		inSex =(EditText) findViewById(R.id.editsex);
		inSubmit=(Button) findViewById(R.id.btninput);

		/**
		 *  处理btn_login的响应任务，启动一个MyTask，处理数据库的查询
		 */
		mBtnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mStrName = (mEtName).getText().toString();
				// AsyncTask异步任务开始
				mTask = new MyTask();
				mTask.execute(mStrName);
			}
		});
		/**
		 *  处理btn_login的响应任务，启动一个InputTask，处理数据库输入
		 */
		inSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v){
				// TODO Auto-generated method stub
				// AsyncTask异步任务开始
				inTask = new InputTask();
				inTask.execute(mStrName);
			}
		});
	}
	/**
	 *  数据库输入线程的具体任务，包括：
	 *  1. 连接servlet
	 *  2. 将用户信息通过param用HttpPost发送给server
	 *  
	 */
		private class InputTask extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {	
				//通过HttpPost连接servlet
				HttpClient hc = new DefaultHttpClient();
				String address = "http://101.6.99.79:8080/ServerJsonDemo/servlet/JsonServlet";
				HttpPost hp = new HttpPost(address);
				String name = (inName).getText().toString();
				String age = (inAge).getText().toString();
				String sex = (inSex).getText().toString();
				
				//通过param发送参数给servlet
				List<NameValuePair>param = new ArrayList<NameValuePair>();
				param.add(new BasicNameValuePair("type", "input"));
				param.add(new BasicNameValuePair("name", name));
				param.add(new BasicNameValuePair("age", age));
				param.add(new BasicNameValuePair("sex", sex));
				try{
					hp.setEntity(new UrlEncodedFormEntity(param, "utf-8")); 
					// 发送请求
					HttpResponse response = hc.execute(hp);
					// 返回200即请求成功
					if (response.getStatusLine().getStatusCode() == 200) {
						//数据写入成功
						System.out.println("上传成功");
					} else {
						System.out.println("连接失败");
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return name;
			}
		}



	class MyTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			HttpClient hc = new DefaultHttpClient();
			// 这里是服务器的IP，不要写成localhost了，即使在本机测试也要写上本机的IP地址，localhost会被当成模拟器自身的
			String address = "http://101.6.99.79:8080/ServerJsonDemo/servlet/JsonServlet";
			HttpPost hp = new HttpPost(address);
			List<NameValuePair>param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("type", "search"));
			param.add(new BasicNameValuePair("name", params[0]));
			String str=null;
			try {
				hp.setEntity(new UrlEncodedFormEntity(param, "utf-8")); //jsonObj.toString()));
				// 发送请求
				HttpResponse response = hc.execute(hp);
				// 返回200即请求成功
				if (response.getStatusLine().getStatusCode() == 200) {
					// 获取响应中的数据，这也是一个JSON格式的数据
					str =PhaseJson(response);
				} else {
					System.out.println("连接失败");
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//返回包好user信息的str
			return str;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//在文本框中显示user信息
			mTvResult.setText(result);
		}
		/**
		 * 处理HttpResponse返回的response信息，从体重提取name, sex, age等信息
		 * @param response
		 * @return
		 * @throws ParseException
		 * @throws IOException
		 * @throws JSONException
		 */
		String PhaseJson(HttpResponse response) throws ParseException, IOException, JSONException
		{
			String age = null, id = null, name=null, sex=null, str=null;
			mStrResult = EntityUtils.toString(response.getEntity());
			// 将返回结果生成JSON对象，返回的格式首先是user数组
			JSONArray userarray = new JSONObject(mStrResult).getJSONArray("users" );
			//从user数组中提取出user的信息
			//对于同名用户，只获取最后一条信息
            for(int i=0; i<userarray.length();i++) {
            	JSONObject userInfo = userarray.getJSONObject(i);
                id = userInfo.getString("id" );
                name = userInfo.getString("name");
                age = userInfo.getString("age" );
                sex = userInfo.getString("sex");
                str = "Id: "+id+"Name: " +name+" Age: "+age+" Sex "+sex;
                System. out.println(str);
            }	
            return str;
		}
	}
}