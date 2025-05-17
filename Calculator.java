import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;

public class Calculator extends JFrame {
    private JTextField display;
    private double firstNumber = 0;
    private String operation = "";
    private boolean startNewNumber = true;

    public Calculator() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        // Display
        display = new JTextField("0");
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Helvetica Neue", Font.PLAIN, 72));
        display.setBackground(Color.BLACK);
        display.setForeground(Color.WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(40, 20, 30, 20));
        add(display, BorderLayout.NORTH);

        // Button Panel with proper layout
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Button colors
        Color lightGray = new Color(165, 165, 165);
        Color darkGray = new Color(51, 51, 51);
        Color orange = new Color(255, 159, 11);

        // First row: AC, ±, %, ÷
        String[] firstRow = { "AC", "±", "%", "÷" };
        for (int i = 0; i < firstRow.length; i++) {
            gbc.gridx = i;
            gbc.gridy = 0;
            CalculatorButton button = new CalculatorButton(firstRow[i]);
            button.setBackground(lightGray);
            button.setForeground(Color.BLACK);
            if (i == 3) {
                button.setBackground(orange);
                button.setForeground(Color.WHITE);
            }
            buttonPanel.add(button, gbc);
            button.addActionListener(new ButtonClickListener());
        }

        // Next three rows
        String[][] rows = {
                { "7", "8", "9", "×" },
                { "4", "5", "6", "-" },
                { "1", "2", "3", "+" }
        };

        for (int row = 0; row < rows.length; row++) {
            for (int col = 0; col < rows[row].length; col++) {
                gbc.gridx = col;
                gbc.gridy = row + 1;
                CalculatorButton button = new CalculatorButton(rows[row][col]);
                if (col == 3) {
                    button.setBackground(orange);
                    button.setForeground(Color.WHITE);
                } else {
                    button.setBackground(darkGray);
                    button.setForeground(Color.WHITE);
                }
                buttonPanel.add(button, gbc);
                button.addActionListener(new ButtonClickListener());
            }
        }

        // Bottom row: 0 (double width), ., =
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        CalculatorButton zeroButton = new CalculatorButton("0");
        zeroButton.setBackground(darkGray);
        zeroButton.setForeground(Color.WHITE);
        buttonPanel.add(zeroButton, gbc);
        zeroButton.addActionListener(new ButtonClickListener());

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        CalculatorButton dotButton = new CalculatorButton(".");
        dotButton.setBackground(darkGray);
        dotButton.setForeground(Color.WHITE);
        buttonPanel.add(dotButton, gbc);
        dotButton.addActionListener(new ButtonClickListener());

        gbc.gridx = 3;
        CalculatorButton equalsButton = new CalculatorButton("=");
        equalsButton.setBackground(orange);
        equalsButton.setForeground(Color.WHITE);
        buttonPanel.add(equalsButton, gbc);
        equalsButton.addActionListener(new ButtonClickListener());

        add(buttonPanel, BorderLayout.CENTER);
        setSize(375, 600);
        setLocationRelativeTo(null);
    }

    class CalculatorButton extends JButton {
        public CalculatorButton(String text) {
            super(text);
            setFont(new Font("Helvetica Neue", Font.PLAIN, 32));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isArmed()) {
                g2.setColor(getBackground().darker());
            } else {
                g2.setColor(getBackground());
            }

            int arc = 40;
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D r = fm.getStringBounds(getText(), g2);
            int x = (getWidth() - (int) r.getWidth()) / 2;
            int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(80, 80);
        }
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = ((JButton) e.getSource()).getText();

            if (command.matches("[0-9]")) {
                if (startNewNumber || display.getText().equals("0")) {
                    display.setText(command);
                    startNewNumber = false;
                } else {
                    display.setText(display.getText() + command);
                }
            } else if (command.equals(".")) {
                if (startNewNumber) {
                    display.setText("0.");
                    startNewNumber = false;
                } else if (!display.getText().contains(".")) {
                    display.setText(display.getText() + ".");
                }
            } else if (command.equals("AC")) {
                display.setText("0");
                firstNumber = 0;
                operation = "";
                startNewNumber = true;
            } else if (command.equals("±")) {
                String text = display.getText();
                if (!text.equals("0")) {
                    if (text.startsWith("-")) {
                        display.setText(text.substring(1));
                    } else {
                        display.setText("-" + text);
                    }
                }
            } else if (command.equals("%")) {
                double value = Double.parseDouble(display.getText());
                display.setText(String.valueOf(value / 100));
                startNewNumber = true;
            } else if (command.matches("÷|×|\\-|\\+")) {
                firstNumber = Double.parseDouble(display.getText());
                operation = command;
                startNewNumber = true;
            } else if (command.equals("=")) {
                if (!operation.isEmpty()) {
                    double secondNumber = Double.parseDouble(display.getText());
                    double result = calculateResult(firstNumber, secondNumber, operation);

                    if (result == (long) result) {
                        display.setText(String.format("%d", (long) result));
                    } else {
                        display.setText(String.valueOf(result));
                    }

                    operation = "";
                    startNewNumber = true;
                }
            }
        }

        private double calculateResult(double first, double second, String op) {
            switch (op) {
                case "÷":
                    return first / second;
                case "×":
                    return first * second;
                case "-":
                    return first - second;
                case "+":
                    return first + second;
                default:
                    return second;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Calculator calculator = new Calculator();
            calculator.setVisible(true);
        });
    }
}