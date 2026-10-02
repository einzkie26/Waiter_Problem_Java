import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public class StackPanel extends JPanel {

    private static final Color BORDER = new Color(203, 213, 225);
    private static final Color INK = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);

    public StackPanel(String title, Stack<Integer> stack) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        setBackground(new Color(248, 250, 252));

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(INK);

        JLabel countLabel = new JLabel(stack.size() + " plate" + (stack.size() == 1 ? "" : "s"));
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        countLabel.setForeground(MUTED);
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        header.add(titleLabel, BorderLayout.WEST);
        header.add(countLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel items = new JPanel();
        items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
        items.setBackground(new Color(248, 250, 252));
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
        scrollPane.getViewport().setBackground(new Color(248, 250, 252));

        add(scrollPane, BorderLayout.CENTER);
    }
}
