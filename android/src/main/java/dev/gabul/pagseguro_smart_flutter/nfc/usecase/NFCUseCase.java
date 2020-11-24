package dev.gabul.pagseguro_smart_flutter.nfc.usecase;

import android.nfc.tech.MifareClassic;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNFCResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNearFieldCardData;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagSimpleNFCData;
import br.com.uol.pagseguro.plugpagservice.wrapper.exception.PlugPagException;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagNFCAuth;
import dev.gabul.pagseguro_smart_flutter.helpers.NFCConstants;
import dev.gabul.pagseguro_smart_flutter.helpers.Utils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class NFCUseCase {

    private final PlugPag mPlugPag;

    public NFCUseCase(PlugPag plugPag) {
        mPlugPag = plugPag;
    }

    public Observable<Integer> writeNfc(PlugPagSimpleNFCData cardData){
        return Observable.create(emitter -> {
            try {

                int resultStartNfc = mPlugPag.startNFCCardDirectly();
                if (resultStartNfc != 1) {
                    emitter.onError(new PlugPagException("Ocorreu um erro ao iniciar serviço nfc"));
                    emitter.onComplete();
                    return;
                }

                PlugPagNFCAuth auth = new PlugPagNFCAuth(PlugPagNearFieldCardData.ONLY_M, (byte) cardData.getSlot(), MifareClassic.KEY_DEFAULT);
                int resultAuth = mPlugPag.authNFCCardDirectly(auth);
                if (resultAuth != 1) {
                    emitter.onError(new PlugPagException(String.format("Erro ao autenticar bloco [ %s ]", cardData.getSlot())));
                    emitter.onComplete();
                    return;
                }

                if(cardData.getSlot() == NFCConstants.CARD_OPENED_BLOCK) {

                    PlugPagSimpleNFCData readCardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, cardData.getSlot(), MifareClassic.KEY_DEFAULT);
                    PlugPagNFCResult resultRead = mPlugPag.readNFCCardDirectly(readCardData);

                    if (resultRead.getResult() == 1) {

                        String isOpened = Utils.removeAsterisco(new String(resultRead.getSlots()[cardData.getSlot()].get("data")));

                        if(isOpened.equals("1")) {
                            emitter.onError(new PlugPagException(String.format("cartao_ja_aberto", cardData.getSlot())));
                            emitter.onComplete();
                            return;
                        }

                    } else {
                        emitter.onError(new PlugPagException(String.format("Ocoreu um erro ao ler bloco [ %s ] do cartão nfc para verificar se o cartão está aberto", cardData.getSlot())));
                        emitter.onComplete();
                        return;
                    }

                }

                Integer result = mPlugPag.writeToNFCCardDirectly(cardData);

                if (result == 1) {
                    emitter.onNext(result);
                } else {
                    emitter.onError(new PlugPagException(String.format("Ocorreu um erro ao escrever no bloco [ %s ]  do cartão nfc", cardData.getSlot())));
                }

                mPlugPag.stopNFCCardDirectly();
            } catch (Exception e){
                e.printStackTrace();
                emitter.onError(e);
            }

            emitter.onComplete();
        });
    }

    public Observable<Integer> reWriteNfc(PlugPagSimpleNFCData cardData){
        return Observable.create(emitter -> {
            try {

                int resultStartNfc = mPlugPag.startNFCCardDirectly();
                if (resultStartNfc != 1) {
                    emitter.onError(new PlugPagException("Ocorreu um erro ao iniciar serviço nfc"));
                    emitter.onComplete();
                    return;
                }

                PlugPagNFCAuth auth = new PlugPagNFCAuth(PlugPagNearFieldCardData.ONLY_M, (byte) cardData.getSlot(), MifareClassic.KEY_DEFAULT);
                int resultAuth = mPlugPag.authNFCCardDirectly(auth);
                if (resultAuth != 1) {
                    emitter.onError(new PlugPagException(String.format("Erro ao autenticar bloco [ %s ]", cardData.getSlot())));
                    emitter.onComplete();
                    return;
                }

                PlugPagSimpleNFCData readCardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, cardData.getSlot(), MifareClassic.KEY_DEFAULT);
                PlugPagNFCResult resultRead = mPlugPag.readNFCCardDirectly(readCardData);

                if (resultRead.getResult() == 1) {

                } else {
                    emitter.onError(new PlugPagException(String.format("Ocoreu um erro ao ler bloco [ %s ] do cartão nfc", cardData.getSlot())));
                    emitter.onComplete();
                    return;
                }

                //Soma valor atual com o valor de recarga
                Double valorAtual = Double.parseDouble(Utils.removeAsterisco(new String(resultRead.getSlots()[cardData.getSlot()].get("data"))));
                Double valorRecarga = Double.parseDouble(Utils.removeAsterisco(new String(cardData.getValue())));
                Double valorNovo = (valorAtual + valorRecarga);
                String valorNovoString = Utils.adicionaAsterisco(valorNovo.toString());
                cardData.setValue(valorNovoString.getBytes());

                Integer result = mPlugPag.writeToNFCCardDirectly(cardData);

                if (result == 1) {
                    emitter.onNext(result);
                } else {
                    emitter.onError(new PlugPagException(String.format("Ocorreu um erro ao escrever no bloco [ %s ]  do cartão nfc", cardData.getSlot())));
                }

                mPlugPag.stopNFCCardDirectly();
            } catch (Exception e){
                e.printStackTrace();
                emitter.onError(e);
            }

            emitter.onComplete();
        });
    }

    public Observable<PlugPagNFCResult> readNfc(Integer block){
        return Observable.create(emitter -> {
            int resultStartNfc = mPlugPag.startNFCCardDirectly();
            if (resultStartNfc != 1){
                emitter.onError(new PlugPagException("Ocorreu um erro ao iniciar serviço nfc"));
                emitter.onComplete();
                return;
            }



            PlugPagNFCAuth auth = new PlugPagNFCAuth(PlugPagNearFieldCardData.ONLY_M, block.byteValue(), MifareClassic.KEY_DEFAULT);
            int resultAuth = mPlugPag.authNFCCardDirectly(auth);
            if (resultAuth != 1){
                emitter.onError(new PlugPagException(String.format("Erro ao autenticar bloco [ %s ]", block)));
                emitter.onComplete();
                return;
            }


            PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, block, MifareClassic.KEY_DEFAULT);

            PlugPagNFCResult result = mPlugPag.readNFCCardDirectly(cardData);

            if (result.getResult() == 1){
                Log.d(NFCUseCase.class.getSimpleName(), Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException(String.format("Ocoreu um erro ao ler bloco [ %s ] do cartão nfc", block)));
            }
            mPlugPag.stopNFCCardDirectly();

            emitter.onComplete();
        });
    }

    public Observable<Object> startNfc(){
        return Observable.create(emitter -> {
            int result = mPlugPag.startNFCCardDirectly();
            if (result == 1){
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException("Ocorreu um erro ao iniciar nfc"));
            }

            emitter.onComplete();
        });
    }

    public Observable<Object> stopNfc(){
        return Observable.create(emitter -> {
            int result = mPlugPag.startNFCCardDirectly();
            if (result == 1){
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException("Ocorreu um erro ao iniciar nfc"));
            }

            emitter.onComplete();
        });
    }

    public Completable abort(){
        return Completable.create(emitter -> mPlugPag.abortNFC());
    }

    public Observable<Integer> clearBlocks(List<Integer> blocks){
        return Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            for (Integer trailerBlock : getSectorTrailerBlocks()){
                if (blocks.contains(trailerBlock)){
                    emitter.onError(new PlugPagException(String.format("O bloco [ %s ] é de permissão de acesso e não pode ser limpo!", trailerBlock)));
                    emitter.onComplete();
                    return;
                }
            }

            for ( Integer block : blocks ){
                emitter.onNext(block);
            }

            if (!emitter.isDisposed()){
                emitter.onComplete();
            }
        })
                .concatMap(block -> { // Using concatMap to ensure that observables are not called at the same time
                    PlugPagSimpleNFCData emptyData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, block, MifareClassic.KEY_DEFAULT);
                    emptyData.setValue(new byte[16]);
                    return writeNfc(emptyData);
                });
    }

    public Observable<Integer> writePermissions(@NonNull byte[] keyA, @NonNull byte[] permissions, @Nullable byte[] keyB){
        return Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            for (Integer i : getSectorTrailerBlocks()){
                emitter.onNext(i);
            }

            if (!emitter.isDisposed()){
                emitter.onComplete();
            }
        })
                .concatMap(sectorTrailerBlock -> {
                    PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(
                            PlugPagNearFieldCardData.ONLY_M,
                            sectorTrailerBlock,
                            MifareClassic.KEY_DEFAULT);
                    cardData.setValue(buildDataAccess(keyA, permissions, keyB));
                    return writeNfc(cardData);
                });
    }

    private List<Integer> getSectorTrailerBlocks(){
        final List<Integer> ret = new ArrayList<>();
        for (int i = 7; i < 64; i += 4){
            ret.add(i);
        }
        return ret;
    }

    private byte[] buildDataAccess(@NonNull byte[] keyA, @NonNull byte[] permissions, @Nullable byte[] keyB){
        byte[] data = new byte[16];
        System.arraycopy(keyA, 0, data, 0, 6);
        System.arraycopy(permissions, 0, data, 6, 4);
        if (keyB != null){
            System.arraycopy(keyB, 0, data, 10, 6);
        }
        return data;
    }


