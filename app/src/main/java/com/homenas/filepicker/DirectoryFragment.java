package com.homenas.filepicker;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.homenas.filepicker.adapter.DirectoryAdapter;
import com.homenas.filepicker.filter.CompositeFilter;
import com.homenas.filepicker.utils.FileUtils;

import java.io.File;

import static com.homenas.filepicker.Constant.ARG_FILE_PATH;
import static com.homenas.filepicker.Constant.ARG_FILTER;
import static com.homenas.filepicker.R.id.recyclerView;

/**
 * Created by engss on 24/10/2017.
 */

public class DirectoryFragment extends Fragment {

    private FileClickListener mFileClickListener;
    private DirectoryAdapter mDirectoryAdapter;
    private RecyclerView mRecyclerView;
    private CompositeFilter mFilter;
    private String mPath;

    interface FileClickListener {
        void onFileClicked(File clickedFile);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFileClickListener = (FileClickListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.directory_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(recyclerView);
        initArgs();
        initFilesList();
    }

    public static DirectoryFragment getInstance(String path, CompositeFilter filter) {
        DirectoryFragment instance = new DirectoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILE_PATH, path);
        args.putSerializable(ARG_FILTER, filter);
        instance.setArguments(args);
        return instance;
    }

    private void initFilesList() {
        mDirectoryAdapter = new DirectoryAdapter(getActivity(), FileUtils.getFileListByDirPath(mPath, mFilter));
        mDirectoryAdapter.setOnItemClickListener(new DirectoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mFileClickListener != null) {
                    mFileClickListener.onFileClicked(mDirectoryAdapter.getModel(position));
                }
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mDirectoryAdapter);
    }

    @SuppressWarnings("unchecked")
    private void initArgs() {
        if (getArguments().getString(ARG_FILE_PATH) != null) {
            mPath = getArguments().getString(ARG_FILE_PATH);
        }
        mFilter = (CompositeFilter) getArguments().getSerializable(ARG_FILTER);
    }
}
