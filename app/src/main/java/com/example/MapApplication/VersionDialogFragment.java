/**
 *
 */
package com.example.MapApplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.DialogFragment;

public class VersionDialogFragment extends DialogFragment
implements OnClickListener{

	@Override
	public Dialog onCreateDialog(android.os.Bundle savedInstanceState)
	{
		Context context = getContext();
		PackageManager pm = context.getPackageManager();
		String message = "Version 0";
		try {
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			message = pi.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return new AlertDialog.Builder(getActivity())
		    .setTitle(R.string.action_version)
		    .setMessage(message)
		    .setPositiveButton("OK", this)
		    .create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		return;
	}


}
