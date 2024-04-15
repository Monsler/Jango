package com.monsler.jango;

import com.monsler.jango.libs.Library;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.lang.StringTemplate.STR;

public class Transcompiler {
    public static StringBuilder C;

    public static final double langVersion = 1.7;
    public static int i;
    public static List<String> tokens;
    public static List<String> included;
    public static boolean vd;

    public void compile(String text){
        long nowtime, lasttime;
        lasttime = System.currentTimeMillis();
        Lexer lexer = new Lexer();
        List<Library> modules = new ArrayList<>();
        C = new StringBuilder();
        C.append(STR."#define JANGO_VERSION \"\{langVersion}\"\n");
        List<String> tokens = lexer.tokenize(text);
        included = new ArrayList<>();
        Transcompiler.tokens = tokens;
        vd = false;
        for(i = 0; i < tokens.size(); i++){
            if(tokens.get(i).equals("proc")){
                vd = true;
                C.append("void").append(" ").append(tokens.get(i-2)).append("(");
                i++;
                while (!tokens.get(i).equals("]")){
                    i++;
                    if(tokens.get(i).equals("[")){
                        C.append("(");
                    }else if(tokens.get(i).equals("]")){
                        C.append(")");
                    }else{
                        C.append(tokens.get(i));
                    }

                }
            }else if(tokens.get(i).equals("is")){
                if (vd){
                    C.append("{\n");
                    vd = false;
                }
            }else if(tokens.get(i).equals(".")){
                C.append("}\n");
            }else if(tokens.get(i).equals("var")){
                i++;
                String varname = tokens.get(i);
                i+=3;
                while (!tokens.get(i).equals("]")){
                    C.append(tokens.get(i));
                    i++;
                }
                final StringBuilder val = new StringBuilder();
                i+=2;
                while (!tokens.get(i).equals(">")){
                    val.append(tokens.get(i));
                    i++;
                }
                if(!val.toString().equals("_") && !val.toString().equals("_str") && !val.toString().equals("_mul")){
                    C.append(" ").append(varname);
                    C.append(" = ").append(val);
                }else if(val.toString().equals("_str")){
                    C.append(" ").append(varname);
                    if (!Transcompiler.included.contains("#include <stdlib.h>")) {
                        String result = Transcompiler.C.toString();
                        Transcompiler.C.setLength(0);
                        Transcompiler.C.append("#include <stdlib.h>\n").append(result);
                        Transcompiler.included.add("#include <stdlib.h>");
                    }
                    C.append(" = (char*) malloc(2056*sizeof(char))");
                }else if(val.toString().equals("_mul")){
                    C.append(" *").append(varname);
                }else if(val.toString().equals("_")){
                    C.append(" ").append(varname);
                }
                appendNewStr();
            }else if(tokens.get(i).equals("do")){
                C.append("{\n");
            }else if(tokens.get(i).equals("for")){
                i++;
                String var_name = tokens.get(i);
                StringBuilder val = new StringBuilder();
                if(tokens.get(i+1).equals("is")) {
                    i++;
                    while (!tokens.get(i).equals(".") && !tokens.get(i + 1).equals(".")) {
                        i++;
                        val.append(tokens.get(i));
                    }
                    i += 2;
                    C.append(STR."for (\{var_name} = \{val}; \{var_name} <= ");
                    val.setLength(0);
                    while (!tokens.get(i + 1).equals("do")) {
                        i++;
                        val.append(tokens.get(i));
                    }
                    C.append(STR."\{val}; \{var_name}++)");
                }else if(tokens.get(i+1).equals("by")) {
                    i++;
                    while (!tokens.get(i).equals(".") && !tokens.get(i + 1).equals(".")) {
                        i++;
                        val.append(tokens.get(i));
                    }
                    i += 2;
                    C.append(STR."for (\{var_name} = \{val}; \{var_name} < ");
                    val.setLength(0);
                    while (!tokens.get(i + 1).equals("do")) {
                        i++;
                        val.append(tokens.get(i));
                    }
                    C.append(STR."\{val}; \{var_name}++)");
                }
            }else if(tokens.get(i).equals("import")) {
                String n = tokens.get(i + 1);
                try {
                    Library library = (Library) Class.forName(STR."com.monsler.jango.libs.\{n}.\{n}").newInstance();
                    modules.add(library);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }else if(tokens.get(i).equals("edit")){
                String vname = tokens.get(i+2);
                i += 5;
                final StringBuilder builder = new StringBuilder();
                while (!tokens.get(i).equals(">")){
                    builder.append(tokens.get(i));
                    i++;
                }
                C.append(STR."\{vname} = \{vname} + \{builder}");
                appendNewStr();
            }else {
                boolean find = false;
                for (int x = 0; x < modules.size(); x++) {
                    find = modules.get(x).invoke(tokens, tokens.get(i));
                }
                if(!find){
                    System.err.println(STR."Warning: undefined member [\{tokens.get(i)}]");
                }
            }
        }

        System.out.println(C);
        FileOutputStream stream;
        try {
            stream = new FileOutputStream("out.c");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            stream.write(C.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            String e = execCmd("gcc out.c");
            System.out.println(e);
            nowtime = System.currentTimeMillis() - lasttime;
            File old = new File("out.bin");
            if (old.exists()){
                old.delete();
            }
            System.out.println(STR."Compilation successfull in \{nowtime} ms.");
            Path path = Path.of("a.out");
            Files.move(path, path.resolveSibling("out.bin"));
            System.exit(0);
        }catch (Exception e){
            System.err.println("JANGO SIGSEGV!!!");
        }

    }

    public static boolean isFunc(){
        return tokens.get(i + 1).equals("[");
    }

    public static void appendNewStr(){
        C.append(";\n");
    }
    public static String execCmd(String cmd) throws java.io.IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
