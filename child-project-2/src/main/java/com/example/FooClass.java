package com.example;

import java.util.Date;

public class FooClass {

  static {
    System.out.println("***** FooClass is being initialized *****");
    d = new Date();
  }

  static final Date d;
}
