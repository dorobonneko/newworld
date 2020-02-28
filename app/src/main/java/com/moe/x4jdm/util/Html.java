package com.moe.x4jdm.util;
import android.text.Spanned;
import android.text.SpannableStringBuilder;
import android.text.style.URLSpan;
import android.text.style.ClickableSpan;
import android.view.View;

public class Html
{
	public static CharSequence getClickableHtml(Spanned html,final OnClickListener l) {
        Spanned spannedHtml = html;
        SpannableStringBuilder clickableHtmlBuilder = new SpannableStringBuilder(spannedHtml);
        URLSpan[] urls = clickableHtmlBuilder.getSpans(0, spannedHtml.length(), URLSpan.class);
        for(final URLSpan span : urls) {
			final URLSpan us=new URLSpan(span.getURL()){
				@Override
				public void onClick(View v){
					l.OnClick(span.getURL());
				};
			};
            clickableHtmlBuilder.setSpan(us,clickableHtmlBuilder.getSpanStart(span),clickableHtmlBuilder.getSpanEnd(span),clickableHtmlBuilder.getSpanFlags(span));
			clickableHtmlBuilder.removeSpan(span);
        }
        return clickableHtmlBuilder;
 }
 public interface OnClickListener{
	 void OnClick(String url);
 }
}
