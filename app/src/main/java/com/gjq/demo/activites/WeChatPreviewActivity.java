package com.gjq.demo.activites;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.gjq.demo.R;
import com.gjq.demo.adapter.PreviewTestAdapter;
import com.gjq.demo.beans.ImageFolder;
import com.gjq.demo.commom.MediaItemLoader;
import com.gjq.demo.commom.MediaType;
import com.gjq.demo.commom.RefreshLayout;
import com.gjq.demo.fragments.PreviewFragment;

import java.util.ArrayList;

public class WeChatPreviewActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<ArrayList<ImageFolder>> {
    private RefreshLayout refreshLayout;
    private PreviewTestAdapter mAdapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, WeChatPreviewActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_chat_preview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        refreshLayout = findViewById(R.id.preview_rl_content);
        refreshLayout.setAdapterAndLoadMoreListener(mAdapter = new PreviewTestAdapter(null)
                , () -> refreshLayout.finishRefresh());
        refreshLayout.setOnItemClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                PreviewFragment
                        .newInstance(((PreviewTestAdapter) adapter).getData().get(position))
                        .show(getSupportFragmentManager(), view);
            }

            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<ArrayList<ImageFolder>> onCreateLoader(int id, Bundle args) {
        return new MediaItemLoader(this, MediaType.MEDIA_VIDEO, null);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ImageFolder>> loader, ArrayList<ImageFolder> data) {
        if (data != null && !data.isEmpty()) {
            mAdapter.setNewData(data.get(0).images);
            mAdapter.disableLoadMoreIfNotFullPage();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ImageFolder>> loader) {

    }
}
