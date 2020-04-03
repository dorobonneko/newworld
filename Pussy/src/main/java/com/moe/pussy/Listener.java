package com.moe.pussy;
import android.graphics.drawable.Drawable;

public interface Listener
{
	void onPlaceHolder(Drawable d);
	void onSuccess(Drawable d);
	void onError(Drawable d);
}
