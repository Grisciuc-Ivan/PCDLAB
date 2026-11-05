import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class LabWorkGUI extends JFrame {
    // Общий массив для всех потоков
    private static final int[] mas = new int[100];

    private JTextArea textArea;
    private JButton startButton;

    public LabWorkGUI() {
        // Настройка главного окна программы
        setTitle("Лабораторная работа - Потоки");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Центрирование окна по центру экрана

        // Текстовое поле для вывода логов
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Кнопка для запуска потоков
        startButton = new JButton("Запустить потоки");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Добавление элементов на форму
        add(scrollPane, BorderLayout.CENTER);
        add(startButton, BorderLayout.SOUTH);

        // Обработка нажатия на кнопку
        startButton.addActionListener(e -> startProcessing());
    }

    private void startProcessing() {
        startButton.setEnabled(false); // Отключаем кнопку на время работы
        textArea.setText(""); // Очищаем поле вывода

        // Генерация массива случайными числами от 1 до 100
        Random random = new Random();
        for (int i = 0; i < mas.length; i++) {
            mas[i] = random.nextInt(100) + 1;
        }
        appendLog("Массив из 100 элементов успешно сгенерирован.");

        // Создаем фоновый поток-менеджер, чтобы не заморозить графический интерфейс (GUI)
        // пока мы будем делать .join() для ожидания Th1 и Th2
        Thread orchestrator = new Thread(() -> {

            // Условие Варианта 3 (Th1)
            Runnable task1 = () -> {
                appendLog("Th1 начал работу (Суммы нечетных чисел два по два)...");
                int firstOdd = -1;
                for (int i = 0; i < mas.length; i++) {
                    if (mas[i] % 2 != 0) {
                        if (firstOdd == -1) {
                            firstOdd = mas[i];
                        } else {
                            int sum = firstOdd + mas[i];
                            appendLog("Th1: Сумма пары (" + firstOdd + " + " + mas[i] + ") = " + sum);
                            firstOdd = -1;
                        }
                    }
                }
                appendLog("Th1 завершил работу.");
            };

            // Заглушка для Условия 2 (Th2)
            Runnable task2 = () -> {
                appendLog("Th2 начал работу (Заглушка - сумма всех четных)...");
                int evenSum = 0;
                for (int num : mas) {
                    if (num % 2 == 0) evenSum += num;
                }
                appendLog("Th2: Сумма всех четных = " + evenSum);
                appendLog("Th2 завершил работу.");
            };

            Thread th1 = new Thread(task1, "Th1");
            Thread th2 = new Thread(task2, "Th2");

            // Запуск рабочих потоков
            th1.start();
            th2.start();

            // Главный (в данном контексте оркестратор) поток ожидает завершения Th1 и Th2
            try {
                th1.join();
                th2.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            // После завершения потоков передаем команду в графический интерфейс для анимации текста
            SwingUtilities.invokeLater(this::displayStudentInfoLetterByLetter);
        });

        orchestrator.start();
    }

    // Безопасный метод для добавления текста в JTextArea из других потоков
    private void appendLog(String text) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(text + "\n");
            // Автоматическая прокрутка вниз
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }

    // Посимвольный вывод с задержкой в 100 мс с использованием Swing Timer
    private void displayStudentInfoLetterByLetter() {
        appendLog("\n--- Вывод информации ---");
        String studentInfo = "Работу выполнил : Грищук Иван, Группа CR-243";

        // Timer из javax.swing вызывается каждые 100 мс прямо в потоке обновления UI
        Timer timer = new Timer(100, new ActionListener() {
            int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < studentInfo.length()) {
                    textArea.append(String.valueOf(studentInfo.charAt(index)));
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                    index++;
                } else {
                    textArea.append("\n"); // Переход на новую строку после завершения
                    startButton.setEnabled(true); // Включаем кнопку обратно
                    ((Timer) e.getSource()).stop(); // Останавливаем таймер
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        // Создание и запуск графического интерфейса в специальном потоке EDT
        SwingUtilities.invokeLater(() -> {
            LabWorkGUI gui = new LabWorkGUI();
            gui.setVisible(true);
        });
    }
}