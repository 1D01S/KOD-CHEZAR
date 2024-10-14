import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.*;

public class CaesarCipherGUI extends JFrame {

    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JTextField shiftField;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton bruteForceButton;
    private JButton loadFileButton;
    private JButton saveFileButton;
    private JButton clearButton;

    public CaesarCipherGUI() {
        // Установка параметров окна
        setTitle("Шифр Цезаря");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Создание элементов GUI
        inputTextArea = new JTextArea(10, 50);
        outputTextArea = new JTextArea(10, 50);
        outputTextArea.setEditable(false); // Блокируем ручное редактирование результата
        shiftField = new JTextField(5);
        shiftField.setToolTipText("Введите числовое значение сдвига");

        // Ограничение на ввод в поле сдвига (только цифры)
        shiftField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '\b') { // '\b' позволяет использовать backspace
                    e.consume(); // Игнорировать любые символы, кроме цифр
                }
            }
        });

        encryptButton = new JButton("Зашифровать");
        decryptButton = new JButton("Расшифровать");
        bruteForceButton = new JButton("Brute Force");
        loadFileButton = new JButton("Загрузить");
        saveFileButton = new JButton("Сохранить");
        clearButton = new JButton("Очистить");

        // Добавляем подсказОЧКА
        inputTextArea.setToolTipText("Введите текст для шифрования или расшифрования");
        outputTextArea.setToolTipText("Здесь отобразится результат шифрования или расшифрования");
        encryptButton.setToolTipText("Нажмите для шифрования введенного текста");
        decryptButton.setToolTipText("Нажмите для расшифровки текста");
        bruteForceButton.setToolTipText("Перебрать все возможные сдвиги для текста");
        loadFileButton.setToolTipText("Загрузить текст из файла");
        saveFileButton.setToolTipText("Сохранить результат в файл");
        clearButton.setToolTipText("Очистить все поля");

        // Панель для ввода текста
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.add(new JLabel("Введите текст:"), BorderLayout.NORTH);
        textPanel.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);

        // Панель для вывода текста
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(new JLabel("Результат:"), BorderLayout.NORTH);
        outputPanel.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);

        // Панель с кнопками и полем для сдвига
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Отступы

        // Добавляем кнопки на панель управления
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        controlPanel.add(encryptButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        controlPanel.add(decryptButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        controlPanel.add(bruteForceButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        controlPanel.add(loadFileButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        controlPanel.add(saveFileButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        controlPanel.add(clearButton, gbc);

        // Панель для сдвига находится на уровне кнопок, справа от кнопок
        JPanel shiftPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        shiftPanel.add(new JLabel("Сдвиг:"));
        shiftPanel.add(shiftField);

        // Добавляем панель для сдвига справа от кнопок
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 6; // Занимает столько же строк, сколько и панель с кнопками
        gbc.anchor = GridBagConstraints.NORTH; // Закрепляем наверху
        controlPanel.add(shiftPanel, gbc);

        // Логотип в правом нижнем углу (Почему бы и нет?)
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/resources/ЛООГОО.png")); // Используем ресурс программы
        Image scaledImage = logoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Масштабируем изображение
        logoIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(logoIcon);
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoPanel.add(logoLabel);

        // Основной Layout
        setLayout(new BorderLayout());
        add(textPanel, BorderLayout.NORTH);
        add(outputPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST); // Панель управления справа
        add(logoPanel, BorderLayout.SOUTH);  // Логотип внизу справа

        // Обработчики действий
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encryptText();
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decryptText();
            }
        });

        bruteForceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bruteForceDecrypt();
            }
        });

        loadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFile();
            }
        });

        saveFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
    }

    private void encryptText() {
        try {
            String text = inputTextArea.getText();
            int shift = validateKey(shiftField.getText());
            String encryptedText = encrypt(text, shift);
            outputTextArea.setText(encryptedText);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void decryptText() {
        try {
            String text = inputTextArea.getText();
            int shift = validateKey(shiftField.getText());
            String decryptedText = decrypt(text, shift);
            outputTextArea.setText(decryptedText);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void bruteForceDecrypt() {
        String text = inputTextArea.getText();
        StringBuilder results = new StringBuilder("Результаты brute force:\n");
        for (int i = 1; i < 33; i++) {
            results.append("Ключ ").append(i).append(": ").append(decrypt(text, i)).append("\n");
        }
        outputTextArea.setText(results.toString());
    }

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String content = readFile(fileChooser.getSelectedFile().getPath());
                inputTextArea.setText(content);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Ошибка чтения файла: " + e.getMessage());
            }
        }
    }

    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String content = outputTextArea.getText();
                writeFile(fileChooser.getSelectedFile().getPath(), content);
                JOptionPane.showMessageDialog(this, "Файл успешно сохранён!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Ошибка сохранения файла: " + e.getMessage());
            }
        }
    }

    private int validateKey(String keyInput) {
        try {
            return Integer.parseInt(keyInput);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный ключ! Ключ должен быть целым числом.");
        }
    }

    private void clearFields() {
        inputTextArea.setText("");
        outputTextArea.setText("");
        shiftField.setText("");
    }

//Основная магия
    private static String encrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();
        shift = shift % 33 + 33;
        for (char i : text.toCharArray()) {
            if (Character.isLetter(i)) {
                if (i >= 'А' && i <= 'Я') {
                    result.append((char) ('А' + (i - 'А' + shift) % 33));
                } else if (i >= 'а' && i <= 'я') {
                    result.append((char) ('а' + (i - 'а' + shift) % 33));
                } else {
                    result.append(i);
                }
            } else {
                result.append(i);
            }
        }
        return result.toString();
    }

    private static String decrypt(String text, int shift) {
        return encrypt(text, -shift);
    }

    private static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    private static void writeFile(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CaesarCipherGUI().setVisible(true));
    }
}
