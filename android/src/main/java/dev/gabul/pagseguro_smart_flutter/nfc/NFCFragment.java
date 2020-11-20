package dev.gabul.pagseguro_smart_flutter.nfc;

import java.util.ArrayList;
import java.util.List;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNFCResult;

import io.flutter.plugin.common.MethodChannel;

public class NFCFragment implements NFCContract {

    final MethodChannel channel;

    public NFCFragment(MethodChannel channel) {
        this.channel = channel;
    }

    //METHODS
    private static final String ON_ERROR = "onError";
    private static final String ON_SUCCESS = "showSuccess";
    private static final String ON_SUCCESS_WRITE = "showSuccessWrite";
    private static final String ON_SUCCESS_WRITE_DIRECTLY = "showSuccessWriteDirectly";
    private static final String ON_SUCCESS_RE_WRITE = "showSuccessReWrite";
    private static final String ON_SUCCESS_FORMAT = "showSuccessFormat";
    private static final String ON_SUCCESS_DEBIT_NFC = "showSuccessDebitNfc";
    private static final String ON_READ_CARD = "onReadCard";
    private static final String ON_WRITE_CARD = "onWriteCard";
    private static final String ON_ABORT = "onAbort";


    @Override
    public void showSuccess(NFCData result) {

        final List<String> results = new ArrayList<>();
        results.add(result.getName());
        results.add(result.getCpf());

        this.channel.invokeMethod(ON_SUCCESS, results);
    }



    @Override
    public void showSuccessStartDirectly(Object result) {
        this.channel.invokeMethod(ON_SUCCESS_WRITE_DIRECTLY, result);
    }

    @Override
    public void showSuccessStopDirectly(Object result) {
        this.channel.invokeMethod(ON_SUCCESS_WRITE_DIRECTLY, result);
    }

    @Override
    public void showSuccessAuthDirectly(Object result) {
        this.channel.invokeMethod(ON_SUCCESS_WRITE_DIRECTLY, result);
    }

    @Override
    public void showSuccessWrite(int result) {
        this.channel.invokeMethod(ON_SUCCESS_WRITE, result);
    }

    @Override
    public void showSuccessWriteDirectly(Object result) {
        this.channel.invokeMethod(ON_SUCCESS_WRITE_DIRECTLY, result);
    }

    @Override
    public void showSuccessReadDirectly(Object result) {

    }

    @Override
    public void showSuccessReWriteDirectly(Object result) {

    }

    @Override
    public void showSuccessFormatDirectly(Object result) {

    }

    @Override
    public void showSuccessReWrite(PlugPagNFCResult result) {
        this.channel.invokeMethod(ON_SUCCESS_RE_WRITE, result.getResult());
    }

    @Override
    public void showSuccessFormat(PlugPagNFCResult result) {
        this.channel.invokeMethod(ON_SUCCESS_FORMAT, result.getResult());
    }

    @Override
    public void showSuccessDebitNfc(PlugPagNFCResult result) {
        this.channel.invokeMethod(ON_SUCCESS_DEBIT_NFC, result.getResult());
    }

    @Override
    public void showError(String message) {
        this.channel.invokeMethod(ON_ERROR,message);

    }

    @Override
    public void onReadCard() {
        this.channel.invokeMethod(ON_READ_CARD,true);

    }

    @Override
    public void onWriteCard() {
        this.channel.invokeMethod(ON_WRITE_CARD,true);

    }

    @Override
    public void onAbort() {
        this.channel.invokeMethod(ON_ABORT,true);

    }
}