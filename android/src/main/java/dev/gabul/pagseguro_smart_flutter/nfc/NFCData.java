package dev.gabul.pagseguro_smart_flutter.nfc;

import java.io.Serializable;
import java.util.Objects;

public class NFCData implements Serializable {

    private String idCashier;
    private String value;
    private String name;
    private String cpf;
    private String numberTag;
    private String currentBalance;
    private String cellPhone;
    private String active;
    private String type;

    public NFCData() {
    }

    public NFCData(String idCashier, String value, String name, String cpf, String numberTag, String currentBalance, String cellPhone, String active, String type) {
        this.idCashier = idCashier;
        this.value = value;
        this.name = name;
        this.cpf = cpf;
        this.numberTag = numberTag;
        this.currentBalance = currentBalance;
        this.cellPhone = cellPhone;
        this.active = active;
        this.type = type;
    }

    public String getIdCashier() {
        return idCashier;
    }

    public void setIdCashier(String idCashier) {
        this.idCashier = idCashier;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNumberTag() {
        return numberTag;
    }

    public void setNumberTag(String numberTag) {
        this.numberTag = numberTag;
    }

    public String getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(String currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NFCData)) return false;
        NFCData nfcData = (NFCData) o;
        return Objects.equals(getIdCashier(), nfcData.getIdCashier()) &&
                Objects.equals(getValue(), nfcData.getValue()) &&
                Objects.equals(getName(), nfcData.getName()) &&
                Objects.equals(getCpf(), nfcData.getCpf()) &&
                Objects.equals(getNumberTag(), nfcData.getNumberTag()) &&
                Objects.equals(getCurrentBalance(), nfcData.getCurrentBalance()) &&
                Objects.equals(getCellPhone(), nfcData.getCellPhone()) &&
                Objects.equals(getActive(), nfcData.getActive()) &&
                Objects.equals(getType(), nfcData.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdCashier(), getValue(), getName(), getCpf(), getNumberTag(), getCurrentBalance(), getCellPhone(), getActive(), getType());
    }
}
