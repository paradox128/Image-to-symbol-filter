import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

public class Main {

    //Текущая палитра в виде строки (для удобства редактирования) и в виде массива (для удобства использования)
    static String rampString = " `.:;÷+({X&$№";
    static String[] ramp = rampString.split("");

    //Наше видео
    static File video = new File("HSR2.mp4");

    //Количесвто кадров, которые нам нужны
    static int n = 15;
    //Массив, в котором хранятся эти кадры
    static BufferedImage[] vid = new BufferedImage[n];

    //Бот для обновления консоли
    static Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }


    //Бот нажимает горячие клавиши Alt+C, чтобы очистить консоль от вывода
    public static void clearConsole() {
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyRelease(KeyEvent.VK_C);
    }

    //Функция, записывающая кадры видео в массив и выводящая их в консоль
    public static void setupVideo() throws JCodecException, IOException, InterruptedException {

        //Запись
        for (int i = 0; i < n; i++) {
            Picture frame = FrameGrab.getFrameFromFile(video, i);
            BufferedImage image = AWTUtil.toBufferedImage(frame);
            vid[i] = image;
            System.out.println(i);
        }

        //ПЛОХО РАБОТАЮЩИЙ (!) вывод
        Scanner s = new Scanner(System.in);
        System.out.println("Ready.");
        s.nextLine();
        s.close();
        for (int i = 0; i < n; i++) {
            Thread.sleep(100);
            redrawImage(vid[i]);
        }
    }

    //Функция, выводящая в консоль градиент текущей палитры
    public static void testRamp() throws IOException, InterruptedException {
        redrawImage(ImageIO.read(new File("gradient.jpg")));
    }


    //Непосредственно функция, обрабатывающая изображение и выводящая результат в консоль
    public static void redrawImage(BufferedImage image) throws InterruptedException {

        int width = image.getWidth();
        int height = image.getHeight();
        int wSym = 10, hSym = 27;   //Высота и ширина одного символа в пикселях

        String[] picture = new String[height / hSym];

        for (int i = hSym; i < height; i += hSym) {
            picture[i / hSym - 1] = "";
            for (int j = wSym; j < width; j += wSym) {

                //Считаем сумму всех значений цветов цвет области 10:27
                int greySum = 0;
                for (int y = i - hSym; y < i; y++)
                    for (int x = j - wSym; x < j; x++) {

                        //Получение параметров r,g,b для пикселя
                        int p = image.getRGB(x, y);
                        int R = (p >> 16) & 0xff;
                        int G = (p >> 8) & 0xff;
                        int B = p & 0xff;

                        greySum += (R + G + B);
                    }

                //Получаем средний цвет области 10:27 и выводим символ палитры, ему соответствующий
                int grey = greySum / (wSym * hSym * 3);
                picture[i / hSym - 1] += ramp[grey * ramp.length / 256];
            }
        }

        //Очистка консоли перед кадром (нужно для видео)
        clearConsole();
        Thread.sleep(10);
        //Вывод в консоль
        for (int i = 0; i < height / hSym; i++)
            System.out.println(picture[i]);
    }


    public static void main(String[] args) throws IOException, InterruptedException, JCodecException {

        setupVideo();
        /*
        Тут код для статичной картинки

        BufferedImage image = ImageIO.read(new File("amogus2.png"));

        redrawImage(image);
        */

    }
}
