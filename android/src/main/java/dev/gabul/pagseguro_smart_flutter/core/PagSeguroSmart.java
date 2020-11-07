package dev.gabul.pagseguro_smart_flutter.core;

import android.content.Context;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagAppIdentification;
import dev.gabul.pagseguro_smart_flutter.nfc.NFCPresenter;
import dev.gabul.pagseguro_smart_flutter.payments.PaymentsPresenter;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class PagSeguroSmart {
    final PlugPag plugPag;
    final MethodChannel mChannel;

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
     private static final String REWRITE_NFC = "paymentReWriteNfc";
     private static final String WRITE_NFC = "paymentWriteNfc";
     private static final String READ_NFC = "paymentReadNfc";
     private static final String FORMAT_NFC = "paymentFormatNfc";
     private static final String DEBIT_NFC = "paymentDebitNfc";

    public PagSeguroSmart(Context context, MethodChannel channel) {
        this.plugPag = new PlugPag(context,new PlugPagAppIdentification("Pagseguro Smart Flutter","0.0.1"));
        this.mChannel = channel;
    }

    public void initPayment(MethodCall call, MethodChannel.Result result) {
        if(this.payment == null)
            this.payment = new PaymentsPresenter(this.plugPag,this.mChannel);
            this.payment.dispose();


        if(this.nfcPayment == null)
            this.nfcPayment = new NFCPresenter(this.plugPag, this.mChannel);
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
            this.nfcPayment.readNFCCard();
        }
        else if(call.method.equals(WRITE_NFC)) {
            this.nfcPayment.writeNFCCard(call.argument("idCaixa"), call.argument("idCarga"), call.argument("valorProdutosString"), call.argument("nome"), call.argument("cpf"), call.argument("numeroTag"), call.argument("saldoAtual"), call.argument("celular"), call.argument("ativo"));
        }
        else if(call.method.equals(REWRITE_NFC)) {
            this.nfcPayment.reWriteNFCCard(call.argument("saldoAtual"), call.argument("valorProdutosString"));
        }
        else if(call.method.equals(FORMAT_NFC)) {
            this.nfcPayment.formatNFCCard();
        }
        else if(call.method.equals(DEBIT_NFC)) {
            this.nfcPayment.debitNFCCard(call.argument("saldoAtual"),call.argument("valorProdutos"));
        }
        else{
            result.notImplemented();
        }
    }


   public void dispose(){
        if(this.payment != null){
            this.payment.dispose();
        }
    }
}


