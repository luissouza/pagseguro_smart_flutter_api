package dev.gabul.pagseguro_smart_flutter.nfc;

import android.nfc.tech.MifareClassic;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNFCResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNearFieldCardData;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagBeepData;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagLedData;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagSimpleNFCData;
import br.com.uol.pagseguro.plugpagservice.wrapper.exception.PlugPagException;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagNFCAuth;
import io.reactivex.Completable;
import io.reactivex.Observable;

public class NFCUseCase {

    private final PlugPag mPlugPag;

    public NFCUseCase(PlugPag plugPag) {
        mPlugPag = plugPag;
    }


    public Observable<PlugPagNFCResult> readNFCCard() {
        return Observable.create(emitter -> {
            PlugPagNearFieldCardData cardData = new PlugPagNearFieldCardData();
            cardData.setStartSlot(1);
            cardData.setEndSlot(28);

            PlugPagNFCResult result = mPlugPag.readFromNFCCard(cardData);


            if (result.getResult() == 1) {
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException());
            }

            emitter.onComplete();
        });
    }

    public Observable<PlugPagNFCResult> writeNFCCard(PlugPagNearFieldCardData dataCard) {

        return Observable.create(emitter -> {

            PlugPagNFCResult result = mPlugPag.writeToNFCCard(dataCard);

            if (result.getResult() == 1) {
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException());
            }

            emitter.onComplete();
        });
    }

    public Observable<Object> writeToNFCCardDirectly(PlugPagSimpleNFCData dataCard) {

        return Observable.create(emitter -> {

            int result = mPlugPag.writeToNFCCardDirectly(dataCard);

            if (result == 1) {
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException());
            }

            emitter.onComplete();
        });
    }

    public Observable<PlugPagNFCResult> readNFCCardDirectly(PlugPagSimpleNFCData cardData) {
        return Observable.create(emitter -> {


            PlugPagNFCAuth auth = new PlugPagNFCAuth(PlugPagNearFieldCardData.ONLY_M, (byte) cardData.getStartSlot(), MifareClassic.KEY_DEFAULT);
            int resultAuth = mPlugPag.authNFCCardDirectly(auth);
            if (resultAuth != 1){
                emitter.onError(new PlugPagException("Erro na autenticação"));
                emitter.onComplete();
                return;
            }

            PlugPagNFCResult result = mPlugPag.readNFCCardDirectly(cardData);

            if (result.getResult() == 1){
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException("Ocoreu um erro ao ler o cartão nfc"));
            }
            mPlugPag.stopNFCCardDirectly();

            emitter.onComplete();
        });
    }


    public Observable<Object> startNFCCardDirectly() {

        return Observable.create(emitter -> {

            int result = mPlugPag.startNFCCardDirectly();

            if (result == 1) {
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException("Erro ao iniciar serviço NFC"));
            }

            emitter.onComplete();
        });
    }

    public Observable<Object> stopNFCCardDirectly() {

        return Observable.create(emitter -> {

            int result = mPlugPag.stopNFCCardDirectly();

            if (result == 1) {
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException("Erro ao parar serviço NFC"));
            }

            emitter.onComplete();
        });
    }

    public Observable<Object> executePlugPagBeepData(PlugPagBeepData plugPagBeepData) {

        return Observable.create(emitter -> {

            int result = mPlugPag.beep(plugPagBeepData);

            if (result == 1) {
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException());
            }

            emitter.onComplete();
        });
    }

    public Observable<Object> showLed(PlugPagLedData plugPagLedData) {

        return Observable.create(emitter -> {

            int result = mPlugPag.setLed(plugPagLedData);

            if (result == 1) {
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException());
            }

            emitter.onComplete();
        });
    }

    public Observable<Object> authNFCCardDirectly(PlugPagNFCAuth plugPagNFCAuth) {


        return Observable.create(emitter -> {

            int result = mPlugPag.authNFCCardDirectly(plugPagNFCAuth);

            if (result == 1) {
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException());
            }

            emitter.onComplete();
        });
    }

    public Completable abort() {
        return Completable.create(emitter -> mPlugPag.abortNFC());
    }
}