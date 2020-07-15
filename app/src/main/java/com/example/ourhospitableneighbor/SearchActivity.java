package com.example.ourhospitableneighbor;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourhospitableneighbor.helper.SizeConverter;
import com.example.ourhospitableneighbor.model.Post;
import com.example.ourhospitableneighbor.view.PanelItemView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

public class SearchActivity extends AppCompatActivity {
    private Adapter adapter = new Adapter();

    private Subject<String> querySubject = PublishSubject.create();
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.SearchActivity_Toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        EditText searchEditText = findViewById(R.id.SearchActivity_SearchEditText);
        searchEditText.requestFocus();
        searchEditText.addTextChangedListener(searchTextWatcher);

        RecyclerView recyclerView = findViewById(R.id.SearchActivity_RecyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Reactive bindings
        Observable<String> queryDebounced = querySubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .map(String::toLowerCase)
                .distinctUntilChanged();
        disposables.add(
                Observable.combineLatest(queryDebounced, PostService.getInstance().getAllPostsObservable(), this::filterPosts)
                        .subscribe(adapter)
        );
    }

    @Override
    protected void onDestroy() {
        disposables.dispose();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Disable animation when going back to parent activity
        overridePendingTransition(0, 0);
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            querySubject.onNext(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private List<Post> filterPosts(String query, List<Post> posts) {
        List<Post> result = new ArrayList<>();
        for (Post p : posts) {
            if (p.getPostTitle().toLowerCase().contains(query) || p.getAddress().toLowerCase().contains(query)) {
                result.add(p);
            }
        }
        return result;
    }

    // RecyclerView classes
    private static class ViewHolder extends RecyclerView.ViewHolder {
        private PanelItemView panelItemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            panelItemView = (PanelItemView) itemView;
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> implements Consumer<List<Post>> {
        List<Post> posts;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            PanelItemView view = new PanelItemView(SearchActivity.this);
            int paddingH = SizeConverter.fromDpToPx(getResources(), 11);
            int paddingV = SizeConverter.fromDpToPx(getResources(), 10);
            view.setConstraintLayoutPadding(paddingH, paddingV, paddingH, paddingV);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.panelItemView.setPost(posts.get(position));
        }

        @Override
        public int getItemCount() {
            return posts != null ? posts.size() : 0;
        }


        @Override
        public void accept(List<Post> posts) {
            this.posts = posts;
            runOnUiThread(this::notifyDataSetChanged);
        }
    }
}