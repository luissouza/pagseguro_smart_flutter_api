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

    public void writeNFCCard(String idCaixa, String idCarga, String valor, String nome, String cpf, String numeroTag, String saldoAtual, String celular, String ativo) {
        dispose();

        PlugPagNearFieldCardData cardData = new PlugPagNearFieldCardData();
        cardData.setStartSlot(1);
        cardData.setEndSlot(28);

        cardData.getSlots()[1].put("data", valor.getBytes());
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
                .subscribe(result -> mFragment.showSuccessWrite(result),
                        throwable -> mFragment.showError(throwable.getMessage()));
    }

    public void reWriteNFCCard(String saldoAtual, String valor) {
        dispose();

        PlugPagNearFieldCardData cardData = new PlugPagNearFieldCardData();
        cardData.setStartSlot(1);
        cardData.setEndSlot(28);

        Double valorAtual = Double.parseDouble(removeAsterisco(saldoAtual));
        Double valorRecarga = Double.parseDouble(removeAsterisco(valor));
        Double valorNovo = (valorAtual + valorRecarga);

        cardData.getSlots()[1].put("data", adicionaAsterisco(valorNovo.toString()).getBytes());

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


    public void formatNFCCard() {
        dispose();

        String vazio = "****************";
        PlugPagNearFieldCardData cardData = new PlugPagNearFieldCardData();
        cardData.setStartSlot(1);
        cardData.setEndSlot(28);

        cardData.getSlots()[1].put("data", vazio.getBytes());
        cardData.getSlots()[2].put("data", vazio.getBytes());
        cardData.getSlots()[6].put("data", vazio.getBytes());
        cardData.getSlots()[8].put("data", vazio.getBytes());
        cardData.getSlots()[9].put("data", vazio.getBytes());
        cardData.getSlots()[10].put("data", vazio.getBytes());

        mSubscribe = mUseCase.writeNFCCard(cardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mFragment.showSuccessFormat(result),
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