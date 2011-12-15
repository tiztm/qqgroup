package com.ztm;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ztm.TestAndroidActivity.MyURLSpan;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;

public class StringUtil {
	
	
	static boolean isNext;
	static String curAreaName;
	static boolean topicWithImg = false;
	
	

	static Pattern mPattern = Pattern.compile("\\[(;|:).{1,4}\\]");
	static Pattern colorPat = Pattern.compile("\\[(1;.*?|37;1|32|33)m");
	//static Pattern rePat = Pattern.compile("\\[(1;.*?|37;1|32|33)m");
   

	public static HashMap<String, String> fFolorAll = null;
	public static void initAll()
	{
		fFolorAll = BBSAll.getFColorAll();
	}
	
	public static String[] getArray(HashMap<String, String> bbsAll2) {
		String[] ba = new String[bbsAll2.size() * 2];
		int i = 0;
		Set<String> keySet = bbsAll2.keySet();
		for (String string : keySet) {
			ba[i] = string;
			ba[i + 1] = bbsAll2.get(string);
			i += 2;
		}
		return ba;
	}
	

	private static  boolean isEnglish(char c) {
		if ((c >= 0 && c <= 9) || (c >= 'a' && c <= 'z')
				|| (c >= 'A' && c <= 'z') || c == ' ') {
			return true;
		} else
			return false;
	}

	
	public static String getStrBetter(String string) {

		String scon = string;// new
		String[] split = scon.split("\n");
		StringBuilder allSb = new StringBuilder();
		for (String sp : split) {
			if(sp.startsWith(":")||sp.contains("※")) continue;
			StringBuilder sb = new StringBuilder(sp);
			int len = sp.length();
			int tempLen = 0;
			for (int i = 0; i < len; i++) {
				char charAt = sb.charAt(i);
				if (isEnglish(charAt)) {
					tempLen++;
				} else {
					tempLen += 2;
				}
				if (tempLen >= 80) {
					sb.insert(i + 1, '\n');
					len++;
					i++;
					tempLen = 0;
				}
			}

			allSb.append(sb.append('\n'));
		}
		string = allSb.toString();
		return string;
	}
	
	
	
