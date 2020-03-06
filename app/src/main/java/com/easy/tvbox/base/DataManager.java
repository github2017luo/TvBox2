package com.easy.tvbox.base;

import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.DownFile;
import com.easy.tvbox.bean.MyObjectBox;
import com.easy.tvbox.bean.PlayProgress;

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
    public Box<PlayProgress> playProgressBox;

    public void init(App app) {
        this.boxStore = MyObjectBox.builder().androidContext(app).build();
        accountBox = boxStore.boxFor(Account.class);
        downFileBox = boxStore.boxFor(DownFile.class);
        playProgressBox = boxStore.boxFor(PlayProgress.class);
    }

    public void updateProgress(List<PlayProgress> list) {
        playProgressBox.removeAll();
        if (!list.isEmpty()) {
            playProgressBox.put(list);
        }
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
            playProgressBox.removeAll();
        }
    }

    public List<PlayProgress> queryPlayProgress() {
        return playProgressBox.query().build().find();
    }

    public void updateAccount(Account account) {
        if (account != null) {
            accountBox.put(account);
        }
    }

    public void deleteAccount() {
        accountBox.removeAll();
        playProgressBox.removeAll();
    }
}
