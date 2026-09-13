import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** Handles only the three files explicitly supplied by the command line. */
public final class FileService {
    private FileService() {
    }

    public static String readUtf8(Path path) throws IOException {
        requireAbsolute(path, "输入文件");
        if (!Files.isRegularFile(path)) {
            throw new IOException("输入文件不存在或不是普通文件: " + path);
        }
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    public static void writeUtf8(Path path, String content) throws IOException {
        requireAbsolute(path, "输出文件");
        if (Files.exists(path) && !Files.isRegularFile(path)) {
            throw new IOException("输出路径不是普通文件: " + path);
        }
        Path parent = path.getParent();
        if (parent != null && !Files.isDirectory(parent)) {
            throw new IOException("输出文件所在目录不存在: " + parent);
        }
        Files.writeString(
                path,
                content,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    private static void requireAbsolute(Path path, String description) {
        if (path == null || !path.isAbsolute()) {
            throw new IllegalArgumentException(description + "路径必须是绝对路径");
        }
    }
}
