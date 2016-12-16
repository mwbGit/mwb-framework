package com.mwb.framework.util;

import com.mwb.framework.log.Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.*;

public class LocalFileUtility {
    
	private static Log LOG = Log.getLog(LocalFileUtility.class);
	
    public static void saveFile(
            String path, 
            String fileName, 
            InputStream is) throws IOException {
        
        OutputStream os = null;
        
        try {
            File directory = new File(path);
            directory.mkdirs();
            
            File file = new File(path + File.separator + fileName);
            
            // Delete the file if exist
            file.delete();
            
            os = new FileOutputStream(file);
    
            byte[] buffer = new byte[1024];
    
            int length;
            
            // copy the file content in bytes
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (is != null) {
                is.close();
            }
            
            if (os != null) {
                os.close();
            }
        }
    }
    
    /**
	 *  将目标图片缩小成200*200并保存
	 * 
	 * @param inputStream
	 * @param outputStream
	 */
	public static void saveSmallImageFile(InputStream inputStream, OutputStream outputStream) { 
		try {
			BufferedImage input = ImageIO.read(inputStream);
			int width = input.getWidth(null);    // 得到源图宽  
	        int height = input.getHeight(null);  // 得到源图长  
			
			int[] resize = resizeFix(width, height, 200, 200);
			
			BufferedImage inputbig = new BufferedImage(resize[0], resize[1], BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) inputbig.getGraphics();
			g.setBackground(Color.WHITE); 
			g.drawImage(input, 0, 0, resize[0], resize[1], null); // 画图
			g.dispose();
			inputbig.flush();

			ImageIO.write(inputbig, "jpg", outputStream);
		} catch (Exception ex) {
			LOG.error("", ex);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}

				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				LOG.error("", e);
			}
		}
	}
	
	public static void deleteTemporaryPicture(String path, String name) {
		try {
			if(StringUtils.isNotBlank(name)) {
				path = path + File.separator + name;
			}
			File file = new File(path);
			if(file.isDirectory()) {
				FileUtils.deleteDirectory(file);
			} else {
				FileUtils.forceDelete(file);
			}
			
		} catch (Exception e) {
			LOG.error("", e);
		}
	}
	
	private static int[] resizeFix(int width, int height, int w, int h) throws IOException {  
        if (width / height > w / h) {  
        	h = height * w / width;              
        } else {
        	w = width * h / height;              
        }
        return new int[] {w, h};
    }
	
}
