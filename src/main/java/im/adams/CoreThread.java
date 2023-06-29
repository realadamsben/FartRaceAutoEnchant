package im.adams;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static im.adams.MainFrame.config;
import static im.adams.MainFrame.run;


public class CoreThread extends Thread {

    @Override
    public void run() {


       while(run){
           Robot robot = null;
           try {
               robot = new Robot();
           } catch (AWTException e) {
               throw new RuntimeException(e);
           }

           try {

               robot.mouseMove(config.xPosEnchant, config.yPosEnchant);

               robot.delay(500);
               robot.mouseMove(config.xPosEnchant + 15, config.yPosEnchant + 17);
               robot.mousePress(InputEvent.BUTTON1_MASK);
               robot.mouseRelease(InputEvent.BUTTON1_MASK);

               robot.delay(500);
               robot.mouseMove(config.xPosConfirm, config.yPosConfirm);

               robot.delay(500);
               robot.mouseMove(config.xPosConfirm - 15, config.yPosConfirm + 17);
               robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
               robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

               sleep(5000);

           } catch (Exception e) {
               e.printStackTrace();
           }
           BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(config.xPosTopLeft, config.yPosTopLeft, config.xPosBottomRight-config.xPosTopLeft, config.yPosBottomRight-config.yPosTopLeft));

           System.out.printf("Width: %s\n", config.xPosBottomRight - config.xPosTopLeft);
           System.out.printf("Height: %s\n", config.yPosBottomRight - config.yPosTopLeft);



           Tesseract tesseract = getTesseract();


           String result = null;
           try {
               result = tesseract.doOCR(screenCapture);
           } catch (TesseractException e) {
               throw new RuntimeException(e);
           }
            
           System.out.println("===== OCR RESULT =====");
           System.out.println(result);
           System.out.println("===== END OCR RE =====");

           AtomicInteger strengths = new AtomicInteger();
           result.lines().forEach((str) -> {
               if (str.toLowerCase().contains("strength")|| str.toLowerCase().contains("pet power")) {
                   strengths.getAndIncrement();
               }
           });
           if (strengths.get() >= config.targetNum) {
               MainFrame.run = false;
               try {
                   WebhookClient webhookClient = WebhookClient.withUrl(config.webhookURI);
                   WebhookMessage webhookMessage = new WebhookMessageBuilder()
                           .setUsername("Fart Race Auto Enchanter")
                           .setContent("Found pet with " + strengths + " strengths, target was " + config.targetNum)
                           .build();
                   webhookClient.send(webhookMessage);
                   webhookClient.close();
               } catch (Exception e) {
               }

               JOptionPane.showConfirmDialog(null, "Found pet with " + strengths + " strengths, target was " + config.targetNum);


               try {
                   sleep(5000);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }

           }
       }
    }
    private static Tesseract getTesseract() {
        Tesseract instance = new Tesseract();
        return instance;
    }
}


