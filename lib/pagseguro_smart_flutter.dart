library pagseguro_smart_flutter;

import 'package:flutter/services.dart';
import 'package:pagseguro_smart_flutter/payments/handler/nfc_handler.dart';
import 'package:pagseguro_smart_flutter/payments/handler/payment_handler.dart';
import 'package:pagseguro_smart_flutter/payments/payment.dart';

class PagseguroSmart {
  final MethodChannel _channel;
  Payment _payment;

  static PagseguroSmart _instance;

  PagseguroSmart(this._channel);

  static PagseguroSmart instance() {
    if (_instance == null) {
      _instance = PagseguroSmart(MethodChannel(CHANNEL_NAME));
    }
    return _instance;
  }

  void initPayment(PaymentHandler handler) {
    _payment = Payment(channel: _channel, paymentHandler: handler);
  }

  void initNfc(NfcHandler handler) {
    _nfc = Nfc(channel: _channel, paymentHandler: handler);
  }

  Payment get payment {
    if (_payment == null) {
      throw "PAYMENT NEED INITIALIZE! \n TRY: PagseguroSmart._instance.initPayment(handler)";
    }
    return _payment;
  }

   Nfc get nfc {
    if (_nfc == null) {
      throw "NFC NEED INITIALIZE! \n TRY: PagseguroSmart._instance.initNfc(handler)";
    }
    return _nfc;
  }
}
