abstract class PaymentHandler {

  void onTransactionSuccess();

  void onError(String message);

  void onMessage(String message);

  void showSuccess(List<dynamic> result);

  void showSuccessWrite(int result);

  void showSuccessReWrite(int result);

  void showSuccessFormat(int result);

  void showSuccessDebitNfc(int result);

  void onLoading(bool show);

  void writeToFile({String transactionCode, String transactionId});

  void onAbortedSuccessfully();

  void disposeDialog();

  void onActivationDialog();

  void onAuthProgress(String message);

  void onTransactionInfo({String transactionCode, String transactionId});
}
