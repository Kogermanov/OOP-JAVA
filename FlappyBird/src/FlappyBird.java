import org.w3c.dom.ls.LSOutput;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    //размеры окна
    int boardWidth = 360;
    int boardHeight = 640;

    //картинки
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image recordsImg;


    class Bird {
        int x = boardWidth / 6;
        int y = boardHeight / 4;
        int width = 34;
        int height = 24;
        Image image;
        Bird(Image im) {
            this.image = im;
        }
    }

    class Pipe {
        int x = boardWidth;
        int distNextPipe;
        int y = 0;
        int width = 64;
        int height = 512;
        Image image;
        boolean passed = false;
        Pipe(Image im) {
            this.image = im;
        }
    }

    //логика игры
    ArrayList<Pipe> pipes;
    Bird bird;
    int birdSpeed = 0;
    int pipeSpeed = -4;
    int gravity = 1;
    Timer gameTimer;
    Timer pipeTimer;
    int openSpace = boardHeight/4;
    boolean gameOver = true;
    double score = 0;
    int newRecord = 0;
    FlappyBird() throws IOException {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        //загружаем картинки
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        recordsImg = new ImageIcon(getClass().getResource("./scorebg.jpg")).getImage();
        //создаем птицу
        bird = new Bird(birdImg);
        //создание труб
        pipes = new ArrayList<Pipe>();

        //создаем таймер для труб
        pipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        pipeTimer.start();
        //создаем таймер для обновления кадров игры
        gameTimer = new Timer(1000/60, this);//60 кадров в секунду
        gameTimer.start();
    }

    public void placePipes() {
        int randx =  (int)(Math.random()*2);
        Pipe topPipe = new Pipe(topPipeImg);
        int randomY = (int)(-topPipe.height/4- Math.random()*topPipe.height/2);
        topPipe.y = randomY;
        topPipe.distNextPipe = randx;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + openSpace + topPipe.height;
        bottomPipe.distNextPipe = randx;
        pipes.add(bottomPipe);
    }

    public void move() {
        //движение птицы
        birdSpeed += gravity;
        bird.y += birdSpeed;
        bird.y = Math.max(bird.y, 0);
        //движение труб
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += pipeSpeed;
            if (collision(bird,pipe)){
                gameOver = true;
            }
            if(bird.y > boardHeight) {
                bird.y = boardHeight-2*bird.height;
                gameOver = true;
            }
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5; //2 трубы снизу и сверху
                pipe.passed = true;
            }
        }

        //конец игры
        if (gameOver) {
            pipeTimer.stop();
            gameTimer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        //выводим фон
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);
        //выводим птицу
        g.drawImage(bird.image, bird.x, bird.y, bird.width,bird.height,null);
        //рисуем трубы
        for (int i = 0 ; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.image, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Score: " + String.valueOf((int) score), 10, 35);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Press SPACE to start", 60, 320);
            g.drawImage(recordsImg, 100, 350, 140, 100, null);



            File records = new File("C:\\Users\\vipko\\IdeaProjects\\FlappyBird\\src\\records.txt");
            String recordNow;
            String[] recArr;
            try {
                Scanner rec = new Scanner(records);
                recordNow = rec.nextLine();
                recArr = recordNow.split("\n");
                newRecord = (Math.max(Integer.valueOf(recArr[0]),(int)score));
                System.out.println(newRecord);
                rec.close();
            } catch (FileNotFoundException e) {
                System.out.println("ошибка открытия");
                throw new RuntimeException(e);
            }



            if (newRecord == score) {
                try {
                    FileWriter newRec = new FileWriter(records);
                    //BufferedWriter write = new BufferedWriter(newRec);
                    //System.out.println(score);
                    newRec.write(String.valueOf(newRecord));
                    newRec.close();
                } catch (IOException e) {
                    System.out.println("ошибка ввода");
                    throw new RuntimeException(e);
                }
            }

            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(Color.BLACK);
            g.drawString("BEST ", 135, 380);
            g.setColor(Color.YELLOW);
            g.drawString(String.valueOf(newRecord), 130, 420);


        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            birdSpeed = -9;
            if(gameOver) {
                gameOver = false;
                bird.y = boardHeight / 4;
                birdSpeed = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                gameTimer.start();
                pipeTimer.start();
            }
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   //верхний левый угол птицы не достигает верхнего правого угла трубы
                a.x + a.width > b.x &&   //верхний правый угол птицы проходит мимо верхнего левого угла трубы
                a.y < b.y + b.height &&  //левый верхний угол птицы не достигает левого нижнего угла трубы
                a.y + a.height > b.y;    //левый нижний угол птицы проходит левый верхний угол трубы
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}