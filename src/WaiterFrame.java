import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Stack;

public class WaiterFrame extends JFrame {

    private JTextField inputPlates, inputQ;
    private JButton startBtn, resetBtn, pauseBtn, resumeBtn, soundBtn;
    private JSlider speedSlider;
    private boolean soundEnabled = true;
    private JPanel stackArea, controlPanel, statsPanel;
    private JLabel currentPrimeLabel, iterationLabel, aStackSizeLabel, bStackSizeLabel;
    private JTextArea stepsArea;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private javax.swing.Timer animationTimer;

    private ArrayList<Stack<Integer>> AStacks = new ArrayList<>();
    private ArrayList<Stack<Integer>> BStacks = new ArrayList<>();
    private ArrayList<Integer> answers = new ArrayList<>();

    public WaiterFrame() {
        setTitle("Waiter Problem Solver");
        setSize(1400, 800);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 245, 250));

        setupControlPanel();
        setupStatsPanel();
        setupStackArea();

        setVisible(true);
    }

    private void setupControlPanel() {
        controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(new Color(255, 255, 255));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel platesLabel = new JLabel("Plate Numbers:");
        platesLabel.setForeground(new Color(50, 50, 50));
        platesLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        controlPanel.add(platesLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        inputPlates = new JTextField(25);
        inputPlates.setText("3 4 7 6 5");
        inputPlates.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        inputPlates.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        controlPanel.add(inputPlates, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel qLabel = new JLabel("Q Iterations:");
        qLabel.setForeground(new Color(50, 50, 50));
        qLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        controlPanel.add(qLabel, gbc);

        gbc.gridx = 4; gbc.gridy = 0;
        inputQ = new JTextField(5);
        inputQ.setText("3");
        inputQ.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        inputQ.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        controlPanel.add(inputQ, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        startBtn = createButton("Start", new Color(76, 175, 80));
        startBtn.addActionListener(e -> startSimulation());
        controlPanel.add(startBtn, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        pauseBtn = createButton("Pause", new Color(255, 152, 0));
        pauseBtn.setEnabled(false);
        pauseBtn.addActionListener(e -> pauseSimulation());
        controlPanel.add(pauseBtn, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        resumeBtn = createButton("Resume", new Color(76, 175, 80));
        resumeBtn.setEnabled(false);
        resumeBtn.addActionListener(e -> resumeSimulation());
        controlPanel.add(resumeBtn, gbc);

        gbc.gridx = 3; gbc.gridy = 1;
        resetBtn = createButton("Reset", new Color(244, 67, 54));
        resetBtn.addActionListener(e -> resetSimulation());
        controlPanel.add(resetBtn, gbc);

        gbc.gridx = 4; gbc.gridy = 1;
        soundBtn = createButton("Sound ON", new Color(156, 39, 176));
        soundBtn.addActionListener(e -> toggleSound());
        controlPanel.add(soundBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel speedLabel = new JLabel("Speed:");
        speedLabel.setForeground(new Color(50, 50, 50));
        speedLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        controlPanel.add(speedLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3;
        speedSlider = new JSlider(50, 1000, 200);
        speedSlider.setBackground(Color.WHITE);
        speedSlider.setForeground(new Color(76, 175, 80));
        controlPanel.add(speedSlider, gbc);

        add(controlPanel, BorderLayout.NORTH);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void toggleSound() {
        soundEnabled = !soundEnabled;
        soundBtn.setText(soundEnabled ? "Sound ON" : "Sound OFF");
        SoundManager.setSoundEnabled(soundEnabled);
    }

    private void setupStatsPanel() {
        statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        statsPanel.setBackground(new Color(255, 255, 255));
        statsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        iterationLabel = createStatLabel("Iteration: 0", new Color(33, 150, 243));
        currentPrimeLabel = createStatLabel("Prime: -", new Color(255, 152, 0));
        aStackSizeLabel = createStatLabel("A Stack: 0", new Color(76, 175, 80));
        bStackSizeLabel = createStatLabel("B Stack: 0", new Color(244, 67, 54));

        statsPanel.add(iterationLabel);
        statsPanel.add(currentPrimeLabel);
        statsPanel.add(aStackSizeLabel);
        statsPanel.add(bStackSizeLabel);

        add(statsPanel, BorderLayout.SOUTH);
    }

    private JLabel createStatLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    private void setupStackArea() {
        stackArea = new JPanel(new BorderLayout(10, 10));
        stackArea.setBackground(new Color(245, 245, 250));
        stackArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel visualPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        visualPanel.setBackground(new Color(245, 245, 250));
        stackArea.add(visualPanel, BorderLayout.WEST);
        
        stepsArea = new JTextArea();
        stepsArea.setEditable(false);
        stepsArea.setBackground(new Color(250, 250, 250));
        stepsArea.setForeground(new Color(50, 50, 50));
        stepsArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        stepsArea.setMargin(new Insets(10, 10, 10, 10));
        stepsArea.setLineWrap(true);
        stepsArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(stepsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        scrollPane.setBackground(new Color(245, 245, 250));
        stackArea.add(scrollPane, BorderLayout.CENTER);
        
        add(stackArea, BorderLayout.CENTER);
    }

    private void resetStacks() {
        JPanel visualPanel = (JPanel)stackArea.getComponent(0);
        visualPanel.removeAll();
        AStacks.clear();
        BStacks.clear();
        answers.clear();
        stepsArea.setText("");
        stackArea.revalidate();
        stackArea.repaint();
    }

    private void startSimulation() {
        if (isRunning) return;
        SoundManager.playSound("start");

        try {
            String[] nums = inputPlates.getText().trim().split("\\s+");
            for (String num : nums) {
                int val = Integer.parseInt(num);
                if (val <= 0) throw new NumberFormatException();
            }
            int q = Integer.parseInt(inputQ.getText().trim());
            if (q <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid positive integers.",
                                          "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        resetSimulation();
        isRunning = true;
        isPaused = false;

        startBtn.setEnabled(false);
        pauseBtn.setEnabled(true);
        resumeBtn.setEnabled(false);
        resetBtn.setEnabled(true);

        String[] nums = inputPlates.getText().trim().split("\\s+");
        Stack<Integer> A0 = new Stack<>();
        for (String n : nums) A0.push(Integer.parseInt(n));
        AStacks.add(A0);

        int q = Integer.parseInt(inputQ.getText().trim());

        animationTimer = new javax.swing.Timer(speedSlider.getValue(), new ActionListener() {
            private int currentIteration = 1;
            private int currentPrime = getPrime(currentIteration);
            private Stack<Integer> currentAi = new Stack<>();
            private Stack<Integer> currentBi = new Stack<>();
            private Stack<Integer> tempStack = new Stack<>();

            {
                Stack<Integer> previousA = AStacks.get(AStacks.size() - 1);
                for (Integer plate : previousA) tempStack.push(plate);
                addStep("\n=== Iteration " + currentIteration + " (Prime: " + currentPrime + ") ===");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPaused || !isRunning) return;

                if (!tempStack.isEmpty()) {
                    int plate = tempStack.pop();
                    SoundManager.playSound("pop");
                    addStep("Pop from A: " + plate);
                    if (plate % currentPrime == 0) {
                        currentBi.push(plate);
                        SoundManager.playSound("push");
                        addStep(" Push " + plate + " to B");
                    } else {
                        currentAi.push(plate);
                        SoundManager.playSound("push");
                        addStep(" Push " + plate + " to newA");
                    }
                    updateStacks(currentAi, currentBi, currentIteration);
                    updateStats(currentIteration, currentPrime, currentAi.size(), currentBi.size());
                } else {
                    AStacks.add(currentAi);
                    BStacks.add(currentBi);
                    addStep("Collect from B: ");
                    Stack<Integer> tempB = new Stack<>();
                    for (Integer v : currentBi) tempB.push(v);
                    while (!tempB.isEmpty()) {
                        int val = tempB.pop();
                        answers.add(val);
                        addStep("  " + val + " → answers");
                    }

                    currentIteration++;
                    if (currentIteration <= q) {
                        currentPrime = getPrime(currentIteration);
                        currentAi = new Stack<>();
                        currentBi = new Stack<>();
                        Stack<Integer> previousA = AStacks.get(AStacks.size() - 1);
                        for (Integer plate : previousA) tempStack.push(plate);
                        addStep("\n=== Iteration " + currentIteration + " (Prime: " + currentPrime + ") ===");
                        updateStats(currentIteration, currentPrime, 0, 0);
                    } else {
                        addStep("\n=== Final: Collect remaining A ===");
                        Stack<Integer> tempA = new Stack<>();
                        for (Integer v : currentAi) tempA.push(v);
                        while (!tempA.isEmpty()) {
                            int val = tempA.pop();
                            answers.add(val);
                            addStep("  " + val + " → answers");
                        }
                        addStep("\n========== FINAL ANSWERS ==========");
                        addStep("Result: " + answers);
                        stopSimulation();
                    }
                }
            }
        });
        animationTimer.start();
    }

    private void pauseSimulation() {
        if (!isRunning) return;
        isPaused = true;
        pauseBtn.setEnabled(false);
        resumeBtn.setEnabled(true);
        if (animationTimer != null) animationTimer.stop();
    }

    private void resumeSimulation() {
        if (!isRunning || !isPaused) return;
        isPaused = false;
        pauseBtn.setEnabled(true);
        resumeBtn.setEnabled(false);
        if (animationTimer != null) animationTimer.start();
    }

    private void resetSimulation() {
        SoundManager.playSound("reset");
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        isRunning = false;
        isPaused = false;

        startBtn.setEnabled(true);
        pauseBtn.setEnabled(false);
        resumeBtn.setEnabled(false);
        resetBtn.setEnabled(true);

        resetStacks();
        updateStats(0, 0, 0, 0);
    }

    private void stopSimulation() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        isRunning = false;
        isPaused = false;

        startBtn.setEnabled(true);
        pauseBtn.setEnabled(false);
        resumeBtn.setEnabled(false);
        resetBtn.setEnabled(true);

        SoundManager.playSound("complete");
        JOptionPane.showMessageDialog(this, "Simulation completed!\n\nFinal Answers: " + answers, 
                                      "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateStacks(Stack<Integer> Ai, Stack<Integer> Bi, int iteration) {
        JPanel visualPanel = (JPanel)stackArea.getComponent(0);
        visualPanel.removeAll();

        JPanel aPanel = new StackPanel("A Stack (Iteration " + iteration + ")", Ai);
        JPanel bPanel = new StackPanel("B Stack " + iteration, Bi);

        visualPanel.add(aPanel);
        visualPanel.add(bPanel);

        visualPanel.revalidate();
        visualPanel.repaint();
    }

    private void addStep(String step) {
        stepsArea.append(step + "\n");
        stepsArea.setCaretPosition(stepsArea.getDocument().getLength());
    }

    private void updateStats(int iteration, int prime, int aSize, int bSize) {
        iterationLabel.setText("Iteration: " + iteration);
        currentPrimeLabel.setText("Prime: " + (prime == 0 ? "-" : prime));
        aStackSizeLabel.setText("A Stack: " + aSize);
        bStackSizeLabel.setText("B Stack: " + bSize);
    }

    private int getPrime(int n) {
        int count = 0, num = 2;
        while (true) {
            if (isPrime(num)) {
                count++;
                if (count == n) return num;
            }
            num++;
        }
    }

    private boolean isPrime(int num) {
        if (num < 2) return false;
        for (int i = 2; i * i <= num; i++)
            if (num % i == 0) return false;
        return true;
    }

}
