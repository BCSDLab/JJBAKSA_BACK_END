package com.jjbacsa.jjbacsabackend.util;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.NonCriticalException;
import com.jjbacsa.jjbacsabackend.image.dto.MultipartImage;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Component
public class ImageUtil {

    public MultipartFile resizing(MultipartFile multipartFIle, int newWidth) {
        byte[] imgBytes = null;

        try {
            BufferedImage inputImage = ImageIO.read(multipartFIle.getInputStream());

            int originalWidth = inputImage.getWidth();
            int originalHeight = inputImage.getHeight();

            int newHeight = (originalHeight * newWidth) / originalWidth;

            if (originalWidth < newWidth) {
                newWidth = originalWidth;
                newHeight = originalHeight;
            }

            Image resizeImage = inputImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

            BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_BGR);

            Graphics graphics = newImage.getGraphics();
            graphics.drawImage(resizeImage, 0, 0, null);
            graphics.dispose();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(newImage, "jpg", out);
            imgBytes = out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new NonCriticalException(ErrorMessage.IMAGE_RESIZING_EXCEPTION);
        }

        return new MultipartImage(imgBytes, multipartFIle.getName(), multipartFIle.getOriginalFilename(), "image/jpg", imgBytes.length);
    }

}
