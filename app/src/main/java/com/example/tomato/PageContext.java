package com.example.tomato;

import android.content.Context;

public interface PageContext<T extends Context> {
    void ThisContext(T result);
}