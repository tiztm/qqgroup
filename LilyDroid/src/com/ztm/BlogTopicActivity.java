package com.ztm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sonyericsson.zoom.ImageTextButton;
import com.sonyericsson.zoom.ImageZoomView;
import com.sonyericsson.zoom.SimpleZoomListener;
import com.sonyericsson.zoom.ZoomState;
import com.sonyericsson.zoom.SimpleZoomListener.ControlType;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import android.widget.AdapterView.OnItemClickListener;

public class BlogTopicActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.bkcolor);
		this.getWindow().setBackgroundDrawable(drawable);
		setTitle("�������");
		Intent intent = getIntent();
		String result = intent.getStringExtra("withSmile");
		String topicUrl = intent.getStringExtra("topicUrl");

		
		
		setContentView(R.layout.blogtopic);
		
		LinearLayout mLoadingLayout=(LinearLayout)findViewById(R.id.topicll);
		
		
		
		TextView textView = (TextView) findViewById(R.id.label);
		textView.setText(StringUtil.getSmilyStr(result,getResources()));
		textView.setTextSize(18);
		
		if(topicUrl!=null)
		{
			final String blogcocon = topicUrl.replace("blogcon", "blogcocon");
			final String blogcomment = topicUrl.replace("blogcon", "blogcomment");
			Button btnPre = (Button) findViewById(R.id.btn_read);
			btnPre.setOnClickListener(new OnClickListener() {
	
				public void onClick(View v) {
					NetTraffic.getUrlHtml(BlogTopicActivity.this,blogcocon, Const.BLOGCOMT,handler);
				}
			});

			mLoadingLayout.setVisibility(btnBarVis);
		}
		else
		{
			//LinearLayout mLoadingLayout=(LinearLayout)findViewById(R.id.topicll);
			mLoadingLayout.setVisibility(btnBarVis);
			isNoBar = true;
		}
		
		
		
	}
	
	
	boolean isNoBar = false;
	int btnBarVis = View.GONE;
	private int getBtnRevtVis()
	{
		if(btnBarVis ==  View.VISIBLE)
		{
			btnBarVis = View.GONE;
			
		}
		else
		{
			btnBarVis = View.VISIBLE;
		}
		return btnBarVis;
			
	}
	
	/**
	 * ���񰴼��¼�
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// ����Ƿ��ؼ�,ֱ�ӷ��ص�����
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(btnBarVis == View.VISIBLE)
			{
				LinearLayout mLoadingLayout=(LinearLayout)findViewById(R.id.topicll);
				mLoadingLayout.setVisibility(getBtnRevtVis());
				return true;
			}
			

		} 
		
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			LinearLayout mLoadingLayout=(LinearLayout)findViewById(R.id.topicll);
			if(mLoadingLayout!=null&&!isNoBar)
			{
				mLoadingLayout.setVisibility(getBtnRevtVis());
				return true;
			}
			else
			{
				return super.onKeyDown(keyCode, event);
			}
		}
		
			return super.onKeyDown(keyCode, event);
		
	}
	
	
	/**
	 *  ��Ϣ���������������½��棬��Ϊ����ͨ�߳����޷��������½����
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			NetTraffic.runningTasks--;
			if (msg.what != Const.MSGPSTNEW && NetTraffic.data.equals("error")) {
				displayMsg("�������ò���е�С����~");
			} else {
				switch (msg.what) {
				case Const.BLOGCOMT:
					chaToComment(NetTraffic.data);
					break;
				default:
					break;
				}
			}
			if (NetTraffic.runningTasks < 1) {
				NetTraffic.runningTasks = 0;
				NetTraffic.progressDialog.dismiss();
			}

		}
	};
	
	private void displayMsg(String msg) {
		Toast.makeText(BlogTopicActivity.this, msg, Toast.LENGTH_SHORT)
				.show();
	}
	
	private void chaToComment(String topicData) {
		char s = 10;
		String backS = s + "";
		String nbs = "<br>";
		topicData = topicData.replaceAll(backS,nbs );
		Document doc = Jsoup.parse(topicData);
		Elements scs = doc.getElementsByTag("textarea");
		if (scs.size() != 1) {
			displayMsg("û������!");
		} else {
			Element textArea = scs.get(0);
			
			
			String infoView = nbs + textArea.text();
			
			if(infoView.length()>5)
			{
			
			infoView = StringUtil.getBetterTopic(infoView);

			String withSmile = StringUtil.addSmileySpans(infoView,null);
			
			Intent intent = new Intent(BlogTopicActivity.this,
					BlogTopicActivity.class);
			
			intent.putExtra("withSmile", withSmile);
			startActivity(intent);
			}
			else
			{
				displayMsg("û������!");
			}
		}

	}
	
	
	
	
}