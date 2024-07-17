package b4a.example;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblmasaadaptera = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtmasaadaptera = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblmasakonopca = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtmasakonopca = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblmasasonde = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtmasasonde = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblzeljeniuzgon = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtzeljeniuzgon = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnizracunaj = null;
public b4a.example.doubletaptoclose _d = null;
public b4a.example.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 35;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 36;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 37;BA.debugLine="D.Initialize (\"Tap BACK again to exit\",Me,\"Before";
mostCurrent._d._initialize /*String*/ (processBA,"Tap BACK again to exit",main.getObject(),"BeforeClose",(int) (2),(int) (2000));
 //BA.debugLineNum = 39;BA.debugLine="lblMasaAdaptera.Initialize(\"\")";
mostCurrent._lblmasaadaptera.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 40;BA.debugLine="lblMasaAdaptera.Text = \"Weight adapter (g):\"";
mostCurrent._lblmasaadaptera.setText(BA.ObjectToCharSequence("Weight adapter (g):"));
 //BA.debugLineNum = 41;BA.debugLine="Activity.AddView(lblMasaAdaptera, 10dip, 10dip, 2";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lblmasaadaptera.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (200)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 43;BA.debugLine="txtMasaAdaptera.Initialize(\"\")";
mostCurrent._txtmasaadaptera.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 44;BA.debugLine="txtMasaAdaptera.InputType = txtMasaAdaptera.INPUT";
mostCurrent._txtmasaadaptera.setInputType(mostCurrent._txtmasaadaptera.INPUT_TYPE_NUMBERS);
 //BA.debugLineNum = 45;BA.debugLine="Activity.AddView(txtMasaAdaptera, 210dip, 10dip,";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._txtmasaadaptera.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (210)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 47;BA.debugLine="lblMasaKonopca.Initialize(\"\")";
mostCurrent._lblmasakonopca.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 48;BA.debugLine="lblMasaKonopca.Text = \"Weight rope (g):\"";
mostCurrent._lblmasakonopca.setText(BA.ObjectToCharSequence("Weight rope (g):"));
 //BA.debugLineNum = 49;BA.debugLine="Activity.AddView(lblMasaKonopca, 10dip, 70dip, 20";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lblmasakonopca.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (70)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (200)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 51;BA.debugLine="txtMasaKonopca.Initialize(\"\")";
mostCurrent._txtmasakonopca.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 52;BA.debugLine="txtMasaKonopca.InputType = txtMasaKonopca.INPUT_T";
mostCurrent._txtmasakonopca.setInputType(mostCurrent._txtmasakonopca.INPUT_TYPE_NUMBERS);
 //BA.debugLineNum = 53;BA.debugLine="Activity.AddView(txtMasaKonopca, 210dip, 70dip, 1";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._txtmasakonopca.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (210)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (70)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 55;BA.debugLine="lblMasaSonde.Initialize(\"\")";
mostCurrent._lblmasasonde.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 56;BA.debugLine="lblMasaSonde.Text = \"Weight sonde (g):\"";
mostCurrent._lblmasasonde.setText(BA.ObjectToCharSequence("Weight sonde (g):"));
 //BA.debugLineNum = 57;BA.debugLine="Activity.AddView(lblMasaSonde, 10dip, 130dip, 200";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lblmasasonde.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (130)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (200)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 59;BA.debugLine="txtMasaSonde.Initialize(\"\")";
mostCurrent._txtmasasonde.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 60;BA.debugLine="txtMasaSonde.InputType = txtMasaSonde.INPUT_TYPE_";
mostCurrent._txtmasasonde.setInputType(mostCurrent._txtmasasonde.INPUT_TYPE_NUMBERS);
 //BA.debugLineNum = 61;BA.debugLine="Activity.AddView(txtMasaSonde, 210dip, 130dip, 10";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._txtmasasonde.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (210)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (130)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 63;BA.debugLine="lblZeljeniUzgon.Initialize(\"\")";
mostCurrent._lblzeljeniuzgon.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 64;BA.debugLine="lblZeljeniUzgon.Text = \"Target lift (g):\"";
mostCurrent._lblzeljeniuzgon.setText(BA.ObjectToCharSequence("Target lift (g):"));
 //BA.debugLineNum = 65;BA.debugLine="Activity.AddView(lblZeljeniUzgon, 10dip, 190dip,";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lblzeljeniuzgon.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (190)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (200)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 67;BA.debugLine="txtZeljeniUzgon.Initialize(\"\")";
