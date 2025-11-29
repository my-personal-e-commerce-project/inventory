package microservice.cloud.inventory.product.domain.exception;

public class InvalidProductException extends RuntimeException {

    public InvalidProductException(String msg) {
        super(msg);
    }
}
