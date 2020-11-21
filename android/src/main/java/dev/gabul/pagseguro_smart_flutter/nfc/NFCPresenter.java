package dev.gabul.pagseguro_smart_flutter.nfc;
import javax.inject.Inject;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNearFieldCardData;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagNFCAuth;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagSimpleNFCData;

import android.nfc.tech.MifareClassic;


import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagBeepData;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagLedData;
import io.flutter.plugin.common.MethodChannel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NFCPresenter  {

    private final NFCUseCase mUseCase;
    private final NFCFragment mFragment;
    private final PlugPag mPlugPag;


    private String type;
    private NFCData readNfcData = null;
    private NFCData writeNfcData = null;

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
        this.executeAction();
    }


    public void writeNFCCard(String idCaixa, String idCarga, String valor, String nome, String cpf, String numeroTag, String saldoAtual, String celular, String ativo) {
        this.type = "write";

        String textToWrite = "teste_com16bytes";
        this.writeNfcData = new NFCData(textToWrite,textToWrite,textToWrite,textToWrite,textToWrite,textToWrite,textToWrite,textToWrite,textToWrite);

       // this.writeNfcData = new NFCDa ta(idCaixa, idCarga, valor,nome,cpf,numeroTag,saldoAtual,celular,ativo);
        //this.writeNfcData = new NFCData(idCaixa, idCarga, valor,nome,cpf,numeroTag,saldoAtual,celular,ativo);
        this.executeAction();
    }

    public void formatNFCCard() {
        dispose();
        this.type = "format";

        String textToWrite = "teste_com16bytes";
        this.writeNfcData = new NFCData(textToWrite,textToWrite,textToWrite,textToWrite,textToWrite,textToWrite,textToWrite,textToWrite,textToWrite);

        this.executeAction();
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

    public void startNFCCardDirectly() {
        dispose();


        mSubscribe = mUseCase.startNFCCardDirectly()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> this.showLed(result),
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

    public void showLed(Object res) {

        mSubscribe = mUseCase.showLed(new PlugPagLedData(PlugPagLedData.LED_BLUE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> this.executeBeep(result),
                        throwable ->mFragment.showError(throwable.getMessage()));

    }

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
//                .subscribe(result -> executeAction(),
//                        throwable ->mFragment.showError(throwable.getMessage()));
//
//    }

    public void executeAction() {

        if(type == "read") {
            this.readValue();
        }

        if(type == "write") {


            this.writeValue(this.writeNfcData);
        }

        if(type == "format") {
            this.writeValue(this.writeNfcData);

        }

    }

    public void stopNFCCardDirectlyRead() {

        mSubscribe = mUseCase.stopNFCCardDirectly()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> mFragment.showSuccess(readNfcData),
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

    //region Ler cartão NFC

    /**
     * Step One
     *
     * Read Value
     *
     */
    public void readValue() {
        if (readNfcData == null) readNfcData = new NFCData();


        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, NFCConstants.VALUE_BLOCK, MifareClassic.KEY_DEFAULT);

        mSubscribe = mUseCase.readNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            readNfcData.setValue(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                        },
                        throwable -> mFragment.showError(throwable.getMessage()),
                        () -> {
                            isRetry = false;
                            readIdCashier();
                        }

                );

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
                            readNfcData.setIdCashier(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                        },
                        throwable -> mFragment.showError(throwable.getMessage()),
                        () -> {
                            isRetry = false;
                            readCpf();
                        }

                );

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
                            readNfcData.setCpf(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                        },
                        throwable ->mFragment.showError(throwable.getMessage()),
                        () -> {
                            isRetry = false;
                            readTag();
                        }
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
                            readNfcData.setNumberTag(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                        },
                        throwable ->mFragment.showError(throwable.getMessage()),
                        () -> {
                            isRetry = false;
                            readName();
                        }
                );

    }


    /**
     * Step five
     *
     * Read Name
     *
     */
    public void readName() {
        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, NFCConstants.NAME_BLOCK, MifareClassic.KEY_DEFAULT);

        mSubscribe = mUseCase.readNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            readNfcData.setName(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                        },
                        throwable ->mFragment.showError(throwable.getMessage()),
                        () -> {
                            isRetry = false;
                            readCellPhone();
                        }
                );

    }

    /**
     * Step six
     *
     * Read Name
     *
     */
    public void readCellPhone() {
        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, NFCConstants.CELL_PHONE_BLOCK, MifareClassic.KEY_DEFAULT);

        mSubscribe = mUseCase.readNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            readNfcData.setCellPhone(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                        },
                        throwable ->mFragment.showError(throwable.getMessage()),
                        () -> {
                            isRetry = false;
                            stopNFCCardDirectlyRead();
                        }
                );

    }
    //endregion

    //region Escreve no cartão NFC

    /**
     * Step One
     *
     * Write Value in Card Nfc
     *
     * @param inputData
     */
    public void writeValue(NFCData inputData) {
        if (isRetry){
            isRetry = false;
            retryAction.run();
            return;
        }

        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, NFCConstants.VALUE_BLOCK, MifareClassic.KEY_DEFAULT);
        cardData.setValue(Utils.convertString2Bytes(inputData.getValue()));

        mSubscribe = mUseCase.writeToNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {

                        },
                        throwable -> {
                            isRetry = true;
                            retryAction = () -> writeValue(inputData);
                            mFragment.showError(throwable.getMessage());
                        },
                        () -> {
                            isRetry = false;
                            writeIdCashier(inputData);
                        }
                );
    }

    /**
     * Step two
     *
     * Write ID Cashier in Card Nfc
     *
     * @param inputData
     */
    public void writeIdCashier(NFCData inputData) {
        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, NFCConstants.ID_CASHIER_BLOCK, MifareClassic.KEY_DEFAULT);
        cardData.setValue(Utils.convertString2Bytes(inputData.getIdCashier()));

        mSubscribe = mUseCase.writeToNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {

                        },
                        throwable -> {
                            isRetry = true;
                            retryAction = () -> writeIdCashier(inputData);
                            mFragment.showError(throwable.getMessage());
                        },
                        () -> {
                            isRetry = false;
                           // writeCpf(inputData);
                        }
                );
    }

    /**
     * Step three
     *
     * Write CPF in Card Nfc
     *
     * @param inputData
     */
    public void writeCpf(NFCData inputData) {
        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, NFCConstants.CPF_BLOCK, MifareClassic.KEY_DEFAULT);
        cardData.setValue(Utils.convertString2Bytes(inputData.getCpf()));

        mSubscribe = mUseCase.writeToNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {

                        },
                        throwable -> {
                            isRetry = true;
                            retryAction = () -> writeCpf(inputData);
                            mFragment.showError(throwable.getMessage());
                        },
                        () -> {
                            isRetry = false;
                            writeTag(inputData);
                        }
                );
    }

    /**
     * Step four
     *
     * Write Number TAG in Card Nfc
     *
     * @param inputData
     */
    public void writeTag(NFCData inputData) {
        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, NFCConstants.TAG_BLOCK, MifareClassic.KEY_DEFAULT);
        cardData.setValue(Utils.convertString2Bytes(inputData.getNumberTag()));

        mSubscribe = mUseCase.writeToNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {

                        },
                        throwable -> {
                            isRetry = true;
                            retryAction = () -> writeTag(inputData);
                            mFragment.showError(throwable.getMessage());
                        },

                        () -> {
                            isRetry = false;
                            writeName(inputData);
                        }
                );
    }

    /**
     * Step five
     *
     * Write Name in Card Nfc
     *
     * @param inputData
     */
    public void writeName(NFCData inputData) {
        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, NFCConstants.NAME_BLOCK, MifareClassic.KEY_DEFAULT);
        cardData.setValue(Utils.convertString2Bytes(inputData.getName()));

        mSubscribe = mUseCase.writeToNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {

                        },
                        throwable -> {
                            isRetry = true;
                            retryAction = () -> writeName(inputData);
                            mFragment.showError(throwable.getMessage());
                        },
                        () -> {
                            isRetry = false;
                            writeCellPhone(inputData);
                        }
                );
    }

    /**
     * Step six
     *
     * Write Cell Phone in Card Nfc
     *
     * @param inputData
     */
    public void writeCellPhone(NFCData inputData) {
        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, NFCConstants.CELL_PHONE_BLOCK, MifareClassic.KEY_DEFAULT);
        cardData.setValue(Utils.convertString2Bytes(inputData.getCellPhone()));

        mSubscribe = mUseCase.writeToNFCCardDirectly(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            isRetry = false;
                            stopNFCCardDirectlyWrite(result);
                        },
                        throwable -> {
                            isRetry = true;
                            retryAction = () -> writeCellPhone(inputData);
                            mFragment.showError(throwable.getMessage());
                        }
                );
    }
    //endregion

}