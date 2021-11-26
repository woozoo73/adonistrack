package com.woozooha.adonistrack.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionEvent extends Event<TransactionInfo> {

    protected String type = "TX";

    public TransactionEvent() {
    }

    public TransactionEvent(TransactionInfo transactionInfo) {
        super(transactionInfo);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(type);
        builder.append("]");
        builder.append(" ");
        builder.append(value);
        return builder.toString();
    }

}