	/**
	 * 解析获取的页面 处理某个特定的话题
	 * 
	 * @param data
	 * @return
	 */
	public static String getTopicInfo(String data,int nowPos,boolean isIP,boolean isWifi,String isPic,String nowLoginId) {
		StringBuffer tiList = new StringBuffer("<br>");
		char s = 10;
		String backS = s + "";
		
		data = data.replaceAll(backS, "<br>");
		
		//分析回复本文   功能<a href='bbspst?board=Pictures&file=M.1323243608.A'>回复本文</a>
		
		Pattern rePat = Pattern.compile("bbspst\\?.*?\\.A"); 
		        Matcher matcher = rePat.matcher(data);
		       List<String> reList = new ArrayList<String>();
		      
		        while (matcher.find()) {
		        	reList.add(matcher.group());
		        	
		        }

		
		Document doc = Jsoup.parse(data);
		Elements tds = doc.getElementsByTag("textarea");
		if(tds.size()>reList.size())
			return null;
		
		String lz = "";
		int k = 0;
		for (Element element : tds) {
			
			String text = element.text();
			String content = "";
			String userId ="";
			content = text;
			String nowP = "";
			{
				nowP = (nowPos + k) + "";
				if (k == 0) {
					nowP = "0";
				}

			}
			if (k == tds.size() - 1) {
				if (k < 30) {
					isNext = false;
				} else {
					isNext = true;
				}

			}
			 if(k==0) {
				 int areaNo = text.indexOf("信区:");
					int titleNo = text.indexOf("标 题:"); 
					if(areaNo>0&&titleNo>0)
					{
				 String area =text.substring(areaNo+4, titleNo); 
				 curAreaName =  area.replaceAll("<br>", ""); 
				 curAreaName = curAreaName.toLowerCase();
				 curAreaName = curAreaName.replaceFirst(
						 curAreaName.substring(0, 1),
						 curAreaName.substring(0, 1)
									.toUpperCase());
					}
					else
					{
						curAreaName = "byztm";
					}
				 
				 
			 }
			k++;
			String nbs = "<br>";// &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

			if (k == 1 && nowPos > 1) {
				content = content.substring(4, content.indexOf("发信站:"))
						+ "<br><br>...(以下省略)<br>";
			} else {
				/** * 处理硬回车 */
				String[] split = content.split("<br>");

				int j = 0;
				int tempBr = 0;
				StringBuffer sb = new StringBuffer();
				for (String sconA : split) {

					if (j < 3) {
						j++;
						if (j == 1) {
							if(!sconA.startsWith("发信人:")) break;
							sconA = sconA.substring(4);
							int ind = sconA.indexOf(" (");
							int inArea = sconA.indexOf(", 信区");
							if(ind<0||inArea<0) break;
							
							userId = sconA.substring(1, ind);
							String DisId = " "+userId;
							if(userId.equals(nowLoginId)) DisId = " 我";
								
							if (k == 1) {
								lz = DisId;
								
								sb.append("<a href='http://bbs.nju.edu.cn/bbsqry?userid="+userId+"'><font color=#0000EE >").append(
										DisId).append(
										"</font></a>").append(
										sconA.substring(ind, inArea)).append(
										nbs)
										.append(sconA.substring(inArea + 1)).append(
												nbs);
										;
								
							} else {
								
								if(lz.equals(DisId))
								{
									DisId=" 楼主";
								}
								
								sb.append("<a href='http://bbs.nju.edu.cn/bbsqry?userid="+userId+"'><font color=#0000EE >").append(
										DisId).append(
										"</font></a>").append(
										sconA.substring(ind, inArea)).append(
												nbs);

							}
							
							continue;
						}
						if (j == 2 && k != 1)
							continue;
						 if(j==3)
						 {
							 if(!sconA.startsWith("发信站:")) break;
							 if(sconA.length()<16) break;
							 sconA =  sconA.substring(15,sconA.length()-1);
							 String date = DateUtil.formatDateToStr(DateUtil.getDatefromStr(sconA));
								if(date == null)
									sconA ="发信于："+sconA;		
								else
									sconA ="发信于："+date;
						 }

						sb.append(sconA).append(nbs);
						continue;
					} else if (sconA.length() < 1) {
						if (tempBr == 0) {
							tempBr = 1;
							sb.append(sconA).append(nbs);
						}
						continue;
					}

					if (sconA.contains("来源:．"))
					{
						if(isIP)
						{
							int ipIndex = sconA.indexOf("[FROM:");
							if(ipIndex>0)
							{
								String ip = sconA.substring(ipIndex+7);
								String ss = fFolorAll.get("[1;3"+(k%6+1)+"m");
								sb.append(ss).append("<br>来源:．").append(ip.substring(0,ip.indexOf(']'))).append("</font><br>");
							}
						}
						continue;
					}
						if	(	sconA.contains("修改:．")
							|| sconA.equals("--")) {
						continue;
					}
					sconA = sconA.trim();
					if(isPic.equals(Const.AllPic)||(isWifi &&isPic.equals(Const.WIFIPic)))

					{
						if (sconA.toLowerCase().startsWith("http:")
								&& (sconA.toLowerCase().endsWith(".jpg") 
										|| sconA.toLowerCase().endsWith(".png")
										||sconA.toLowerCase().endsWith(".jpeg")
										||sconA.toLowerCase().endsWith(".gif")
										))
						{
							sb.append("<a href='"+sconA+"'><img src='").append(sconA).append("'></a><br>");
							topicWithImg = true;
							continue;
						}
					}

					tempBr = 0;
					String scon = "";
					try {
						scon = new String(sconA.getBytes("gb2312"),
								"iso-8859-1");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if (scon.length() < 71 || scon.length() > 89) {
						sb.append(sconA + nbs);
					} else {
						sb.append(sconA);
					}

				}
				if(sb.length()>0)
					content = sb.toString();
			}
			String sFL;
			if (nowP == "0") {
				sFL = "楼主:";
			} else {
				sFL = nowP+"楼:";
			}
			if(userId.equals(nowLoginId))
			{
				tiList.append("<a href='http://bbs.nju.edu.cn/"+reList.get(k-1)+"'>[<font color=#0000EE >回复</font>]</a>&nbsp;&nbsp;")
				.append("<a href='http://bbs.nju.edu.cn/"+reList.get(k-1).replace("bbspst?", "bbsedit?")+"'>[<font color=#0000EE>修改</font>]</a>&nbsp;&nbsp;")
				.append("<a href='http://bbs.nju.edu.cn/"+reList.get(k-1).replace("bbspst?", "bbsdel?")+"'>[<font color=#0000EE>删除</font>]</a><br>")
				
				.append(sFL)
				.append(content)
				
				.append("</font>")
				
				.append("<img src='xian'><br><br>");
			}
			else
			{
				tiList.append("<a href='http://bbs.nju.edu.cn/"+reList.get(k-1)+"'>[<font color=#0000EE >回复</font>]</a>").append(sFL).append(content).append(
				"</font><img src='xian'><br><br>");
			}
			
			
		}
		
		String ss = tiList.toString()
//		+"<a href='curArea'>[<font color=#0000EE >本讨论区</font>]</a>&nbsp;&nbsp;"
//		+"<a href='prev'>[<font color=#0000EE >上一页</font>]</a>&nbsp;&nbsp;"
//		+"<a href='next'>[<font color=#0000EE >下一页</font>]</a>&nbsp;&nbsp;"
//		+"<a href='huifu'>[<font color=#0000EE >回复</font>]</a>"
		;
		return addSmileySpans(ss);
	}

	
	
    public static String addSmileySpans(String text) {
        
    	//替换表情
    
        Matcher matcher = mPattern.matcher(text);
       
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
        	matcher.appendReplacement(sb, "<img src=\""+matcher.group()+"\">");
        }
        
        matcher.appendTail(sb);
        
        //替换字体颜色
        StringBuffer sb2= new StringBuffer();
       
        matcher = colorPat.matcher(sb);

        while (matcher.find()) {
        	String ss = fFolorAll.get(matcher.group(0));
        	if(ss==null)
        		ss = "";
        	matcher.appendReplacement(sb2,ss );
        }
        
        matcher.appendTail(sb2);
        return sb2.toString().replaceAll("\\[\\+reset\\]|\\[m|\\[(0|[0-9]{1,2})m", "</font>");
    }
	
	
   

	/**
	 * 解析获取的页面 处理10大列表
	 * 
	 * @param data
	 * @return
	 */
	public static List<TopicInfo> getTop10Topic(String data) {
		List<TopicInfo> tiList = new ArrayList<TopicInfo>();
		Document doc = Jsoup.parse(data);
		Elements tds = doc.getElementsByTag("td");
		if (tds.size() != 55) {
			TopicInfo ti = new TopicInfo();
			ti.setTitle("网络故障请重试!");
			tiList.add(ti);
			return tiList;
		}

		for (int i = 1; i < 11; i++) {
			int pos = i * 5;
			TopicInfo ti = new TopicInfo();
			ti.setRank(i + "");
			ti.setLink((tds.get(pos + 2).getElementsByTag("a")).get(0).attr(
					"href"));// 设置title
			ti.setTitle("□ "+tds.get(pos + 2).text());// 设置title
			ti.setArea(tds.get(pos + 1).text());
			ti.setNums(tds.get(pos + 4).text());
			ti.setAuthor(tds.get(pos + 3).text());
			tiList.add(ti);
		}

		return tiList;
	}



}
