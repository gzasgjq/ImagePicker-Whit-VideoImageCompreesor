package com.gjq.demo.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gjq.demo.R;
import com.gjq.demo.fragments.dummy.DummyContent;
import com.gjq.demo.fragments.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 */
public class ItemFragment extends Fragment {

    private int pos;

    public ItemFragment() {
    }

    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int position) {
        ItemFragment fragment = new ItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pos = getArguments().getInt("pos", -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        int color = 0;
        switch (pos){
            case 0:
                color = Color.parseColor("#EAF048");
                break;
            case 1:
                color = Color.parseColor("#9FF048");
                break;
            case 2:
                color = Color.parseColor("#2A5200");
                break;
            default:
                break;
        }
        view.setBackgroundColor(color);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(DummyContent.ITEMS));
        }
        return view;
    }
}
