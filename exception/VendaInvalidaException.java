package exception;

public class VendaInvalidaException extends Exception {
    public VendaInsuficienteException(String mensagem){
        super(mensagem);
    }
}