//    public Observable<PlugPagNFCResult> readNFCCard() {
//        return Observable.create(emitter -> {
//
//            PlugPagNearFieldCardData cardData = new PlugPagNearFieldCardData();
//            cardData.setStartSlot(1);
//            cardData.setEndSlot(28);
//
//            PlugPagNFCResult result = mPlugPag.readFromNFCCard(cardData);
//
//
//            if (result.getResult() == 1) {
//                emitter.onNext(result);
//            } else {
//                emitter.onError(new PlugPagException());
//            }
//
//            emitter.onComplete();
//        });
//    }
//
//    public Observable<PlugPagNFCResult> writeNFCCard(PlugPagNearFieldCardData dataCard) {
//
//
//        return Observable.create(emitter -> {
//
////            int resultStartNfc = mPlugPag.startNFCCardDirectly();
////            if (resultStartNfc != 1) {
////                emitter.onError(new PlugPagException("Ocorreu um erro ao iniciar serviço nfc no write"));
////                emitter.onComplete();
////                return;
////            }
//
//            PlugPagNFCResult result = this.mPlugPag.writeToNFCCard(dataCard);
//
//            if (result.getResult() == 1) {
//                emitter.onNext(result);
//            } else {
//                emitter.onError(new PlugPagException());
//            }
//
//           // this.mPlugPag.stopNFCCardDirectly();
//            emitter.onComplete();
//        });
//    }
//
//    public Observable<Object> writeToNFCCardDirectly(PlugPagSimpleNFCData dataCard) {
//
//        return Observable.create(emitter -> {
//
//            int resultStartNfc = mPlugPag.startNFCCardDirectly();
//            if (resultStartNfc != 1) {
//                emitter.onError(new PlugPagException("Ocorreu um erro ao iniciar serviço nfc"));
//                emitter.onComplete();
//                return;
//            }
//
//            int resultAuth = this.mPlugPag.authNFCCardDirectly(new PlugPagNFCAuth(PlugPagNearFieldCardData.ONLY_M, (byte) dataCard.getStartSlot(), MifareClassic.KEY_DEFAULT));
//
//            if (resultAuth != 1) {
//
//                mPlugPag.setLed(new PlugPagLedData(PlugPagLedData.LED_RED));
//                mPlugPag.setLed(new PlugPagLedData(PlugPagLedData.LED_OFF));
//                emitter.onError(new PlugPagException("Erro na autenticação"));
//                emitter.onComplete();
//                return;
//            }
//
//            int result = this.mPlugPag.writeToNFCCardDirectly(dataCard);
//
//            if (result == 1) {
//                emitter.onNext(result);
//            } else {
//                mPlugPag.setLed(new PlugPagLedData(PlugPagLedData.LED_RED));
//                mPlugPag.setLed(new PlugPagLedData(PlugPagLedData.LED_OFF));
//                emitter.onError(new PlugPagException("Erro ao escrever no cartão NFC"));
//            }
//
//            System.out.println("WRITE result.getResult() : " + result);
//
//            mPlugPag.stopNFCCardDirectly();
//
//            emitter.onComplete();
//        });
//    }
//
//    public Observable<PlugPagNFCResult> readNFCCardDirectly(PlugPagSimpleNFCData cardData) {
//        return Observable.create(emitter -> {
//
//            int resultStartNfc = mPlugPag.startNFCCardDirectly();
//            if (resultStartNfc != 1) {
//                emitter.onError(new PlugPagException("Ocorreu um erro ao iniciar serviço nfc"));
//                emitter.onComplete();
//                return;
//            }
//
//            PlugPagNFCAuth auth = new PlugPagNFCAuth(PlugPagNearFieldCardData.ONLY_M, (byte) cardData.getStartSlot(), MifareClassic.KEY_DEFAULT);
//            int resultAuth = mPlugPag.authNFCCardDirectly(auth);
//            if (resultAuth != 1) {
//                emitter.onError(new PlugPagException("Erro na autenticação"));
//                emitter.onComplete();
//                return;
//            }
//
//            PlugPagNFCResult result = mPlugPag.readNFCCardDirectly(cardData);
//            System.out.println("result.getResult() " + result.getResult());
//            if (result.getResult() == 1) {
//                System.out.println("result.getResult() : " + Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
//                emitter.onNext(result);
//            } else {
//                emitter.onError(new PlugPagException("Ocoreu um erro ao ler o cartão nfc"));
//            }
//
//            mPlugPag.stopNFCCardDirectly();
//            emitter.onComplete();
//        });
//    }
//
//
//
//    public Observable<Object> startNFCCardDirectly() {
//
//        return Observable.create(emitter -> {
//
//            int result = mPlugPag.startNFCCardDirectly();
//
//            if (result == 1) {
//                emitter.onNext(result);
//            } else {
//                emitter.onError(new PlugPagException("Erro ao iniciar serviço NFC"));
//            }
//
//            emitter.onComplete();
//        });
//    }
//
//    public Observable<Object> stopNFCCardDirectly() {
//
//        return Observable.create(emitter -> {
//
//            int result = mPlugPag.stopNFCCardDirectly();
//
//            if (result == 1) {
//                emitter.onNext(result);
//            } else {
//                emitter.onError(new PlugPagException("Erro ao parar serviço NFC"));
//            }
//
//            emitter.onComplete();
//        });
//    }
//
//    public Observable<Object> executePlugPagBeepData(PlugPagBeepData plugPagBeepData) {
//
//        return Observable.create(emitter -> {
//
//            int result = mPlugPag.beep(plugPagBeepData);
//
//            if (result == 1) {
//                emitter.onNext(result);
//            } else {
//                emitter.onError(new PlugPagException());
//            }
//
//            emitter.onComplete();
//        });
//    }
//
//    public Observable<Object> showLed(PlugPagLedData plugPagLedData) {
//
//        return Observable.create(emitter -> {
//
//            int result = mPlugPag.setLed(plugPagLedData);
//
//            if (result == 1) {
//                emitter.onNext(result);
//            } else {
//                emitter.onError(new PlugPagException());
//            }
//
//            emitter.onComplete();
//        });
//    }
//
//
//    public Observable<PlugPagNFCResult> abortNfc() {
//        return Observable.create(emitter -> {
//
//            PlugPagNFCResult result = mPlugPag.abortNFC();
//            System.out.println("abort result.getResult() " + result.getResult());
//
//            if (result.getResult() == 1) {
//                emitter.onNext(result);
//            } else {
//                emitter.onError(new PlugPagException("Ocoreu um erro ao abortar o cartão nfc"));
//            }
//
//            emitter.onComplete();
//        });
//    }
}