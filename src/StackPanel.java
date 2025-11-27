import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public class StackPanel extends JPanel {

    public StackPanel(String title, Stack<Integer> stack) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setBackground(new Color(255, 255, 255));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel items = new JPanel();
        items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
        items.setBackground(new Color(255, 255, 255));
        items.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Stack<Integer> temp = (Stack<Integer>) stack.clone();

        while (!temp.isEmpty()) {
            int value = temp.pop();
            StackBox box = new StackBox(value);
            box.setAlignmentX(Component.CENTER_ALIGNMENT);
            items.add(box);
            items.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        JScrollPane scrollPane = new JScrollPane(items);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(255, 255, 255));

        add(scrollPane, BorderLayout.CENTER);
    }
}
