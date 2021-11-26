package com.woozooha.adonistrack.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class TransactionInfo implements Call, Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Long start;

    private Boolean autoCommit;

    private Boolean commit;

    private Boolean rollback;

    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("TX-%d", id));
        if (autoCommit != null) {
            if (autoCommit) {
                builder.append(" end");
            } else {
                builder.append(" begin");
            }
        }
        if (commit != null) {
            builder.append(" commit");
        }
        if (rollback != null) {
            builder.append(" rollback");
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionInfo that = (TransactionInfo) o;
        return Objects.equals(id, that.id) && Objects.equals(autoCommit, that.autoCommit) && Objects.equals(commit, that.commit) && Objects.equals(rollback, that.rollback);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, autoCommit, commit, rollback);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[id=");
        builder.append(id);
        builder.append(", start=");
        builder.append(start);
        if (autoCommit != null) {
            builder.append(", autoCommit=");
            builder.append(autoCommit);
        }
        if (commit != null) {
            builder.append(", commit=");
            builder.append(commit);
        }
        if (rollback != null) {
            builder.append(", rollback=");
            builder.append(rollback);
        }
        builder.append("]");
        return builder.toString();
    }

}
