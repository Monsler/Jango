import com.monsler.jango.Transcompiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public static void main(String[] args) throws IOException {
    new Transcompiler().compile(Files.readString(Path.of(args[0])));
}