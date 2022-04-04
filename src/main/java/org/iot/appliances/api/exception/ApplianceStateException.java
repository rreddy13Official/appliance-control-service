package org.iot.appliances.api.exception;

import java.util.Objects;

public class ApplianceStateException extends RuntimeException {


    private final String title;
    private final String detail;
    private final int status;

    public ApplianceStateException(String title, String detail, int status) {
        super(detail);
        this.title = title;
        this.detail = detail;
        this.status = status;
    }

    public ApplianceStateException(String title, String detail, int status, Throwable exception) {
        super(detail, exception);
        this.title = title;
        this.detail = detail;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public int getStatus() {
        return status;
    }
}
