package com.easy.tvbox.base;

import android.text.TextUtils;
import android.util.Log;

import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.DownFile;
import com.easy.tvbox.bean.DownFile_;
import com.easy.tvbox.bean.MyObjectBox;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;

public class DataManager {

    private static DataManager dataManager;

    public static synchronized DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    private DataManager() {
    }

    public BoxStore boxStore;
    public Box<Account> accountBox;
    public Box<DownFile> downFileBox;

    public void init(App app) {
        this.boxStore = MyObjectBox.builder().androidContext(app).build();
        accountBox = boxStore.boxFor(Account.class);
        downFileBox = boxStore.boxFor(DownFile.class);
    }

    public Account queryAccount() {
        return accountBox.query().build().findFirst();
    }

    public boolean isLogin() {
        Account account = queryAccount();
        return account != null;
    }

    public void login(Account account) {
        if (account != null) {
            accountBox.removeAll();
            accountBox.put(account);
        }
    }

    public void updateAccount(Account account) {
        if (account != null) {
            accountBox.put(account);
        }
    }

    public void deleteAccount() {
        accountBox.removeAll();
    }

    public DownFile isDownloaded(String downLoadPath) {
        if (downLoadPath != null) {
            DownFile downFile = downFileBox.query().equal(DownFile_.downLoadPath, downLoadPath).build().findFirst();
            return downFile;
        }
        return null;
    }

    public void removeAllDownLoad() {
        downFileBox.removeAll();
    }

    public void saveDownloadInfo(DownFile downFile) {
        if (downFile != null) {
            downFileBox.put(downFile);
        }
    }

    public DownFile getUnDownLoad() {
        return downFileBox.query().notEqual(DownFile_.progress, 100).build().findFirst();
    }

    public void updateDownInfo(String path) {
        DownFile downFile = downFileBox.query().equal(DownFile_.path, path).build().findFirst();
        if (downFile != null && downFile.getProgress() != 100) {
            downFile.setProgress(100);
            downFileBox.put(downFile);
            Log.d("Download", "下载进度已更新：" + downFile.toString());
        }
    }

    public String getDownloadPath(String url) {
        if (!TextUtils.isEmpty(url)) {
            DownFile downFile = downFileBox.query().equal(DownFile_.downLoadPath, url).build().findFirst();
            if (downFile != null && downFile.getProgress() == 100) {
                return downFile.getPath();
            }
        }
        return null;
    }

    /**
     * 获取所有已下载的内容
     *
     * @return
     */
    public List<DownFile> getDownloaded() {
        return downFileBox.query().equal(DownFile_.progress, 100).build().find();
    }

    public String getDownloadFilePath() {
        DownFile downFile = downFileBox.query().build().findFirst();
        if (downFile != null) {
            return downFile.getFilePath();
        }
        return null;
    }
}
