package com.example.sourcebase.domain.enumeration;

import lombok.Getter;

@Getter
public enum ERank {
    A_PLUS("A+"),
    A("A"),
    B_PLUS("B+"),
    B("B"),
    C_PLUS("C+"),
    C("C");

    private final String value;

    ERank(String value) {
        this.value = value;
    }
}
