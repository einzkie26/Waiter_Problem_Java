import javax.swing.*;
import java.awt.*;

public class DescriptionPanel extends JPanel {

    private static final Color NAVY = new Color(28, 44, 68);
    private static final Color INK = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color CANVAS = new Color(241, 245, 249);
    private static final Color SURFACE = new Color(255, 255, 255);
    private static final Color TEAL = new Color(13, 148, 136);

    public DescriptionPanel() {
        setLayout(new BorderLayout());
        setBackground(CANVAS);
        setBorder(BorderFactory.createEmptyBorder(32, 42, 32, 42));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(SURFACE);
        content.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            BorderFactory.createEmptyBorder(28, 32, 28, 32)));

        JPanel titleRow = new JPanel(new BorderLayout(16, 0));
        titleRow.setOpaque(false);
        JLabel title = new JLabel("How the Waiter Problem works");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(NAVY);
        titleRow.add(title, BorderLayout.WEST);
        content.add(titleRow);
        content.add(Box.createVerticalStrut(8));

        JLabel intro = createBodyLabel("The Waiter Problem uses prime numbers to sort plates through repeated passes. "
            + "This page illustrates the complete algorithm; use the Simulation tutorial to learn the controls.");
        content.add(intro);
        content.add(Box.createVerticalStrut(18));
        content.add(new ProcessIllustration());
        content.add(Box.createVerticalStrut(22));

        content.add(createSection("1. Prime-number passes", "Each iteration uses the next prime number: 2, 3, 5, 7, and so on."));
        content.add(Box.createVerticalStrut(14));
        content.add(createSection("2. Divisibility test", "The simulator checks every plate against the current prime number."));
        content.add(Box.createVerticalStrut(14));
        content.add(createSection("3. Separate the stacks", "Plates divisible by the prime move to stack B. All other plates continue in a new stack A."));
        content.add(Box.createVerticalStrut(14));
        content.add(createSection("4. Collect the result", "After the requested iterations, the remaining plates are collected and shown as the final answer."));
        content.add(Box.createVerticalStrut(22));

        JPanel timing = new JPanel(new BorderLayout());
        timing.setBackground(new Color(240, 253, 250));
        timing.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(153, 246, 228)),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        JLabel timingText = createBodyLabel("After completing two runs, open Statistics to compare elapsed time and average step time in milliseconds. "
            + "Use Export CSV to save the comparison.");
        timingText.setForeground(new Color(15, 69, 69));
        timing.add(timingText, BorderLayout.CENTER);
        content.add(timing);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CANVAS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSection(String heading, String description) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JLabel headingLabel = new JLabel(heading);
        headingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headingLabel.setForeground(TEAL);
        section.add(headingLabel);
        section.add(Box.createVerticalStrut(4));
        section.add(createBodyLabel(description));
        return section;
    }

    private JLabel createBodyLabel(String text) {
        JLabel label = new JLabel("<html><div style='width: 760px'>" + text + "</div></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(MUTED);
        return label;
    }

    private static class ProcessIllustration extends JPanel {
        private static final Color BORDER = new Color(148, 163, 184);
        private static final Color BLUE = new Color(219, 234, 254);
        private static final Color BLUE_INK = new Color(30, 64, 175);
        private static final Color AMBER = new Color(254, 243, 199);
        private static final Color AMBER_INK = new Color(146, 64, 14);
        private static final Color TEAL_SOFT = new Color(204, 251, 241);
        private static final Color TEAL_INK = new Color(15, 69, 69);

        ProcessIllustration() {
            setPreferredSize(new Dimension(760, 170));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
            setBackground(new Color(248, 250, 252));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int boxY = 48;
            int boxHeight = 58;
            int boxWidth = 132;
            int firstX = 22;
            int secondX = 196;
            int resultX = 542;
            int branchX = 390;

            drawBox(g, firstX, boxY, boxWidth, boxHeight, BLUE, BLUE_INK,
                "STACK A", "input plates");
            drawBox(g, secondX, boxY, boxWidth, boxHeight, AMBER, AMBER_INK,
                "PRIME TEST", "divide by 2");
            drawBox(g, resultX, 22, boxWidth, boxHeight, TEAL_SOFT, TEAL_INK,
                "STACK B", "divisible plates");
            drawBox(g, resultX, 100, boxWidth, boxHeight, BLUE, BLUE_INK,
                "STACK A", "the rest");

            drawArrow(g, firstX + boxWidth, boxY + boxHeight / 2, secondX, boxY + boxHeight / 2);
            drawArrow(g, secondX + boxWidth, boxY + boxHeight / 2, branchX, 51);
            drawArrow(g, secondX + boxWidth, boxY + boxHeight / 2, branchX, 129);
            drawArrow(g, branchX, 51, resultX, 51);
            drawArrow(g, branchX, 129, resultX, 129);

            g.setColor(new Color(100, 116, 139));
            g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g.drawString("repeat with next prime -> final answers -> Statistics", 214, 164);
            g.dispose();
        }

        private void drawBox(Graphics2D g, int x, int y, int width, int height,
                             Color fill, Color textColor, String heading, String detail) {
            g.setColor(fill);
            g.fillRoundRect(x, y, width, height, 12, 12);
            g.setColor(BORDER);
            g.drawRoundRect(x, y, width, height, 12, 12);
            g.setColor(textColor);
            g.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g.drawString(heading, x + 12, y + 23);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g.drawString(detail, x + 12, y + 42);
        }

        private void drawArrow(Graphics2D g, int startX, int startY, int endX, int endY) {
            g.setColor(new Color(100, 116, 139));
            g.setStroke(new BasicStroke(1.6f));
            g.drawLine(startX, startY, endX, endY);
            double angle = Math.atan2(endY - startY, endX - startX);
            int arrowSize = 6;
            int leftX = (int) (endX - arrowSize * Math.cos(angle - Math.PI / 6));
            int leftY = (int) (endY - arrowSize * Math.sin(angle - Math.PI / 6));
            int rightX = (int) (endX - arrowSize * Math.cos(angle + Math.PI / 6));
            int rightY = (int) (endY - arrowSize * Math.sin(angle + Math.PI / 6));
            g.drawLine(endX, endY, leftX, leftY);
            g.drawLine(endX, endY, rightX, rightY);
        }
    }
}
