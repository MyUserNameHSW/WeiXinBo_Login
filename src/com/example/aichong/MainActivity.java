package com.example.aichong;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.weibo.AccessTokenKeeper;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MainActivity extends Activity {
	private IWXAPI mWeixinAPI;
	String WEIXIN_APP_ID = "wx6ddbc65ef7a1d9eb";
	String WEIXIN_APP_SECRET = "27073db17f8e94edb3275fd97871ceb7";
	public static final String WEIBP_APP_KEY = "4235798887"; // 应用的APP_KEY
	public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";// 应用的回调页
	AuthInfo mAuthInfo;
	Oauth2AccessToken mAccessToken;
	SsoHandler mSsoHandler;
	IWeiboShareAPI mWeiboShareAPI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initWeibo();
	}

	private void initWeibo() {
		// TODO Auto-generated method stub
		mAuthInfo = new AuthInfo(MainActivity.this, WEIBP_APP_KEY,
				REDIRECT_URL, "-->");
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, WEIBP_APP_KEY);
		mWeiboShareAPI.registerApp(); // 将应用注册到微博客户端

	}

	// 微博登录
	public void toWeibo(View view) {
		gotoSina();
	}

	// 微信登录
	public void toWeixin(View view) {
		if (mWeixinAPI == null) {
			mWeixinAPI = WXAPIFactory.createWXAPI(this, WEIXIN_APP_ID, true);
		}
		mWeixinAPI.registerApp(WEIXIN_APP_ID);
		loginByWeiXin();
	}

	private void loginByWeiXin() {
		// TODO Auto-generated method stub
		if (!mWeixinAPI.isWXAppInstalled()) {
			return;
		}

		SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "wechat_sdk_demo";
		mWeixinAPI.sendReq(req);
	}

	// 微博登录回调
	class AuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			// 获取微博用户登录信息
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			String access_token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			final String idstr = values.getString("uid");
			Log.e("access_token", access_token); // 中解析
			Log.e("expires_in", expires_in);
			Log.e("idstr", idstr);
			mAccessToken = new Oauth2AccessToken(access_token, expires_in);

			if (mAccessToken.isSessionValid()) {
				AccessTokenKeeper.writeAccessToken(MainActivity.this,
						mAccessToken);
				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
						.format(new java.util.Date(mAccessToken
								.getExpiresTime()));
				AccessTokenKeeper.writeAccessToken(MainActivity.this,
						mAccessToken);
				// AccessTokenKeeper.readAccessToken(LoginOthersActivity.this);
			} else {
				// 当您注册的应用程序签名不正确时，就会收到错误Code，请确保签名正确
				String code = values.getString("code", "");
				Log.e("ErrorCode", code + "------------");
			}
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO Auto-generated method stub

		}
	}

	private void gotoSina() {
		mSsoHandler = new SsoHandler(this, mAuthInfo);
		mSsoHandler.authorize(new AuthListener());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
}
