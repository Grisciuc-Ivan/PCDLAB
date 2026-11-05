import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;

public class lab2 extends JFrame {
    private JTextArea textArea;
    private JButton startButton;

    public lab2() {
        // Настройка главного окна программы
        setTitle("Лабораторная работа №2 - Вариант 3 (Средний уровень)");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Центрируем окно по экрану

        // Текстовое поле для вывода результатов
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Кнопка для запуска логики
        startButton = new JButton("Создать и вывести структуру потоков");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        startButton.addActionListener(e -> executeLabLogic());

        // Добавление элементов на форму
        add(scrollPane, BorderLayout.CENTER);
        add(startButton, BorderLayout.SOUTH);

        // Перенаправление System.out в JTextArea для захвата вывода метода list()
        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
        System.setOut(printStream);
    }

    private void executeLabLogic() {
        textArea.setText(""); // Очищаем поле перед новым запуском

        // Получаем основную группу потоков (Main)
        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();

        // Создаем иерархию подгрупп
        ThreadGroup groupGN = new ThreadGroup(mainGroup, "GN");
        ThreadGroup groupGH = new ThreadGroup(groupGN, "GH");
        ThreadGroup groupGM = new ThreadGroup(mainGroup, "GM");

        // Инициализация потоков группы GH
        Thread tha = new Thread(groupGH, "Tha"); tha.setPriority(4);
        Thread thb = new Thread(groupGH, "Thb"); thb.setPriority(3);
        Thread thc = new Thread(groupGH, "Thc"); thc.setPriority(6);
        Thread thd = new Thread(groupGH, "Thd"); thd.setPriority(3);

        // Инициализация потоков группы GN
        Thread thA = new Thread(groupGN, "ThA"); thA.setPriority(3);

        // Инициализация потоков группы GM
        Thread th1_gm = new Thread(groupGM, "Th1"); th1_gm.setPriority(2);
        Thread th2_gm = new Thread(groupGM, "Th2"); th2_gm.setPriority(3);
        Thread th3_gm = new Thread(groupGM, "Th3"); th3_gm.setPriority(3);

        // Инициализация потоков главной группы
        Thread th1_main = new Thread(mainGroup, "Th1_Main"); th1_main.setPriority(8);
        Thread th2_main = new Thread(mainGroup, "Th2_Main"); th2_main.setPriority(3);

        Thread[] allThreads = {
                tha, thb, thc, thd,
                thA,
                th1_gm, th2_gm, th3_gm,
                th1_main, th2_main
        };

        System.out.println("--- Информация о потоках выполнения ---");
        // Выводим информацию ДО старта во избежание NullPointerException
        for (Thread t : allThreads) {
            System.out.println("Имя: " + String.format("%-8s", t.getName()) +
                    " | Группа: " + String.format("%-10s", t.getThreadGroup().getName()) +
                    " | Приоритет: " + t.getPriority());
        }

        // Запуск потоков
        for (Thread t : allThreads) {
            t.start();
        }

        System.out.println("\n--- Стандартный вывод иерархии (метод list()) ---");
        // Метод list() выводит дерево потоков в System.out, который мы перенаправили в JTextArea
        mainGroup.list();
    }

    // Точка входа в программу
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new lab2().setVisible(true);
        });
    }

    // Внутренний класс для перехвата консольного вывода и передачи его в JTextArea
    class CustomOutputStream extends OutputStream {
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) {
            textArea.append(String.valueOf((char) b));
            textArea.setCaretPosition(textArea.getDocument().getLength()); // Прокрутка вниз
        }

        @Override
        public void write(byte[] b, int off, int len) {
            textArea.append(new String(b, off, len));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }
}