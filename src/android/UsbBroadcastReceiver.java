package it.oi.cordova.serial;

import org.apache.cordova.CallbackContext;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.List;

public class UsbBroadcastReceiver extends BroadcastReceiver {
	// logging tag
	private final String TAG = UsbBroadcastReceiver.class.getSimpleName();
	// usb permission tag name
	public static final String USB_PERMISSION ="it.oi.cordova.serial.USB_PERMISSION";
	// cordova callback context to notify the success/error to the cordova app
	private CallbackContext callbackContext;
	// cordova activity to use it to unregister this broadcast receiver
	private Activity activity;

	private UsbManager manager;
	private UsbDevice device;

	public UsbBroadcastReceiver(CallbackContext callbackContext, Activity activity)
	{
		this.callbackContext = callbackContext;
		this.activity = activity;
	}
	
	/**
	 * Custom broadcast receiver that will handle the cordova callback context
	 * @param callbackContext
	 * @param activity
	 */
	public UsbBroadcastReceiver(CallbackContext callbackContext, Activity activity, UsbManager manager, UsbDevice device)
	{
		this.callbackContext = callbackContext;
		this.activity = activity;
		this.manager = manager;
		this.device = device;
	}

	
	/**
	 * Handle permission answer
	 * @param context
	 * @param intent
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (USB_PERMISSION.equals(action)) {

			boolean hasPerm;
			if (this.manager != null)
			{
				hasPerm = manager.hasPermission(device);
			}
			else
			{
				// deal with the user answer about the permission
				hasPerm = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
			}
			Log.d(TAG, "hasPerm = " + hasPerm);
			if (hasPerm) {
				Log.d(TAG, "Permission to connect to the device was accepted!");
				callbackContext.success("Permission to connect to the device was accepted!");
			} 
			else {
				Log.d(TAG, "Permission to connect to the device was denied!");
				callbackContext.error("Permission to connect to the device was denied!");
			}
			// unregister the broadcast receiver since it's no longer needed
			activity.unregisterReceiver(this);
		}
	}	
}
