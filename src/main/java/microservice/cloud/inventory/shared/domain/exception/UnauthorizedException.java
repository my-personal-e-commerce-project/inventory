package microservice.cloud.inventory.shared.domain.exception;

public class UnauthorizedException extends RuntimeException{
    
    public UnauthorizedException(String msg) {
        super(msg);
    }
}
