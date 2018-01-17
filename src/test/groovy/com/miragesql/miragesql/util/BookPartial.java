package com.miragesql.miragesql.util;

import com.miragesql.miragesql.annotation.*;

//// Entity partially annotated
public class BookPartial {
    @PrimaryKey(generationType=PrimaryKey.GenerationType.IDENTITY)
    public Long id;
    public String  name;
    public String  title;
    public Integer year;
}