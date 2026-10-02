import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Stack;

public class WaiterFrame extends JFrame {

    private static final Color NAVY = new Color(28, 44, 68);
    private static final Color INK = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color SURFACE = new Color(255, 255, 255);
    private static final Color CANVAS = new Color(241, 245, 249);
    private static final Color TEAL = new Color(13, 148, 136);
    private static final Color AMBER = new Color(217, 119, 6);
    private static final Color RED = new Color(220, 38, 38);
    private static final int DEFAULT_SPEED_MILLIS = 500;

    private JTextField inputPlates, inputQ;
    private JButton startBtn, resetBtn, pauseBtn, resumeBtn, soundBtn;
    private JSlider speedSlider;
    private boolean soundEnabled = true;
    private JPanel stackArea, controlPanel, statsPanel, comparisonPanel, visualPanel;
    private JPanel simulationTutorialPanel;
    private JPanel tutorialInputArea;
    private JTabbedPane contentTabs;
    private JLabel currentPrimeLabel, iterationLabel, aStackSizeLabel, bStackSizeLabel;
    private JLabel elapsedTimeLabel, averageStepLabel;
    private JLabel previousElapsedLabel, previousAverageLabel;
    private JTextArea stepsArea;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private javax.swing.Timer animationTimer;
    private long simulationStartNanos;
    private long processingNanos;
    private long stepCount;
    private long lastRunElapsedNanos;
    private double lastRunAverageMillis;
    private boolean hasPreviousRun;
    private long previousCompletedElapsedNanos;
    private double previousCompletedAverageMillis;
    private long currentCompletedElapsedNanos;
    private double currentCompletedAverageMillis;
    private boolean hasPreviousCompletedRun;
    private StatisticsPanel statisticsPanel;
    private DescriptionPanel descriptionPanel;
    private JLabel tutorialInstructionLabel, tutorialStepLabel;
    private JButton tutorialHighlightButton, tutorialHeaderButton;
    private JPanel tutorialStepsPanel;
    private ArrayList<JLabel> tutorialStepLabels = new ArrayList<>();
    private int tutorialStep = -1;
    private TutorialOverlay tutorialOverlay;
    private Border inputPlatesDefaultBorder, inputQDefaultBorder, startButtonDefaultBorder;
    private Border visualPanelDefaultBorder, statsPanelDefaultBorder, comparisonPanelDefaultBorder;

    private ArrayList<Stack<Integer>> AStacks = new ArrayList<>();
    private ArrayList<Stack<Integer>> BStacks = new ArrayList<>();
    private ArrayList<Integer> answers = new ArrayList<>();

    public WaiterFrame() {
        setTitle("Waiter Problem Solver");
        setSize(1280, 780);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        setLocationRelativeTo(null);
        getContentPane().setBackground(CANVAS);

        setupControlPanel();
        setupStatsPanel();
        setupStackArea();
        tutorialOverlay = new TutorialOverlay();
        setGlassPane(tutorialOverlay);

        setVisible(true);
    }

