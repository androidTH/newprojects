package com.d6zone.android.app.widget;

import com.d6zone.android.app.activities.MyInfoActivity;
import com.d6zone.android.app.activities.PublishFindDateActivity;
import com.d6zone.android.app.activities.ReleaseNewTrendsActivity;
import com.d6zone.android.app.activities.UserInfoActivity;
import com.d6zone.android.app.activities.VoiceChatCreateActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * author : jinjiarui
 * time   : 2019/09/05
 * desc   :
 * version:
 */
public class ObserverManager extends Observable {
    private static ObserverManager observerManager;
    private List<Observer> mListObservers = new ArrayList<>();

    public static ObserverManager getInstance() {
        if (null == observerManager) {
            synchronized (ObserverManager.class) {
                if (null == observerManager) {
                    observerManager = new ObserverManager();
                }
            }
        }
        return observerManager;
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
        mListObservers.add(o);
    }

    @Override
    public synchronized void deleteObserver(Observer o) {
        super.deleteObserver(o);
        mListObservers.add(o);
    }

    @Override
    public void notifyObservers(Object arg) {
        super.notifyObservers(arg);
        for(Observer observer : mListObservers){
            if(observer instanceof ReleaseNewTrendsActivity){
                observer.update(this,arg);
            }else if(observer instanceof PublishFindDateActivity){
                observer.update(this,arg);
            }else if(observer instanceof UserInfoActivity){
                observer.update(this,arg);
            }else if(observer instanceof MyInfoActivity){
                observer.update(this,arg);
            }else if(observer instanceof VoiceChatCreateActivity){
                observer.update(this,arg);
            }
        }
    }
}
