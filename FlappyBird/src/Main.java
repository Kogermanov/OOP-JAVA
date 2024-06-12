import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception{
        JFrame frame = new JFrame("Flappy bird");//рама
        frame.setSize(360, 640);//размер окна
        frame.setLocationRelativeTo(null);//окно в центре
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//завершение работы приложения
        //frame.setVisible(true);

        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack();
        flappyBird.requestFocus();
        frame.setVisible(true);//делаем окно видимым
    }
}
