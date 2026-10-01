package exception;

public class VendaInvalidaException extends Exception {
    public VendaInvalidaException(String mensagem){
        super(mensagem);
    }
}