package com.monsler.jango.libs;

import com.monsler.jango.Transcompiler;

import java.util.List;

public interface Library {

    boolean invoke(List<String> tokens, String method_name);
}
