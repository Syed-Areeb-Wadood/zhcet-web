package amu.zhcet.common.error;

public class UpdateException extends RuntimeException {

    public UpdateException(String field) {
        super(field + " is not updatable for this item!");
    }

}
