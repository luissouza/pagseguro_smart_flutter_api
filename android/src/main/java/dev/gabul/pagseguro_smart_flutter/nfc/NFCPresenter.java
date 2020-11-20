package dev.gabul.pagseguro_smart_flutter.nfc;
import javax.inject.Inject;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNearFieldCardData;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagNFCAuth;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagTransactionResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagAppIdentification;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNearFieldCardData;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagSimpleNFCData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNFCResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagAbortResult;
import java.util.List;
import java.util.ArrayList;

import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagBeepData;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagLedData;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagNFCAuth;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.result.PlugPagNFCInfosResult;
import io.flutter.plugin.common.MethodChannel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagSimpleNFCData;

public class NFCPresenter  {

    private final NFCUseCase mUseCase;
    private final NFCFragment mFragment;
    private final PlugPag mPlugPag;

    private String idCaixa;
    private String idCarga;
    private String valor;
    private String nome;
    private String cpf;
    private String numeroTag;
    private String saldoAtual;
    private String celular;
    private String ativo;
    private String type;

    private NFCData nfcData = null;

    private Disposable mSubscribe;

    private Boolean isRetry = false;
    private RetryAction retryAction = null;

    @Inject
    public NFCPresenter(PlugPag plugPag, MethodChannel channel) {
        mUseCase = new NFCUseCase(plugPag);
        mPlugPag = plugPag;
        mFragment = new NFCFragment(channel);
    }

    public void readNFCCard() {
        this.type = "read";
        this.startNFCCardDirectly();
    }


    public void writeNFCCard(String idCaixa, String idCarga, String valor, String nome, String cpf, String numeroTag, String saldoAtual, String celular, String ativo) {
        this.type = "write";
        this.idCaixa = idCaixa;
        this.idCarga = idCarga;
        this.valor = valor;
        this.nome = nome;
        this.cpf = cpf;
        this.numeroTag = numeroTag;
        this.saldoAtual = saldoAtual;
        this.celular = celular;
        this.ativo = ativo;
        this.startNFCCardDirectly();
    }

    public void formatNFCCard() {
        dispose();
        this.type = "format";
        this.startNFCCardDirectly();
    }

//     public void readNFCCardDirectly(Object res) {
//        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, 1, MifareClassic.KEY_DEFAULT);
//
//        mSubscribe = mUseCase.readNFCCardDirectly(cardData)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(result -> this.stopNFCCardDirectlyRead(result),
//                        throwable ->mFragment.showError(throwable.getMessage()));
//
//    }


    public void readValue() {
        if (nfcData == null) nfcData = new NFCData();


        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, NFCConstants.VALUE_BLOCK, MifareClassic.KEY_DEFAULT);

        mSubscribe = mUseCase.readNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            nfcData.setValue(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                            readIdCashier();
                        },
                        throwable -> mFragment.showError(throwable.getMessage()));

    }

    /**
     * Step two
     *
     * Read Id Cashier
     *
     */
    public void readIdCashier() {
        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, NFCConstants.ID_CASHIER_BLOCK, MifareClassic.KEY_DEFAULT);

        mSubscribe = mUseCase.readNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            nfcData.setIdCashier(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                            readCpf();
                        },
                        throwable ->mFragment.showError(throwable.getMessage()));

    }

    /**
     * Step three
     *
     * Read CPF
     *
     */
    public void readCpf() {
        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, NFCConstants.CPF_BLOCK, MifareClassic.KEY_DEFAULT);

        mSubscribe = mUseCase.readNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            nfcData.setCpf(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                            readTag();
                        },
                        throwable ->mFragment.showError(throwable.getMessage())
                );

    }

    /**
     * Step four
     *
     * Read TAG
     *
     */
    public void readTag() {
        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, NFCConstants.TAG_BLOCK, MifareClassic.KEY_DEFAULT);

        mSubscribe = mUseCase.readNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            nfcData.setNumberTag(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                            readName();
                        },
                        throwable ->mFragment.showError(throwable.getMessage())
                );

    }


    /**
     * Step five
     *
     * Read Name
     *
     */
    public void readName() {
        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, NFCConstants.TAG_BLOCK, MifareClassic.KEY_DEFAULT);

        mSubscribe = mUseCase.readNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            nfcData.setName(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                            readCellPhone();
                        },
                        throwable ->mFragment.showError(throwable.getMessage())
                );

    }


    public void readCellPhone() {
        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, NFCConstants.TAG_BLOCK, MifareClassic.KEY_DEFAULT);

        mSubscribe = mUseCase.readNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            nfcData.setCellPhone(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                            stopNFCCardDirectlyRead();
                        },
                        throwable ->mFragment.showError(throwable.getMessage())
                );

    }

    public void startNFCCardDirectly() {
        dispose();


        mSubscribe = mUseCase.startNFCCardDirectly()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> this.executeBeep(result),
                        throwable ->mFragment.showError(throwable.getMessage()));
    }

    public void executeBeep(Object res) {
        dispose();
        mSubscribe = mUseCase.executePlugPagBeepData(new PlugPagBeepData(PlugPagBeepData.FREQUENCE_LEVEL_5, 5))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> executeAction(),
                        throwable ->mFragment.showError(throwable.getMessage()));

    }

//    public void showLed(Object res) {
//
//        mSubscribe = mUseCase.showLed(new PlugPagLedData(PlugPagLedData.LED_YELLOW))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(result -> this.authNFCCardDirectly(result),
//                        throwable ->mFragment.showError(throwable.getMessage()));
//
//    }

