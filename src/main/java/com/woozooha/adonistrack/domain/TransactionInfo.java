package com.woozooha.adonistrack.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class TransactionInfo implements Call, Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Long start;

    private Boolean autoCommit;

    private Boolean commit;

    private Boolean rollback;

    private String message;

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

        // id 비교 (null 체크 포함)
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        // autoCommit 비교
        if (autoCommit != null ? !autoCommit.equals(that.autoCommit) : that.autoCommit != null) return false;
        // commit 비교
        if (commit != null ? !commit.equals(that.commit) : that.commit != null) return false;
        // rollback 비교
        if (rollback != null ? !rollback.equals(that.rollback) : that.rollback != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        // Java 1.5 표준 방식 (이펙티브 자바 스타일)
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (autoCommit != null ? autoCommit.hashCode() : 0);
        result = 31 * result + (commit != null ? commit.hashCode() : 0);
        result = 31 * result + (rollback != null ? rollback.hashCode() : 0);
        return result;
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
