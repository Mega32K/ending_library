package com.mega.endinglib.util.mc.render;

import com.mega.endinglib.EndingLibrary;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.function.Consumer;

public class GLFWImageUtils {
    public static GLFWImage fromInputStream(InputStream inputStream, float scale) throws IOException {
        BufferedImage image = ImageIO.read(inputStream);
        if (image == null)
            throw new IOException("Failed to load image");
        int width = image.getWidth();
        int height = image.getHeight();
        int newWidth = (int)(width * scale);
        int newHeight = (int)(height * scale);
        Image scaledImage = image.getScaledInstance(newWidth, newHeight, 4);
        BufferedImage scaledBufferedImage = new BufferedImage(newWidth, newHeight, 2);
        Graphics2D g2d = scaledBufferedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        int[] pixels = new int[newWidth * newHeight];
        scaledBufferedImage.getRGB(0, 0, newWidth, newHeight, pixels, 0, newWidth);
        ByteBuffer buffer = ByteBuffer.allocateDirect(newWidth * newHeight * 4);
        buffer.order(ByteOrder.nativeOrder());
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int pixel = pixels[y * newWidth + x];
                buffer.put((byte)(pixel >> 16 & 0xFF));
                buffer.put((byte)(pixel >> 8 & 0xFF));
                buffer.put((byte)(pixel & 0xFF));
                buffer.put((byte)(pixel >> 24 & 0xFF));
            }
        }
        buffer.flip();
        GLFWImage glfwImage = GLFWImage.malloc();
        glfwImage.width(newWidth);
        glfwImage.height(newHeight);
        glfwImage.pixels(buffer);
        return glfwImage;
    }

    public static void safeGetImage(InputStream inputStream, float scale, Consumer<GLFWImage> consumer) {
        GLFWImage image = null;
        try {
            image = fromInputStream(inputStream, scale);
            consumer.accept(image);
        } catch (IOException e) {
            EndingLibrary.LOGGER.error(e);
        } finally {
            if (image != null)
                image.free();
        }
    }
}
