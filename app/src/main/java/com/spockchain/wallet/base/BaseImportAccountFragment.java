package com.spockchain.wallet.base;

import com.spockchain.wallet.ui.fragment.OnImportAccountSuccessListener;

public abstract class BaseImportAccountFragment extends BaseFragment {

    protected OnImportAccountSuccessListener listener;

    public void setOnImportAccountSuccessListener(OnImportAccountSuccessListener listener) {
        this.listener = listener;
    }

    protected void notifyImportAccountSuccess() {
        if (listener != null) {
            listener.onSuccess();
        }
    }

}
