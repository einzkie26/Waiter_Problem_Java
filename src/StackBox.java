import javax.swing.*;
import java.awt.*;

public class StackBox extends JPanel {

    private int value;

    public StackBox(int value) {
        this.value = value;
        setPreferredSize(new Dimension(100, 50));
        setMaximumSize(new Dimension(100, 50));
        setOpaque(false);

        JLabel label = new JLabel("" + value, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradient = new GradientPaint(0, 0, new Color(76, 175, 80),
                                                   0, getHeight(), new Color(56, 142, 60));
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

        g2d.setColor(new Color(40, 100, 40));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

        g2d.dispose();
    }
}
