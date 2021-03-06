package com.chiului.android_mvvm_architecture.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chiului.android_mvvm_architecture.R;
import com.chiului.android_mvvm_architecture.lifecycler.BaseLifecycle;
import com.chiului.android_mvvm_architecture.utilities.ConfigsKt;
import com.chiului.android_mvvm_architecture.utilities.ToastUtil;

import org.jetbrains.annotations.NotNull;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

/**
 * 基础Activity$
 *
 * @author 神经大条蕾弟
 * @date 2020/07/20 22:29
 */
public abstract class BaseNavFragment extends Fragment {

    /**
     * 是否为导航图栈顶（最上层）（该参数为子类控制）
     */
    private boolean isTopStack = false;

    /**
     * 点击回退记录时间戳
     */
    private long mExitTime;

    private View mRootView;

    public boolean isTopStack() {
        return isTopStack;
    }

    public void setTopStack(boolean topStack) {
        isTopStack = topStack;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getLifecycle().addObserver(new BaseLifecycle());
        initBaseWidget();
        mRootView = initViewModel(inflater, container, savedInstanceState);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBackPressDispatcher();
        initView(view, savedInstanceState);
    }

    /**
     * 监听回退操作
     */
    private void initBackPressDispatcher() {
        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navUp();
            }
        });
    }

    /**
     * 返回处理
     */
    public void navUp() {
        if (isTopStack) {
            // 最上层直接关闭
            backExit();
        } else {
            // 由系统判断
            boolean navigateUp = findNavController(BaseNavFragment.this).navigateUp();
            if (!navigateUp) {
                // 没有上一层就关闭 Activity
                backExit();
            }
        }
    }

    /**
     * 关闭 Activity
     */
    private void backExit() {
        if (System.currentTimeMillis() - mExitTime > ConfigsKt.BACK_EXIT) {
            ToastUtil.INSTANCE.show(getActivity(), R.string.toast_back_exit, Toast.LENGTH_SHORT);
            mExitTime = System.currentTimeMillis();
        } else {
            getActivity().finish();
        }
    }

    /**
     * 初始化基类小工具
     * 在 onCreating()之前
     */
    private void initBaseWidget(){
        Bundle data = getArguments();
        if (data != null) {
            initBundle(data);
        }
    }

    /**
     * 初始化参数（可选）
     */
    public void initBundle(@NotNull Bundle bundle) {}

    /**
     * 初始化 ViewModel
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return  碎片根布局
     */
    public abstract View initViewModel(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    /**
     * 初始化 View
     */
    public abstract void initView(@NonNull View view, @Nullable Bundle savedInstanceState);

}
