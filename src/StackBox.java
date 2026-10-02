import javax.swing.*;
import java.awt.*;

public class StackBox extends JPanel {

    private int value;

    public StackBox(int value) {
        this.value = value;
        setPreferredSize(new Dimension(130, 48));
        setMaximumSize(new Dimension(130, 48));
        setOpaque(false);

        JLabel label = new JLabel("" + value, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 17));
        label.setForeground(new Color(15, 69, 69));
        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(204, 251, 241));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

        g2d.setColor(new Color(45, 212, 191));
        g2d.setStroke(new BasicStroke(1.2f));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

        g2d.dispose();
    }
}
