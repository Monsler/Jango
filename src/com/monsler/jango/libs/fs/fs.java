package com.monsler.jango.libs.fs;

import com.monsler.jango.Transcompiler;
import com.monsler.jango.libs.Library;

import java.util.List;

public class fs implements Library {
    @Override
    public boolean invoke(List<String> tokens, String method_name) {
        if (!Transcompiler.included.contains("#include <stdio.h>")) {
            String result = Transcompiler.C.toString();
            Transcompiler.C.setLength(0);
            Transcompiler.C.append("#include <stdio.h>\n").append(result);
            Transcompiler.included.add("#include <stdio.h>");
        }
        if (method_name.equals("readFile")){
            if(Transcompiler.isFunc()){
                String name = Transcompiler.tokens.get(Transcompiler.i+2);
                String var = Transcompiler.tokens.get(Transcompiler.i+4);
                String mode = Transcompiler.tokens.get(Transcompiler.i+6);
                Transcompiler.i += 6;
                Transcompiler.C.append(STR."\{var} = fopen(\{name}, \{mode})");
                Transcompiler.appendNewStr();
            }
        }else if(method_name.equals("closeFile")){
            if(Transcompiler.isFunc()){
                String name = Transcompiler.tokens.get(Transcompiler.i+2);
                Transcompiler.i += 2;
                Transcompiler.C.append(STR."fclose(\{name})");
                Transcompiler.appendNewStr();
            }
        }
        return true;
    }
}
