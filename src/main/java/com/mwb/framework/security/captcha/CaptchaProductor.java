package com.mwb.framework.security.captcha;

import nl.captcha.Captcha;
import nl.captcha.Captcha.Builder;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.ChineseTextProducer;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.producer.TextProducer;
import nl.captcha.text.renderer.DefaultWordRenderer;
import nl.captcha.text.renderer.WordRenderer;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CaptchaProductor {

    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String FONT_SIZE = "font.size";
    private static final String LENGTH = "length";
    private static final String NOISE = "noise";
    private static final String TEXT = "text";

    private int width = 200;
    private int height = 50;
    private int fontSize = 40;
    private int length = 4;
    private boolean noise = true;
    private String text = "number"; // number, word, chinese

    private TextProducer textProducer;
    private WordRenderer wordRenderer;

    public CaptchaProductor(Properties properties) {
        // Parse properties
        if (StringUtils.isNumeric(properties.getProperty(WIDTH))) {
            width = Integer.valueOf(properties.getProperty(WIDTH));
        }

        if (StringUtils.isNumeric(properties.getProperty(HEIGHT))) {
            height = Integer.valueOf(properties.getProperty(HEIGHT));
        }

        if (StringUtils.isNumeric(properties.getProperty(FONT_SIZE))) {
            fontSize = Integer.valueOf(properties.getProperty(FONT_SIZE));
        }

        if (StringUtils.isNumeric(properties.getProperty(LENGTH))) {
            length = Integer.valueOf(properties.getProperty(LENGTH));
        }

        if (StringUtils.isNotBlank(properties.getProperty(NOISE))) {
            noise = Boolean.valueOf(properties.getProperty(NOISE));
        }

        if (StringUtils.isNotBlank(properties.getProperty(TEXT))) {
            text = properties.getProperty(TEXT);
        }

        // Initialize word render
        List<Font> fontList = new ArrayList<Font>();
        fontList.add(new Font("Courier", Font.BOLD, fontSize));
        List<Color> colorList = new ArrayList<Color>();
        colorList.add(Color.BLACK);

        wordRenderer = new DefaultWordRenderer(colorList, fontList);

        // Initialize text producer
        if ("chinese".equalsIgnoreCase(text)) {
            textProducer = new ChineseTextProducer(length);
        } else if ("number".equalsIgnoreCase(text)) {
            // no 0 and i to distinguish with I and O
            char[] numberChar = new char[] { '2', '3', '4', '5', '6', '7', '8' };

            textProducer = new DefaultTextProducer(length, numberChar);
        } else if ("word".equalsIgnoreCase(text)) {
            // no o and o to distinguish with 0 and 1
            char[] numberChar = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'k', 'm', 'n', 'p', 'r', 'w', 'x',
                    'y' };
            textProducer = new DefaultTextProducer(length, numberChar);
        } else {
            textProducer = new DefaultTextProducer();
        }
    }

    public String createCaptcha(HttpServletRequest request, HttpServletResponse response) {
        Builder builder = new Captcha.Builder(width, height);
        builder.addBorder().addBackground().addText(textProducer, wordRenderer);

        if (noise) {
            builder.addNoise();
        }

        Captcha captcha = builder.build();

        CaptchaServletUtil.writeImage(response, captcha.getImage());

        String answer = captcha.getAnswer();

        request.getSession().setAttribute(Captcha.NAME, answer);

        return answer;
    }
    
    public String createCaptchaForApp(HttpServletRequest request, HttpServletResponse response) {
        Builder builder = new Captcha.Builder(width, height);
        builder.addBorder().addBackground().addText(textProducer, wordRenderer);

        if (noise) {
            builder.addNoise();
        }

        Captcha captcha = builder.build();

        CaptchaServletUtil.writeImage(response, captcha.getImage());

        String answer = captcha.getAnswer();

        return answer;
    }

    public boolean checkAndRemoveCaptcha(HttpServletRequest request, String captcha) {
        boolean result = isValidCaptcha(request, captcha);
        request.getSession().removeAttribute(Captcha.NAME);
        return result;
       
    }
    
    public boolean isValidCaptcha(HttpServletRequest request, String captcha) {
        String answer = (String) request.getSession().getAttribute(Captcha.NAME);
        
        if (StringUtils.isBlank(answer) || StringUtils.isBlank(captcha)) {
        	return false;
        }
        
        return StringUtils.equalsIgnoreCase(answer, captcha);
       
    }
}
