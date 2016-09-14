package com.github.mzule.androidweekly.ui.activity;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.mzule.androidweekly.R;
import com.github.mzule.androidweekly.api.ApiCallback;
import com.github.mzule.androidweekly.api.ArticleApi;
import com.github.mzule.androidweekly.entity.Article;
import com.github.mzule.androidweekly.entity.Issue;
import com.github.mzule.androidweekly.ui.adapter.ArticleAdapter;
import com.github.mzule.androidweekly.ui.adapter.SlideAdapter;
import com.github.mzule.androidweekly.ui.view.ProgressView;
import com.github.mzule.androidweekly.webview.WebViewHelper;
import com.github.mzule.layoutannotation.Layout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;


@Layout(R.layout.activity_main)
public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.progressView)
    ProgressView progressView;
    @Bind(R.id.slideListView)
    ListView slideListView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private ArticleAdapter adapter;
    private SlideAdapter slideAdapter;
    private List<Issue> issues;
    private ArticleApi articleApi;
    private Issue currentIssue;

    @OnItemClick(R.id.slideListView)
    void onSlideItemClick(AdapterView<?> parent, View view, int position, long id) {
        Issue issue = (Issue) parent.getAdapter().getItem(position);
        active(issue);
        slideAdapter.notifyDataSetChanged();
        currentIssue = issue;
        sendArticleListRequest(issue.getUrl());

        drawerLayout.closeDrawers();
        progressView.start();
    }

    @OnItemClick(R.id.listView)
    void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object item = parent.getAdapter().getItem(position);
        if (item instanceof Article) {
            //startActivity(ArticleActivity.makeIntent(this, (Article) item));
            WebViewHelper.openArticle(this,(Article) item
            );
        }
    }

    @OnClick(R.id.slideMenuButton)
    void onSlideMenuClick() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.favoriteButton)
    void onFavoriteClick() {
        startActivity(FavoriteActivity.makeIntent(this));
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.searchButton)
    void onSearchClicK() {
        startActivity(SearchActivity.makeIntent(this));
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.aboutButton)
    void onAboutClick() {
        startActivity(AboutActivity.makeIntent(this));
        drawerLayout.closeDrawers();
    }

    @Override
    protected void afterBind() {
        articleApi = new ArticleApi();

        adapter = new ArticleAdapter(this);
        listView.setAdapter(adapter);

        slideAdapter = new SlideAdapter(this);
        slideListView.setAdapter(slideAdapter);

        sendArticleListRequest(null);
        sendIssueListRequest();
    }

    private void active(Issue issue) {
        for (Issue i : issues) {
            i.setActive(i == issue);
        }
    }

    private void sendArticleListRequest(String issue) {
        articleApi.getPage(issue, new ApiCallback<List<Object>>() {
            @Override
            public void onSuccess(List<Object> data, boolean fromCache) {
                stopRefresh();
                adapter.clear();
                adapter.addAndNotify(data);
                listView.setSelection(0);
                progressView.stop();
            }

            @Override
            public void onFailure(Exception e) {
                stopRefresh();
                progressView.stop();
            }
        });
    }

    private void stopRefresh() {
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void sendIssueListRequest() {
        articleApi.getArchive(new ApiCallback<List<Issue>>() {
            @Override
            public void onSuccess(List<Issue> data, boolean fromCache) {
                issues = data;
                slideAdapter.clear();
                slideAdapter.addAndNotify(issues);
                active(issues.get(0));
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);

        swipeRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_colorscheme_first,
                R.color.swiperefresh_colorscheme_secondary,
                R.color.swiperefresh_colorscheme_third
        );
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        if(currentIssue == null){
            sendArticleListRequest(null);
        }else{
            sendArticleListRequest(currentIssue.getUrl());
        }
    }


}
