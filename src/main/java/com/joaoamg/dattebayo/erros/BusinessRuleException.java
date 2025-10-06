package com.joaoamg.dattebayo.erros;


public class BusinessRuleException extends RuntimeException {
  public BusinessRuleException(String message) {
    super(message);
  }
}