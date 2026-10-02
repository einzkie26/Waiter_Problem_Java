import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class StatisticsPanel extends JPanel {

    private static final Color NAVY = new Color(28, 44, 68);
    private static final Color INK = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color SURFACE = new Color(255, 255, 255);
    private static final Color CANVAS = new Color(241, 245, 249);
    private static final Color TEAL = new Color(13, 148, 136);

    private final ComparisonChart elapsedChart;
    private final ComparisonChart averageStepChart;
    private final JLabel summaryLabel;
    private boolean hasCompletedRun;
    private boolean hasPreviousRun;
    private long previousElapsedNanos;
    private double previousAverageMillis;
    private long currentElapsedNanos;
    private double currentAverageMillis;

    public StatisticsPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(CANVAS);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Run statistics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(NAVY);
        summaryLabel = new JLabel("Complete a run to compare results.");
        summaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        summaryLabel.setForeground(MUTED);
        heading.add(title);
        heading.add(Box.createVerticalStrut(4));
        heading.add(summaryLabel);
        header.add(heading, BorderLayout.WEST);

        JButton exportButton = createButton("Export CSV", NAVY);
        exportButton.addActionListener(e -> exportComparisonCsv());
        header.add(exportButton, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel charts = new JPanel(new GridLayout(1, 2, 16, 0));
        charts.setOpaque(false);
        elapsedChart = new ComparisonChart("Elapsed time", "ms");
        averageStepChart = new ComparisonChart("Average step", "ms");
        charts.add(elapsedChart);
        charts.add(averageStepChart);
        add(charts, BorderLayout.CENTER);
    }

    public void updateRuns(boolean hasPreviousRun, long previousElapsedNanos,
                           double previousAverageMillis, long currentElapsedNanos,
                           double currentAverageMillis) {
        this.hasCompletedRun = true;
        this.hasPreviousRun = hasPreviousRun;
        this.previousElapsedNanos = previousElapsedNanos;
        this.previousAverageMillis = previousAverageMillis;
        this.currentElapsedNanos = currentElapsedNanos;
        this.currentAverageMillis = currentAverageMillis;

        double previousMillis = hasPreviousRun ? previousElapsedNanos / 1_000_000.0 : 0;
        double currentMillis = currentElapsedNanos / 1_000_000.0;
        elapsedChart.setValues(previousMillis, currentMillis, hasPreviousRun);
        averageStepChart.setValues(previousAverageMillis, currentAverageMillis, hasPreviousRun);
        summaryLabel.setText(hasPreviousRun
            ? "Comparing the two most recent completed runs."
            : "This is the first completed run. The next run will create a comparison.");
    }

    private JButton createButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        return button;
    }

    private void exportComparisonCsv() {
        if (!hasCompletedRun) {
            JOptionPane.showMessageDialog(this, "",
                                          "No statistics", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("waiter-run-comparison.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (PrintWriter writer = new PrintWriter(new FileWriter(chooser.getSelectedFile()))) {
            writer.println("run,elapsed_ms,average_step_ms");
            if (hasPreviousRun) {
                writer.printf(Locale.US, "previous,%.2f,%.2f%n",
                    previousElapsedNanos / 1_000_000.0, previousAverageMillis);
            }
            writer.printf(Locale.US, "current,%.2f,%.2f%n",
                currentElapsedNanos / 1_000_000.0, currentAverageMillis);
            JOptionPane.showMessageDialog(this, "Statistics exported successfully.",
                                          "CSV exported", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not export the CSV file:\n" + e.getMessage(),
                                          "Export error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ComparisonChart extends JPanel {
        private final String title;
        private final String unit;
        private double previousValue;
        private double currentValue;
        private boolean hasPrevious;

        ComparisonChart(String title, String unit) {
            this.title = title;
            this.unit = unit;
            setPreferredSize(new Dimension(360, 260));
            setBackground(SURFACE);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(16, 16, 12, 16)));
        }

        void setValues(double previousValue, double currentValue, boolean hasPrevious) {
            this.previousValue = previousValue;
            this.currentValue = currentValue;
            this.hasPrevious = hasPrevious;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(INK);
            g.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g.drawString(title, 16, 24);

            int chartTop = 52;
            int chartBottom = getHeight() - 48;
            int chartLeft = 34;
            int chartRight = getWidth() - 24;
            int chartHeight = Math.max(1, chartBottom - chartTop);
            double maxValue = Math.max(previousValue, currentValue);
            if (maxValue <= 0) maxValue = 1;

            g.setColor(new Color(226, 232, 240));
            g.drawLine(chartLeft, chartTop, chartLeft, chartBottom);
            g.drawLine(chartLeft, chartBottom, chartRight, chartBottom);
            for (int i = 1; i <= 3; i++) {
                int y = chartBottom - (chartHeight * i / 3);
                g.drawLine(chartLeft, y, chartRight, y);
            }

            int barWidth = Math.min(72, Math.max(36, (chartRight - chartLeft) / 5));
            int previousX = chartLeft + (chartRight - chartLeft) / 4 - barWidth / 2;
            int currentX = chartLeft + (chartRight - chartLeft) * 3 / 4 - barWidth / 2;
            drawBar(g, previousX, chartBottom, barWidth, previousValue, maxValue,
                    new Color(148, 163, 184), "Previous");
            drawBar(g, currentX, chartBottom, barWidth, currentValue, maxValue,
                    TEAL, "Current");
            g.dispose();
        }

        private void drawBar(Graphics2D g, int x, int bottom, int width, double value,
                             double maxValue, Color color, String label) {
            int height = (int) Math.round((bottom - 58) * value / maxValue);
            if (value > 0) height = Math.max(3, height);
            g.setColor(color);
            g.fillRoundRect(x, bottom - height, width, height, 8, 8);
            g.setColor(INK);
            g.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String valueText = hasPrevious || "Current".equals(label)
                ? String.format(Locale.US, "%.1f %s", value, unit)
                : "-";
            int valueWidth = g.getFontMetrics().stringWidth(valueText);
            g.drawString(valueText, x + (width - valueWidth) / 2, bottom - height - 8);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            int labelWidth = g.getFontMetrics().stringWidth(label);
            g.drawString(label, x + (width - labelWidth) / 2, bottom + 20);
        }
    }
}
