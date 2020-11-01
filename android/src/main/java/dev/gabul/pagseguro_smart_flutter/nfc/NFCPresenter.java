package dev.gabul.pagseguro_smart_flutter.nfc;
import javax.inject.Inject;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNearFieldCardData;
import io.flutter.plugin.common.MethodChannel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NFCPresenter  {

    private final NFCUseCase mUseCase;
    private final NFCFragment mFragment;

    private Disposable mSubscribe;

    @Inject
    public NFCPresenter(PlugPag plugPag, MethodChannel channel) {
        mUseCase = new NFCUseCase(plugPag);
        mFragment = new NFCFragment(channel);
    }

    public void readNFCCard() {
        dispose();
        mSubscribe = mUseCase.readNFCCard()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mFragment.showSuccess(result),
                           throwable ->mFragment.showError(throwable.getMessage()));
    }

    public void writeNFCCard(String idCaixa, String idCarga, String valor, String nome, String cpf, String numeroTag, String saldoAtual) {
        dispose();

        PlugPagNearFieldCardData cardData = new PlugPagNearFieldCardData();
        cardData.setStartSlot(1);
        cardData.setEndSlot(28);

        cardData.getSlots()[1].put("data", valor.getBytes());
        cardData.getSlots()[2].put("data", idCaixa.getBytes());
        cardData.getSlots()[6].put("data", idCarga.getBytes());
        cardData.getSlots()[8].put("data", cpf.getBytes());
      //  cardData.getSlots()[9].put("data", numeroTag.getBytes());
        cardData.getSlots()[10].put("data", nome.getBytes());

        mSubscribe = mUseCase.writeNFCCard(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mFragment.showSuccessWrite(result),
                        throwable -> mFragment.showError(throwable.getMessage()));
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
}