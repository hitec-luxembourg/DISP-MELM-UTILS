package lu.hitec.pssu.melm.exceptions;

public class LibraryValidatorException extends Exception {

  private static final long serialVersionUID = 5356159319901982032L;

  public LibraryValidatorException(final String msg) {
    super(msg);
  }
  
  public LibraryValidatorException(final String msg, final Exception e) {
    super(msg, e);
  }
}
