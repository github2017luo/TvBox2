package com.easy.tvbox.ui.phone;

import com.easy.tvbox.base.BaseView;
import com.easy.tvbox.bean.ImageCode;
import com.easy.tvbox.bean.PhoneCode;
import com.easy.tvbox.bean.Respond;

public interface UpdatePhoneView extends BaseView {

    void updatePhone(Respond respond);

    void validOldPhoneCallback(Respond respond);

    void imageCodeCallback(Respond<ImageCode> respond);

    void sendMessageCallback(Respond<PhoneCode> respond);
}
