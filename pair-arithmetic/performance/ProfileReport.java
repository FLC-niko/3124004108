import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedFrame;
import jdk.jfr.consumer.RecordedMethod;
import jdk.jfr.consumer.RecordingFile;

/** 从真实 JFR 执行采样生成可直接放入博客的热点图。 */
public final class ProfileReport {
    private static final int WIDTH = 1500;
    private static final int HEIGHT = 900;
    private static final List<String> PROJECT_CLASSES = List.of(
            "ExpressionGenerator", "BinaryExpression", "NumberExpression", "Rational",
            "ExpressionParser", "ExerciseFiles", "AnswerGrader", "Main");

    private ProfileReport() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("用法：ProfileReport <recording.jfr> <report.png>");
        }
        Path recording = Path.of(args[0]);
        Path output = Path.of(args[1]);
        ProfileData data = readProfile(recording);
        draw(data, output);
        System.out.println("性能热点图已生成：" + output);
    }

    private static ProfileData readProfile(Path recording) throws IOException {
        Map<String, Integer> samples = new HashMap<>();
        int allExecutionSamples = 0;
        Duration duration = Duration.ZERO;
        try (RecordingFile file = new RecordingFile(recording)) {
            while (file.hasMoreEvents()) {
                RecordedEvent event = file.readEvent();
                if (event.getEventType().getName().equals("jdk.ExecutionSample")) {
                    allExecutionSamples++;
                    if (event.getStackTrace() == null) {
                        continue;
                    }
                    for (RecordedFrame frame : event.getStackTrace().getFrames()) {
                        RecordedMethod method = frame.getMethod();
                        String className = method.getType().getName();
                        if (PROJECT_CLASSES.contains(className)) {
                            String name = className + "." + method.getName();
                            samples.merge(name, 1, Integer::sum);
                            break;
                        }
                    }
                }
                Duration end = Duration.between(event.getStartTime(), event.getEndTime());
                if (end.compareTo(duration) > 0) {
                    duration = end;
                }
            }
        }
        List<Map.Entry<String, Integer>> methods = new ArrayList<>(samples.entrySet());
        methods.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                .thenComparing(Map.Entry.comparingByKey()));
        if (methods.isEmpty()) {
            throw new IllegalStateException("JFR 中没有采集到项目方法，请增加基准测试轮数");
        }
        return new ProfileData(allExecutionSamples, methods.stream().limit(9).toList());
    }

    private static void draw(ProfileData data, Path output) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(new Color(247, 249, 252));
        graphics.fillRect(0, 0, WIDTH, HEIGHT);

        graphics.setColor(new Color(27, 35, 48));
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 42));
        graphics.drawString("JFR 性能采样：表达式生成热点", 70, 78);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));
        graphics.setColor(new Color(92, 103, 119));
        graphics.drawString("图中数据来自 performance-recording.jfr 的真实 ExecutionSample", 72, 118);

        int applicationSamples = data.methods().stream().mapToInt(Map.Entry::getValue).sum();
        graphics.setColor(new Color(255, 255, 255));
        graphics.fillRoundRect(70, 150, 1360, 92, 24, 24);
        graphics.setColor(new Color(60, 73, 92));
        graphics.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 22));
        graphics.drawString("全部执行样本：" + data.allSamples()
                + "    可归属到项目方法的热点样本：" + applicationSamples, 105, 207);

        int chartLeft = 500;
        int chartTop = 290;
        int barHeight = 45;
        int rowGap = 64;
        int maximum = data.methods().get(0).getValue();
        Font labelFont = new Font(Font.MONOSPACED, Font.PLAIN, 20);
        graphics.setFont(labelFont);
        FontMetrics metrics = graphics.getFontMetrics();

        for (int index = 0; index < data.methods().size(); index++) {
            Map.Entry<String, Integer> method = data.methods().get(index);
            int y = chartTop + index * rowGap;
            String label = method.getKey();
            graphics.setColor(new Color(41, 50, 65));
            graphics.drawString(label, chartLeft - 25 - metrics.stringWidth(label), y + 30);

            int width = Math.max(4, (int) Math.round(790.0 * method.getValue() / maximum));
            graphics.setColor(index == 0 ? new Color(238, 117, 77) : new Color(65, 135, 224));
            graphics.fillRoundRect(chartLeft, y, width, barHeight, 14, 14);
            graphics.setColor(new Color(35, 45, 58));
            double percent = 100.0 * method.getValue() / applicationSamples;
            graphics.drawString(method.getValue() + " 次  " + String.format("%.1f%%", percent),
                    chartLeft + width + 16, y + 30);
        }

        graphics.setColor(new Color(188, 197, 209));
        graphics.setStroke(new BasicStroke(2));
        graphics.drawLine(chartLeft, chartTop - 20, chartLeft, 840);
        graphics.setColor(new Color(92, 103, 119));
        graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        graphics.drawString("采样口径：每个执行样本取调用栈中最靠上的项目方法", 72, 866);
        graphics.dispose();
        ImageIO.write(image, "png", output.toFile());
    }

    private record ProfileData(int allSamples, List<Map.Entry<String, Integer>> methods) {
    }
}
