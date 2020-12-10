library pagseguro_smart_flutter;

import 'package:flutter/services.dart';
import 'package:pagseguro_smart_flutter/payments/handler/nfc_handler.dart';
import 'package:pagseguro_smart_flutter/payments/handler/payment_handler.dart';
import 'package:pagseguro_smart_flutter/payments/payment.dart';
import 'package:pagseguro_smart_flutter/payments/nfc.dart';

class NfcSmart {
  final MethodChannel _channel;
  Nfc _nfc;

  static NfcSmart _instance;

  NfcSmart(this._channel);

  static NfcSmart instance() {
    if (_instance == null) {
      _instance = NfcSmart(MethodChannel(CHANNEL_NFC_NAME));
    }
    return _instance;
  }

  void initNfc(NfcHandler handler) {
    _nfc = Nfc(channel: _channel, nfcHandler: handler);
  }

   Nfc get nfc {
    if (_nfc == null) {
      throw "NFC NEED INITIALIZE! \n TRY: PagseguroSmart._instance.initNfc(handler)";
    }
    return _nfc;
  }
}
