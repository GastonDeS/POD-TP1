package ar.edu.itba.pod.models;

import java.io.Serializable;
import java.util.List;

public class AdminClientResponse<T> implements Serializable {

    private List<T> errorList;
    private int successAmount;

    public AdminClientResponse(List<T> errorList, int successAmount) {
        this.errorList = errorList;
        this.successAmount = successAmount;
    }

    public List<T> getErrorList() {
        return errorList;
    }

    public int getSuccessAmount() {
        return successAmount;
    }
}
