package org.mifos.identityaccountmapper.util;

public enum PaymentModalityEnum {
    ACCOUNT_ID("00"),
    MSISDN("01"),
    VOUCHER("02");
    private final String value;

    PaymentModalityEnum(String value) {
        this.value = value;
    }

    public static String getValueByKey(String key) {
        for (PaymentModalityEnum pair : values()) {
            if (pair.name().equalsIgnoreCase(key)) {
                return pair.getValue();
            }
        }
        return null;
    }
    public static String getKeyByValue(String value) {
        for (PaymentModalityEnum pair : values()) {
            if (pair.getValue().equalsIgnoreCase(value)) {
                return pair.name();
            }
        }
        return null;
    }


    public String getValue() {
        return this.value;
    }
}
