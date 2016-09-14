package com.github.mzule.androidweekly.webview;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;

import com.github.mzule.androidweekly.R;
import com.github.mzule.androidweekly.entity.Article;
import com.github.mzule.androidweekly.ui.activity.ArticleActivity;
import com.github.mzule.androidweekly.webview.chromium.CustomTabActivityHelper;

public class WebViewHelper {

    public static void openArticle(final Activity activity, final Article article) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder(null)
                .setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                .addMenuItem(activity.getString(R.string.action_share),PendingIntent.getActivity(activity,0,getShareIntent(article.getLink()),PendingIntent.FLAG_UPDATE_CURRENT))
                .setShowTitle(true)
                .build();
        CustomTabActivityHelper.openCustomTab(activity, customTabsIntent, Uri.parse(article.getLink()),
                new CustomTabActivityHelper.CustomTabFallback() {
                    @Override
                    public void openUri(Activity activity, Uri uri) {
                        activity.startActivity(ArticleActivity.makeIntent(activity,article));
                    }
                }
        );
        // */
    }

    private static Intent getShareIntent(String url){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("text/plain");
        return  sendIntent;
    }

}
