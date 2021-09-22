package com.d6.android.app.models;

import java.util.ArrayList;
import java.util.List;

/**
 * author : jinjiarui
 * time   : 2021/09/21
 * desc   :
 * version:
 */
public class IPList {
    private List<IPBean> domain;

    public List<IPBean> getDomain() {
        if (domain == null) {
            return new ArrayList<>();
        }
        return domain;
    }

    public void setDomain(List<IPBean> domain) {
        this.domain = domain;
    }
}
