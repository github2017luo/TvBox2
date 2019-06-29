package com.easy.tvbox.base;

import android.content.Context;

import com.easy.tvbox.bean.Error;
import com.easy.tvbox.bean.ErrorRespond;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.http.AppRequestStore;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter<V> extends BaseLifecycleObserver {
    protected AppRequestStore requestStore;
    protected V mView;
    protected Context context;
    protected CompositeDisposable mCompositeSubscription = new CompositeDisposable();

    public BasePresenter() {
        this.requestStore = AppRequestStore.getInstance();
    }

    /**
     * 初始化必须调用
     */
    public void init(V v) {
        this.mView = v;
        this.onAttached();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public abstract void onAttached();

    public void onDetached() {
        mCompositeSubscription.dispose();
    }


    public Respond getThrowableRespond(Throwable throwable) {
        ErrorRespond respond = new ErrorRespond();
        if (throwable != null) {
            Error error = new Error();
            error.setCode(404);
            error.setMessage(throwable.getMessage());
            respond.setError(error);
        }
        return respond;
    }
}
