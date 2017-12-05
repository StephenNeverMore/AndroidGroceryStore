package com.stephen.aty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.stephen.R;
import com.stephen.views.AutoScrollImageView;

public class AutoScrollActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ScrollAdapter scrollAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_scroll);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        scrollAdapter = new ScrollAdapter();
        recyclerView.setAdapter(scrollAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int fPos = linearLayoutManager.findFirstVisibleItemPosition();
                int lPos = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                for (int i = fPos; i <= lPos; i++) {
                    View view = linearLayoutManager.findViewByPosition(i);
                    final AutoScrollImageView scrollView = (AutoScrollImageView) view.findViewById(R.id.scroll_image_view);
                    scrollView.setDy(linearLayoutManager.getHeight() - view.getTop());
                }
            }
        });
    }


    private class ScrollAdapter extends RecyclerView.Adapter<ScrollHolder> {

        @Override
        public ScrollHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scroll, parent, false);
            return new ScrollHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ScrollHolder holder, int position) {
            if (position > 0 && position % 5 == 0) {
                holder.textView.setVisibility(View.GONE);
                holder.imageView.setVisibility(View.VISIBLE);
            } else {
                holder.textView.setVisibility(View.VISIBLE);
                holder.imageView.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }

    private class ScrollHolder extends RecyclerView.ViewHolder {
        private AutoScrollImageView imageView;
        private TextView textView;

        public ScrollHolder(View itemView) {
            super(itemView);
            imageView = (AutoScrollImageView) itemView.findViewById(R.id.scroll_image_view);
            textView = (TextView) itemView.findViewById(R.id.scroll_text_view);
        }
    }
}
