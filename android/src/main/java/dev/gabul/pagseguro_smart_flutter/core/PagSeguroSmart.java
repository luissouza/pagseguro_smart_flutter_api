package dev.gabul.pagseguro_smart_flutter.core;

import android.content.Context;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import dev.gabul.pagseguro_smart_flutter.managers.UserDataManager;
import dev.gabul.pagseguro_smart_flutter.nfc.NFCFragment;
import dev.gabul.pagseguro_smart_flutter.nfc.NFCPresenter;
import dev.gabul.pagseguro_smart_flutter.nfc.usecase.NFCUseCase;
import dev.gabul.pagseguro_smart_flutter.payments.PaymentsPresenter;
import dev.gabul.pagseguro_smart_flutter.user.usecase.EditUserUseCase;
import dev.gabul.pagseguro_smart_flutter.user.usecase.GetUserUseCase;
import dev.gabul.pagseguro_smart_flutter.user.usecase.NewUserUseCase;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class PagSeguroSmart {
    private PlugPag plugPag;
    private MethodChannel mChannel;
    private NFCUseCase mUseCase;
    private NFCFragment mFragment;
    UserDataManager mUserManager;

     //FUNCTIONS
     PaymentsPresenter payment;

     NFCPresenter nfcPayment;

     //METHODS
     private static final String PAYMENT_DEBIT = "paymentDebit";
     private static final String PAYMENT_CREDIT = "paymentCredit";
     private static final String PAYMENT_CREDIT_PARC = "paymentCreditParc";
     private static final String PAYMENT_VOUCHER = "paymentVoucher";
     private static final String PAYMENT_ABORT = "paymentAbort";
     private static final String LAST_TRANSACTION = "paymentLastTransaction";
     private static final String REFUND = "paymentRefund";

     //NFC
     private static final String WRITE_NFC = "paymentWriteNfc";
     private static final String READ_NFC = "paymentReadNfc";
     private static final String FORMAT_NFC = "paymentFormatNfc";
     private static final String REWRITE_NFC = "paymentReWriteNfc";
     private static final String DEBIT_NFC = "paymentDebitNfc";


    public PagSeguroSmart(Context context, MethodChannel channel) {
        this.plugPag = new PlugPag(context);
        this.mChannel = channel;
    }

    public void initPayment(MethodCall call, MethodChannel.Result result) {
        if(this.payment == null)
            this.payment = new PaymentsPresenter(this.plugPag,this.mChannel);
            this.payment.dispose();


        //if(this.nfcPayment == null)

            mUseCase = new NFCUseCase(plugPag);
            mFragment = new NFCFragment(mChannel);
            mUserManager = new UserDataManager(
                    new GetUserUseCase(mUseCase),
                    new NewUserUseCase(mUseCase),
                    new EditUserUseCase(mUseCase)
            );

            this.nfcPayment = new NFCPresenter(mFragment, mUseCase, mUserManager);
            this.nfcPayment.dispose();

        if(call.method.equals(PAYMENT_DEBIT)) {
           this.payment.doDebitPayment(call.argument("value"));

        }
        else if(call.method.equals(PAYMENT_CREDIT)) {
            this.payment.creditPayment(call.argument("value"));

        }
        else if(call.method.equals(PAYMENT_CREDIT_PARC)) {
            this.payment.creditPaymentParc(call.argument("value"),call.argument("type"),call.argument("parc"));

        }
        else if(call.method.equals(PAYMENT_VOUCHER)) {
            this.payment.doVoucherPayment(call.argument("value"));

        }
       else if(call.method.equals(PAYMENT_ABORT)) {
            this.payment.abortTransaction();
            result.success(true);
        }
        else if(call.method.equals(LAST_TRANSACTION)) {
            this.payment.getLastTransaction();
        }
        else if(call.method.equals(REFUND)) {
            this.payment.doRefund(call.argument("transactionCode"),call.argument("transactionId"));
            result.success(true);
        }
        else if(call.method.equals(READ_NFC)) {
            this.nfcPayment.readNFCCard(call.argument("idEvento"));
        }
        else if(call.method.equals(WRITE_NFC)) {
            this.nfcPayment.writeNFCCard(call.argument("valor"), call.argument("nome"), call.argument("cpf"), call.argument("numeroTag"), call.argument("celular"), call.argument("aberto"), call.argument("idEvento"));
        }
        else if(call.method.equals(REWRITE_NFC)) {
            this.nfcPayment.reWriteNFCCard(call.argument("valor"), call.argument("idEvento"));
        }
        else if(call.method.equals(FORMAT_NFC)) {
            this.nfcPayment.formatNFCCard();
        }
       else if(call.method.equals(DEBIT_NFC)) {
           this.nfcPayment.debitNFCCard(call.argument("idEvento"),call.argument("valorProdutos"));
        }
        else {
            result.notImplemented();
        }
    }


   public void dispose(){
        if(this.payment != null){
            this.payment.dispose();
        }
    }
}


