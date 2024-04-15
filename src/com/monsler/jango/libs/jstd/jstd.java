package com.monsler.jango.libs.jstd;

import com.monsler.jango.Lexer;
import com.monsler.jango.Transcompiler;
import com.monsler.jango.libs.Library;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class jstd implements Library {
    @Override
    public boolean invoke(List<String> tokens, String method_name) {
        if (method_name.equals("exit")){
            if (Transcompiler.isFunc()) {
                if (!Transcompiler.included.contains("#include <stdlib.h>")) {
                    String result = Transcompiler.C.toString();
                    Transcompiler.C.setLength(0);
                    Transcompiler.C.append("#include <stdlib.h>\n").append(result);
                    Transcompiler.included.add("#include <stdlib.h>");
                }
                Transcompiler.i += 2;
                StringBuilder b = new StringBuilder();
                while (!tokens.get(Transcompiler.i).equals("]")) {
                    b.append(tokens.get(Transcompiler.i));
                    Transcompiler.i++;
                }
                Transcompiler.C.append("exit").append("(").append(b).append(")");
                Transcompiler.appendNewStr();
            }
        }else if(method_name.equals("write")){
            if(Transcompiler.isFunc()) {
                if (!Transcompiler.included.contains("#include <stdio.h>")) {
                    String result = Transcompiler.C.toString();
                    Transcompiler.C.setLength(0);
                    Transcompiler.C.append("#include <stdio.h>\n").append(result);
                    Transcompiler.included.add("#include <stdio.h>");
                }
                Transcompiler.C.append("printf");
                Transcompiler.i++;
                Transcompiler.C.append("(");
                while (!tokens.get(Transcompiler.i + 1).equals("]")) {
                    Transcompiler.i++;
                    Transcompiler.C.append(tokens.get(Transcompiler.i));
                }
                Transcompiler.C.append(")");
                Transcompiler.appendNewStr();
            }
        }else if(method_name.equals("return")) {
            if(Transcompiler.isFunc()) {
                Transcompiler.i += 2;
                Transcompiler.C.append("return ");
                while (!tokens.get(Transcompiler.i).equals("]")) {
                    Transcompiler.C.append(tokens.get(Transcompiler.i));
                    Transcompiler.i++;
                }
                Transcompiler.appendNewStr();
            }
        }else if(method_name.equals("process")){
            Transcompiler.i++;
            if(tokens.get(Transcompiler.i).equals("mainModule")){
                Transcompiler.C.append("int main(int argc, char **args)");
                Transcompiler.vd = true;
            }
        }else if (method_name.equals("wrapLine")){
            if(Transcompiler.isFunc()) {
                Transcompiler.C.append("printf(\"\\n\")");
                Transcompiler.appendNewStr();
                Transcompiler.i += 2;
            }
        } else if (method_name.equals("rand")) {
            if(Transcompiler.isFunc()){
                if (!Transcompiler.included.contains("#include <stdlib.h>")) {
                    String result = Transcompiler.C.toString();
                    Transcompiler.C.setLength(0);
                    Transcompiler.C.append("#include <stdlib.h>\n").append(result);
                    Transcompiler.included.add("#include <stdlib.h>");
                }
                if (!Transcompiler.included.contains("#include <time.h>")) {
                    String result = Transcompiler.C.toString();
                    Transcompiler.C.setLength(0);
                    Transcompiler.C.append("#include <time.h>\n").append(result);
                    Transcompiler.included.add("#include <time.h>");
                }
                String name = Transcompiler.tokens.get(Transcompiler.i+2);
                int i1 = Integer.parseInt(Transcompiler.tokens.get(Transcompiler.i+4));
                int i2 = Integer.parseInt(Transcompiler.tokens.get(Transcompiler.i+6));
                Transcompiler.i+=6;
                Transcompiler.C.append(STR."\{name} = rand() % (\{i1} + 1 - \{i2}) + \{i1}");
                Transcompiler.appendNewStr();
            }
        } else if (method_name.equals("extern")) {
            Transcompiler.i += 1;
            while (!Transcompiler.tokens.get(Transcompiler.i).equals(".")){
                Transcompiler.C.append(Transcompiler.tokens.get(Transcompiler.i));
                Transcompiler.i++;
            }
            Transcompiler.C.append("\n");

        }else if(method_name.equals("input")){
            if(Transcompiler.isFunc()) {
                String name = Transcompiler.tokens.get(Transcompiler.i + 2);
                String conf = Transcompiler.tokens.get(Transcompiler.i + 4);
                Transcompiler.C.append(STR."scanf(\{conf}, \{name})");
                Transcompiler.appendNewStr();
            }
        }else if(method_name.equals("if")){
            if(Transcompiler.isFunc()){
                Transcompiler.C.append("if(");
                Transcompiler.i+=2;
                while (!Transcompiler.tokens.get(Transcompiler.i).equals("]")){
                    Transcompiler.C.append(Transcompiler.tokens.get(Transcompiler.i));
                    Transcompiler.i++;
                }
                Transcompiler.C.append(")");
            }
        }else if(method_name.equals("else")){
            Transcompiler.C.append("else");
        }else if(method_name.equals("while")){
            if(Transcompiler.isFunc()){
                Transcompiler.C.append("while(");
                Transcompiler.i+=2;
                while (!Transcompiler.tokens.get(Transcompiler.i).equals("]")){
                    Transcompiler.C.append(Transcompiler.tokens.get(Transcompiler.i));
                    Transcompiler.i++;
                }
                Transcompiler.C.append(")");
            }
        }else if(method_name.equals("include")){
            if(Transcompiler.isFunc()) {
                Transcompiler.C.append("#include ");
                Transcompiler.i+=2;
                while (!Transcompiler.tokens.get(Transcompiler.i).equals("]")){
                    Transcompiler.C.append(Transcompiler.tokens.get(Transcompiler.i));
                    Transcompiler.i++;
                }
                Transcompiler.C.append("\n");
            }
        }else if(method_name.equals("using")){
            if(Transcompiler.isFunc()){
                String filename = Transcompiler.tokens.get(Transcompiler.i+2);
                Lexer lexer = new Lexer();
                try {
                    List<String> toks = lexer.tokenize(Files.readString(Path.of(filename)));
                    Transcompiler.tokens.addAll(toks);
                } catch (IOException e) {
                    System.err.println(STR."Error - file \{filename} is not exist.");
                }
            }
        }
        return true;
    }
}
