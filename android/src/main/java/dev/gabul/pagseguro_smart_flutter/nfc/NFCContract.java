package dev.gabul.pagseguro_smart_flutter.nfc;

import com.hannesdorfmann.mosby.mvp.MvpView;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNFCResult;

 public interface NFCContract  {

    void showSuccess(NFCData result);
    void showSuccessWrite(int result);

    void showSuccessStartDirectly(Object result);
    void showSuccessAuthDirectly(Object result);
    void showSuccessWriteDirectly(Object result);
    void showSuccessReadDirectly(Object result);
    void showSuccessReWriteDirectly(Object result);
    void showSuccessFormatDirectly(Object result);
    void showSuccessReWrite(PlugPagNFCResult result);
    void showSuccessStopDirectly(Object result);
    void showSuccessFormat(PlugPagNFCResult result);
    void showSuccessDebitNfc(PlugPagNFCResult result);
    void showError(String message);
    void onReadCard();
    void onWriteCard();
    void onAbort();
}