//    public void authNFCCardDirectly(int block) {
//        dispose();
//
//        mSubscribe = mUseCase.authNFCCardDirectly(new PlugPagNFCAuth(PlugPagNearFieldCardData.ONLY_M, (byte) 1, MifareClassic.KEY_DEFAULT))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(result -> this.hideLed(result),
//                        throwable ->mFragment.showError(throwable.getMessage()));
//    }

//    public void hideLed(Object res) {
//
//        mSubscribe = mUseCase.showLed(new PlugPagLedData(PlugPagLedData.LED_OFF))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(result -> executeAction(result),
//                        throwable ->mFragment.showError(throwable.getMessage()));
//
//    }

    public void executeAction() {

        if(type == "read") {
            this.readValue();
        }

        if(type == "write") {
            this.writeNFCCardDirectly();
        }

        if(type == "format") {
            this.formatNFCCardDirectly();
        }

    }

    public void stopNFCCardDirectlyRead() {

        mSubscribe = mUseCase.stopNFCCardDirectly()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> mFragment.showSuccess(nfcData),
                        throwable ->mFragment.showError(throwable.getMessage())
                );
    }

    public void stopNFCCardDirectlyWrite(Object res) {

        mSubscribe = mUseCase.stopNFCCardDirectly()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mFragment.showSuccessWrite((int) result),
                        throwable ->mFragment.showError(throwable.getMessage()));
    }

    public void stopNFCCardDirectlyFormat(Object res) {

        mSubscribe = mUseCase.stopNFCCardDirectly()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mFragment.showSuccessWrite((int) result),
                        throwable ->mFragment.showError(throwable.getMessage()));
    }


    public void writeNFCCardDirectly() {
        dispose();

        byte[] bytePassword = {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, 1, bytePassword);
        cardData.setStartSlot(1);
        cardData.setEndSlot(12);
        //cardData.setTypeCard(4);
        //cardData.setTimeOutRead(10);
        //cardData.setCardType(4);

        cardData.getSlots()[1].put("data", this.valor.getBytes());
        cardData.getSlots()[2].put("data", this.idCaixa.getBytes());
        cardData.getSlots()[6].put("data", this.idCarga.getBytes());
        cardData.getSlots()[8].put("data", this.cpf.getBytes());
        cardData.getSlots()[9].put("data", this.numeroTag.getBytes());
        cardData.getSlots()[10].put("data", this.nome.getBytes());
        cardData.getSlots()[11].put("data", this.celular.getBytes());
        cardData.getSlots()[12].put("data", this.ativo.getBytes());

        mSubscribe = mUseCase.writeToNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> this.stopNFCCardDirectlyWrite(result),
                        throwable ->mFragment.showError(throwable.getMessage()));
    }

    public void reWriteNFCCard(String idCaixa, String idCarga, String valor, String nome, String cpf, String numeroTag, String saldoAtual, String celular, String ativo) {
        dispose();

        PlugPagNearFieldCardData cardData = new PlugPagNearFieldCardData();
        cardData.setStartSlot(1);
        cardData.setEndSlot(12);

        Double valorAtual = Double.parseDouble(removeAsterisco(saldoAtual));
        Double valorRecarga = Double.parseDouble(removeAsterisco(valor));
        Double valorNovo = (valorAtual + valorRecarga);

        cardData.getSlots()[1].put("data", adicionaAsterisco(valorNovo.toString()).getBytes());
        cardData.getSlots()[2].put("data", idCaixa.getBytes());
        cardData.getSlots()[6].put("data", idCarga.getBytes());
        cardData.getSlots()[8].put("data", cpf.getBytes());
        cardData.getSlots()[9].put("data", numeroTag.getBytes());
        cardData.getSlots()[10].put("data", nome.getBytes());
        cardData.getSlots()[11].put("data", celular.getBytes());
        cardData.getSlots()[12].put("data", ativo.getBytes());

        mSubscribe = mUseCase.writeNFCCard(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mFragment.showSuccessReWrite(result),
                        throwable -> mFragment.showError(throwable.getMessage()));
    }

    public void debitNFCCard(String saldo, String produtos) {
        dispose();

        PlugPagNearFieldCardData cardData = new PlugPagNearFieldCardData();
        cardData.setStartSlot(1);
        cardData.setEndSlot(28);

        Double saldoAtual = Double.parseDouble(removeAsterisco(saldo));
        Double valorProdutos = Double.parseDouble(removeAsterisco(produtos));
        Double valorNovo = (saldoAtual - valorProdutos);

        cardData.getSlots()[1].put("data", adicionaAsterisco(valorNovo.toString()).getBytes());

        mSubscribe = mUseCase.writeNFCCard(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mFragment.showSuccessDebitNfc(result),
                        throwable -> mFragment.showError(throwable.getMessage()));
    }


    public void formatNFCCardDirectly() {

        String textToWrite = "teste_com16bytes";

        mSubscribe = mUseCase.writeToNFCCardDirectly(new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, 6, textToWrite.getBytes()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> this.stopNFCCardDirectlyFormat(result),
                        throwable ->mFragment.showError(throwable.getMessage()));
    }


    public void dispose() {
        if (mSubscribe != null) {
            mSubscribe.dispose();
        }
    }

    public void abort() {
        mSubscribe = mUseCase.abort()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private String removeAsterisco(String valor) {

        if(valor != null) {
            return valor.replace("*", "");
        }

        return "";
    }


    public String adicionaAsterisco(String valor) {
        if (valor.length() >= 16) {
            return valor;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 16 - valor.length()) {
            sb.append('*');
        }
        sb.append(valor);

        return sb.toString();
    }

}