    private void setupControlPanel() {
        controlPanel = new JPanel(new BorderLayout(20, 0));
        controlPanel.setBackground(SURFACE);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(18, 28, 18, 28)));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Waiter Problem");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(NAVY);
        titlePanel.add(title);
        controlPanel.add(titlePanel, BorderLayout.WEST);

        JPanel inputs = new JPanel(new GridBagLayout());
        tutorialInputArea = inputs;
        inputs.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 6, 0, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        inputs.add(createFieldLabel("PLATES"), gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPlates = new JTextField(25);
        inputPlates.setText("3 4 7 6 5");
        styleInput(inputPlates);
        inputs.add(inputPlates, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        inputs.add(createFieldLabel("ITERATIONS"), gbc);

        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        inputQ = new JTextField(5);
        inputQ.setText("3");
        styleInput(inputQ);
        inputs.add(inputQ, gbc);

        gbc.gridx = 4; gbc.fill = GridBagConstraints.NONE;
        inputs.add(createFieldLabel("SPEED"), gbc);
        gbc.gridx = 5; gbc.weightx = 0.7; gbc.fill = GridBagConstraints.HORIZONTAL;
        speedSlider = new JSlider(50, 1000, DEFAULT_SPEED_MILLIS);
        speedSlider.setBackground(SURFACE);
        speedSlider.setForeground(TEAL);
        speedSlider.setToolTipText("Fixed animation interval: " + DEFAULT_SPEED_MILLIS + " ms");
        speedSlider.setEnabled(false);
        inputs.add(speedSlider, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0));
        actions.setOpaque(false);
        startBtn = createButton("Start", TEAL);
        startBtn.addActionListener(e -> startSimulation());
        actions.add(startBtn);
        pauseBtn = createButton("Pause", AMBER);
        pauseBtn.setEnabled(false);
        pauseBtn.addActionListener(e -> pauseSimulation());
        actions.add(pauseBtn);
        resumeBtn = createButton("Resume", TEAL);
        resumeBtn.setEnabled(false);
        resumeBtn.addActionListener(e -> resumeSimulation());
        actions.add(resumeBtn);
        resetBtn = createButton("Reset", RED);
        resetBtn.addActionListener(e -> resetSimulation());
        actions.add(resetBtn);
        tutorialHeaderButton = createButton("Tutorial", NAVY);
        tutorialHeaderButton.setToolTipText("Open a guided, step-by-step tutorial for operating the simulation.");
        tutorialHeaderButton.addActionListener(e -> startUserTutorial());
        actions.add(tutorialHeaderButton);
        soundBtn = createButton("Sound ON", NAVY);
        soundBtn.addActionListener(e -> toggleSound());
        actions.add(soundBtn);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setOpaque(false);
        rightPanel.add(inputs, BorderLayout.CENTER);
        rightPanel.add(actions, BorderLayout.SOUTH);
        controlPanel.add(rightPanel, BorderLayout.CENTER);

        add(controlPanel, BorderLayout.NORTH);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        return label;
    }

    private void styleInput(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(INK);
        field.setBackground(new Color(248, 250, 252));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            BorderFactory.createEmptyBorder(6, 9, 6, 9)));
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    private void toggleSound() {
        soundEnabled = !soundEnabled;
        soundBtn.setText(soundEnabled ? "Sound ON" : "Sound OFF");
        SoundManager.setSoundEnabled(soundEnabled);
    }

    private void setupStatsPanel() {
        statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 26, 10));
        statsPanel.setBackground(SURFACE);
        statsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        iterationLabel = createStatLabel("ITERATION  0", TEAL);
        currentPrimeLabel = createStatLabel("CURRENT PRIME  -", AMBER);
        aStackSizeLabel = createStatLabel("A STACK  0", new Color(37, 99, 235));
        bStackSizeLabel = createStatLabel("B STACK  0", RED);

        statsPanel.add(iterationLabel);
        statsPanel.add(currentPrimeLabel);
        statsPanel.add(aStackSizeLabel);
        statsPanel.add(bStackSizeLabel);

        comparisonPanel = createComparisonPanel();
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(SURFACE);
        footer.add(statsPanel, BorderLayout.NORTH);
        footer.add(comparisonPanel, BorderLayout.SOUTH);

        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createComparisonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(8, 28, 10, 28)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 12, 2, 12);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1;
        panel.add(createComparisonLabel("RUN COMPARISON", MUTED, Font.BOLD, 10), gbc);
        gbc.gridx = 1; gbc.weightx = 0;
        panel.add(createComparisonLabel("PREVIOUS RUN", MUTED, Font.BOLD, 10), gbc);
        gbc.gridx = 2;
        panel.add(createComparisonLabel("CURRENT RUN", NAVY, Font.BOLD, 10), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createComparisonLabel("Elapsed time", MUTED, Font.PLAIN, 11), gbc);
        previousElapsedLabel = createComparisonLabel("-", MUTED, Font.BOLD, 11);
        gbc.gridx = 1;
        panel.add(previousElapsedLabel, gbc);
        elapsedTimeLabel = createComparisonLabel("0 ms", NAVY, Font.BOLD, 11);
        gbc.gridx = 2;
        panel.add(elapsedTimeLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createComparisonLabel("Average step", MUTED, Font.PLAIN, 11), gbc);
        previousAverageLabel = createComparisonLabel("-", MUTED, Font.BOLD, 11);
        gbc.gridx = 1;
        panel.add(previousAverageLabel, gbc);
        averageStepLabel = createComparisonLabel("0.00 ms", NAVY, Font.BOLD, 11);
        gbc.gridx = 2;
        panel.add(averageStepLabel, gbc);

        return panel;
    }

    private JLabel createComparisonLabel(String text, Color color, int style, int size) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Segoe UI", style, size));
        return label;
    }

    private JLabel createStatLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return label;
    }

    private void setupStackArea() {
        stackArea = new JPanel(new BorderLayout(10, 10));
        stackArea.setBackground(CANVAS);
        stackArea.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        simulationTutorialPanel = createSimulationTutorialPanel();
        stackArea.add(simulationTutorialPanel, BorderLayout.NORTH);
        
        visualPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        visualPanel.setBackground(CANVAS);
        visualPanel.setPreferredSize(new Dimension(570, 0));
        stackArea.add(visualPanel, BorderLayout.WEST);

        inputPlatesDefaultBorder = inputPlates.getBorder();
        inputQDefaultBorder = inputQ.getBorder();
        startButtonDefaultBorder = startBtn.getBorder();
        visualPanelDefaultBorder = visualPanel.getBorder();
        statsPanelDefaultBorder = statsPanel.getBorder();
        comparisonPanelDefaultBorder = comparisonPanel.getBorder();
        
        stepsArea = new JTextArea();
        stepsArea.setEditable(false);
        stepsArea.setBackground(new Color(15, 23, 42));
        stepsArea.setForeground(new Color(226, 232, 240));
        stepsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        stepsArea.setMargin(new Insets(16, 16, 16, 16));
        stepsArea.setLineWrap(true);
        stepsArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(stepsArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        scrollPane.setBackground(CANVAS);
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
        stackArea.add(scrollPane, BorderLayout.CENTER);
        
        contentTabs = new JTabbedPane();
        contentTabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        contentTabs.addTab("Simulation", stackArea);
        statisticsPanel = new StatisticsPanel();
        contentTabs.addTab("Statistics", statisticsPanel);
        descriptionPanel = new DescriptionPanel();
        contentTabs.addTab("Description", descriptionPanel);
        add(contentTabs, BorderLayout.CENTER);
    }

    private JPanel createSimulationTutorialPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(new Color(240, 253, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(153, 246, 228)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        panel.setVisible(false);

        JPanel message = new JPanel();
        message.setOpaque(false);
        message.setLayout(new BoxLayout(message, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Simulation tutorial");
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(new Color(15, 69, 69));
        tutorialInstructionLabel = new JLabel("Select Highlight tutorial to learn how to operate the system.");
        tutorialInstructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tutorialInstructionLabel.setForeground(new Color(15, 69, 69));
        message.add(title);
        message.add(tutorialInstructionLabel);

        tutorialStepsPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        tutorialStepsPanel.setOpaque(false);
        String[] steps = {
            "1. Enter inputs",
            "2. Click Start",
            "3. Watch the run",
            "4. Compare results"
        };
        for (String step : steps) {
            JLabel stepLabel = new JLabel(step, SwingConstants.CENTER);
            stepLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            stepLabel.setForeground(new Color(148, 163, 184));
            stepLabel.setOpaque(true);
            stepLabel.setBackground(new Color(226, 232, 240));
            stepLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            tutorialStepLabels.add(stepLabel);
            tutorialStepsPanel.add(stepLabel);
        }

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        tutorialStepLabel = new JLabel("Step 0 of 4");
        tutorialStepLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tutorialStepLabel.setForeground(MUTED);
        tutorialHighlightButton = createButton("Next step", TEAL);
        tutorialHighlightButton.setToolTipText("Move to the next instruction in the guided tutorial.");
        tutorialHighlightButton.addActionListener(e -> advanceTutorial());
        actions.add(tutorialStepLabel);
        actions.add(tutorialHighlightButton);

        panel.add(message, BorderLayout.CENTER);
        panel.add(tutorialStepsPanel, BorderLayout.SOUTH);
        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private void startUserTutorial() {
        tutorialStep = -1;
        simulationTutorialPanel.setVisible(true);
        tutorialHeaderButton.setEnabled(false);
        tutorialOverlay.setVisible(true);
        advanceTutorial();
        stackArea.revalidate();
        stackArea.repaint();
    }

    private void advanceTutorial() {
        tutorialStep = (tutorialStep + 1) % 5;
        clearTutorialHighlights();
        if (tutorialStep == 4) {
            finishTutorial();
            return;
        }

        switch (tutorialStep) {
            case 0:
                highlightComponent(inputPlates, new Color(13, 148, 136));
                highlightComponent(inputQ, new Color(13, 148, 136));
                tutorialInstructionLabel.setText("Step 1: Enter plate numbers and the number of iterations above.");
                break;
            case 1:
                highlightComponent(startBtn, new Color(13, 148, 136));
                tutorialInstructionLabel.setText("Step 2: Click Start to begin the fixed-speed simulation.");
                break;
            case 2:
                highlightComponent(visualPanel, new Color(13, 148, 136));
                highlightComponent(stepsArea, new Color(13, 148, 136));
                tutorialInstructionLabel.setText("Step 3: Watch stack A, stack B, and the activity log update.");
                break;
            case 3:
                highlightComponent(statsPanel, new Color(13, 148, 136));
                highlightComponent(comparisonPanel, new Color(13, 148, 136));
                tutorialInstructionLabel.setText("Step 4: Run again, then open Statistics to compare and export results.");
                break;
            default:
                break;
        }
        for (int index = 0; index < tutorialStepLabels.size(); index++) {
            JLabel stepLabel = tutorialStepLabels.get(index);
            boolean active = index == tutorialStep;
            stepLabel.setForeground(active ? new Color(15, 69, 69) : new Color(148, 163, 184));
            stepLabel.setBackground(active ? new Color(153, 246, 228) : new Color(226, 232, 240));
        }
        tutorialStepLabel.setText("Step " + (tutorialStep + 1) + " of 4");
        tutorialOverlay.setTutorialState(tutorialStep, getTutorialTarget(), tutorialInstructionLabel.getText());
    }

    private Component getTutorialTarget() {
        switch (tutorialStep) {
            case 0: return tutorialInputArea;
            case 1: return startBtn;
            case 2: return visualPanel;
            case 3: return contentTabs;
            default: return null;
        }
    }

    private void finishTutorial() {
        clearTutorialHighlights();
        tutorialStep = -1;
        simulationTutorialPanel.setVisible(false);
        tutorialHeaderButton.setEnabled(true);
        tutorialOverlay.setVisible(false);
        stackArea.revalidate();
        stackArea.repaint();
    }

    private void clearTutorialHighlights() {
        inputPlates.setBorder(inputPlatesDefaultBorder);
        inputQ.setBorder(inputQDefaultBorder);
        startBtn.setBorder(startButtonDefaultBorder);
        visualPanel.setBorder(visualPanelDefaultBorder);
        statsPanel.setBorder(statsPanelDefaultBorder);
        comparisonPanel.setBorder(comparisonPanelDefaultBorder);
    }

    private void highlightComponent(JComponent component, Color color) {
        component.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    }

    private class TutorialOverlay extends JPanel {
        private static final Color HIGHLIGHT_BLUE = new Color(59, 130, 246);
        private Component target;
        private final JLabel overlayInstruction;

        TutorialOverlay() {
            setOpaque(false);
            setLayout(new BorderLayout());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent event) {
                    forwardMouseEvent(event);
                }

                @Override
                public void mouseReleased(MouseEvent event) {
                    forwardMouseEvent(event);
                }

                @Override
                public void mouseClicked(MouseEvent event) {
                    forwardMouseEvent(event);
                }
            });

            JPanel footer = new JPanel(new BorderLayout(14, 0));
            footer.setBackground(new Color(15, 23, 42));
            footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HIGHLIGHT_BLUE, 2),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
            overlayInstruction = new JLabel();
            overlayInstruction.setForeground(Color.WHITE);
            overlayInstruction.setFont(new Font("Segoe UI", Font.BOLD, 13));
            JButton nextButton = createButton("Next step", HIGHLIGHT_BLUE);
            nextButton.addActionListener(e -> advanceTutorial());
            JButton exitButton = createButton("Exit", new Color(71, 85, 105));
            exitButton.addActionListener(e -> finishTutorial());

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            buttons.setOpaque(false);
            buttons.add(exitButton);
            buttons.add(nextButton);
            footer.add(overlayInstruction, BorderLayout.CENTER);
            footer.add(buttons, BorderLayout.EAST);
            add(footer, BorderLayout.SOUTH);
        }

        void setTutorialState(int step, Component target, String instruction) {
            this.target = target;
            overlayInstruction.setText("Step " + (step + 1) + " of 4: " + instruction);
            revalidate();
            repaint();
        }

        private void forwardMouseEvent(MouseEvent event) {
            if (target == null || !target.isShowing()) return;
            Point targetPoint = SwingUtilities.convertPoint(this, event.getPoint(), target);
            if (!target.contains(targetPoint)) return;

            Component eventTarget = target;
            if (!(target instanceof JTabbedPane)) {
                Component deepest = SwingUtilities.getDeepestComponentAt(
                    target, targetPoint.x, targetPoint.y);
                if (deepest != null) eventTarget = deepest;
            }
            MouseEvent forwarded = SwingUtilities.convertMouseEvent(this, event, eventTarget);
            eventTarget.dispatchEvent(forwarded);
            if (event.getID() == MouseEvent.MOUSE_PRESSED) {
                eventTarget.requestFocusInWindow();
            }
            event.consume();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(0, 0, 0, 185));
            g.fillRect(0, 0, getWidth(), getHeight());

            if (target != null && target.isShowing()) {
                Rectangle bounds = SwingUtilities.convertRectangle(
                    target.getParent(), target.getBounds(), this);
                int padding = 6;
                g.setComposite(AlphaComposite.Clear);
                g.fillRoundRect(bounds.x - padding, bounds.y - padding,
                    bounds.width + padding * 2, bounds.height + padding * 2, 10, 10);
                g.setComposite(AlphaComposite.SrcOver);
                g.setColor(HIGHLIGHT_BLUE);
                g.setStroke(new BasicStroke(3));
                g.drawRoundRect(bounds.x - padding, bounds.y - padding,
                    bounds.width + padding * 2, bounds.height + padding * 2, 10, 10);
            }
            g.dispose();
        }
    }


    private void resetStacks() {
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
        simulationStartNanos = System.nanoTime();
        updateComparisonLabels();

        startBtn.setEnabled(false);
        pauseBtn.setEnabled(true);
        resumeBtn.setEnabled(false);
        resetBtn.setEnabled(true);

        String[] nums = inputPlates.getText().trim().split("\\s+");
        Stack<Integer> A0 = new Stack<>();
        for (String n : nums) A0.push(Integer.parseInt(n));
        AStacks.add(A0);

        int q = Integer.parseInt(inputQ.getText().trim());

        animationTimer = new javax.swing.Timer(DEFAULT_SPEED_MILLIS, new ActionListener() {
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
                long stepStartNanos = System.nanoTime();

                if (!tempStack.isEmpty()) {
                    int plate = tempStack.pop();
                    SoundManager.playSound("pop");
                    addStep("Pop from A: " + plate);
                    if (plate % currentPrime == 0) {
                        currentBi.push(plate);
                        addStep(" Push " + plate + " to B");
                    } else {
                        currentAi.push(plate);
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
                        updateTiming(stepStartNanos);
                        if (hasPreviousRun) {
                            previousCompletedElapsedNanos = lastRunElapsedNanos;
                            previousCompletedAverageMillis = lastRunAverageMillis;
                            hasPreviousCompletedRun = true;
                        }
                        currentCompletedElapsedNanos = System.nanoTime() - simulationStartNanos;
                        currentCompletedAverageMillis = stepCount == 0
                            ? 0
                            : processingNanos / 1_000_000.0 / stepCount;
                        statisticsPanel.updateRuns(hasPreviousCompletedRun,
                            previousCompletedElapsedNanos, previousCompletedAverageMillis,
                            currentCompletedElapsedNanos, currentCompletedAverageMillis);
                        lastRunElapsedNanos = System.nanoTime() - simulationStartNanos;
                        lastRunAverageMillis = stepCount == 0
                            ? 0
                            : processingNanos / 1_000_000.0 / stepCount;
                        hasPreviousRun = true;
                        stopSimulation();
                        return;
                    }
                }

                updateTiming(stepStartNanos);
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
        simulationStartNanos = 0;
        processingNanos = 0;
        stepCount = 0;
        updateTimingLabels();
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

    private void updateTiming(long stepStartNanos) {
        processingNanos += System.nanoTime() - stepStartNanos;
        stepCount++;
        updateTimingLabels();
    }

    private void updateTimingLabels() {
        long elapsedNanos = simulationStartNanos == 0
            ? 0
            : System.nanoTime() - simulationStartNanos;
        double elapsedMillis = elapsedNanos / 1_000_000.0;
        double averageMillis = stepCount == 0
            ? 0
            : processingNanos / 1_000_000.0 / stepCount;
        elapsedTimeLabel.setText(String.format("%.0f ms", elapsedMillis));
        averageStepLabel.setText(String.format("%.2f ms", averageMillis));
        updateComparisonLabels();
    }

    private void updateComparisonLabels() {
        if (!hasPreviousRun) {
            previousElapsedLabel.setText("-");
            previousAverageLabel.setText("-");
        } else {
            previousElapsedLabel.setText(String.format("%.0f ms", lastRunElapsedNanos / 1_000_000.0));
            previousAverageLabel.setText(String.format("%.2f ms", lastRunAverageMillis));
        }
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
