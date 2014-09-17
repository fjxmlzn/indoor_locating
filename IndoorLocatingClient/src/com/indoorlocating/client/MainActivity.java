package com.indoorlocating.client;


import android.support.v7.app.ActionBarActivity;

import android.os.*;
import android.util.TypedValue;
import android.view.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

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






import android.net.wifi.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.content.*;
import android.text.*;


public class MainActivity extends ActionBarActivity {
	private Button scanBtn;
	private Button uploadBtn;
	private Button locateBtn;
	private ListView wifiInfoView;
	private TextView locateInfoView;
	private AutoCompleteTextView descEdit;
	private WifiManager wifiManager;
	private WifiReceiver wifiReceiver;
	private List<ScanResult> wifiList = new ArrayList<ScanResult>();
	private List<String> wifiNameList;
	private List<String> wifiInfoList;
	
	private HashSet<String> descriptionSet = new HashSet<String>();
	private String description;
	
	private Toast toast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		wifiReceiver = new WifiReceiver();
		registerReceiver(wifiReceiver, new IntentFilter(
						WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		// set up the GUI components and register listeners for
		scanBtn = (Button)findViewById(R.id.scan_button);
		uploadBtn = (Button)findViewById(R.id.upload_button);
		locateBtn = (Button)findViewById(R.id.locate_button);
		wifiInfoView = (ListView)findViewById(R.id.wifi_info_text);
		locateInfoView = (TextView)findViewById(R.id.locate_info_text);
		descEdit = (AutoCompleteTextView)findViewById(R.id.des_edit);

		//wifiInfoView.setMovementMethod(new android.text.method.ScrollingMovementMethod());
		
		uploadBtn.setEnabled(false);
		scanBtn.setOnClickListener(new MyClickListener());
		uploadBtn.setOnClickListener(new MyClickListener());    
	    locateBtn.setOnClickListener(new MyClickListener());
	    descEdit.addTextChangedListener(new MyTextWatcher());
	    
	    toast = Toast.makeText(getApplicationContext(), " ", Toast.LENGTH_SHORT);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class WifiReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context c, Intent intent) {
			wifiNameList = new ArrayList<String>();
			wifiInfoList = new ArrayList<String>();
			wifiList = wifiManager.getScanResults();
			for (int i = 0; i < wifiList.size(); i++) {
				wifiNameList.add(i + ") " + wifiList.get(i).SSID + "\n" + wifiList.get(i).BSSID);
				wifiInfoList.add(String.format("Level: %d\nCapability: %s\nFreq: %d\nTimestamp: %d",
									 wifiList.get(i).level,
									 wifiList.get(i).capabilities,
									 wifiList.get(i).frequency,
									 wifiList.get(i).timestamp));
			}
			//wifiInfoView.setText(tmpSb);
			wifiInfoView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item,
						R.id.list_item, wifiNameList));
			wifiInfoView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					TextView textView = (TextView)view;
					if (textView.getText().toString().equals(wifiNameList.get(position))) {
						textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
								getResources().getDimension(R.dimen.textsize_small));
						textView.setText(wifiInfoList.get(position));
					}
					else {
						textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
								getResources().getDimension(R.dimen.textsize_large));
						textView.setText(wifiNameList.get(position));		
					}
				}
			});
			toast.cancel();
			toast = Toast.makeText(getApplicationContext(), getString(R.string.scan_finish_string),
						Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	/** �ϴ���ǰѵ������      */
	private class UploadTask extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {	
			if (wifiList.size() == 0) {
				publishProgress(getString(R.string.upload_empty_string));
				return description;
			}
				
			//ͨ��HttpPost����servlet
			HttpClient hc = new DefaultHttpClient();
			String address = "http://59.66.141.113:8080/IndoorLocatingServer/";
			HttpPost hp = new HttpPost(address);
	
			JSONArray vector = new JSONArray();
			JSONObject wifiInfo;
			//��wifiList�д���JSONArray
			try {
	            for(ScanResult sr : wifiList) {
	            	wifiInfo = new JSONObject();
	                wifiInfo.put("bssid", sr.BSSID);
	                wifiInfo.put("level", sr.level);
	                vector.put(wifiInfo);
	            }
			}
	        catch (JSONException ex) {
	        	ex.printStackTrace();
	        }
            
			//ͨ��param���Ͳ�����Servlet
			List<NameValuePair>param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("type", "input"));
			param.add(new BasicNameValuePair("label", description));
			param.add(new BasicNameValuePair("vector", vector.toString()));

			try{
				hp.setEntity(new UrlEncodedFormEntity(param, "utf-8")); 
				// ��������
				HttpResponse response = hc.execute(hp);
				String progressStr;
				// ����200������ɹ�
				if (response.getStatusLine().getStatusCode() == 200)
					if (new JSONObject(EntityUtils.toString(response.getEntity())).getInt("flag") == 1)
						progressStr = getString(R.string.upload_success_string);
					else
						progressStr =  getString(R.string.upload_fail_string);
				else
					progressStr =  getString(R.string.connect_fail_string);
				publishProgress(progressStr);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return description;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// ��ʾ�ϴ�״̬
			descriptionSet.add(result);
			descEdit.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                 		R.layout.dropdown_item, new ArrayList<String>(descriptionSet)));
		}
		
		@Override
		protected void onProgressUpdate(String... progress) {
			toast.cancel();
			toast = Toast.makeText(getApplicationContext(), progress[0], Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	
	/** ���͵�ǰWIFI��Ϣ����Զ�̻�ȡ��λ��Ϣ   */
	class LocateTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String progressStr;
			String str = null;
			HttpClient hc = new DefaultHttpClient();
			// �����Ƿ�������IP����Ҫд��localhost�ˣ���ʹ�ڱ�������ҲҪд�ϱ�����IP��ַ��localhost�ᱻ����ģ���������
			String address = "http://59.66.141.113:8080/IndoorLocatingServer/";
			HttpPost hp = new HttpPost(address);
			List<NameValuePair>param = new ArrayList<NameValuePair>();
			
			JSONArray vector = new JSONArray();
			JSONObject wifiInfo;
			//��wifiList�д���JSONArray
			try {
	            for(ScanResult sr : wifiList) {
	            	wifiInfo = new JSONObject();
	                wifiInfo.put("bssid", sr.BSSID);
	                wifiInfo.put("level", sr.level);
	                vector.put(wifiInfo);
	            }
			}
	        catch (JSONException ex) {
	        	ex.printStackTrace();
	        }
			
			param.add(new BasicNameValuePair("type", "query"));
			param.add(new BasicNameValuePair("vector", vector.toString()));
			try {
				hp.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
				// ��������
				HttpResponse response = hc.execute(hp);
				// ����200������ɹ�
				if (response.getStatusLine().getStatusCode() == 200) {
					// ��ȡ��Ӧ�е����ݣ���Ҳ��һ��JSON��ʽ������
					progressStr = getString(R.string.locate_success_string);
					str = parseResponse(response);
				} else {
					progressStr = getString(R.string.connect_fail_string);
				}
				publishProgress(progressStr);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//���ض�λ���
			return str;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			//���ı�������ʾ��λ��Ϣ
			locateInfoView.setText(result);
			//��ʾ��λ���
		}
		
		@Override
		protected void onProgressUpdate(String... progressStr) {
			toast.cancel();
			toast = Toast.makeText(getApplicationContext(), progressStr[0], Toast.LENGTH_SHORT);
			toast.show();
		}
		/**
		 * ����HttpResponse���ص�response��Ϣ
		 * @param response
		 * @return
		 * @throws ParseException
		 * @throws IOException
		 * @throws JSONException
		 */
		String parseResponse(HttpResponse response) throws ParseException, IOException, JSONException
		{
			String str;
			// �÷��ؽ������JSON����
			JSONObject jsObj = new JSONObject(EntityUtils.toString(response.getEntity()));
			
			if (jsObj.getInt("flag") == 1)
				str = jsObj.getString("label");
			else
				str = getString(R.string.locate_fail_string);
            return str;
		}
	}
	
	/** �������¼�     */
	private class MyClickListener implements View.OnClickListener{  
		@Override
		public void onClick(View v) {  
			switch (v.getId()) {  
			case R.id.scan_button: //ɨ��WIFI 
				wifiManager.startScan();
				toast.cancel();
				toast = Toast.makeText(getApplicationContext(), getString(R.string.scan_start_string),
							Toast.LENGTH_SHORT);
				toast.show();
				if (descEdit.getText().toString().length() > 0)
					uploadBtn.setEnabled(true);
				break;  
			case R.id.upload_button: //�ϴ����� 
				AsyncTask<String, String, String> uploadTask = new UploadTask();
				uploadTask.execute(description);
				toast.cancel();
				toast = Toast.makeText(getApplicationContext(), getString(R.string.upload_start_string),
							Toast.LENGTH_SHORT);
				toast.show();
				break;  
			case R.id.locate_button: //��λ
				AsyncTask<String, String, String> locateTask = new LocateTask();
				locateTask.execute();
				toast.cancel();
				toast = Toast.makeText(getApplicationContext(), getString(R.string.locate_start_string),
						Toast.LENGTH_SHORT);
				toast.show();
				break;
			default:  
				break;  
			}
		}
		
	}
	
	/** �����޸������¼�       */
	private class MyTextWatcher implements TextWatcher {
	
		public void afterTextChanged(Editable s) {
			description = descEdit.getText().toString();
			if (description.length() > 0)
				uploadBtn.setEnabled(true);
			else
				uploadBtn.setEnabled(false);
		}
		
		public void onTextChanged(CharSequence a, int b, int c, int d){}
		public void beforeTextChanged(CharSequence a, int b, int c, int d){}
	}
}