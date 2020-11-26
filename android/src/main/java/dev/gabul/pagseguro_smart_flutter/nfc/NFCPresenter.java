package dev.gabul.pagseguro_smart_flutter.nfc;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;


import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagLedData;
import dev.gabul.pagseguro_smart_flutter.helpers.NFCConstants;
import dev.gabul.pagseguro_smart_flutter.helpers.Utils;
import dev.gabul.pagseguro_smart_flutter.managers.UserDataManager;
import dev.gabul.pagseguro_smart_flutter.nfc.usecase.NFCUseCase;
import dev.gabul.pagseguro_smart_flutter.user.UserData;
import io.flutter.plugin.common.MethodChannel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NFCPresenter  {

    private final NFCUseCase mUseCase;
    private final NFCFragment mFragment;
    private final UserDataManager mUserManager;
    private Disposable mSubscribe;

    @Inject
    public NFCPresenter(NFCFragment mFragment, NFCUseCase nfcUseCase, UserDataManager userManager) {
        this.mUseCase = nfcUseCase;
        this.mFragment = mFragment;
        this.mUserManager = userManager;
    }

    public void readNFCCard() {

        mSubscribe = mUseCase.controlLed(new PlugPagLedData(PlugPagLedData.LED_BLUE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> getUserData(),
                        throwable -> mFragment.showErrorRead(throwable.getMessage())
                );

    }

    public void getUserData() {

        mSubscribe = mUserManager.getUserData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> showSuccessRead(result),
                        throwable -> mFragment.showErrorRead(throwable.getMessage())
                );


    }

    public void showSuccessRead(UserData userData) {

        mSubscribe = mUseCase.controlLed(new PlugPagLedData(PlugPagLedData.LED_GREEN))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> mFragment.showSuccess(userData),
                        throwable -> mFragment.showErrorRead(throwable.getMessage())
                );
    }



    public void writeNFCCard(String value, String name, String cpf, String numberTag, String cellPhone, String active) {

        UserData userData = new UserData(Utils.adicionaAsterisco(value), Utils.adicionaAsterisco(name), Utils.adicionaAsterisco(cpf), Utils.adicionaAsterisco(numberTag), Utils.adicionaAsterisco(cellPhone), Utils.adicionaAsterisco(active));

        mSubscribe = mUseCase.controlLed(new PlugPagLedData(PlugPagLedData.LED_BLUE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> writeUserData(userData),
                        throwable -> mFragment.showErrorRead(throwable.getMessage())
                );

    }

    public void writeUserData(UserData userData) {

        mSubscribe = mUserManager.writeUserData(userData)
                .lastElement()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> showSuccessWrite(result),
                        throwable -> mFragment.showErrorWrite(throwable.getMessage()),
                        () -> {
                            Log.d(NFCPresenter.class.getSimpleName(), "writeUser finished");
                            // mView.onWriteNfcSuccessful();
                        }
                );


    }


    public void showSuccessWrite(Integer res) {

        mSubscribe = mUseCase.controlLed(new PlugPagLedData(PlugPagLedData.LED_GREEN))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> mFragment.showSuccessWrite(res),
                        throwable -> mFragment.showErrorRead(throwable.getMessage())
                );
    }

    public void reWriteNFCCard(String value) {

        UserData userData = new UserData(Utils.adicionaAsterisco(value), null, null, null, null, null);

        mSubscribe = mUserManager.reWriteUserData(userData)
                .lastElement()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> mFragment.showSuccessReWrite(result),
                        throwable -> mFragment.showErrorReWrite(throwable.getMessage()),
                        () -> {
                            Log.d(NFCPresenter.class.getSimpleName(), "writeUser finished");
                            // mView.onWriteNfcSuccessful();
                        }
                );
    }

    public void formatNFCCard() {

        // using ArrayList to have no block limit
        List<Integer> blocks = Arrays.asList(
                NFCConstants.VALUE_BLOCK,
                NFCConstants.NAME_BLOCK,
                NFCConstants.CPF_BLOCK,
                NFCConstants.TAG_BLOCK,
                NFCConstants.CELL_PHONE_BLOCK,
                NFCConstants.CARD_OPENED_BLOCK
        );
        mSubscribe = mUseCase.clearBlocks(blocks)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinct()
                .subscribe(
                        result -> mFragment.showSuccessFormat(result),
                        throwable -> mFragment.showErrorFormat(throwable.getMessage())
                        //mView::onBlockCleanSuccessful
                );

    }

    public void dispose() {
        if(mSubscribe != null){
            mSubscribe.dispose();
        }
    }

}