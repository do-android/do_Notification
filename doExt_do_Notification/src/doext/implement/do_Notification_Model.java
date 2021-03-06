package doext.implement;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import core.DoServiceContainer;
import core.helper.DoJsonHelper;
import core.helper.DoUIModuleHelper;
import core.interfaces.DoIPage;
import core.interfaces.DoIScriptEngine;
import core.object.DoInvokeResult;
import core.object.DoSingletonModule;
import doext.define.do_Notification_IMethod;

/**
 * 自定义扩展SM组件Model实现，继承DoSingletonModule抽象类，并实现Do_Notification_IMethod接口方法；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象； 获取DoInvokeResult对象方式new
 * DoInvokeResult(this.model.getUniqueKey());
 */
public class do_Notification_Model extends DoSingletonModule implements do_Notification_IMethod {

	private Toast mToast;

	public do_Notification_Model() throws Exception {
		super();
	}

	/**
	 * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V）
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public boolean invokeSyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		if ("toast".equals(_methodName)) {
			this.toast(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		return super.invokeSyncMethod(_methodName, _dictParas, _scriptEngine, _invokeResult);
	}

	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用， 可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @throws Exception
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V）
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名 #如何执行异步方法回调？可以通过如下方法：
	 *                    _scriptEngine.callback(_callbackFuncName,
	 *                    _invokeResult);
	 *                    参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	 *                    获取DoInvokeResult对象方式new
	 *                    DoInvokeResult(this.model.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {

		if ("alert".equals(_methodName)) {
			alert(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		}
		if ("confirm".equals(_methodName)) {
			this.confirm(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		}
		return super.invokeAsyncMethod(_methodName, _dictParas, _scriptEngine, _callbackFuncName);
	}

	/**
	 * 弹出alert窗口；
	 * 
	 * @throws Exception
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void alert(JSONObject _dictParas, final DoIScriptEngine _scriptEngine, final String _callbackFuncName) throws Exception {
		String _title = DoJsonHelper.getString(_dictParas, "title", "");
		String _content = DoJsonHelper.getString(_dictParas, "text", "");
		String _buttontext = DoJsonHelper.getString(_dictParas, "buttontext", "确定");
		final Activity _activity = (Activity) DoServiceContainer.getPageViewFactory().getAppContext();

		final AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
		builder.setMessage(_content).setTitle(_title).setCancelable(false).setPositiveButton(_buttontext.length() > 0 ? _buttontext : "确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				_scriptEngine.callback(_callbackFuncName, new DoInvokeResult(getUniqueKey()));
			}
		});
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!_activity.isFinishing()) {
					AlertDialog alert = builder.create();
					alert.show();
				}
			}
		});
	}

	/**
	 * 弹出confirm窗口；
	 * 
	 * @throws Exception
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void confirm(JSONObject _dictParas, final DoIScriptEngine _scriptEngine, final String _callbackFuncName) throws Exception {
		String _title = DoJsonHelper.getString(_dictParas, "title", "");
		String _content = DoJsonHelper.getString(_dictParas, "text", "");
		String _button1text = DoJsonHelper.getString(_dictParas, "button1text", "确定");
		String _button2text = DoJsonHelper.getString(_dictParas, "button2text", "取消");
		Activity _activity = (Activity) DoServiceContainer.getPageViewFactory().getAppContext();
		final AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
		final DoInvokeResult _invokeResult = new DoInvokeResult(getUniqueKey());
		builder.setMessage(_content).setTitle(_title).setCancelable(false).setPositiveButton(_button1text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					_invokeResult.setResultInteger(1);
					_scriptEngine.callback(_callbackFuncName, _invokeResult);
				} catch (Exception e) {
					throw new RuntimeException("confirm", e);
				}
			}
		}).setNegativeButton(_button2text, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				try {
					_invokeResult.setResultInteger(2);
					_scriptEngine.callback(_callbackFuncName, _invokeResult);
				} catch (Exception e) {
					throw new RuntimeException("confirm", e);
				}
			}
		});
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}

	/**
	 * 弹出toast窗口；
	 * 
	 * @throws Exception
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void toast(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		final String _text = DoJsonHelper.getString(_dictParas, "text", "");
		final DoIPage _page = _scriptEngine.getCurrentPage();
		final int _x = DoJsonHelper.getInt(_dictParas, "x", -1);
		final int _y = DoJsonHelper.getInt(_dictParas, "y", -1);
		final Activity _activity = (Activity) DoServiceContainer.getPageViewFactory().getAppContext();
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mToast != null) {
					mToast.cancel();
				}
				mToast = Toast.makeText(_activity, _text, android.widget.Toast.LENGTH_SHORT);
				if (_page != null && (_x >= 0 || _y >= 0)) {
					double _xZoom = _page.getRootView().getXZoom();
					double _yZoom = _page.getRootView().getYZoom();
					double _realX = _x * _xZoom;
					double _realY = _y * _yZoom;

					View view = mToast.getView();
					if (view != null) {
						DoUIModuleHelper.measureView(view);
					}

					if (_x >= 0 && _y < 0) { // 只设置了x坐标 y居中
						int _viewHeight = view.getMeasuredHeight();
						_realY = (DoServiceContainer.getGlobal().getScreenHeight() - _viewHeight) / 2;
					}

					if (_y >= 0 && _x < 0) { // 只设置了y坐标 x居中
						int _viewWidth = view.getMeasuredWidth();
						_realX = (DoServiceContainer.getGlobal().getScreenWidth() - _viewWidth) / 2;
					}

					mToast.setGravity(Gravity.LEFT | Gravity.TOP, (int) _realX, (int) _realY);
				}
				mToast.show();
			}
		});
	}
}
