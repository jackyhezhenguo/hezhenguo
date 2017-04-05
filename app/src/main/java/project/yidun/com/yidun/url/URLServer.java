package project.yidun.com.yidun.url;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class URLServer {
	public static final String EXECUTED_SUCCESS = "0";
	public static final String TYPE_USER = "1";
	public static final String TYPE_COMPANY = "2";
	private static final String TAG = "URLServer";
	// 开发环境地址
//	public static final String SERVER_ADDRESS = "http://backend.yipinair.com//hxbb-backend-1.0.0/";
//	public static final String SERVER_ADDRESS = "http://1ta5871727.iask.in:39375/";
	public static final String SERVER_ADDRESS = "http://119.23.38.71/";
//	public static final String SERVER_ADDRESS = "http://yp-air-backend-dev.obaymax.com";
//		public static final String SERVER_ADDRESS = "http://shicaid.imwork.net";

	private static Handler mHandler;

	private Callback callback;

	public interface Callback {
		void setLatitude(double lat);
		void setLongitude(double lon);
		/**
		 * when the showNum is gained , it will callback the mothod
		 * 
		 * @param showNum
		 */
		void onShowGained(String showNum);
	}

	// TODO
	public URLServer() {
	}

	public URLServer(Handler handler) {
		mHandler = handler;
	}

	// TODO
	public URLServer(Handler handler, Callback callback) {
		mHandler = handler;
		this.callback = callback;
	}

	public Bitmap getBitmap(String uri,int what) {
		Log.d(TAG, "in getbitmap");
		URL url = null;
		Bitmap bitmap = null;
		try {
			url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(6000);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			InputStream inputStream = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(inputStream);
			inputStream.close();
			mHandler.obtainMessage(what, bitmap).sendToTarget();
		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
		return bitmap;
	}

	public Map<String, Bitmap> getBitmap(String id, String uri) {
		URL url = null;
		Map<String, Bitmap> map = new HashMap<String, Bitmap>();
		try {
			url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(1000);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			InputStream inputStream = conn.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			inputStream.close();
			map.put(id, bitmap);
			mHandler.obtainMessage(0, map).sendToTarget();
		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
		return map;
	}

	/**
	 * <<<<<<< HEAD 验证手机用户
	 * 
	 * @param mobile
	 */
	// TODO 验证手机用户
	public void checkMobile(String mobile) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("mobile", mobile);

			URL url = new URL(SERVER_ADDRESS + "/sms/userRegister/" + mobile);
			sendRequest(url, null, paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * ======= >>>>>>> 34a67ba02080a7e5248b9220a2a9fe2c761672ce 调用服务端获取短信验证码接口
	 * 
	 * @param mobile
	 *            手机号码
	 * @param validateType
	 *            验证类型
	 */
	public void getPhoneCode(String mobile, int validateType) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("mobile", mobile);
			paramJson.put("type", validateType);
			URL url = new URL(SERVER_ADDRESS + "/sms/userFindPassword/" + mobile);
			sendRequest(url, null, paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端获取短信验证码接口
	 * 
	 * @param phone
	 *            手机号码
	 */
	public void getRegistPhoneCode(String phone) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("mobile", phone);
			URL url = new URL(SERVER_ADDRESS + "/sms/userRegister/" + phone);
			sendRequest(url, null, paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端登录接口
	 * 
	 * @param userName
	 *            注册帐号或手机号码
	 * @param passwd
	 *            登录密码
	 */
	public void login(String userName, String passwd) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("userName", userName);
			paramJson.put("password", passwd);
			URL url = new URL(SERVER_ADDRESS + "/user/login");
			sendRequest(url, null, paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {

			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端获取指定区域空气质量接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param centralParam
	 *            中心点坐标
	 * @param southWestParam
	 *            西南角坐标
	 * @param southEastParam
	 *            东北角坐标
	 * @param northWestParam
	 *            西北角坐标
	 * @param northEastParam
	 *            东南角坐标
	 * 
	 *            PS：该方法不合适
	 */

	// TODO 调用服务端获取指定区域空气质量接口
	public void getAreaData(String token, JSONObject centralParam, JSONObject southWestParam, JSONObject southEastParam,
			JSONObject northWestParam, JSONObject northEastParam) {
		try {
			JSONObject paramJson = new JSONObject();
			paramJson.put("central", centralParam);
			paramJson.put("northEast", northEastParam);
			paramJson.put("northWest", northWestParam);
			paramJson.put("southEast", southEastParam);
			paramJson.put("southWest", southWestParam);
			URL url = new URL(SERVER_ADDRESS + "/air/area");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}


	/**
	 * 调用服务端取样接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param address
	 *            取样地址
	 * @param lat
	 *            取样地址经度
	 * @param lng
	 *            取样地址纬度
	 */
	public void getSampleData(String token, String address, Number lat, Number lng) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("address", address);
			paramJson.put("lat", lat);
			paramJson.put("lng", lng);
			URL url = new URL(SERVER_ADDRESS + "/air/poi");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	public void getSampleHwData(String token, String address,String airQuality,String time,String humidity,String scene,String temperature,String tvoc) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("address", address);
			paramJson.put("airQuality", airQuality);
			paramJson.put("createTime", time);
			paramJson.put("humidity", humidity);
			paramJson.put("scene", scene);
			paramJson.put("temperature", temperature);
			paramJson.put("tvoc", tvoc);
			URL url = new URL(SERVER_ADDRESS + "/air/upload/hwdata");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端获取指定区域空气质量
	 * 
	 * @param token
	 *            登录令牌
	 * @param lat
	 *            经度
	 * @param lng
	 *            纬度
	 */
	public void getPoiData(String token, Number lat, Number lng) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("lat", lat);
			paramJson.put("lng", lng);
			URL url = new URL(SERVER_ADDRESS + "/air/poi");
			sendRequestShowNumber(callback, url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 支付宝请求订单Api
	 * @param token
	 * 			令牌
	 * @param orderIds
	 * 			订单id
	 * @param price
	 * 			订单总价
	 * @param reqNo
	 * 			流水号
	 * @param subject
	 * 			支付说明
	 * @param what
	 * 			期望handler的处理what
	 */
	public void getAlipayOrder(String token,int[] orderIds,double price,long reqNo,String subject,int what){
		/*
		 * 1--Android
		 */
		final String teSource = "1";
		JSONObject paramJson = new JSONObject();
		JSONArray ids = new JSONArray();
		try {
			for (int i = 0; i < orderIds.length; i++) {
				ids.put(orderIds[i]);
			}
			paramJson.put("orderIds", ids);
			paramJson.put("price", price);
			paramJson.put("reqNo", reqNo);
			paramJson.put("subject", subject);
			paramJson.put("teSource", teSource);
			Log.i(TAG, paramJson.toString());
			URL url = new URL(SERVER_ADDRESS+"/order/orderPay/alipay");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes(), what);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param token 
	 * 		令牌
	 * @param msg 
	 * 		支付失败说明
	 * @param orderIds 
	 * 		订单号，多个用英文逗号隔开
	 * @param payNumber 
	 * 		支付公司返回流水号
	 * @param payStatus 
	 * 		支付状态：T支付成功；F支付失败
	 * @param seqNo 
	 * 		支付序列号 
	 * @param sign 
	 * 		32位MD5小写加密
	 * @param timestamp 
	 * 		时间戳
	 * @param what 
	 * 		Handler响应的what
	 */
	public void putAlipayResult(String token,String msg,String orderIds,String payNumber,String payStatus,String seqNo,String sign,long timestamp,int what){
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("msg", msg );
			paramJson.put("orderIds", orderIds );
			paramJson.put("payNumber", payNumber);
			paramJson.put("payStatus", payStatus );
			paramJson.put("seqNo", seqNo);
			paramJson.put("sign", sign);
			paramJson.put("timestamp", timestamp );
			Log.i(TAG, paramJson.toString());
			URL url = new URL(SERVER_ADDRESS+"/order/orderPayAfter");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes(), what);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  获取订单详情
	 * @param token
	 * @param orderId
	 * 			订单id
	 * @param what
	 */
	public void getOrderDetail(String token,long orderId,int what){
		URL url;
		try {
			url = new URL(SERVER_ADDRESS+"/order/"+orderId);
			sendGetRequest(url, token,what);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 调用服务端放飞空气球接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param lat
	 *            放飞者的经度
	 * @param lng
	 *            放飞者的纬度
	 * @param address
	 *            放飞者的地址
	 * @param value
	 *            放飞的文字信息
	 */
	public void flyBall(String token, String address, Number lat, Number lng, String value) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("address", address);
			paramJson.put("lat", lat);
			paramJson.put("lng", lng);
			paramJson.put("value", value);
			URL url = new URL(SERVER_ADDRESS + "/balloon/add");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端获取随机空气球接口
	 * 
	 * @param token
	 *            登录令牌
	 */
	public void acceptBall(String token) {
		try {
			JSONObject paramJson = new JSONObject();
			URL url = new URL(SERVER_ADDRESS + "/balloon/find");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端获取AQI取样数据历史
	 * 
	 * @param token
	 *            登录令牌
	 * @param page
	 *            当前页，首页为0
	 * @param pageSize
	 *            每页显示条数，须大于0，默认10条
	 */
	public void getAqiDataHistory(String token, Integer page, Integer pageSize) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("page", page);
			paramJson.put("pageSize", pageSize);
			URL url = new URL(SERVER_ADDRESS + "/air/history");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	/**
	 * 调用服务端获取AQI上传过的数据
	 * 
	 * @param token
	 *            登录令牌
	 * @param page
	 *            当前页，首页为0
	 * @param pageSize
	 *            每页显示条数，须大于0，默认10条
	 */
	public void getAqiDataHw(String token, Integer page, Integer pageSize) {
		JSONObject paramJson = new JSONObject();
	try {
		paramJson.put("page", page);
		paramJson.put("pageSize", pageSize);
		URL url = new URL(SERVER_ADDRESS + "/air/get/hwdata");
		sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
	} catch (IOException e) {
		Log.e(TAG, Log.getStackTraceString(e));
	} catch (JSONException e) {
		Log.e(TAG, Log.getStackTraceString(e));
	}
	}
	
	/**
	 * 退出登录
	 * @param token
	 */
	public void logout(String token,int what){
		try {
			URL url = new URL(SERVER_ADDRESS + "/user/logout");
			sendGetRequest(url, token, what);
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端服务商注册接口
	 * 
	 * @param businessAddress
	 *            企业地址
	 * @param businessCity
	 *            企业所在城市
	 * @param businessCode
	 *            企业注册号
	 * @param businessLat
	 *            企业纬度
	 * @param businessLng
	 *            企业经度
	 * @param businessName
	 *            企业名称
	 * @param captcha
	 *            验证，必填
	 * @param email
	 *            邮箱，必填
	 * @param mobile
	 *            手机号，必填
	 * @param password
	 *            密码，必填
	 * @param type
	 *            用户类型 1 个人；2 企业
	 * 
	 */
	public void companyRegister(String businessAddress, String businessCity, String businessCode, Number businessLat,
			Number businessLng, String businessName, String captcha, String email, String mobile, String password,
			Integer type) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("businessAddress", businessAddress);
			paramJson.put("businessCity", businessCity);
			paramJson.put("businessCode", businessCode);
			paramJson.put("businessLat", businessLat);
			paramJson.put("businessLng", businessLng);
			paramJson.put("businessName", businessName);
			paramJson.put("captcha", captcha);
			paramJson.put("email", email);
			paramJson.put("mobile", mobile);
			paramJson.put("password", password);
			paramJson.put("type", type);
			URL url = new URL(SERVER_ADDRESS + "/user/bizRegister");
			sendRequest(url, null, paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端更新用户信息接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param birthday
	 *            生日
	 * @param email
	 *            邮箱
	 * @param icon
	 *            头像
	 * @param nickname
	 *            昵称
	 * @param sex
	 *            性别
	 * @param what
	 *            hanlder的what
	 */
	public void modifyInfo(String token, String birthday, String email, String icon, String nickname, String sex,int what) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("birthday", birthday);
//			paramJson.put("email", email);
			paramJson.put("icon", icon);
			paramJson.put("nickname", nickname);
			paramJson.put("sex", sex);
			URL url = new URL(SERVER_ADDRESS + "/user/modifyInfo");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes(),what);
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端获取单个商户详情接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param bizId
	 *            商户id
	 * @param keywords
	 *            关键词
	 */
	public void costBizInfo(String token, Integer bizId, String keywords) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("bizId", bizId);
			paramJson.put("keywords", keywords);
			URL url = new URL(SERVER_ADDRESS + "/cost/biz");
			Log.d(TAG, paramJson.toString());
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	
	/**
	 *  一键反馈
	 * @param token
	 * @param des
	 * @param type
	 * @param what
	 */
	public void feedBack(String token,String des,int type,int what){
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("description", des);
			paramJson.put("type", what);
			URL url = new URL(SERVER_ADDRESS + "/suggest/save");
			Log.d(TAG,paramJson.toString());
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes(),what);
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端生成订单接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param bizId
	 *            企业商户id
	 * @param contactMobile
	 *            联系人手机
	 * @param contactName
	 *            联系人
	 * @param payType
	 *            支付方式
	 * @param serviceId
	 *            净化服务id
	 * @param targetAddress
	 *            目标地点
	 * @param targetTime
	 *            要求服务时间
	 * @param totalPrice
	 *            订单总价
	 */
	public void costBookInfo(String token, int bizId, String contactMobile, String contactName,
			int payType, int[] serviceId, String targetAddress, String targetTime, double totalPrice) {
		JSONObject paramJson = new JSONObject();
		JSONArray serviceIdArray = new JSONArray();
		try {
			paramJson.put("bizId", bizId);
			paramJson.put("contactMobile", contactMobile);
			paramJson.put("contactName", contactName);
			paramJson.put("payType", payType);
			for(int id:serviceId){
				serviceIdArray.put(id);
			}
			paramJson.put("serviceId", serviceIdArray);
			paramJson.put("targetAddress", targetAddress);
			paramJson.put("targetTime", targetTime);
			totalPrice = (double)((int)(totalPrice*100))/100;
			paramJson.put("totalPrice", totalPrice);
			Log.i(TAG, paramJson.toString());
			URL url = new URL(SERVER_ADDRESS + "/cost/book");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes("utf-8"));
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端获取查询服务列表接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param city
	 *            城市
	 * @param keywords
	 *            关键词
	 * @param orderBy
	 *            排序方式 11评分降序；12评分升序；21价格降序；22价格升序；31成交量降序； 32成交量升序
	 * @param page
	 *            当前页，首页为0
	 * @param pageSize
	 *            每页显示条数，须大于0，默认10条
	 */
	public void costListInfo(String token, Integer city, String keywords, Integer orderBy, Integer page,
			Integer pageSize) {
		JSONObject paramJson = new JSONObject();
		try {
			// paramJson.put("city", city);
			paramJson.put("keywords", keywords);
			paramJson.put("orderBy", orderBy);
			paramJson.put("page", page);
			paramJson.put("pageSize", pageSize);
			URL url = new URL(SERVER_ADDRESS + "/cost/list");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端查询服务商地图接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param city
	 *            城市
	 * @param keywords
	 *            关键词
	 * @param lat
	 *            按地图查询时，当前位置纬度
	 * @param lng
	 *            按地图查询时，当前位置经度
	 * @param page
	 *            当前页，首页为0
	 * @param pageSize
	 *            每页显示条数，须大于0，默认10条
	 */
	public void costMap(String token, Integer city, String keywords, Integer lat, Integer lng, Integer page,
			Integer pageSize) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("city", city);
			paramJson.put("keywords", keywords);
			paramJson.put("lat", lat);
			paramJson.put("lng", lng);
			paramJson.put("page", page);
			paramJson.put("pageSize", pageSize);
			URL url = new URL(SERVER_ADDRESS + "/cost/map");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端新增服务接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param cost
	 *            费用
	 * @param name
	 *            新增的服务名称
	 * @param parentId
	 *            父类型id
	 */
	public void addBizService(String token, double cost, Integer id, String name, Integer parentId) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("cost", cost);
//			paramJson.put("id", id);
			paramJson.put("name", name);
			paramJson.put("parentId", parentId);
			URL url = new URL(SERVER_ADDRESS + "/biz/service/add");
			Log.d(TAG, paramJson.toString());
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 删除服务
	 * @param token
	 * @param id
	 */
	public void deleteBizService(String token,int id){
		try {
			URL url = new URL(SERVER_ADDRESS + "/biz/service/delete/"+id);
			sendGetRequest(url, token);
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 一键举报
	 * @param token
	 * @param
	 * @param
	 */
	public void report(String token, String address, String createTime, String humidity, Number lat, Number lng,Integer scene,String rep,String temperature, String tvoc) {
		JSONObject paramJson = new JSONObject();
		try {
			Log.e("333",createTime);
			paramJson.put("address", address);
			paramJson.put("createTime", createTime);
			paramJson.put("humidity", humidity);
			paramJson.put("lat", lat);
			paramJson.put("lng", lng);
			paramJson.put("scene", scene);
			paramJson.put("suggest", rep);
			paramJson.put("temperature", temperature);
			paramJson.put("tvoc", tvoc);
			
			URL url = new URL(SERVER_ADDRESS + "/air/tipoff/save");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端编辑服务接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param cost
	 * @param id
	 * @param name
	 * @param parentId
	 * 			如car分组的id，home分组的id
	 */
	public void editBizService(String token, double cost, Integer id, String name, Integer parentId) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("cost", cost);
			paramJson.put("id", id);
			paramJson.put("name", name);
			paramJson.put("parentId", parentId);
			URL url = new URL(SERVER_ADDRESS + "/biz/service/edit");
			Log.d(TAG, paramJson.toString());
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端企业信息编辑接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param name
	 *            企业名称
	 * @param bank
	 *            开户行
	 * @param bankNumber
	 *            银行帐号
	 * @param contactPhone
	 *            联系电话
	 */
	public void editBizDetails(Integer id, String token, String name, String bank, String bankNumber,
			String contactPhone) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("id", id);
			paramJson.put("name", name);
			paramJson.put("bank", bank);
			paramJson.put("bankNumber", bankNumber);
			paramJson.put("contactPhone ", contactPhone);
			URL url = new URL(SERVER_ADDRESS + "/biz/details/edit");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用服务端保存企业简介接口
	 * 
	 * @param id
	 *            主键ID，必填
	 * @param token
	 *            登录令牌
	 * @param remark
	 *            企业简介文字
	 */
	public void saveBizRemark(Integer id, String token, String remark) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("id", id);
			paramJson.put("remark", remark);
			URL url = new URL(SERVER_ADDRESS + "/biz/details/edit");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用服务端QQ第三方登录接口
	 * 
	 * @param figureurl
	 *            头像
	 * @param gender
	 *            性别
	 * @param nickname
	 *            昵称
	 * @param openId
	 *            openId
	 * @param sign
	 *            MD5小写签名
	 * @param timestamp
	 *            时间戳
	 */
	public void loginByQQ(String figureurl, String gender, String nickname, String openId, String sign,
			Integer timestamp) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("figureurl", figureurl);
			paramJson.put("gender", gender);
			paramJson.put("nickname", nickname);
			paramJson.put("openId", openId);
			paramJson.put("sign", sign);
			paramJson.put("timestamp", timestamp);
			URL url = new URL(SERVER_ADDRESS + "/user/loginByQQ");
			sendRequest(url, null, paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public void getCompaniesByCity(String token, double lat, double lng, int pageSize) {

		try {
			JSONObject paramJson = new JSONObject();
			// paramJson.put("city", 0);
			paramJson.put("lat", lat);
			paramJson.put("lng", lng);
			// paramJson.put("page", 0);
			// paramJson.put("keywords", null);
			// paramJson.put("pageSize", 20);
			// paramJson.put("sort", null);
			URL url = new URL(SERVER_ADDRESS + "/cost/map");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

	}

	/**
	 * 调用服务端修改订单信息接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param address
	 *            收货地址
	 * @param city
	 *            收货城市
	 * @param contactMobile
	 *            手机号
	 * @param contactName
	 *            收货人
	 * @param id
	 *            订单id
	 */
	public void editOrder(String token, String address, String city, String contactMobile, String contactName,
			Integer id) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("address", address);
			paramJson.put("city", city);
			paramJson.put("contactMobile", contactMobile);
			paramJson.put("contactName", contactName);
			paramJson.put("id", id);
			URL url = new URL(SERVER_ADDRESS + "/order/edit");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端保存订单评价接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param description
	 *            文字评价
	 * @param orderDetailsId
	 *            订单详情id
	 * @param score
	 *            星星评分
	 * @param shortDescription
	 *            简短评价id
	 */
	public void saveOrderEvaluate(String token, String description, Integer orderDetailsId, Integer score,
			Integer shortDescription) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("description", description);
			paramJson.put("orderDetailsId", orderDetailsId);
			paramJson.put("score", score);
			if(shortDescription != null) paramJson.put("shortDescription", shortDescription);
			URL url = new URL(SERVER_ADDRESS + "/order/evaluate/save");
			Log.d(TAG, paramJson.toString());
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端获取订单列表接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param begin
	 *            查询起始时间
	 * @param dateType
	 *            查询时间类型：1预订时间；2支付时间；3要求完成时间；4实际完成时间；为null表示不加时间
	 * @param end
	 *            查询截止时间
	 * @param orderBy
	 *            排序方式：11时间降序；12时间升序
	 * @param orderStatus
	 *            订单状态；1未确认；2已确认；3 未完成；4 已完成；5无效单
	 * @param page
	 *            当前页，首页为0
	 * @param pageSize
	 *            每页显示条数，须大于0，默认10条
	 * @param what
	 *            响应handler的what
	 */
	public void getOrderList(String token, String begin, Integer dateType, String end, Integer orderBy,
			Integer orderStatus, Integer page, Integer pageSize,int what) {
		JSONObject paramJson = new JSONObject();
		try {
			if(begin != null) paramJson.put("begin", begin);
			if(dateType != null) paramJson.put("dateType", dateType);
			if(end != null) paramJson.put("end", end);
			paramJson.put("orderBy", orderBy);
			paramJson.put("orderStatus", orderStatus);
			paramJson.put("page", page);
			paramJson.put("pageSize", pageSize);
			URL url = new URL(SERVER_ADDRESS + "/order/list");
			Log.d(TAG,paramJson.toString());
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes(),what);
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端获取订单列表接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param begin
	 *            查询起始时间
	 * @param dateType
	 *            查询时间类型：1预订时间；2支付时间；3要求完成时间；4实际完成时间；为null表示不加时间
	 * @param end
	 *            查询截止时间
	 * @param orderBy
	 *            排序方式：11时间降序；12时间升序
	 * @param orderStatus
	 *            订单状态；1未确认；2已确认；3 未完成；4 已完成；5无效单
	 * @param page
	 *            当前页，首页为0
	 * @param pageSize
	 *            每页显示条数，须大于0，默认10条
	 * @param what
	 *            响应handler的what
	 */
	public void getBizOrderList(String token, String begin, Integer dateType, String end, Integer orderBy,
			Integer orderStatus, Integer page, Integer pageSize,int what) {
		JSONObject paramJson = new JSONObject();
		try {
			if(begin != null) paramJson.put("begin", begin);
			if(dateType != null) paramJson.put("dateType", dateType);
			if(end != null) paramJson.put("end", end);
			paramJson.put("orderBy", orderBy);
			paramJson.put("orderStatus", orderStatus);
			paramJson.put("page", page);
			paramJson.put("pageSize", pageSize);
			URL url = new URL(SERVER_ADDRESS + "/biz/order/list");
			Log.d(TAG,paramJson.toString());
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes(),what);
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端忘记密码接口
	 * 
	 * @param mobile
	 *            手机号码
	 * @param vcode
	 *            验证码
	 * @param password
	 *            密码
	 */
	public void forgetpassword(String mobile, String vcode, String password) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("mobile", mobile);
			paramJson.put("verificationCode", vcode);
			paramJson.put("newPassword", password);
			URL url = new URL(SERVER_ADDRESS + "/user/password/forget");
			sendRequest(url, null, paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端密码重置接口
	 * 
	 * @param newpwd
	 *            新密码
	 * @param newconfirmpwd
	 *            确认新密码
	 */
	public void resetPassword(String newpwd, String newconfirmpwd) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("newPwd", newpwd);
			paramJson.put("newConfirmPwd", newconfirmpwd);
			URL url = new URL(SERVER_ADDRESS + "/user/password/forget");
			sendRequest(url, null, paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 获取当前用户信息
	 * @param token
	 */
	public void getUserInfo(String token){
		try {
			URL url = new URL(SERVER_ADDRESS + "/user/info");
			sendGetRequest(url, token);
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端修改密码接口
	 * 
	 * @param userId
	 *            用户编号
	 * @param token
	 *            登录令牌
	 * @param oldPassword
	 *            旧密码
	 * @param newPassword
	 *            新密码
	 */
	public void modifyPassword(String userId, String token, String oldPassword, String newPassword) {
		JSONObject paramJson = new JSONObject();
		try {
//			paramJson.put("userId", userId);
			paramJson.put("oldPassword", oldPassword);
			paramJson.put("newPassword", newPassword);
			Log.d(TAG,paramJson.toString());
			URL url = new URL(SERVER_ADDRESS + "/user/changePassword");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端修改手机号接口
	 * 
	 * @param userId
	 *            用户编号
	 * @param token
	 *            登录令牌
	 * @param oldMobile
	 *            旧手机号码
	 * @param newMobile
	 *            新手机号码
	 */
	public void modifyMobile(String userId, String token, String oldMobile, String newMobile, String vcode) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("userId", userId);
			paramJson.put("oldMobile", oldMobile);
			paramJson.put("newMobile", newMobile);
			paramJson.put("verificationCode", vcode);
			URL url = new URL(SERVER_ADDRESS + "/api/commons/mobile/update");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 
	 * @param email
	 *            注册邮箱
	 * @param pwd
	 *            注册密码
	 * @param confirmPwd
	 *            确认密码
	 * @param mobile
	 *            手机号码
	 * @param vcode
	 *            验证码
	 */
	public void userRegist(String token, String email, String pwd, String confirmPwd, String mobile, String vcode) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("email", email);
			paramJson.put("pwd", pwd);
			paramJson.put("confirmPwd", confirmPwd);
			paramJson.put("mobile", mobile);
			paramJson.put("verificationCode", vcode);
			URL url = new URL(SERVER_ADDRESS + "/user/register");
			sendRequest(url, createTokenMap(token), paramJson.toString().getBytes());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 调用服务端上传用户图像接口
	 * 
	 * @param token
	 *            登录令牌
	 * @param path
	 *            图片文件地址
	 */
	public void updateImage(String token, String path,int what) {
		try {
			URL url = new URL(SERVER_ADDRESS + "/user/uploadImage");
			sendImageRequest(url, token, new File(path),what);
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 上传文件
	 * @param token
	 * @param file
	 */
	public void uploadVoice(String token, String file) {
		JSONObject paramJson = new JSONObject();
		try {
			paramJson.put("file", file);
			URL url = new URL(SERVER_ADDRESS + "/file/uploadFile");
			uploadFileRequest(url, createTokenMap(token), new File(file));
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 向服务端发送字符请求消息
	 * 
	 * @param url
	 *            服务端地址
	 * @param headerParam
	 *            HTTP头参数
	 * @param requestData
	 *            请求参数
	 */
	// TODO 向服务端发送字符请求消息
	private void sendRequestShowNumber(final Callback callback, final URL url, final Map<String, Object> headerParam,
			final byte[] requestData) {
		new AsyncTask<String, String, String>() {

			@Override
			protected String doInBackground(String... params) {
				HttpURLConnection httpURLConnection = null;
				OutputStream outputStream = null;
				String respStr = null;
				try {
					httpURLConnection = (HttpURLConnection) url.openConnection();
					httpURLConnection.setConnectTimeout(6000);
					httpURLConnection.setDoOutput(true);
					httpURLConnection.setDoInput(true);
					httpURLConnection.setRequestMethod("POST");
					httpURLConnection.setUseCaches(false);
					httpURLConnection.setRequestProperty("Content-Type", "application/json");
					httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
					// httpURLConnection.connect();
					// 设置HTTP头信息
					if (headerParam != null && headerParam.size() > 0) {
						Iterator<Map.Entry<String, Object>> it = headerParam.entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry<String, Object> entry = it.next();
							httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue().toString());
						}
					}
					outputStream = httpURLConnection.getOutputStream();
					outputStream.write(requestData);

					int response = httpURLConnection.getResponseCode();
					// 处理请求状态码=200则正常，400则是错误请求
					if (response == HttpURLConnection.HTTP_OK) {
						InputStream inptStream = httpURLConnection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(inptStream));
						StringBuffer builder = new StringBuffer();
						for (String s = reader.readLine(); s != null; s = reader.readLine()) {
							builder.append(s);
						}
						respStr = builder.toString();
						// Log.v(TAG, respStr);
						// Log.v(TAG, url.toString());
						// if (mHandler != null) {
						// mHandler.obtainMessage(0, respStr).sendToTarget();
						// }
					} else {
						InputStream inptStream = httpURLConnection.getErrorStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(inptStream));
						StringBuffer builder = new StringBuffer();
						for (String s = reader.readLine(); s != null; s = reader.readLine()) {
							builder.append(s);
						}
						respStr = builder.toString();
						// Log.v(TAG, url.toString());
						// if (mHandler != null) {
						// mHandler.obtainMessage(0, respStr).sendToTarget();
						// }
					}
				} catch (IOException e) {
					Log.e(TAG, Log.getStackTraceString(e));
				} finally {
					closeStream(outputStream);
					closeConnect(httpURLConnection);
				}
				return respStr;
			}

			@Override
			protected void onPostExecute(String result) {
				callback.onShowGained(result);
			}
		}.execute();

	}

	public void sendRequest(URL url, Map<String, Object> headerParam, byte[] requestData) {
		sendRequest(url, headerParam, requestData, 0);
	}

	/**
	 * 同上，不过多个参数what
	 * 
	 * @param url
	 * @param headerParam
	 * @param requestData
	 * @param what
	 *            handler发送的what值
	 */
	private void sendRequest(URL url, Map<String, Object> headerParam, byte[] requestData, int what) {
		HttpURLConnection httpURLConnection = null;
		OutputStream outputStream = null;
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(3000);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("Content-Type", "application/json");
			httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
			// httpURLConnection.connect();
			// 设置HTTP头信息
			if (headerParam != null && headerParam.size() > 0) {
				Log.d("111","youmy");
				Iterator<Map.Entry<String, Object>> it = headerParam.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, Object> entry = it.next();
					httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue().toString());
				}
			}
			outputStream = httpURLConnection.getOutputStream();
			outputStream.write(requestData);

			int response = httpURLConnection.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				InputStream inptStream = httpURLConnection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inptStream));
				StringBuffer builder = new StringBuffer();
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					builder.append(s);
				}
				String respStr = builder.toString();
				Log.e("22222", respStr);
				if (mHandler != null) {
					mHandler.obtainMessage(what, respStr).sendToTarget();
				}
			} else {
				/** 在此对httpStatus400进行处理 **/
				InputStream inptStream = httpURLConnection.getErrorStream();
				Log.d(TAG, "处理错误请求时返回的inptStream==" + inptStream.toString());
				BufferedReader reader = new BufferedReader(new InputStreamReader(inptStream));
				StringBuffer builder = new StringBuffer();
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					builder.append(s);
				}
				String respStr = builder.toString();
				mHandler.obtainMessage(what, respStr).sendToTarget();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeStream(outputStream);
			closeConnect(httpURLConnection);
		}
	}
	private void sendRequestLanou(URL url, Map<String, Object> headerParam, byte[] requestData, int what) {
		HttpURLConnection httpURLConnection = null;
		OutputStream outputStream = null;
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(3000);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("Content-Type", "application/json");
			httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
			// httpURLConnection.connect();
			// 设置HTTP头信息
			if (headerParam != null && headerParam.size() > 0) {
				Log.d("111","youmy");
				Iterator<Map.Entry<String, Object>> it = headerParam.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, Object> entry = it.next();
					httpURLConnection.setRequestProperty("lano", entry.getValue().toString());
				}
			}

			outputStream = httpURLConnection.getOutputStream();
			outputStream.write(requestData);

			int response = httpURLConnection.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				InputStream inptStream = httpURLConnection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inptStream));
				StringBuffer builder = new StringBuffer();
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					builder.append(s);
				}
				String respStr = builder.toString();
				Log.e("22222", respStr);
				if (mHandler != null) {
					mHandler.obtainMessage(what, respStr).sendToTarget();
				}
			} else {
				/** 在此对httpStatus400进行处理 **/
				InputStream inptStream = httpURLConnection.getErrorStream();
				Log.d(TAG, "处理错误请求时返回的inptStream==" + inptStream.toString());
				BufferedReader reader = new BufferedReader(new InputStreamReader(inptStream));
				StringBuffer builder = new StringBuffer();
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					builder.append(s);
				}
				String respStr = builder.toString();
				mHandler.obtainMessage(what, respStr).sendToTarget();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeStream(outputStream);
			closeConnect(httpURLConnection);
		}
	}

	/**
	 * 调用服务端接口获取企业访问统计
	 * 
	 * @param bizId
	 * 
	 */
	public void getBizVisits(String token, int bizId) {
		try {
			URL url = new URL(SERVER_ADDRESS + "/biz/visits/"+bizId);
			sendGetRequest(url, token);
		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	
	public void getAcceptAirBall(String token) {
		try {
			URL url = new URL(SERVER_ADDRESS + "/balloon/find");
			sendGetRequest(url, token);
		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	
	/**
	 * 获取其它用户的信息
	 * @param token
	 */
	public void getOtherUserInfo(String token , String id) {
		try {
			URL url = new URL(SERVER_ADDRESS + "/user/info/" + id);
			sendGetRequest(url, token);
		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 蓝鸥生成邀请码
	 * @param token
	 */
	public void buildInvites(String token) {
		try {
			JSONObject paramJson = new JSONObject();
//			paramJson.put("lano","11");
			URL url = new URL(SERVER_ADDRESS + "invites/build");
			Log.d("111",token);
			sendRequestLanou(url, createTokenMap(token), paramJson.toString().getBytes(),0);

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
//		catch (JSONException e) {
//			Log.e(TAG, Log.getStackTraceString(e));
//		}
	}

	/**
	 * 获取蓝鸥收益
	 * @param
	 */
	public void getIncome(String token , int page, int size) {
		try {
			JSONObject paramJson = new JSONObject();

			URL url = new URL(SERVER_ADDRESS + "incomes/"+"?page="+0+"&szie="+10);
			sendLanouGetRequest(url, token,0);

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 获取蓝鸥邀请过的下线成员
	 * @param
	 */
	public void getDownStaffInvitation(String token, int page, int size) {
		try {
			JSONObject paramJson = new JSONObject();

			URL url = new URL(SERVER_ADDRESS + "invites?" + "page="+page+"&szie="+size);
			sendLanouGetRequest(url, token,0);

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 获取蓝鸥下线成员
	 * @param
	 */
	public void getDownStaffMember(String token , String select, int page, int size) {
		try {
			JSONObject paramJson = new JSONObject();

			URL url = new URL(SERVER_ADDRESS + "users/downlinestaff/distance/"+select+"?page="+page+"&szie="+size);
			sendLanouGetRequest(url, token,0);

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 获取蓝鸥余额
	 * @param
	 */
	public void getMyBalance(String token) {
		try {
			JSONObject paramJson = new JSONObject();

			URL url = new URL(SERVER_ADDRESS + "mybalance");
			sendLanouGetRequest(url, token, 0);

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	/**
	 * 获取蓝鸥下线成员
	 * @param
	 */
	public void getDownLine(String token) {
		try {
			JSONObject paramJson = new JSONObject();

			URL url = new URL(SERVER_ADDRESS + "users/downlinestaff/count");
			sendLanouGetRequest(url, token, 0);

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	/**
	 * 获取蓝鸥涡轮价钱
	 * @param
	 */
	public void getWolunprice(String token) {
		try {
			JSONObject paramJson = new JSONObject();

			URL url = new URL(SERVER_ADDRESS + "/investments/turbo");
			sendLanouGetRequest(url, token, 0);

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	/**
	 * 获取蓝鸥确认付款
	 * @param
	 */
	public void getYes(String token) {
		try {
			JSONObject paramJson = new JSONObject();

			URL url = new URL(SERVER_ADDRESS + "/investments");
			sendRequestLanou(url, createTokenMap(token),paramJson.toString().getBytes(), 0);

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}


	/**
	 * 注册蓝鸥
	 * @param
	 */
	public void register(String inviteCode,String mobile,String smsCode) {
		try {
			JSONObject paramJson = new JSONObject();
			paramJson.put("inviteCode", inviteCode);
			paramJson.put("mobile", mobile);
			paramJson.put("smsCode", smsCode);

			URL url = new URL(SERVER_ADDRESS + "users/register");
			sendRequest(url, null, paramJson.toString().getBytes());

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 获取涡轮个人信息
	 * @param
	 */
	public void getPerson(String token) {
		try {

			URL url = new URL(SERVER_ADDRESS + "myself");
			sendLanouGetRequest(url, token, 0);

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 获取涡轮排位信息蓝鸥
	 * @param
	 */
	public void getWolun(String token) {
		try {

			URL url = new URL(SERVER_ADDRESS + "investments/investmentschedules");
			sendLanouGetRequest(url, token, 0);

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	/**
	 * 获取是否有新版本
	 * @param
	 */
	public void getVersion(String token) {
		try {

			URL url = new URL(SERVER_ADDRESS + "versions/last");
			sendLanouGetRequest(url, token, 0);

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	/**
	 * 获取公告内容
	 * @param
	 */
	public void getGonggao(String token) {
		try {

			URL url = new URL(SERVER_ADDRESS + "notices/display");
			sendLanouGetRequest(url, token, 0);

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	/**
	 * 获取当前用户信息
	 * @param
	 */
	public void getCurrentInfo(String token) {
		try {
			URL url = new URL(SERVER_ADDRESS + "myself");
			sendLanouGetRequest(url, token, 0);

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}



	//put方式发送蓝鸥个人资料
//	/**
//	 * PUT基础请求
//	 *
//	 * @param url 请求地址
//	 * @param values 提交参数
//	 * @return byte[] 请求成功后的结果
//	 */
//	public static byte[] doPut(String url, List<NameValuePair> values) {
//		byte[] ret = null;
//
//		CookieManager manager = CookieManager.getInstance();
//		if (url != null && url.length() > 0) {
//			URL myUrl = URL.parseString(url);
//			StringBuilder sb = new StringBuilder();
//			Cookie[] cookies = manager.getCookies(url);
//			if (cookies != null && cookies.length > 0) {
//				for (Cookie cookie : cookies) {
//					sb.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
//				}
//
//			}
//			HttpPut request = new HttpPut(url);
//			String sck = sb.toString();
//			if (sck.length() > 0) {
//				request.setHeader("Cookie", sck);
//			}
//			request.setHeader("Accept-Encoding", "gzip, deflate");
//			request.setHeader("Accept-Language", "zh-CN");
//			request.setHeader("Accept", "application/json, application/xml, text/html, text/*, image/*, */*");
//
//			DefaultHttpClient client = new DefaultHttpClient();
//			if (values != null && values.size() > 0) {
//				try {
//					UrlEncodedFormEntity entity;
//					entity = new UrlEncodedFormEntity(values);
//					request.setEntity(entity);
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//			try {
//
//				HttpResponse response = client.execute(request);
//				if (response != null) {
//					StatusLine statusLine = response.getStatusLine();
//					int statusCode = statusLine.getStatusCode();
//					if (statusCode == 200 || statusCode == 403) {
//						Header[] headers = response.getHeaders("Set-Cookie");
//						if (headers != null && headers.length > 0) {
//							for (Header header : headers) {
//								manager.setCookie(url, header.getValue());
//							}
//						}
//						HttpEntity entity = response.getEntity();
//						InputStream inputStream = entity.getContent();
//						if (inputStream != null) {
//							BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//							StringBuffer builder = new StringBuffer();
//							for (String s = reader.readLine(); s != null; s = reader.readLine()) {
//								builder.append(s);
//							}
//							String respStr = builder.toString();
//							Log.e("22222", respStr);
//							if (mHandler != null) {
//								mHandler.obtainMessage(0, respStr).sendToTarget();
//							}
//						}
//					}
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return ret;
//	}


//	public String lanouPut(String token) {
//		String ret = null;
//		try {
//
//			//跟HttpPost请求方式的代码差别并不大
//			HttpPut request = new HttpPut(SERVER_ADDRESS + "myself");  //关键是这句，DEV_URL为你的api接口地址
//			//添加参数
//			List<NameValuePair> values = new ArrayList<NameValuePair>();
////			values.add(new BasicNameValuePair("id", id));
//			values.add(new BasicNameValuePair("lano", token));
//
////			 request.setHeader("Accept-Language", "zh-CN");    //这里可以设置一些请求头
////			 request.setHeader("Accept","application/json, application/xml, text/html, text/*, image/*, */*");
//			HttpClient client = new DefaultHttpClient();
//
//			UrlEncodedFormEntity urlEntity = new UrlEncodedFormEntity(values,
//					"UTF-8");
//			request.setEntity(urlEntity);
//
//			HttpResponse response = client.execute(request);
//			if (response != null) {
//				StatusLine statusLine = response.getStatusLine();
//				int statusCode = statusLine.getStatusCode();
//				if (statusCode == 200 || statusCode == 403) {
//					HttpEntity entity = response.getEntity();
//					InputStream inputStream = entity.getContent();
//					if (inputStream != null) {
//						BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//						StringBuffer builder = new StringBuffer();
//						for (String s = reader.readLine(); s != null; s = reader.readLine()) {
//							builder.append(s);
//						}
//						String respStr = builder.toString();
//						Log.e("22222", respStr);
//						if (mHandler != null) {
//							mHandler.obtainMessage(0, respStr).sendToTarget();
//						}
//					}
//				}
//			}
//
//
//		} catch (Exception e) {
//
//		} finally {
//		}
//		return ret;
//	}

//	public String httpUrlConnectionPut(String httpUrl, String... params) {
	public String httpUrlConnectionPut(String token,String id,String nickname,String mobile,String id1, String account,String bankName,String holder,String phone) {
		String result = "";
		URL url = null;
		try {
			url = new URL(URLServer.SERVER_ADDRESS + "users");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (url != null) {
			try {
				HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
				urlConn.setRequestProperty("content-type", "application/json");
				urlConn.setDoInput(true);
				urlConn.setDoOutput(true);
				urlConn.setConnectTimeout(5 * 1000);
				//设置请求方式为 PUT
				urlConn.setRequestMethod("PUT");

				urlConn.setRequestProperty("Content-Type", "application/json");
				urlConn.setRequestProperty("Accept", "application/json");

				urlConn.setRequestProperty("Charset", "UTF-8");
//				urlConn.setRequestProperty("lano", token);


				DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());

//				number, String, array, object, 'true', 'false' or 'null'

				//写入请求参数
				//这里要注意的是，在构造JSON字符串的时候，实践证明，最好不要使用单引号，而是用“\”进行转义，否则会报错
				// 关于这一点在上面给出的参考文章里面有说明
//				String jsonParam = "{\"id\":"+id+",\"nickname\":"+nickname+",\"mobile\":"+mobile+",\"card\":{\"id\":"+id1+",\"account\":"+account+",\"bankName\":"+bankName+",\"holder\":"+holder+",\"phone\":"+phone+"}}";
//				String jsonParam = "{\"appid\":6,\"appkey\":\"0cf0vGD/ClIrVmvVT/r5hEutH5M=\",\"openid\":200}";


				JSONObject jsonObject = new JSONObject();
				JSONObject card = new JSONObject();
				card.put("account",account);
				card.put("bankName",bankName);
				card.put("holder",holder);
				card.put("phone",phone);
				if (id1 != null) {
					card.put("id", id1);
				}
				jsonObject.put("id",id);
				jsonObject.put("nickname",nickname);
				jsonObject.put("mobile",mobile);
				jsonObject.put("card",card);

//				String jsonString="[{'id':'1'},{'id':'2'}]";
//				ObjectMapper mapper = new ObjectMapper(); List<Bean> beanList = mapper.readValue(jsonString, new TypeReference<List<Bean>>() {});

				Log.d("111",account);
				dos.write(jsonObject.toString().getBytes());
				dos.flush();
				dos.close();

				if (urlConn.getResponseCode() == 200) {
					InputStreamReader isr = new InputStreamReader(urlConn.getInputStream());
					BufferedReader br = new BufferedReader(isr);
					String inputLine = null;
					while ((inputLine = br.readLine()) != null) {
						result += inputLine;
					}
					String respStr = result.toString();
						Log.e("22222", respStr);
						if (mHandler != null) {
							mHandler.obtainMessage(0, respStr).sendToTarget();
						}

					isr.close();
					urlConn.disconnect();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;

	}


	/**
	 * 提现蓝鸥
	 * @param
	 */
	public void getCash(String token,String money) {
		try {
			JSONObject paramJson = new JSONObject();

			URL url = new URL(SERVER_ADDRESS + "enchashments/cash/" + money);
			sendRequestLanou(url, createTokenMap(token), paramJson.toString().getBytes(),0);

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}


	/**
	 * 登录蓝鸥
	 * @param
	 */
	public void enter(String mobile,String smsCode) {
		try {
			JSONObject paramJson = new JSONObject();
			paramJson.put("mobile", mobile);
			paramJson.put("smsCode", smsCode);

			URL url = new URL(SERVER_ADDRESS + "users/login");
			sendRequest(url, null, paramJson.toString().getBytes());

		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (JSONException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 获取蓝鸥登录短信验证码
	 * @param
	 */
	public void getSms(String mobile) {
		try {
			URL url = new URL(SERVER_ADDRESS + "sms/login?mobile=" + mobile);
			sendGetRequest(url, null);
		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	/**
	 * 获取蓝鸥注册短信验证码
	 * @param
	 */
	public void getSmsZhuce(String mobile) {
		try {
			URL url = new URL(SERVER_ADDRESS + "sms/register?mobile=" + mobile);
			sendGetRequest(url, null);
		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}


	/**
	 * 获取最新的空气质量地图更新时间
	 * @param token
	 */
	public void getLatestMapUpdateTime(String token) {
		try {
			URL url = new URL(SERVER_ADDRESS + "/air/updateTime");
			sendGetRequest(url, token);
		} catch (MalformedURLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	/**
	 * 发送Get请求获取信息
	 **/
	private void sendGetRequest(URL url, String token) {
		Log.e("55555", "nnnnnnnnnnn2");
		sendGetRequest(url, token,0);
	}
	private void sendGetRequest(URL url, String token,int what) {
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setDoInput(true);
			if (token != null) {
				httpURLConnection.setRequestProperty("X-Access-Auth-Token", token);
			}
			httpURLConnection.setRequestMethod("GET");

			httpURLConnection.connect();
			int response = httpURLConnection.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				InputStream inptStream = httpURLConnection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inptStream));
				StringBuffer builder = new StringBuffer();
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					builder.append(s);
				}
				String respStr = builder.toString();
				Log.v(TAG, url.toString());
				Log.v("111", respStr);
				if (mHandler != null) {
					mHandler.obtainMessage(what, respStr).sendToTarget();
					Log.e("55555", "nnnnnnnnnnn3");
				}
			} else {
				/** 在此对httpStatus400进行处理 **/
				InputStream inptStream = httpURLConnection.getErrorStream();
				Log.d(TAG, "处理错误请求时返回的inptStream==" + inptStream.toString());
				BufferedReader reader = new BufferedReader(new InputStreamReader(inptStream));
				StringBuffer builder = new StringBuffer();
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					builder.append(s);
				}
				String respStr = builder.toString();
				mHandler.obtainMessage(what, respStr).sendToTarget();
			}
		} catch (ProtocolException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} finally {
			closeConnect(httpURLConnection);
		}
	}

	private void sendLanouGetRequest(URL url, String token,int what) {
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setDoInput(true);
			if (token != null) {
				httpURLConnection.setRequestProperty("lano", token);
			}
			httpURLConnection.setRequestMethod("GET");

			httpURLConnection.connect();
			int response = httpURLConnection.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				InputStream inptStream = httpURLConnection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inptStream));
				StringBuffer builder = new StringBuffer();
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					builder.append(s);
				}
				String respStr = builder.toString();
				Log.v(TAG, url.toString());
				Log.v("111", respStr);
				if (mHandler != null) {
					mHandler.obtainMessage(what, respStr).sendToTarget();
					Log.e("55555", "nnnnnnnnnnn3");
				}
			} else {
				/** 在此对httpStatus400进行处理 **/
				InputStream inptStream = httpURLConnection.getErrorStream();
				Log.d(TAG, "处理错误请求时返回的inptStream==" + inptStream.toString());
				BufferedReader reader = new BufferedReader(new InputStreamReader(inptStream));
				StringBuffer builder = new StringBuffer();
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					builder.append(s);
				}
				String respStr = builder.toString();
				mHandler.obtainMessage(what, respStr).sendToTarget();
			}
		} catch (ProtocolException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} finally {
			closeConnect(httpURLConnection);
		}
	}
	

	/**
	 * 向服务端发送图片请求消息
	 * 
	 * @param url
	 *            服务端地址
	 *
	 */
	
	private void sendImageRequest(URL url, String token, File file){
		sendImageRequest(url, token, file, 0);
	}

	private void sendImageRequest(URL url, String token, File file,int what) {
		String PREFIX = "--" , LINE_END = "\r\n";
		HttpURLConnection httpURLConnection = null;
		DataOutputStream outputStream = null;
		try {
			String BOUNDARY = UUID.randomUUID().toString();
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
			httpURLConnection.setRequestProperty("X-Access-Auth-Token", token);
			
			if(file != null && file.exists()){
				/** * 当文件不为空，把文件包装并且上传 */  
                OutputStream outputSteam=httpURLConnection.getOutputStream();   
                DataOutputStream dos = new DataOutputStream(outputSteam);   
                StringBuffer sb = new StringBuffer();   
                sb.append(PREFIX);   
                sb.append(BOUNDARY); sb.append(LINE_END);   
                /**  
                * 这里重点注意：  
                * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件  
                * filename是文件的名字，包含后缀名的 比如:abc.png  
                */   
                sb.append("Content-Disposition: form-data; name=\"image\"; filename=\""+file.getName()+"\""+LINE_END);  
                sb.append("Content-Type: application/octet-stream; charset=utf-8"+LINE_END);   
                sb.append(LINE_END);   
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);  
                byte[] bytes = new byte[1024];   
                int len = 0;   
                while((len=is.read(bytes))!=-1)   
                {   
                   dos.write(bytes, 0, len);   
                }   
                is.close();   
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();   
                dos.write(end_data);   
                dos.flush();
                
                int response = httpURLConnection.getResponseCode();
                Log.e(TAG, "response code:"+response);
    			if (response == HttpURLConnection.HTTP_OK) {
    				InputStream inptStream = httpURLConnection.getInputStream();
    				BufferedReader reader = new BufferedReader(new InputStreamReader(inptStream));
    				StringBuffer builder = new StringBuffer();
    				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
    					builder.append(s);
    				}
    				String respStr = builder.toString();
    				Log.v(TAG, url.toString());
    				Log.v(TAG, respStr);
    				mHandler.obtainMessage(what, respStr).sendToTarget();
    			} else {
    				/** 在此对httpStatus400进行处理 **/
    				InputStream inptStream = httpURLConnection.getErrorStream();
    				Log.d(TAG, "处理错误请求时返回的inptStream==" + inptStream.toString());
    				BufferedReader reader = new BufferedReader(new InputStreamReader(inptStream));
    				StringBuffer builder = new StringBuffer();
    				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
    					builder.append(s);
    				}
    				String respStr = builder.toString();
    				mHandler.obtainMessage(what, respStr).sendToTarget();
    			}
			}
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} finally {
			closeStream(outputStream);
			closeConnect(httpURLConnection);
		}
	}

	/**
	 * 构造并获取令牌数据集
	 * 
	 * @param token
	 *            服务端地址
	 * @return 令牌数据集
	 */
	public Map<String, Object> createTokenMap(String token) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("X-Access-Auth-Token", token);
		return map;
	}

	/**
	 * 关闭输出流
	 * 
	 * @param stream
	 *            输出流
	 */
	private void closeStream(OutputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
	}

	/**
	 * 关闭HttpURLConnection连接
	 * 
	 * @param connect
	 */
	private void closeConnect(HttpURLConnection connect) {
		if (connect != null) {
			connect.disconnect();
		}
	}
	

			/**
			 * 上传文件
			 * @param url
			 * @param headerParam
			 * @param file
			 */
	private void uploadFileRequest(URL url, Map<String, Object> headerParam, File file) {
		HttpURLConnection httpURLConnection = null;
		DataOutputStream outputStream = null;
		try {
			String uuid = UUID.randomUUID().toString();
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setReadTimeout(3000);
			httpURLConnection.setConnectTimeout(3000);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + uuid);
			httpURLConnection.setRequestProperty("Cache-Control", "max-age=0");
			// 设置HTTP头信息
			if (headerParam != null && headerParam.size() > 0) {
				Iterator<Map.Entry<String, Object>> it = headerParam.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, Object> entry = it.next();
					httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue().toString());
				}
			}
			StringBuffer strBuffer = new StringBuffer();
			strBuffer.append("--" + uuid + "\r\n");
			strBuffer.append("Content-Disposition: form-data; name=\"file\"; filename=\"record.wav\"\r\n");
			strBuffer.append("Content-Type: application/octet-stream;charset=ISO-8859-1\r\n");
			strBuffer.append("Content-Transfer-Encoding: binary\r\n");
			strBuffer.append("\r\n");
			outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
			outputStream.writeBytes(strBuffer.toString());
			// 设置输入流
			InputStream fStream = new FileInputStream(file);
			byte[] bytes = new byte[1024];
			int length = 0;
			while ((length = fStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, length);
			}
			fStream.close();
			outputStream.writeBytes("\r\n--" + uuid + "--\r\n");
			outputStream.flush();
			int response = httpURLConnection.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				InputStream inptStream = httpURLConnection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inptStream));
				StringBuffer builder = new StringBuffer();
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					builder.append(s);
				}
				String respStr = builder.toString();
//				Log.e("1111", respStr);
				mHandler.obtainMessage(1, respStr).sendToTarget();
			}
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} finally {
			closeStream(outputStream);
			closeConnect(httpURLConnection);
		}
	}
			
	     
}
	
	


