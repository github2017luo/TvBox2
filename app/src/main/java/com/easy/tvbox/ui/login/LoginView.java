package com.easy.tvbox.ui.login;

import com.easy.tvbox.base.BaseView;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.ImageCode;
import com.easy.tvbox.bean.PhoneCode;
import com.easy.tvbox.bean.Respond;

public interface LoginView extends BaseView {

    void sendMessageCallback(Respond<PhoneCode> respond);

    void loginCallback(Respond<Account> respond);

    void imageCodeCallback(Respond<ImageCode> respond);
}
