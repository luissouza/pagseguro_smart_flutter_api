import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:pagseguro_smart_flutter/payments/handler/payment_handler.dart';
import 'package:pagseguro_smart_flutter/payments/utils/payment_types.dart';

const CHANNEL_NAME = "pagseguro_smart_flutter";

class Payment {
  final MethodChannel channel;
  final PaymentHandler paymentHandler;

  Payment ({@required this.channel, @required this.paymentHandler}) {
    channel.setMethodCallHandler(_callHandler);
    
  }

  Future<void> closeMethodCallHandler() async {
    return channel.setMethodCallHandler(null);
  }

  Future<bool> creditPayment(int value) async {
    return channel.invokeMethod(PaymentTypeCall.CREDIT.method, {"value": value});
  }

  Future<bool> creditPaymentParc(int value, int parc, {PaymentTypeCredit type = PaymentTypeCredit.CLIENT}) async {
    return channel.invokeMethod(PaymentTypeCall.CREDIT_PARC.method, {"value": value, "parc": parc, "type": type.value});
  }

  Future<bool> debitPayment(int value) async {
    return channel.invokeMethod(PaymentTypeCall.DEBIT.method, {"value": value});
  }

  Future<bool> voucherPayment(int value) async {
    return channel.invokeMethod(PaymentTypeCall.VOUCHER.method, {"value": value});
  }

  Future<bool> pixPayment(int value) async {
    return channel.invokeMethod(PaymentTypeCall.PIX.method, {"value": value});
  }

  //OPERATIONS
  Future<bool> abortTransaction() async {
    return channel.invokeMethod(PaymentTypeCall.ABORT.method);
  }

  Future<bool> lastTransaction() async {
    return channel.invokeMethod(PaymentTypeCall.LAST_TRANSACTION.method);
  }

  Future<bool> refund({String transactionCode, String transactionId}) async {
    return channel.invokeMethod(PaymentTypeCall.REFUND.method, {"transactionCode": transactionCode, "transactionId": transactionId});
  }

  Future<bool> readNfc(idEvento) async {
    return channel.invokeMethod(PaymentTypeCall.READ_NFC.method, {"idEvento": idEvento});
  }

  Future<bool> writeNfc(valor, nome, cpf, numeroTag, celular, aberto, idEvento) async {
    return channel.invokeMethod(PaymentTypeCall.WRITE_NFC.method, {"valor": valor, "nome": nome, "cpf": cpf, "numeroTag": numeroTag, "celular": celular, "aberto": aberto, "idEvento": idEvento});
  }

  Future<bool> reWriteNfc(valor, idEvento) async {
    return channel.invokeMethod(PaymentTypeCall.REWRITE_NFC.method, {"valor": valor, "idEvento": idEvento});
  }

  Future<bool> refundNfc(valor, idEvento) async {
    return channel.invokeMethod(PaymentTypeCall.REFUND_NFC.method, {"valor": valor, "idEvento": idEvento});
  }

  Future<bool> formatNfc() async {
    return channel.invokeMethod(PaymentTypeCall.FORMAT_NFC.method);
  }

  Future<bool> debitNfc(idEvento, valor) async {
    return channel.invokeMethod(PaymentTypeCall.DEBIT_NFC.method, {"idEvento": idEvento, "valor": valor});
  }

  Future<dynamic> _callHandler(MethodCall call) async {
    switch (call.method.handler) {
      case PaymentTypeHandler.ON_TRANSACTION_SUCCESS:
        {
          paymentHandler.onTransactionSuccess();
        }
        break;
      case PaymentTypeHandler.ON_ERROR:
        {
          paymentHandler.onError(call.arguments);
        }
        break;
      case PaymentTypeHandler.ON_MESSAGE:
        {
          paymentHandler.onMessage(call.arguments);
        }
        break;
      case PaymentTypeHandler.ON_MESSAGE_CODE:
        {
          paymentHandler.onMessageCode(call.arguments);
        }
        break;
      case PaymentTypeHandler.ON_LOADING:
        {
          paymentHandler.onLoading(call.arguments);
        }
        break;
      case PaymentTypeHandler.WRITE_TO_FILE:
        {
          paymentHandler.writeToFile(
              transactionCode: call.arguments['transactionCode'],
              transactionId: call.arguments['transactionId']);
        }
        break;
      case PaymentTypeHandler.ON_ABORTED_SUCCESSFULLY:
        {
          paymentHandler.onAbortedSuccessfully();
        }
        break;
      case PaymentTypeHandler.DISPOSE_DIALOG:
        {
          paymentHandler.disposeDialog();
        }
        break;
      case PaymentTypeHandler.ACTIVE_DIALOG:
        {
          paymentHandler.onActivationDialog();
        }
        break;
      case PaymentTypeHandler.ON_AUTH_PROGRESS:
        {
          paymentHandler.onAuthProgress(call.arguments);
        }
        break;

      case PaymentTypeHandler.ON_TRANSACTION_INFO:
        {
          paymentHandler.onTransactionInfo(
              transactionCode: call.arguments['transactionCode'],
              transactionId: call.arguments['transactionId']);
        }
        break;

      case PaymentTypeHandler.SHOW_SUCCESS:
        {
          paymentHandler.showSuccess(call.arguments);
        }
        break;

      case PaymentTypeHandler.SHOW_SUCCESS_WRITE:
        {
          paymentHandler.showSuccessWrite(call.arguments);
        }
        break;

      case PaymentTypeHandler.SHOW_SUCCESS_RE_WRITE:
        {
          paymentHandler.showSuccessReWrite(call.arguments);
        }
        break;

      case PaymentTypeHandler.SHOW_SUCCESS_DEBIT_NFC:
        {
          paymentHandler.showSuccessDebitNfc(call.arguments);
        }
        break;

      case PaymentTypeHandler.SHOW_SUCCESS_REFUND_NFC:
        {
          paymentHandler.showSuccessRefundNfc(call.arguments);
        }
        break;

      case PaymentTypeHandler.SHOW_SUCCESS_FORMAT:
        {
          paymentHandler.showSuccessFormat(call.arguments);
        }
        break;

      case PaymentTypeHandler.SHOW_ERROR_READ:
        {
          paymentHandler.showErrorRead(call.arguments);
        }
        break;

      case PaymentTypeHandler.SHOW_ERROR_WRITE:
        {
          paymentHandler.showErrorWrite(call.arguments);
        }
        break;

      case PaymentTypeHandler.SHOW_ERROR_RE_WRITE:
        {
          paymentHandler.showErrorReWrite(call.arguments);
        }
        break;

      case PaymentTypeHandler.SHOW_ERROR_FORMAT:
        {
          paymentHandler.showErrorFormat(call.arguments);
        }
        break;

      case PaymentTypeHandler.SHOW_ERROR_DEBIT_NFC:
        {
          paymentHandler.showErrorDebitNfc(call.arguments);
        }
        break;

      case PaymentTypeHandler.SHOW_ERROR_REFUND_NFC:
        {
          paymentHandler.showErrorRefundNfc(call.arguments);
        }
        break;

      default:
        throw "METHOD NOT IMPLEMENTED";
    }
    return true;
  }
}
