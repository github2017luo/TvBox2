package com.easy.tvbox.ui.login;

import com.easy.tvbox.base.BaseView;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.AppVersion;
import com.easy.tvbox.bean.CheckLogin;
import com.easy.tvbox.bean.ImageCode;
import com.easy.tvbox.bean.Respond;

public interface LoginView extends BaseView {

    void loginCallback(Respond<Account> respond);

    void imageCodeCallback(Respond<ImageCode> respond);

    void checkLoginCallback(Respond<CheckLogin> respond);

    void checkUpdateCallback(Respond<AppVersion> respond);
}
