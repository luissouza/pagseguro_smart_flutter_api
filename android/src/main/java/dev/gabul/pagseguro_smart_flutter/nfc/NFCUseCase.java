package dev.gabul.pagseguro_smart_flutter.nfc;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNFCResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNearFieldCardData;
import br.com.uol.pagseguro.plugpagservice.wrapper.exception.PlugPagException;
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

    public Completable abort() {
        return Completable.create(emitter -> mPlugPag.abortNFC());
    }
}