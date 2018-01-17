package com.miragesql.miragesql.util;

import com.miragesql.miragesql.annotation.*;

// Entity annotated, but with default name changed.
@Table(name = "book")
public class BookChanged {
  @PrimaryKey(generationType=PrimaryKey.GenerationType.IDENTITY)
  public Long id;
  public String  name;
  public String  title;
  public Integer year;
}