mostCurrent._txtzeljeniuzgon.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 68;BA.debugLine="txtZeljeniUzgon.InputType = txtZeljeniUzgon.INPUT";
mostCurrent._txtzeljeniuzgon.setInputType(mostCurrent._txtzeljeniuzgon.INPUT_TYPE_NUMBERS);
 //BA.debugLineNum = 69;BA.debugLine="Activity.AddView(txtZeljeniUzgon, 210dip, 190dip,";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._txtzeljeniuzgon.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (210)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (190)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 71;BA.debugLine="btnIzracunaj.Initialize(\"btnIzracunaj\")";
mostCurrent._btnizracunaj.Initialize(mostCurrent.activityBA,"btnIzracunaj");
 //BA.debugLineNum = 72;BA.debugLine="btnIzracunaj.Color = Colors.Yellow";
mostCurrent._btnizracunaj.setColor(anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 //BA.debugLineNum = 73;BA.debugLine="btnIzracunaj.Text = \"CALCULATE LIFT WITH ADAPTER\"";
mostCurrent._btnizracunaj.setText(BA.ObjectToCharSequence("CALCULATE LIFT WITH ADAPTER"));
 //BA.debugLineNum = 74;BA.debugLine="Activity.AddView(btnIzracunaj, 10dip, 250dip, 300";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._btnizracunaj.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (250)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (70)));
 //BA.debugLineNum = 76;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 101;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 102;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 103;BA.debugLine="D.TapToClose";
mostCurrent._d._taptoclose /*String*/ ();
 //BA.debugLineNum = 104;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 106;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 82;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 84;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 78;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 80;BA.debugLine="End Sub";
return "";
}
public static String  _btnizracunaj_click() throws Exception{
float _masa_adaptera = 0f;
float _masa_konopca = 0f;
float _masa_sonde = 0f;
float _zeljeni_uzgon = 0f;
float _uzgon_na_vagi = 0f;
 //BA.debugLineNum = 86;BA.debugLine="Sub btnIzracunaj_Click";
 //BA.debugLineNum = 87;BA.debugLine="Try";
try { //BA.debugLineNum = 88;BA.debugLine="Dim masa_adaptera As Float = txtMasaAdaptera.Tex";
_masa_adaptera = (float)(Double.parseDouble(mostCurrent._txtmasaadaptera.getText()));
 //BA.debugLineNum = 89;BA.debugLine="Dim masa_konopca As Float = txtMasaKonopca.Text";
_masa_konopca = (float)(Double.parseDouble(mostCurrent._txtmasakonopca.getText()));
 //BA.debugLineNum = 90;BA.debugLine="Dim masa_sonde As Float = txtMasaSonde.Text";
_masa_sonde = (float)(Double.parseDouble(mostCurrent._txtmasasonde.getText()));
 //BA.debugLineNum = 91;BA.debugLine="Dim zeljeni_uzgon As Float = txtZeljeniUzgon.Tex";
_zeljeni_uzgon = (float)(Double.parseDouble(mostCurrent._txtzeljeniuzgon.getText()));
 //BA.debugLineNum = 93;BA.debugLine="Dim uzgon_na_vagi As Float = (masa_sonde + masa_";
_uzgon_na_vagi = (float) ((_masa_sonde+_masa_konopca+_zeljeni_uzgon)-_masa_adaptera);
 //BA.debugLineNum = 95;BA.debugLine="Msgbox(\"Target lift on scale: \" & uzgon_na_vagi";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Target lift on scale: "+BA.NumberToString(_uzgon_na_vagi)+" gram"),BA.ObjectToCharSequence("Result"),mostCurrent.activityBA);
 } 
       catch (Exception e9) {
			processBA.setLastException(e9); //BA.debugLineNum = 97;BA.debugLine="Msgbox(\"Please enter nummeric value\", \"Error\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Please enter nummeric value"),BA.ObjectToCharSequence("Error"),mostCurrent.activityBA);
 };
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 23;BA.debugLine="Private lblMasaAdaptera As Label";
mostCurrent._lblmasaadaptera = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private txtMasaAdaptera As EditText";
mostCurrent._txtmasaadaptera = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private lblMasaKonopca As Label";
mostCurrent._lblmasakonopca = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private txtMasaKonopca As EditText";
mostCurrent._txtmasakonopca = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private lblMasaSonde As Label";
mostCurrent._lblmasasonde = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private txtMasaSonde As EditText";
mostCurrent._txtmasasonde = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private lblZeljeniUzgon As Label";
mostCurrent._lblzeljeniuzgon = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private txtZeljeniUzgon As EditText";
mostCurrent._txtzeljeniuzgon = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private btnIzracunaj As Button";
mostCurrent._btnizracunaj = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Dim D As DoubleTaptoClose";
mostCurrent._d = new b4a.example.doubletaptoclose();
 //BA.debugLineNum = 33;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return "";
}
}
